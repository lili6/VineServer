/**
 * 
 */
package vine.core.net.session;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于http的用户会话缓存、管理类
 * @author PanChao
 * @author liguofang
 */
public class UserHttpSessionManager extends UserSessionManager {
	private static final Logger log = LoggerFactory.getLogger(UserHttpSessionManager.class);
	
	UserHttpSessionManager(){}
	
	@Override
	public UserSession createSession(Object connectSession) {		
		HttpSession httpSession = (HttpSession) connectSession;
		UserSession session = new UserHttpSession(httpSession);		
		getCache().add(session);
		session.create(); //应用实现用户会话创建监听器
		return session;
	}
	

	@Override
	public void closeSession(Object connectSession) {
		HttpSession httpSession = (HttpSession) connectSession;
		if(httpSession == null){
			log.error("close session failed, session is null, " + httpSession);
			return;
		}
		UserSession session = getCache().getBySessionId(httpSession.getId());
		if(session == null) {
			if (log.isDebugEnabled()) { log.debug("关闭会话失败，UserSession未找到，HttpSession[{}]", httpSession); }
			return;
		}
		destroySession(session, false);
	}

	
}
