package vine.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http工具类
 * @author PanChao
 */
public class HttpClient {
	private static final Logger log = LoggerFactory.getLogger(HttpClient.class);
	
	private String ip;
	private int port;
	private String cookie;

	public static HttpClient create(String ip, int port) {
		HttpClient client = new HttpClient();
		client.ip = ip;
		client.port = port;
		return client;
	}
	
	/**
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public byte[] sendPost(String path, byte[] data) {
		return sendPost(path, null, data);
	}
	
	/**
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public byte[] sendPost(String path, Map<String, String> headers, byte[] data) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		InputStream in = null;
		OutputStream out = null;
		try {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			URL realUrl = new URL("http://" + ip + ":" + port + path);
			URLConnection conn = realUrl.openConnection(); // 建立连接

			if (headers == null) {
				// 设置通用的请求属性
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("Pragma", "no-cache");
				conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			} else {
				for (String key : headers.keySet()) {
					conn.setRequestProperty(key, headers.get(key));
				}
			}
			if (cookie != null) {
				conn.setRequestProperty("Cookie", cookie);
			}

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);

			// 获取URLConnection对象对应的输出流,并发送POST请求
			out = conn.getOutputStream();
			if (data != null) {
				out.write(data);
			}

			// 读取URL的响应数据
			String newCookie = conn.getHeaderField("Set-Cookie");
			if (newCookie != null && (cookie == null || !cookie.equals(newCookie))) {
				cookie = newCookie;
			}
			in = conn.getInputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				byteOut.write(buffer, 0, len);
			}
		} catch (Exception e) {
			log.error("发送POST请求失败", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		return byteOut.toByteArray();
	}

	public String getCookie() {
		return cookie;
	}
}
