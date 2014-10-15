package vine.core.config;

/**
 * 资源路径类型
 * Created by liguofang on 2014/10/14.
 */
public enum ResourcePathType {
    /** 表示资源所处位置在classes目录下，以“/”开头表示classes根目录 */
    CLASSPATH,
    /** 表示资源所处位置在文件系统某处，可以是绝对或相对路径 */
    FILEPATH
}
