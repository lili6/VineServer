/**
 * 
 */
package vine.core.net.packet;



/**
 * 进入消息数据过滤器接口
 * 将在接收到消息并解析后，业务代码调用前被执行，可以有多个消息数据过滤器
 * 不对packet本身的值进行修改，只做过滤条件判断等处理
 * @author PanChao
 * @author liguofang
 */
public interface MessageInFilter extends MessageFilter {
}
