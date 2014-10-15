package vine.core.net.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.net.action.ActionRunner;

/**
 * 线程处理基类
 * 
 * @author liguofang
 *
 */
public abstract class LogicThread extends Thread {
	private final static Logger log = LoggerFactory.getLogger(LogicThread.class);
	
	//protected ConcurrentLinkedQueue<ThreadMessage> msgQueue = null;
	//子类赋值，每个线程对应注册的ID
	protected Long threadId = null;
	
	/**
	 * 获取当前的线程Id
	 * @return
	 */
	public Long getThreadId() {
		return threadId;
	}
	/**
	 * 由子类实现，在初始化注册后返回的Id，并设置
	 * @param threadId
	 */
	protected void setThreadId(Long threadId) {		
		this.threadId = threadId;
		String threadName = this.getName();
		// 修改默认线程命名
		if(threadName!=null && threadName.startsWith("Thread-")){
			this.setName(this.getClass().getSimpleName()+"-"+this.threadId);
		}
	}
	/**
	 * 调用Action进行处理
	 * @param tm
	 */
	protected void invokeAction(ThreadMessage tm) {
		ActionRunner.runAction(tm.getSession(), tm.getPacket());
	}
	

}
