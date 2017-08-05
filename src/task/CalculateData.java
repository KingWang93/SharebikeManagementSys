package task;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import entity.Grid;
import impl.GridImpl;
import io.vertx.core.json.JsonArray;
import util.PropertiesUtil;
import util.SpatialUtil;

public class CalculateData implements Runnable{
	private static JsonArray result;
	@Override
	public void run() {
		System.out.println("程序启动，开始计算网格");
		System.out.println(System.currentTimeMillis());
		HashMap<String, Grid> gridmap=SpatialUtil.createGridIndex();
		GridImpl impl=new GridImpl();
		JsonArray ja=new JsonArray();
		for(Entry<String, Grid> entry:gridmap.entrySet()){
			ja.addAll(impl.getTrail(entry.getValue()));
		}
		result=ja;
		System.out.println("所有线总共有："+ja.size()+"条");
		System.out.println("网格计算完毕");
		System.out.println(System.currentTimeMillis());
	}
	
	public static JsonArray getresult(){
		return result;
	}
	
}
