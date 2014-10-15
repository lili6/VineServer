package vine.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * BeanUtil
 */
public class BeanUtil {
	private static Logger log = LoggerFactory.getLogger(BeanUtil.class);
	
	/**
	 * clone
	 * @param object
	 * @return
	 */
	public static Object clone(Object object){
		if(object == null) return null;
		Object newObject = null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			newObject = ois.readObject();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return newObject;
	}
	
	/**
	 * copyProperties
	 * @param source
	 * @param target
	 */
	public static void copyProperties(Object source, Object target) {
		if (source == null || target == null) return;
		try {
			BeanUtils.copyProperties(source, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * copySameProperties
	 * @param source
	 * @param target
	 */
	@SuppressWarnings("rawtypes")
	public static void copySameProperties(Object source, Object target) {
		if (source == null || target == null) return;
		
		Class fromClass = source.getClass();
		Class toClass = target.getClass();

		Field[] oneFields = fromClass.getDeclaredFields();
		Field[] twoFields = toClass.getDeclaredFields();

		for (Field one : oneFields) {
			one.setAccessible(true);
			for (Field two : twoFields) {
				two.setAccessible(true);
				if (one.getName().equals(two.getName())
						&& one.getType().getName().equals(two.getType().getName())) {
					try {
						two.set(target, one.get(source));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void setFieldValue(Object target, String fname, Class<?> ftype, Object fvalue) {
		setFieldValue(target, target.getClass(), fname, ftype, fvalue);
	}

	public static void setFieldValue(Object target, Class<?> clazz, String fname, Class<?> ftype, Object fvalue) {
		if (target == null || fname == null || "".equals(fname)
				|| (fvalue != null && !ftype.isAssignableFrom(fvalue.getClass()))) {
			return;
		}

		try {
			Method method = clazz.getDeclaredMethod(
					"set" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1), ftype);
			//if (!Modifier.isPublic(method.getModifiers())) {
			method.setAccessible(true);
			//}
			method.invoke(target, fvalue);
		} catch (Exception me) {
			if (log.isDebugEnabled()) {
				log.debug("", me);
			}
			try {
				Field field = clazz.getDeclaredField(fname);
				//if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
				//}
				field.set(target, fvalue);
			} catch (Exception fe) {
				if (log.isDebugEnabled()) {
					log.debug("", fe);
				}
			}
		}
	}

	public static Object getFieldValue(Object target, String fname) {
		return getFieldValue(target, target.getClass(), fname);
	}

	public static Object getFieldValue(Object target, Class<?> clazz, String fname) {
		if (target == null || fname == null || "".equals(fname)) {
			return null;
		}

		boolean exCatched = false;
		try {
			String methodname = "get" + StringUtils.capitalize(fname);
			Method method = clazz.getDeclaredMethod(methodname);
			//if (!Modifier.isPublic(method.getModifiers())) {
			method.setAccessible(true);
			//}
			return method.invoke(target);
		} catch (NoSuchMethodException e) {
			exCatched = true;
		} catch (InvocationTargetException e) {
			exCatched = true;
		} catch (IllegalAccessException e) {
			exCatched = true;
		}

		if (exCatched) {
			try {
				Field field = clazz.getDeclaredField(fname);
				//if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
				//}
				return field.get(target);
			} catch (Exception fe) {
				if (log.isDebugEnabled()) {
					log.debug("", fe);
				}
			}
		}
		return null;
	}
	
}
