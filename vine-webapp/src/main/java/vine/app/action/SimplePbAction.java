package vine.app.action;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import vine.app.message.HOpCodeEx;
import vine.app.message.SampleMessage;
import vine.app.message.SampleMessage.CSTest;
import vine.core.net.action.clazz.Module;
import vine.core.net.action.clazz.RequestModule;
import vine.core.net.packet.Packet;
import vine.core.net.packet.PbPacket;
import vine.core.net.session.UserSession;

/**
 * Created by liguofang on 2014/10/17.
 */
/**
 * 支持协议为Protobuf格式的处理类
 * 包括正常流程，异常流程，以及业务异常流程等的处理
 * 需要登录或者不需要登录的各种情况
 * @author liguofang
 * @2014年5月19日下午3:01:01
 */
@Controller
@Module(100)
public class SimplePbAction {
    private static final Logger log = LoggerFactory.getLogger(SimplePbAction.class);

    @RequestModule(value = HOpCodeEx.CSTest, needOnline = false)
    public Packet simple(UserSession session, Packet packet) {
        if (log.isDebugEnabled()) {
            log.debug("pb例子开始执行,请求数据包:\n{}", packet);
        }
        SampleMessage.CSTest csTest = null;
        byte[] buf = (byte[]) packet.getData();
        try {
            csTest = CSTest.parseFrom(buf);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        if (log.isDebugEnabled()) {
            log.debug("转换为CSTest:\n{}", csTest);
        }

        SampleMessage.SCTestRet.Builder resp = SampleMessage.SCTestRet.newBuilder();
        resp.setContent("I am " + csTest.getName());
        resp.setRetCode(0);

        PbPacket result = new PbPacket();

        result.setPacketId(HOpCodeEx.SCTestRet);
        result.setData(resp.build().toByteArray());

        if (log.isDebugEnabled()) {
            log.debug("pb例子执行完毕，返回数据包:\n{}", result);
        }
        return result;
    }
}