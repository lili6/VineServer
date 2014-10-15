package vine.core.config;

/**
 * Created by liguofang on 2014/10/14.
 * 资源路径表示类
 */
public class ResourcePath {
    private ResourcePathType pathType;
    /** 资源路径 */
    private String path;
    public ResourcePath(ResourcePathType pathType, String path) {
        super();
        this.pathType = pathType;
        this.path = path;
    }
    public ResourcePathType getPathType() {
        return pathType;
    }
    public void setPathType(ResourcePathType pathType) {
        this.pathType = pathType;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
