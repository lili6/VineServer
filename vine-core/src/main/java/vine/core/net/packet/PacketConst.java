package vine.core.net.packet;
/**
 * 与客户端交互通讯数据时使用的常量
 * 
 * @author liguofang
 *
 */
public class PacketConst {
	//======================框架报文头定义==开始====================================================
	/** 向客户端发送的消息中包含的消息ID变量名称 */
	public static final String PACKET_ID 		= "packetId";
	/** 时间戳key名称*/
	public static final String PACKET_STAMP	 	= "stamp";
	/** 通讯响应码key名称*/
	public static final String PACKET_RET_CODE  = "retCode";
	/** 标识位key名称, 预留*/
	public static final String PACKET_FLAG		= "flag";
	//======================框架报文头定义==开始====================================================
	
	/** 消息数据默认编码 */
	public static final String MESSAGE_DEFAULT_ENCODING = "UTF-8";
	/** 向客户端发送的批量缓冲消息的KEY值 */
	public static final String KEY_DATA_BATCH = "v";
	/** 向客户端发送的消息中包含的命令编号变量名称 */
	public static final String KEY_COMMAND = "c";
	/** 向客户端发送的消息中包含的业务执行结果状态变量名称 */
	public static final String KEY_RESULT = "rs";
	/** 向客户端发送的消息中包含的用户ID变量名称 */
	public static final String KEY_SESSION_USER_ID = "userId";
	/** 向客户端发送/接收的消息中包含的用于标识消息唯一性的变量名称 */
	public static final String KEY_MESSAGE_ID = "mid";
	/** 标识已经是过滤器的消息，不再重复过滤 */
	public static final String KEY_FILTER_HANDLE = "filter";
	/** 向客户端发送业务处理成功结果的值 */
	public static final Integer VALUE_RESULT_SUCCESS = 1;
	/** 向客户端发送业务处理失败结果的值 */
	public static final Integer VALUE_RESULT_FAILED = 0;
	/** 向客户端发送业务处理失败结果，结果类型为需要用户登录 */
	public static final Integer VALUE_RESULT_NEEDONLINE = -99;
	/** 请求的消息内容不存在 */
	public static final int VALUE_RESULT_MESSAGE_EMPTY = -100;
	/** 请求的消息解析错误 */
	public static final int VALUE_RESULT_MESSAGE_PARSE_ERROR = -101;
	/** command参数不存在 */
	public static final int VALUE_RESULT_COMMAND_EMPTY = -102;
	/** 请求的command不存在 */
	public static final int VALUE_RESULT_COMMAND_NOT_EXIST = -103;	
	/** 请求的command已锁定，在处理中 */
	public static final int VALUE_RESULT_COMMAND_LOCKED = -104;
	/**返回预留的FLAG值*/	
	public static final int VALUE_RESULT_PACKET_FLAG = 9;
	
	
	//======================框架层面错误码定义==开始====================================================
	/**报文头中，框架通讯调度处理的成功 */
	public static final int RETCODE_SUCCESS 					= 0;
	/**请求的消息ID不存在*/
	public static final int RETCODE_PACKETID_NOT_EXIST		 	= 1;
	/** 请求的command已锁定，在处理中 */
	public static final int RETCODE_PACKETID_LOCKED 			= 2;
	/** 向客户端发送业务处理失败结果，结果类型为需要用户登录 */
	public static final int RETCODE_USER_NEEDONLINE 			= 3;	
	/** 请求的消息内容不存在 */
	public static final int RETCODE_REQUEST_MESSAGE_EMPTY 		= 4;
	/** 请求的消息解析错误 */
	public static final int RETCODE_MESSAGE_PARSE_ERROR 		= 5;
		
	
	//======================框架层面错误码定义==结束=====================================================
	
	/** 客户端与服务器之前上次心跳时间 */
	public static final String KEY_LAST_HEARBEAT = "last_hearbeat";
	
	/** 获取成功或者失败Value */
	public static Integer getResultValue(boolean rs){
		return rs ? VALUE_RESULT_SUCCESS : VALUE_RESULT_FAILED;
	}
}
