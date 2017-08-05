package verticle;

import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import task.RealTimePositionTask;
import util.PropertiesUtil;

public class PushRoadWeightVerticle extends AbstractVerticle{
	@Override
	public void start() throws Exception {
		EventBus eventBus=vertx.eventBus();
		DeliveryOptions deliveryOptions=new DeliveryOptions(new JsonObject().put("timeout", Integer.parseInt(PropertiesUtil.getProperties("common", "sendtimeout"))));
		
		eventBus.consumer("chat.to.server", message -> {
			eventBus.publish("roadWeight", RealTimePositionTask.getRoadWeight(),deliveryOptions);
		});
		vertx.setPeriodic(Integer.parseInt(PropertiesUtil.getProperties("common", "PushInterval")), timerID -> {
			eventBus.publish("roadWeight", RealTimePositionTask.getRoadWeight(),deliveryOptions);
			System.out.println(new Date()+":推送完毕");
		});
	}

}
