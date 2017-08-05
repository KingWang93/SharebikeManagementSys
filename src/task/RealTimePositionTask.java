package task;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import impl.RealTimeImpl;
import io.vertx.core.json.JsonObject;
import util.PropertiesUtil;

public class RealTimePositionTask implements Runnable{
	private static JsonObject result;
	private static JsonObject roadweight;
	@Override
	public void run() {
		RealTimeImpl impl=new RealTimeImpl();
		result=impl.getRTpoints();
		roadweight=impl.calcWeight();
		System.out.println("实时位置线程执行完毕!");
	}
	public static JsonObject getresult(){
		return result;
	}
	public static JsonObject getRoadWeight(){
		return roadweight;
	}
	
}
