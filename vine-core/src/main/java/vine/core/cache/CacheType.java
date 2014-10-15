/**
 * 
 */
package vine.core.cache;

/**
 * 缓存数据的方式
 * @author PanChao
 * @author liguofang
 */
public enum CacheType {
	/** 在应用程序内部缓存用户数据 */
	CLASS, 
	/** 使用Memcached缓存用户数据 */
	MEMCACHED,
	/** 使用Redis缓存用户数据 */
	REDIS
}
