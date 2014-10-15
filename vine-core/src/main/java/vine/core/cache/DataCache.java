package vine.core.cache;

import java.util.Set;

 
/**
 * 数据缓存接口类
 * 
 * @author liguofang
 *
 */
public interface DataCache {	
	/**
	 * 存放key,object的数据到缓存中
	 * 如果key不存在则新建，存在则更新
	 * @param key
	 * @param object
	 * @return boolean
	 */
	public boolean set(String key, Object object);
	/**
	 * 根据key值获取对象
	 * @param key
	 * @return Object
	 */
	public Object get(String key);
	/**
	 * 根据key值删除缓存对象
	 * @param key
	 * @return
	 */
	public boolean remove(String key);
	
	/**
	 * 根据通配符获取keys集合
	 * @param pattern 通配符
	 * @return Set<String>
	 */
	public Set<String> keys(String pattern);
	
}

