/**
 * 
 */
package vine.core.net.session;


/**
 * 用户会话关闭监听器，实现后用于执行用户数据持久化，缓存清理等工作
 * @author PanChao
 */
public interface UserSessionClosedListener {
	public void onClosed(UserSession session);
}
