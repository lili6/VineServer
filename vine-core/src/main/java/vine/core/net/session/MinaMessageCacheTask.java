package vine.core.net.session;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrd.dolphin.net.packet.Packet;

/**
 * 用户客户端接收的消息缓冲推送Timer
 * @author PanChao
 */
public class MinaMessageCacheTask extends TimerTask {
	private static final Logger log = LoggerFactory.getLogger(MinaMessageCacheTask.class);
	/**任务执行的延迟时间,ms*/
	private static final long delayTime = 200;
	/**发送间隔时间， ms*/
	private static final long interval = 50;
	private static final MinaMessageCacheTask instance = new MinaMessageCacheTask();
	
	private static int logCount = 0;
	
	private Timer timer = null;
	private Set<UserSession> sessions = new ConcurrentHashSet<>();
	
	public static MinaMessageCacheTask getInstance() {
		return instance;
	}
	
	/**
	 * 启动缓冲消息推送任务
	 */
	public void start() {
		try {
			timer = new Timer("Timer-" + MinaMessageCacheTask.class.getSimpleName()); 
			timer.scheduleAtFixedRate(this, delayTime,interval);
		} catch (Exception e) {
			log.error("创建消息推送程序失败", e);
		}
	}
	
	/**
	 * 添加要监听缓存消息的用户会话
	 * @param session
	 */
	void addListenSession(UserSession session) {
		sessions.add(session);
	}
	
	/**
	 * 用户退出后从监听队列中移除该用户会话
	 * @param session
	 */
	void removeListenSession(UserSession session) {
		sessions.remove(session);
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("MinaMessageCacheTask");		
		for (UserSession session : sessions) {
			if(!session.isOnline()){
				UserSessionManager.getManager().destroySession(session, true);
				continue;
			}
			// 消息未积压到一定时间，不发送
			if(session.getMessageLastAddTime() == 0 || 
					System.currentTimeMillis() - session.getMessageLastAddTime() < 100) 
				continue;
			Packet data = session.popWaitMessage();
			if (data == null) {
				continue;
			}
			session.responseImmediately(data);
		}
	}

}
