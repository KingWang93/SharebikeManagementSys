package verticle;

import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import task.CalculateData;
import util.PropertiesUtil;

public class PushTrailVerticle extends AbstractVerticle {
	
	
//	public static void main(String[] args) {
//		System.out.println("程序启动，开始计算网格");
//
//		JsonArray ja=new JsonArray();
//		BufferedReader reader = null;
//		try {
//			reader = new BufferedReader(new FileReader("data.txt"));
//		} catch (FileNotFoundException e2) {
//			e2.printStackTrace();
//		}
//		String line="";
//		try {
//			while((line=reader.readLine())!=null){
//				ja=new JsonArray(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("网格计算完毕");
//		result=ja;
//		Vertx vertx=Vertx.vertx();
//		vertx.deployVerticle(PushTrailVerticle.class.getName());
//	}
	
//	public static void main(String[] args) {
//		CalculateData calthread=new CalculateData();
//		calthread.run();
//		ScheduledExecutorService exc=Executors.newSingleThreadScheduledExecutor();
//		exc.scheduleWithFixedDelay(calthread, Integer.parseInt(PropertiesUtil.getProperties("common", "GJCTrackTimeInterval")), Integer.parseInt(PropertiesUtil.getProperties("common", "GJCTrackTimeInterval")), TimeUnit.MILLISECONDS);
//		Vertx vertx=Vertx.vertx();
//		vertx.deployVerticle(PushTrailVerticle.class.getName());
//	}
	@Override
	public void start() throws Exception {
		EventBus eventBus=vertx.eventBus();
		DeliveryOptions deliveryOptions=new DeliveryOptions(new JsonObject().put("timeout", Integer.parseInt(PropertiesUtil.getProperties("common", "sendtimeout"))));
		eventBus.consumer("chat.to.server", message -> {
			System.out.println(new Date()+":客户端发往服务端的消息内容为:"+message.body().toString());
			eventBus.publish("Grid_10_10_54", CalculateData.getresult(),deliveryOptions);
			System.out.println(new Date()+":数据发布出去的时间");
		});
		
		vertx.setPeriodic(Integer.parseInt(PropertiesUtil.getProperties("common", "PushInterval")), timerID -> {
			eventBus.publish("Grid_10_10_54", CalculateData.getresult(),deliveryOptions);
			System.out.println(new Date()+":推送完毕");
		});
	}
}
