package application;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import vine.core.net.session.UserSession;
import vine.core.net.session.UserSessionManager;


/**
 * HTTP会话创建和销毁监听器，初始化/销毁用户会话对象
 * @author liguofang
 */
public class SessionListener implements HttpSessionListener {
	private static final Logger log = Logger.getLogger(SessionListener.class);
	
	/**
	 * 监听HTTP会话创建
	 */
    public void sessionCreated(HttpSessionEvent se) {
    	UserSession session = UserSessionManager.getManager().createSession(se.getSession());
    	if (log.isDebugEnabled()) { log.debug("一个新的会话被创建，HttpSessionId：" + session.getSessionId()); }
    }

    /**
     * 监听HTTP会话销毁
     */
    public void sessionDestroyed(HttpSessionEvent se) {
    	if (log.isDebugEnabled()) { log.debug("会话被销毁，HttpSessionId：" + se.getSession().getId()); }
    	UserSessionManager.getManager().closeSession(se.getSession());
    }
}
