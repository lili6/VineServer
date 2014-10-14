/**
 * 
 */
package vine.core.net.session;


/**
 * 用户会话建立监听器，实现后用于执行用户数据持久化，缓存清理等工作
 * @author liguofang
 */
public interface UserSessionCreatedListener {
	public void onCreated(UserSession session);
}
