package vine.core.net.thread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程池管理器
 * 负责线程的生命周期管理
 * @author liguofang
 *
 */
public class ThreadPoolManager {
	private static Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);
	/*线程ID*/
	private static AtomicLong threadNum = new AtomicLong(0);	
	/*线程池 key：ThreadId，value：LogicThread*/
	private static ConcurrentMap<Long, LogicThread> pool = new ConcurrentHashMap<Long,LogicThread>(); 
	
	/**
	 * 线程注册
	 * @param thread
	 * @return 线程Id
	 */
	public static boolean registerThread(Long threadId,LogicThread thread) {
		if(!pool.containsKey(threadId)){
			threadNum.incrementAndGet();
			thread.setThreadId(threadId);
			pool.put(threadId, thread);
			log.debug("线程[{}]注册成功...",threadId);
			return true;
		}
		return false;
	}
	
	public static void unRegisterThread(long threadId) {		
		pool.remove(threadId);
		threadNum.decrementAndGet();
	}
	
	public static void init() {
		
	}
	/**
	 * 启动所有线程
	 */
	public static void startAll() {
		log.info("开始启动所有线程：");
		for(Long threadId:pool.keySet()) {
			start(threadId);
			
		}
	}
	/**
	 * 启动threadId对应的线程
	 * @param threadId
	 */
	public static void start(long threadId) {
		LogicThread lt = pool.get(threadId);
		if (lt != null) {
			lt.start();	
			log.info("线程[{}]已启动.",threadId);
		} else {
			log.error("threadId:[{}]对应的线程不存在,启动失败...",threadId);
		}
	}
	
	/**
	 * 根据ThreadId选择某一个线程
	 * @param threadId
	 */
	public static LogicThread selectThread(long threadId) {
		LogicThread lt = pool.get(threadId);
		if (lt != null) {
			return lt;
		} else {
			log.warn("线程选择错误：["+threadId +"]对应的线程不存在!");
			return null;			 
		}
	}
	
}
