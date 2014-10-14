/**
 * 
 */
package vine.core.net.session;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于mina的用户会话缓存、管理类
 * @author PanChao
 * @author liguofang
 */
public class UserMinaSessionManager extends UserSessionManager {
	private static final Logger log = LoggerFactory.getLogger(UserMinaSessionManager.class);
	
	UserMinaSessionManager(){}
	
	@Override
	public UserSession createSession(Object connectSession) {
		IoSession ioSession = (IoSession) connectSession;
		UserSession session = new UserMinaSession(ioSession);		
		getCache().add(session);
		session.create(); //应用实现用户会话创建监听器
		return session;
	}
	
	@Override
	public void closeSession(Object connectSession){
		IoSession ioSession = (IoSession) connectSession;
		if(ioSession == null){
			log.error("close session failed, session is null, " + ioSession);
			return;
		}
		long sessionId = ioSession.getId();
		UserSession session = getCache().getBySessionId(String.valueOf(sessionId));		
		if(session == null) {
			if (log.isDebugEnabled()) { log.debug("UserSession未找到，sessionId[{}]已经移除，IoSession[{}]",sessionId, ioSession); }
			return;
		}
		destroySession(session, false); 
	}

}

