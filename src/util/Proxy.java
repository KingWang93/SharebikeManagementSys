package util;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

/**
 * 代理
 * @author KingWang
 *
 */
public class Proxy extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    HttpClient client = vertx.createHttpClient(new HttpClientOptions());
    vertx.createHttpServer().requestHandler(req -> {
      System.out.println("Proxying request: " + req.uri());
      HttpClientRequest c_req = client.request(req.method(), 25033, "10.73.199.229", req.uri(), c_res -> {
        System.out.println("Proxying response!");
//        System.out.println("Proxying response:" + c_res.statusCode());
        req.response().setChunked(true);
        req.response().setStatusCode(c_res.statusCode());
        req.response().headers().setAll(c_res.headers());
        c_res.handler(data -> {
          System.out.println("Proxying response body");
//          System.out.println("Proxying response body: " + data.toString("ISO-8859-1"));
          req.response().write(data);
        });
        c_res.endHandler((v) -> req.response().end());
      });
      c_req.setChunked(true);
      c_req.headers().setAll(req.headers());
      req.handler(data -> {
        System.out.println("Proxying request body ");
//        System.out.println("Proxying request body " + data.toString("ISO-8859-1"));
        c_req.write(data);
      });
      req.endHandler((v) -> c_req.end());
    }).listen(1001);
  }
}