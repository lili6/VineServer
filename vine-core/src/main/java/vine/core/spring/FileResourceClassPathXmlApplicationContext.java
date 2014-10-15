package vine.core.spring;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Created by liguofang on 2014/10/14.
 * 使用绝对路径为spring加载properties配置文件的扩展类
 */
public class FileResourceClassPathXmlApplicationContext extends
        ClassPathXmlApplicationContext {
    private String[] filePaths;

    public FileResourceClassPathXmlApplicationContext(String location, String... filePaths){
        super(location);
        this.filePaths = filePaths;
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
        Resource[] resources = new Resource[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            String filePath = filePaths[i];
            resources[i] = new FileSystemResource(filePath);
        }
        cfg.setLocations(resources);
        cfg.postProcessBeanFactory(beanFactory);
    }
}
