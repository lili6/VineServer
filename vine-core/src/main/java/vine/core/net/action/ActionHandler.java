package vine.core.net.action;

/**
 * Created by liguofang on 2014/10/14.
 * 通讯接口Action处理类信息
 *
 */
public abstract class ActionHandler {
    /*请求/推送消息的命令*/
    protected int opCode;

    protected String pushId;
    /*执行该Action处理时，须做在线或者登陆验证*/
    protected boolean needOnline = true;

    public abstract void executeAction(Object[] params,
                                       ActionTaskResultListener resultListener);

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public boolean isNeedOnline() {
        return needOnline;
    }

    public void setNeedOnline(boolean needOnline) {
        this.needOnline = needOnline;
    }
}
