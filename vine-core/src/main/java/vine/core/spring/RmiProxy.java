/**
 * 
 */
package vine.core.spring;

import java.lang.reflect.Method;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * 动态访问RMI的代理类
 * @author PanChao
 */
public class RmiProxy {
	private RmiProxyFactoryBean factory;
	private Object service;
	private FastClass serviceClazz;
	
	private RmiProxy(RmiProxyFactoryBean factory) {
		this.factory = factory;
		this.service = factory.getObject();
		this.serviceClazz = FastClass.create(this.service.getClass());
	}
	
	/**
	 * 创建RMI代理
	 * @param serviceInterface RMI接口类路径，如：com.xiao.data.service.PlayerService
	 * @param serviceUrl RMI服务地址，如：rmi://127.0.0.1:8501/PlayerService
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static RmiProxy create(String serviceInterface, String serviceUrl) throws ClassNotFoundException{
		RmiProxyFactoryBean factory = null;
		factory = new RmiProxyFactoryBean();
		factory.setServiceInterface(Class.forName(serviceInterface));
		factory.setServiceUrl(serviceUrl);
		factory.afterPropertiesSet();
		return new RmiProxy(factory);
	}
	
	/**
	 * 调用RMI服务方法
	 * @param methodName 方法名称
	 * @param args 方法参数
	 * @return 方法执行后的返回值
	 * @throws Exception
	 */
	public Object invoke(String methodName, Object... args) throws Exception {
		Method method = findMethod(serviceClazz.getJavaClass(), methodName, args);
		if (method == null) return null;
		FastMethod fastMethod = serviceClazz.getMethod(method);
		return fastMethod.invoke(service, args);
	}
	
	private Method findMethod(Class clazz, String methodName, Object... args) {
		Method method = null;
		Class[] argsClazz = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			Object o = args[i];
			argsClazz[i] = o.getClass();
		}
		Method[] ms = serviceClazz.getJavaClass().getDeclaredMethods();
		for (Method m : ms) {
			if (!m.getName().equals(methodName)) continue;// 方法名不同
			Class[] paramsClazz = m.getParameterTypes();
			if (paramsClazz.length != argsClazz.length) continue;// 参数数量不一样
			boolean paramFind = true;
			for (int i = 0; i < paramsClazz.length; i++) {
				Class paramClazz = paramsClazz[i];
				String paramClazzName = changeSimpleTypeName(paramClazz.getName());
				Class argClazz = argsClazz[i];
				paramFind &= paramClazzName.equals(argClazz.getName());
			}
			if (paramFind) {
				method = m;
				break;
			}
		}
		return method;
	}
	
	private String changeSimpleTypeName(String typeName) {
		if (typeName.equals("int")) {
			typeName = "java.lang.Integer";
		} else if (typeName.equals("long")) {
			typeName = "java.lang.Long";
		} else if (typeName.equals("float")) {
			typeName = "java.lang.Float";
		} else if (typeName.equals("double")) {
			typeName = "java.lang.Double";
		} else if (typeName.equals("boolean")) {
			typeName = "java.lang.Boolean";
		} else if (typeName.equals("byte")) {
			typeName = "java.lang.Byte";
		} else if (typeName.equals("short")) {
			typeName = "java.lang.Short";
		}
		return typeName;
	}

	public RmiProxyFactoryBean getFactory() {
		return factory;
	}

	public Object getService() {
		return service;
	}
}
