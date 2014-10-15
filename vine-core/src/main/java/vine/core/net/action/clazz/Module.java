package vine.core.net.action.clazz;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义通讯接口主模块<br/>
 * 提供一个int值，指定主模块的命令编号，如：100<br/>
 * @author liguofang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    /** 主模块的命令编号，如：100 */
    int value();
}
