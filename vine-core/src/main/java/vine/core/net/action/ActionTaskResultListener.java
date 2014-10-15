package vine.core.net.action;

import vine.core.net.packet.Packet;

/**
 * Created by liguofang on 2014/10/14.
 * 通讯接口Action处理结果监听接口，用于异步Action处理中
 */
public interface ActionTaskResultListener {
    /**
     * 接收Action处理结果信息
     * @param handler Action处理结果来源
     * @param packet 结果数据
     */
    void receiveResult(ActionHandler handler, Packet packet);
}
