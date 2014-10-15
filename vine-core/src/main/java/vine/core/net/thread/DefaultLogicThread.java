package vine.core.net.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缺省的线程实现类
 * @author liguofang
 * 定时执行一些任务，并从线程对应的queue中读取信息，进行简单的处理
 */
public class DefaultLogicThread extends LogicThread {
	private final static Logger log = LoggerFactory.getLogger("DefaultLogicThread");
	
	
	
	/**
	 * 在run方法中写实现逻辑
	 */
	@Override
	public void run() {
		while (true) {
		//	log.debug("DefaultLogicThread-[ThreadId={}]正在运行...",threadId);
			/*
			if (msgQueue != null) { 
				int size  = msgQueue.size();
			
				if (size !=0 ) {
					log.info("消息处理开始,size[{}]...",size);
					for(ThreadMessage tm:msgQueue) {						
						log.info("处理消息：{}",tm.getPacket());
						invokeAction(tm); //
						msgQueue.remove(tm); // 
					}		
					log.info("消息处理完毕!");
				}
			}
			*/
			try {
				this.sleep(10000);  //TODO　应用参数化配置				
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
		}
	}
	
	
}
