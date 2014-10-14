package vine.core.net.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mrd.dolphin.net.packet.PacketConst;

/**
 * CharacterFilter
 * @author liguofang
 */

public class EncodingFilter implements Filter {
	private static String DEFAULT_ENCODING = PacketConst.MESSAGE_DEFAULT_ENCODING;
	private static String DEFAULT_CONTENT_TYPE = "text/html; charset=" + PacketConst.MESSAGE_DEFAULT_ENCODING;
	private static String ENCODING = null;
	private static String CONTENT_TYPE = null;
	
	/** init */
	public void init(FilterConfig config) throws ServletException {
		ENCODING = config.getInitParameter("encoding");
		if (ENCODING == null || ENCODING.equals("")) {
			ENCODING = DEFAULT_ENCODING;
		}
		CONTENT_TYPE = config.getInitParameter("contentType");
		if (CONTENT_TYPE == null || CONTENT_TYPE.equals("")) {
			CONTENT_TYPE = DEFAULT_CONTENT_TYPE;
		}
	}
	
	/** destroy */
	public void destroy() {
		
	}
	
	/** doFilter */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request =(HttpServletRequest)req;
		HttpServletResponse response =(HttpServletResponse)res;
		request.setCharacterEncoding(ENCODING);
		response.setCharacterEncoding(ENCODING);
		response.setContentType(CONTENT_TYPE);
		chain.doFilter(req, res);
	}
	
}
