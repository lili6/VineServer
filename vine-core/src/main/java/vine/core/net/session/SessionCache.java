package vine.core.net.session;

import java.util.Collection;
import java.util.List;

/**
 * 用户会话缓存数据访问接口
 * @author PanChao
 * @author liguofang
 */
public interface SessionCache {
	
	/**
	 * 添加新的用户会话，用于刚建立连接时
	 * @param session
	 */
	public void add(UserSession session);
	
	/**
	 * 绑定会话所属用户，用于用户登录后
	 * @param userId
	 * @param session
	 */
	public void bind(String userId, UserSession session);
	
	/**
	 * 更新用户会话缓存数据，例：更新到memcached中
	 * @param session
	 */
	public void update(UserSession session);
	
	/**
	 * 从缓存中删除用户会话
	 * @param session
	 */
	public void remove(UserSession session);

	/**
	 * 通过用户ID查找用户会话
	 * @param userId
	 * @return
	 */
	public UserSession getByUserId(String userId);
	
	/**
	 * 通用会话ID查找用户会话
	 * @param sessionId
	 * @return
	 */
	public UserSession getBySessionId(String sessionId);
	
	/**
	 * 获取所有用户会话
	 * @return
	 */
	public List<UserSession> getAllSession();

	/**
	 * 获取所有在线用户会话
	 * @return
	 */
	@Deprecated
	public List<UserSession> getAllOnlineSession();
	
	/**
	 * 获取所有在线用户会话
	 * @return
	 */
	public Collection<UserSession> getAllOnlineSessions();
	
	/**
	 * 统计当前缓存的所有用户会话数量
	 * @return
	 */
	public int countAll();
	
	/**
	 * 统计当前缓存的在线用户会话数量
	 * @return
	 */
	public int countAllOnline();
}
