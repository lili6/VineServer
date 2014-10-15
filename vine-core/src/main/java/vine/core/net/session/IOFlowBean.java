package vine.core.net.session;
/**
 * 统计bean
 * @author liguofang
 *
 */
public class IOFlowBean {

	/*消息包流量大小*/
	private long totalRate;
	/*消息包个数*/
	private int count;

	public long getTotalRate() {
		return totalRate;
	}
	public void setTotalRate(long totalRate) {
		this.totalRate = totalRate;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
	
	
}

