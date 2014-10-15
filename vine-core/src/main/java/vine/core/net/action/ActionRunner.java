package vine.core.net.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.net.packet.Packet;
import vine.core.net.session.GameActionTaskResultListener;
import vine.core.net.session.UserHttpSession;
import vine.core.net.session.UserMinaSession;
import vine.core.net.session.UserSession;

/**
 * Created by liguofang on 2014/10/14.
 * 通讯接口Action处理运行工具类
 * 从Http和Mina都路由到此类中，进行Action的处理
 */
public class ActionRunner {
    private static final Logger log = LoggerFactory.getLogger(ActionRunner.class);

    /**
     * 运行通讯请求处理Action
     * @param session
     * @param p
     */
    public static void runAction(UserSession session , Packet p) {
        ActionHandler actionHandler = ActionRegister.getRegister().getRequestHandler(p.getPacketId());
        if(actionHandler == null){
            session.unlock(p.getPacketId());
            log.error("packetId:[{}]未注册!请求处理忽略! ",p.getPacketId());
            return;
        }
        if (log.isDebugEnabled()){log.debug("PacketId[{}]是否在运行：[{}]",p.getPacketId(),session.isLocked(p.getPacketId()));}
        if (session.isLocked(p.getPacketId())) {
            log.error("用户的相同请求仍在执行中，不能再次请求[SessionId:{}][IP:{}][packetId:{}],请求处理忽略! \n",
                    session.getSessionId(),session.getRemoteAddress(), p.getPacketId());
            return;
        }
        session.lock(p.getPacketId());
        if (log.isDebugEnabled()) {	log.debug("请求[PacketId={}]开始处理,加锁...",p.getPacketId());}
        /** 过滤器的处理放在MessageDispatcher中，action执行之前
         if (log.isDebugEnabled()){log.debug("PacketId[{}]调用过滤器进行消息处理...",m.getPacketId());}

         List<MessageInFilter> filters = MessageFilterPool.getMessageInFilters();
         if (filters != null && filters.size() > 0) {
         for (MessageInFilter filter : filters) {
         MessageFilterResult result = filter.filter(session, m);
         if (!result.isNext()) {
         if (log.isDebugEnabled()) {
         log.debug("[IP:" + session.getRemoteAddress() + "]过滤器处理：" + filter);
         }
         GameActionTaskResultListener.sendData(new UserSession[] {session}, actionHandler, m);
         // 解锁
         session.unlock(m.getPacketId());
         return;
         }
         }
         }
         if (log.isDebugEnabled()){log.debug("PacketId[{}]调用过滤器处理完毕...",m.getPacketId());}
         */
        runAction(new UserSession[] {session}, actionHandler, p);
    }

    /**
     * 运行消息推送处理Action
     * @param session 用户会话
     * @param pushId 推送ID
     * @param pushObj 推送的数据对象，由Action中的推送处理方法决定如何取其数据推送
     */
    public static void runAction(UserSession session, String pushId, Object pushObj) {
        ActionHandler pushHandler = ActionRegister.getRegister().getPushHandler(pushId);
        if(pushHandler == null){
            log.error("pushId: {}未定义",pushId);
            return;
        }
        runAction(new UserSession[] {session}, pushHandler, pushObj);
    }

    /**
     * 运行消息推送处理Action，推送给多个用户
     * @param sessions 多个用户会话
     * @param pushId 推送ID
     * @param pushObj 推送的数据对象，由Action中的推送处理方法决定如何取其数据推送
     */
    public static void runAction(UserSession[] sessions, String pushId, Object pushObj) {
        ActionHandler pushHandler = ActionRegister.getRegister().getPushHandler(pushId);
        if(pushHandler == null){
            log.error("pushId:{}未定义",pushId);
            return;
        }
        runAction(sessions, pushHandler, pushObj);
    }

    /**
     * 运行Action
     * @param sessions 多个用户会话
     * @param handler Action处理封装类，包含要执行的Action及其服务方法信息
     * @param param Action将要接收的参数
     */
    private static void runAction(UserSession[] sessions, ActionHandler handler, Object param) {
        if (sessions == null || sessions.length == 0) {
            log.error("session is null 处理忽略");
            return;
        }
        if (sessions[0] == null) {
            log.error("session is null 处理忽略");
            return;
        }
        log.debug("是否需要用户在线[{}]",handler.isNeedOnline());
        if (handler.isNeedOnline()) {
            if (!sessions[0].isOnline()) {// 用户不在线
                // 提示需要登录 TODO, 需要与客户端约定消息格式
//				Packet m = (Packet)param;
//				Packet result = m.packError(m.getPacketId(), PacketConst.RETCODE_USER_NEEDONLINE, m.getPacketType());
                log.error("提示玩家需要登录，处理忽略!!!");
                GameActionTaskResultListener.sendData(sessions, handler, null);
                return;
            }
        }

        if (param instanceof Packet) {// 检查上次的消息是否未发送出去（客户端未收到，再次请求相同操作）
            Packet m = (Packet)param;
            Integer msgId = m.getPacketId();
            if(log.isDebugEnabled()) {log.debug("消息包ID:[{}]",msgId);}
            Packet lastMessage = (Packet) sessions[0].getLastResponseMessage();
            if(log.isDebugEnabled()) {log.debug("上次消息包内容为:[{}]",lastMessage);}
            if (msgId != null && lastMessage != null) {// 客户端已传消息ID
                Integer lastMsgId = lastMessage.getPacketId();
                if(log.isDebugEnabled()) {log.debug("上次请求消息包ID:[{}]",lastMsgId);}
                if (msgId.equals(lastMsgId)) {// 请求时带有消息ID，且和上次响应的ID相同 // not executed...
                    if (log.isDebugEnabled()) {
                        log.debug("返回上一次的重复消息[HttpSessionId:{}][IP:{}]",sessions[0].getSessionId() ,
                                sessions[0].getRemoteAddress(),lastMessage.toString());
                    }
//					lastMessage.put(PacketConst.KEY_FILTER_HANDLE, "true");
                    GameActionTaskResultListener.sendData(sessions, handler, lastMessage);
                    return;
                }
            }
        }

        Object[] params = null;
        if (handler.getPushId() == null || handler.getPushId().equals("")) {
            params = new Object[] {sessions[0], param};
        } else {// 是推送，不需要传递用户会话数据给Action
            params = new Object[] {param};
        }
        log.debug("ActionRunner.params[{}],",params);

        if (sessions[0] instanceof UserHttpSession) {// HTTP类的请求，不使用多线程
            log.debug("同步处理executeAction.params[{}],sessions[{}]",params,sessions);
            handler.executeAction(params, new GameActionTaskResultListener(sessions));
        } else if (sessions[0] instanceof UserMinaSession) { //MINA请求，使用异步线程处理
//			Disruptor<ActionDisruptorEvent> dis = DisruptorFactory.getDisruptor(ActionDisruptorEvent.class);
//			ActionDisruptorEvent event = dis.createNewEvent();
//			event.setHandler(handler);
//			event.setSessions(sessions);
//	        event.setParam(params);
//			dis.dispatchEvent(event);
            //action的调用需要同步完成，保证在一个调用者的线程中。2014-7-08 liguofang
            log.debug("同步处理executeAction.params[{}],sessions[{}]",params,sessions);
            handler.executeAction(params, new GameActionTaskResultListener(sessions));
        }
    }
}
