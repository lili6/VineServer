/**
 * 
 */
package application;

import java.io.Serializable;

import org.apache.log4j.Logger;
import vine.core.net.session.UserSession;
import vine.core.net.session.UserSessionClosedListener;


/**
 * 服务器会话关闭监听器，作持久化操作
 * @author liguofang
 */
public class SaveUserListener implements UserSessionClosedListener, Serializable {
	private static final long serialVersionUID = -1929026866477656857L;
	private static final Logger log = Logger.getLogger(SaveUserListener.class);
	public static SaveUserListener instance = new SaveUserListener();
	
	@Override
	public void onClosed(UserSession session) {

	}

}
