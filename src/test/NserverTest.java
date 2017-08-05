package test;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class NserverTest {
	public static void main(String[] args) {
		Vertx vertx=Vertx.vertx();
		Router router=Router.router(vertx);
		JsonObject jo=new JsonObject();
		jo.put("key", "value");
		router.route("/1").handler(routingContext -> {

			  // This handler will be called for every request
			  HttpServerResponse response = routingContext.response();
			  response.putHeader("content-type", "text/plain");

			  // Write to the response and end it
			  response.end("Hello World 1!");
			});
		router.route("/2").handler(routingContext -> {

			  // This handler will be called for every request
			  HttpServerResponse response = routingContext.response();
			  response.putHeader("content-type", "text/plain");

			  // Write to the response and end it
			  response.end("Hello World 2!");
			});
		for(int i=0;i<10;i++){
			HttpServer server=vertx.createHttpServer();
			server.requestHandler(router::accept).listen(8080, res->{
				if(res.succeeded()){
					System.out.println("成功");
				}else{
					System.out.println("失败");
				}
			});
		}
	}
}
