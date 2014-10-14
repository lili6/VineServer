/**
 * 
 */
package vine.core.net.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.dolphin.net.HOpCode;
import com.mrd.dolphin.net.action.ActionRunner;
import com.mrd.dolphin.net.packet.MessageFilter;
import com.mrd.dolphin.net.packet.MessageFilterPool;
import com.mrd.dolphin.net.packet.Packet;
import com.mrd.dolphin.net.packet.Packet.PacketType;
import com.mrd.dolphin.net.packet.PacketConst;
import com.mrd.dolphin.net.session.GameActionTaskResultListener;
import com.mrd.dolphin.net.session.UserHttpSession;
import com.mrd.dolphin.net.session.UserSession;
import com.mrd.dolphin.net.session.UserSessionClosedListener;
import com.mrd.dolphin.net.session.UserSessionManager;
import com.mrd.dolphin.thread.MessageDispatcher;
import com.mrd.dolphin.utils.StringUtil;

/**
 * 游戏通讯处理Servlet，处理：<br/>
 * 用户会话关闭监听器的注册、通讯内容解析为JSON、Action转发 要监听用户会话关闭，
 * 在web.xml中置该servlet的配初始化参数：
 * UserSessionClosedLis，指定实现了UserSessionClosedListener接口的类
 * MessageFilter，指定消息类型STRING, JSON
 * 
 * @author liguofang
 */
public class VineServerServlet extends HttpServlet {
	private static final long serialVersionUID = -9081727533648612685L;
	private static final Logger log = LoggerFactory.getLogger(VineServerServlet.class);
	private static final int BUFFER_SIZE = 1024;
	//用户会话关闭监听器
	private UserSessionClosedListener sessionClosedListener;
	
	/** 指定消息类型STRING, JSON，Protobuf 默认为JSON */
	private String packetType = null;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		//会话关闭时监听器
		String listenerClassPath = config.getInitParameter("UserSessionClosedLis");
		if (listenerClassPath != null) {
			try {
				Class listenerClazz = Class.forName(listenerClassPath);
				sessionClosedListener = (UserSessionClosedListener) listenerClazz
						.newInstance();
			} catch (Exception e) {
				log.error("SessionClosedListener配置错误：" + listenerClassPath, e);
			}
		}
		//消息过滤器
		String messageFilterClassPaths = config.getInitParameter("MessageFilter");
		if (messageFilterClassPaths != null) {
			String[] messageFilterClassPath = messageFilterClassPaths
					.split(",");
			for (String classPath : messageFilterClassPath) {
				try {
					 Class filterClazz = Class.forName(classPath);
					 MessageFilter filter = (MessageFilter) filterClazz.newInstance();
					 MessageFilterPool.addMessageFilter(filter);
				} catch (Exception e) {
					log.error("MessageFilter配置错误：" + classPath, e);
				}
			}
		}
		//消息包类型
		packetType = config.getInitParameter("packetType");
		if (packetType == null) {
			packetType = PacketType.PB.name(); // 缺省是PB格式，其他各种格式都要进行测试
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String ip = req.getRemoteAddr();
//		if (Firewall.defend(ip)) { TODO 防火墙判断需要后面添加
//			return;
//		}
		long begin = System.currentTimeMillis();
		HttpSession httpSession = req.getSession();
		UserSession session = UserSessionManager.getCache().getBySessionId(httpSession.getId());
		if (session == null) {
			session = UserSessionManager.getManager().createSession(httpSession);
			if (log.isDebugEnabled()) {
				log.debug("[IP:{}]用户会话不存在，重新创建，HttSessionID:[{}]"
						,ip, httpSession.getId());
			}
		}
		if (session.getClosedListeners().size() == 0
				&& sessionClosedListener != null) {// 第一次访问，添加会话关闭监听器
			session.addClosedListener(sessionClosedListener);
		}
		session.setReceiveTime(begin);
		session.setLastRequestTime(begin);
		UserHttpSession userHttpSession = (UserHttpSession) session;
		userHttpSession.setHttpRequest(req);// 设置最新的HttpRequest
		userHttpSession.putHttpResponse("default", resp);//缓存最新的HttpResponse
		
		//读取请求数据流
		byte[] buff = readContent(req);		
		if (buff == null) {
			log.error("[IP:{}]请求的消息错误[{}]",ip, buff);
			//对错误的相应数据处理
			Packet packet = Packet.packError(HOpCode.OPCODE_COMM_ERROR,
					PacketConst.RETCODE_REQUEST_MESSAGE_EMPTY,PacketType.valueOf(packetType));
			GameActionTaskResultListener.sendData(new UserSession[] {session}, null, packet);				
			return;
		}
		log.info("接收[总长度：{}][HttpSessionId:{}][IP:{}] \n 请求buffer:\n[{}]",
					buff.length, httpSession.getId(), ip, StringUtil.bytes2HexStr(buff));
		
		Packet m = Packet.parseHttpRequest(buff, PacketType.valueOf(packetType));// 解析消息数据
		log.debug("包类型：[{}]，拆包后 :",packetType,m);
		//根据消息类型组成不同的包类型		
		if (m == null) {
			log.error("[IP:" + ip + "]消息解析失败，消息内容：[{}]", m );			
			// 对错误的相应数据处理
			Packet packet = Packet.packError(HOpCode.OPCODE_COMM_ERROR,
					PacketConst.RETCODE_MESSAGE_PARSE_ERROR,PacketType.valueOf(packetType));
			log.error("组错误包返回...");
			GameActionTaskResultListener.sendData(new UserSession[] {session}, null, packet);			
			return;
		}
		if(m.getPacketId() <= 0 || m.getPacketId() >= 65536){
			log.error("[IP:{}]请求消息包[{}]不存在,消息内容:[{}]", ip, PacketConst.PACKET_ID , m);
			// 对错误的相应数据处理
			Packet packet = Packet.packError(HOpCode.OPCODE_COMM_ERROR,
					PacketConst.RETCODE_PACKETID_NOT_EXIST,PacketType.valueOf(packetType));
			log.error("组错误包返回...");
			GameActionTaskResultListener.sendData(new UserSession[] {session}, null, packet);	
			return;
		}
		log.info("PacketId[{}]是否锁定:[{}]",m.getPacketId(),session.isLocked(m.getPacketId()) );
		if (!session.isLocked(m.getPacketId())) {// 请求命令是可执行的，缓存request和response
			userHttpSession.putHttpResponse(session.getSessionId(), resp);// 缓存最新的HttpResponse
		}
//		ActionRunner.runAction(session, m);
		MessageDispatcher.dispatch(session, m);
		if (log.isDebugEnabled()) {
			long end = System.currentTimeMillis();		
			log.info("请求响应总执行时间[HttpSessionId:{}][IP:{}][packetId:{}]:{}毫秒 \n" , 
					httpSession.getId(), ip, m.getPacketId(),(end - begin));
		}
		

	}
	/**
	 * 读取消息包信息流
	 * @param req
	 * @return
	 */
	private byte[] readContent(HttpServletRequest req) {
		try {
			InputStream is = req.getInputStream();
			int bufSize = req.getContentLength();//数据流长度
			byte[] buffer = new byte[bufSize];
			int size = is.read(buffer);
			int readedSize = size;
			if (size != bufSize) {
				while(size >-1) {
					size = is.read(buffer, readedSize,bufSize-readedSize);
					readedSize += size;
				}
			}			
			return buffer;
		} catch(Exception e) {
			log.error("读取Http请求数据流错误\n:{}",e);
			e.printStackTrace();
			return null;
		}
	}
}
