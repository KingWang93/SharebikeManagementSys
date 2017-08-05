package test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

public class ShareDataTest {
	public static void main(String[] args) {
		Vertx vertx=Vertx.vertx();
//		vertx.createHttpServer().listen(8080, res->{
//			if(res.succeeded()){
//				System.out.println("服务开启成功");
//			}else{
//				System.out.println("服务开启失败");
//			}
//		});
		SharedData data=vertx.sharedData();
		LocalMap<String,String> map=data.getLocalMap("top");
		map.put("key", "value");
		System.out.println(map);
		DeploymentOptions options = new DeploymentOptions().setInstances(16);
		vertx.deployVerticle(Verticle2.class.getName(),options);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		vertx.deployVerticle(Verticle1.class.getName());
	}
}
