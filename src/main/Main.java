package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import entity.BikeType;
import io.vertx.core.Vertx;
import task.CalculateData;
import task.RealTimePositionTask;
import util.JDBCUtil;
import util.PropertiesUtil;
import util.RoadIndex;
import verticle.IniVerticle;
import verticle.PushPositionVerticle;
import verticle.PushRoadWeightVerticle;
import verticle.PushTrailVerticle;

public class Main {
	private static ComboPooledDataSource ztcpool;
	private static ComboPooledDataSource gjcpool;
	private static ComboPooledDataSource taxipool;
	public static void main(String[] args) {
		//初始化数据库连接池
		iniJdbcPool();
		//计算道路的R树索引
		createRoadRtree();
		//计算轨迹和实时位置线程
		CalculateData calthread1=new CalculateData();
		calthread1.run();
		RealTimePositionTask calthread2=new RealTimePositionTask();
		calthread2.run();
		ScheduledExecutorService exc=Executors.newSingleThreadScheduledExecutor();
		exc.scheduleWithFixedDelay(calthread1, Integer.parseInt(PropertiesUtil.getProperties("common", "GJCTrackTimeInterval")), Integer.parseInt(PropertiesUtil.getProperties("common", "GJCTrackTimeInterval")), TimeUnit.MILLISECONDS);
		exc.scheduleWithFixedDelay(calthread2, Integer.parseInt(PropertiesUtil.getProperties("common", "GJCTrackTimeInterval")), Integer.parseInt(PropertiesUtil.getProperties("common", "GJCTrackTimeInterval")), TimeUnit.MILLISECONDS);
		//创建Vertx，并部署服务
		Vertx vertx=Vertx.vertx();
		vertx.deployVerticle(IniVerticle.class.getName());
		vertx.deployVerticle(PushTrailVerticle.class.getName());
		vertx.deployVerticle(PushPositionVerticle.class.getName());
		vertx.deployVerticle(PushRoadWeightVerticle.class.getName());
		System.out.println("服务开启已经全部开启！");
		//代理
//		vertx.deployVerticle(Proxy.class.getName());
	}
	
	public static  void iniJdbcPool(){
		ztcpool=JDBCUtil.getPool("ztc");
		gjcpool=JDBCUtil.getPool("gjc");
		taxipool=JDBCUtil.getPool("taxi");
	}
	public static void createRoadRtree(){
		RoadIndex.createRtree_Road();
	}
	public static ComboPooledDataSource getPool(BikeType bike){  	
		switch (bike) {
		case MOBAI:
			return ztcpool;
		case HELLOBIKE:
			return gjcpool;
		case OFO:
			return taxipool;
		default:
			return null;
		}
		
	}
}
