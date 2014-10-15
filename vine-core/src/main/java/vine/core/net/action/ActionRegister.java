package vine.core.net.action;

import vine.core.net.action.clazz.ActionClassRegister;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liguofang on 2014/10/14.
 * 通讯接口Action处理器注册、管理类
 */
public abstract class ActionRegister {
    private static final ActionClassRegister classRegister = new ActionClassRegister();
//    private static final ActionScriptRegister scriptRegister = new ActionScriptRegister();
    private static ActionType actionType = null;

    protected Map<Integer, ActionHandler> commandActionMap = new HashMap<Integer, ActionHandler>();
    protected Map<String, ActionHandler> pushActionMap = new HashMap<String, ActionHandler>();

    /**
     * 获取Action注册/管理类
     * @param type Action处理方式，CLASS或SCRIPT
     * @return
     */
    public static ActionRegister getRegister(ActionType type) {
        if (actionType == null) {// 第一次获取，记录类型
            actionType = type;
        }
        if (actionType == ActionType.CLASS) {
            return classRegister;
        } else if (actionType == ActionType.SCRIPT) {
//            return scriptRegister;
        }
        return classRegister;// 如果类型未找到，默认使用Class类型的用户会话管理器
    }

    /**
     * 获取默认Action注册/管理类
     * @return
     */
    public static ActionRegister getRegister() {
        if (actionType == ActionType.CLASS) {
            return classRegister;
        } else if (actionType == ActionType.SCRIPT) {
//            return scriptRegister;
        }
        return classRegister;// 如果类型未找到，默认使用Class类型的用户会话管理器
    }
    /**
     * <pre>
     * 初始化，应用程序启动时调用
     * Class方式：参数1：使用了Module注解的Action类包路径，如：com.ssgn.testgame.action
     * 	参数2：是否使用spring初始化Action类（可选，默认为false）
     * Script方式：3个参数：脚本文件路径、脚本可用扩展函数类路径、脚本类型(后缀名，可选参数)
     * </pre>
     */
    public abstract void init(String... initParam);

    /**
     * 通过命令编号查找通讯请求服务
     * @param opCode 命令编号，如：100001
     * @return
     */
    public ActionHandler getRequestHandler(int opCode) {
        return commandActionMap.get(opCode);
    }

    /**
     * 通过推送ID查找消息推送服务
     * @param pushId 推送ID，如：pushRole
     * @return
     */
    public ActionHandler getPushHandler(String pushId){
        return pushActionMap.get(pushId);
    }
}
