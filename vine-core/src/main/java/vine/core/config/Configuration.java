package vine.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.utils.UsefulHashMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by liguofang on 2014/10/10.
 */
public class Configuration  extends UsefulHashMap<Object, Object> {
    //public static final String KEY_GAME_CODE = "game_code";
    private static final long serialVersionUID = 4375542438017399056L;
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);
    private static Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    /**
     * 获得配置信息，如果没有配置，则返回defValue
     * @param key
     * @param defValue
     * @return
     */
    public Object get(Object key,Object defValue){
        Object val = super.get(key);
        if(val == null) val = defValue;
        return val;
    }

    /**
     * 初始化properties文件
     * @param paths
     */
    public void load(ResourcePath... paths) {
        for (ResourcePath path : paths) {
            if (path.getPathType() == ResourcePathType.CLASSPATH) {
                loadPropertiesForClass(path);
            } else if (path.getPathType() == ResourcePathType.FILEPATH) {
                loadPropertiesForFile(path);
            }
        }
    }


    /**
     * 加载Classpath下的properties文件
     */
    protected void loadPropertiesForClass(ResourcePath path) {
        Properties prop = new Properties();
        try  {
            InputStream in = Configuration.class.getResourceAsStream(path.getPath());
            prop.load(in);
        } catch (Exception e) {
            log.error("Classpath Properties init Failed. path:" + path, e);
        }
        this.putAll(prop);
    }

    /**
     * 加载绝对或相对路径下的properties文件
     */
    protected void loadPropertiesForFile(ResourcePath path) {
        Properties prop = new Properties();
        try  {
            FileInputStream in = new FileInputStream(path.getPath());
            prop.load(in);
        } catch (IOException e) {
            log.error("File Properties init Failed. path:" + path, e);
        }
        this.putAll(prop);
    }
}
