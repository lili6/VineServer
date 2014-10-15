package vine.core.net;

/**
 * Created by liguofang on 2014/10/14.
 * ProtoBuf协议基类
 * 协议范围<1-65536>
 *     系统保留1-1000，其他功能模块应用从10001开始，分段计数
 */
public class HOpCode {
    /* 接收到的消息包为心跳包*/
    public static final int OPCODE_RECEIVE_HEARTBEAT 			= 1;
    /* 接受到的消息包ID错误或者不存在,在通讯层面的错误 */
//    public static final int OPCODE_COMM_ERROR		= 2;

}
