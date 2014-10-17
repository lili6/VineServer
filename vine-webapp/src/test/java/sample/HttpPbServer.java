package sample;


import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import vine.app.factory.SpringUtil;
import vine.core.net.ServerType;
import vine.core.net.http.VineServerServlet;

/**
 * Web容器启动
 * jetty内嵌容器启动
 * 
 * @author liguofang
 * @2014年5月19日下午2:53:56
 */
public class HttpPbServer {

    private static final int PORT = 8080;

    private static void init() {
        SpringUtil.init();
    }

    private static void startPbServer() {
        init();
        Server server = new Server();

        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setOutputBufferSize(32768);

        ServerConnector http = new ServerConnector(server,new HttpConnectionFactory(http_config));

        http.setPort(PORT);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/vineapp");
        server.setHandler(context);

        ServletHolder servletHolder = new ServletHolder(new VineServerServlet());

        //设置Servlet初始化参数
        servletHolder.setInitParameter("UserSessionClosedLis", "application.SaveUserListener");
        servletHolder.setInitParameter("packetType","PB");
        servletHolder.setInitParameter("MessageFilter","application.RequestInFilter,application.RequestOutFilter");

        //添加映射
        context.addServlet(servletHolder , "/server/*");
        try {
            server.start();
            server.join();
            System.out.println("Server Started........");
        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        startPbServer();
    }

}
