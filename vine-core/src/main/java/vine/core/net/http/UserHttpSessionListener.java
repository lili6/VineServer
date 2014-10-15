package vine.core.net.http;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.net.session.UserSessionManager;


/**
 * HTTP会话创建和销毁监听器，初始化/销毁用户会话对象
 * @author PanChao
 */
public class UserHttpSessionListener implements HttpSessionListener {
	private static final Logger log = LoggerFactory.getLogger(UserHttpSessionListener.class);
	
	/**
	 * 监听HTTP会话创建
	 */
    public void sessionCreated(HttpSessionEvent se) {
    	if (log.isDebugEnabled()) { log.debug("会话已创建..."); }
    	UserSessionManager.getManager().createSession(se.getSession());
    }

    /**
     * 监听HTTP会话销毁
     */
    public void sessionDestroyed(HttpSessionEvent se) {
    	if (log.isDebugEnabled()) { log.debug("会话已销毁..."); }
    	UserSessionManager.getManager().closeSession(se.getSession());
    }
}
