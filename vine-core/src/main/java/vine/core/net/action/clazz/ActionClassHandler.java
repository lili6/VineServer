package vine.core.net.action.clazz;

import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vine.core.net.action.ActionHandler;
import vine.core.net.action.ActionTaskResultListener;
import vine.core.net.packet.Packet;
import vine.core.net.packet.PacketConst;

/**
 * 通讯接口Action处理信息
 * Created by liguofang on 2014/10/14.
 */
public class ActionClassHandler extends ActionHandler {
    private static final Logger log = LoggerFactory.getLogger(ActionClassHandler.class);
    /** 模块的处理类实例 */
    private Object action;
    /** 模块的处理方法定义 */
    private FastMethod method;

    ActionClassHandler(int opCode, Object action,FastMethod method) {
        super();
        this.opCode = opCode;
        this.action = action;
        this.method = method;

    }
    ActionClassHandler(int opCode, String pushId, Object action, FastMethod method) {
        super();
        this.opCode = opCode;
        this.pushId = pushId;
        this.action = action;
        this.method = method;
    }
    @Override
    public void executeAction(Object[] params,
                              ActionTaskResultListener resultListener) {
        long begin = System.currentTimeMillis();
        Integer msgId = null;
        Packet resp = null;
        for (Object param : params) {
            if (param instanceof Packet) {
                Integer tmpMsgId = ((Packet) param).getPacketId();
                resp =(Packet) param;
                if (tmpMsgId != null && tmpMsgId != 0) {
                    msgId = tmpMsgId;
                    break;
                }
            }
        }
//		if (log.isDebugEnabled()){log.debug("before method invoke params===={}\n",params);}
        Packet result = null;
        try {// 运行该Action处理
            Object ret = method.invoke(action, params);
            if (null != ret && ret instanceof Packet) {
                result = (Packet) ret;
                resp.setRetCode(PacketConst.VALUE_RESULT_SUCCESS);
                result.setStamp(System.currentTimeMillis());
                result.setFlag(PacketConst.VALUE_RESULT_PACKET_FLAG);
                if(log.isDebugEnabled())log.debug("返回Action处理结果result：[{}]",result);
            }
        } catch (Exception e) {
            log.error("Action[packetId={}]执行异常,result=null\n{}", msgId, e);
            e.printStackTrace();
        } finally {
            if (resultListener != null) {
                resultListener.receiveResult(this, result);
            }
            long cost = System.currentTimeMillis() - begin;
            if(log.isDebugEnabled()){
                log.debug("Action[packetId={}] executed time[{}] ms",msgId,cost);
            }
        }
    }

    public Object getAction() {
        return action;
    }
    public void setAction(Object action) {
        this.action = action;
    }
    public FastMethod getMethod() {
        return method;
    }
    public void setMethod(FastMethod method) {
        this.method = method;
    }
}
