package vine.core.cache;

import java.io.Serializable;


/**
 * 
 * @author liguofang
 * 
 * 
 */
public class Data implements Serializable {
	/**HASH KEY=prefix:seqno*/
	private String  key; 	
	/**数据结构*/
	private Object value;
	
	
	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}


	public String toString() {
		return "key = " +  key + ", value = " + value;
	}
}
