package vine.core.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import vine.core.config.Configuration;

import java.util.Locale;
import java.util.Properties;

/**
 * Spring工具类
 * Created by liguofang on 2014/10/14.
 */
public class SpringBeanFactory {
    private static final Logger log = LoggerFactory.getLogger(SpringBeanFactory.class);
    /** Spring配置文件默认位置 */
    private static String CONFIG = "context/context*.xml";
    private static ApplicationContext CONTEXT = null;
    private static Locale LOCALE = new Locale("zh_CN");

    private SpringBeanFactory() {
    }

    public static synchronized void init(){
        try {
            if(CONTEXT == null){
                CONTEXT = new ClassPathXmlApplicationContext(CONFIG);
                Properties props = new Properties();
                for(Object key : Configuration.getInstance().keySet()){
                    props.put(key, Configuration.getInstance().get(key));
                }
                PropertyPlaceholderConfigurer propHolder = new PropertyPlaceholderConfigurer();
                propHolder.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
                propHolder.setIgnoreUnresolvablePlaceholders(true);
                propHolder.setProperties(props);

                ((ClassPathXmlApplicationContext)CONTEXT).addBeanFactoryPostProcessor(propHolder);
                ((ClassPathXmlApplicationContext)CONTEXT).refresh();
            }
            log.error("AppBeanFactory初始化成功!");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("AppBeanFactory初始化出错", e);
        }
    }

    /**
     * 使用绝对路径为spring加载properties配置文件的初始化方法
     * @param resPaths
     */
    public static synchronized void init(String... resPaths){
        try {
            if(CONTEXT == null){
                CONTEXT = new FileResourceClassPathXmlApplicationContext(CONFIG, resPaths);
            }
        } catch (Exception e) {
            log.error("Spring初始化出错", e);
        }
    }

    public static synchronized void reset(){
        try {
            CONTEXT = new ClassPathXmlApplicationContext(CONFIG);
        } catch (Exception e) {
            log.error("Spring重载出错", e);
        }
    }

    public static synchronized ApplicationContext getContext(){
        if(CONTEXT == null) init();
        return CONTEXT;
    }

    public static void setLocale(String language){
        LOCALE = new Locale(language);
    }

    public static Object getBean(String name){
        try {
            return CONTEXT.getBean(name);
        } catch (Exception e) {
            log.error("Spring查找Bean出错", e);
        }
        return null;
    }

    public static String getMessage(String key){
        try {
            return CONTEXT.getMessage(key, null, LOCALE);
        } catch (Exception e) {
            log.error("Spring查找Message出错", e);
        }
        return null;
    }

    public static String getMessage(String key, Object... args){
        try {
            return CONTEXT.getMessage(key, args, LOCALE);
        } catch (Exception e) {
            log.error("Spring查找Message出错", e);
        }
        return null;
    }

    public static String getMessage(Locale locale, String key, Object... args){
        try {
            return CONTEXT.getMessage(key, args, locale);
        } catch (Exception e) {
            log.error("Spring查找Message出错", e);
        }
        return null;
    }
}
