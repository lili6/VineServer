package vine.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 序列化和反序列化工具
 * @author liguofang
 *
 */
public class SerializeUtil {
	private static Logger log = LoggerFactory.getLogger(SerializeUtil.class);
	/**
	 * 序列化对象
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			//序列化	
			baos = new ByteArrayOutputStream();	
			oos = new ObjectOutputStream(baos);	
			oos.writeObject(object);	
			byte[] bytes = baos.toByteArray();	
			return bytes;
		} catch (Exception e) {		 
			log.error("对象序列化时错误，Object=[{}]", object);
			e.printStackTrace();
		}
			return null;
	}
	/**
	 * 反序列化生成对象
	 * @param bytes
	 * @return
	 */
	public static Object unserialize(byte[] bytes) {

		ByteArrayInputStream bais = null;
		try {
			//反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			log.error("反序列化对象时错误，[{}]", e);
			e.printStackTrace();
		}
		return null;

	}


}
