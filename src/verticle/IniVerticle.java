package verticle;

import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.impl.RouterImpl;
import util.PropertiesUtil;

public class IniVerticle extends AbstractVerticle{
	@Override
	public void start() throws Exception {
		SockJSHandlerOptions sockjsopt=new SockJSHandlerOptions().setHeartbeatInterval(Integer.parseInt(PropertiesUtil.getProperties("common", "HeartBeatInterval")));//默认25秒
		
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx,sockjsopt);
		Router router=new RouterImpl(vertx);
		BridgeOptions opt=new BridgeOptions();
		opt.setPingTimeout(Integer.parseInt(PropertiesUtil.getProperties("common", "pingTimeout")));//默认10秒
		int cols=Integer.parseInt(PropertiesUtil.getProperties("common", "cols"));
		int rows=Integer.parseInt(PropertiesUtil.getProperties("common", "rows"));
		for(int i=0;i<cols*rows;i++){
			opt.addOutboundPermitted(new PermittedOptions().setAddress("Grid_"+String.valueOf(rows)+"_"+String.valueOf(cols)+"_"+String.valueOf(i+1)));
		}
		opt.addOutboundPermitted(new PermittedOptions().setAddress("realtimePosition"));
		opt.addOutboundPermitted(new PermittedOptions().setAddress("roadWeight"));
		opt.addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"));
		//解决跨域问题
		router.route().handler(CorsHandler.create("*")
				.allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.OPTIONS)
				.allowedMethod(HttpMethod.POST).allowedHeader("X-PINGARUNER").allowedHeader("Content-Type"));
		router.route().handler(BodyHandler.create().setBodyLimit(-1));
		router.route("/eventbus/bikeTrail/*").handler(sockJSHandler.bridge(opt,bridgeEvent->{
			switch (bridgeEvent.type()) {
			case SOCKET_CREATED:
				System.out.println(new Date()+":This event will occur when a new SockJS socket is created.");
				break;
			case SOCKET_IDLE:
				System.out.println(new Date()+":This event will occur when SockJS socket is on idle for longer period of time than initially configured.");
				break;
			case SOCKET_PING:
				System.out.println(new Date()+":This event will occur when the last ping timestamp is updated for the SockJS socket.");
				break;
			case SOCKET_CLOSED:
				System.out.println(new Date()+":This event will occur when a SockJS socket is closed.");
				break;
			case SEND:
				System.out.println(new Date()+":This event will occur when a message is attempted to be sent from the client to the server.");
				break;
			case PUBLISH:
				System.out.println(new Date()+":This event will occur when a message is attempted to be published from the client to the server.");
				break;
			case RECEIVE:
				System.out.println(new Date()+":This event will occur when a message is attempted to be delivered from the server to the client.");
				break;
			case REGISTER:
				System.out.println(new Date()+":This event will occur when a client attempts to register a handler.");
				break;
			case UNREGISTER:
				System.out.println(new Date()+":This event will occur when a client attempts to unregister a handler.");
				break;
			default:
//				System.out.println("default");
				break;
			}
			bridgeEvent.complete(true);
		}));
		HttpServerOptions httpopt=new HttpServerOptions().setMaxWebsocketFrameSize(Integer.parseInt(PropertiesUtil.getProperties("common", "maxWebsocketFrameSize")));
		HttpServer server=vertx.createHttpServer(httpopt);
		server.requestHandler(router::accept).listen(8081, res -> {
			if (res.succeeded()) {
				System.out.println("服务开启成功！");
			} else {
				System.out.println("服务开启失败");
			}
		});
	}
}
