package test;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import util.SpatialUtil;

public class Verticle1 extends AbstractVerticle {
	
	@Override
	public void start() throws Exception {
		LocalMap<String, String> map=vertx.sharedData().getLocalMap("top");
		System.out.println(map.get("key"));
		System.out.println(map.get("verticle2"));
	}

}
