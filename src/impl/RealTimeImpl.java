package impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.index.strtree.STRtree;

import dao.QueryRTPosition;
import entity.BikeType;
import entity.Point;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import task.RealTimePositionTask;
import util.RoadIndex;

public class RealTimeImpl {
	public JsonObject getRTpoints(){
		JsonObject result=new JsonObject();
		QueryRTPosition query=new QueryRTPosition();
		List<Point> list1=query.getRealTimePositon(BikeType.OFO);
		List<Point> list2=query.getRealTimePositon(BikeType.HELLOBIKE);
		List<Point> list3=query.getRealTimePositon(BikeType.MOBAI);
		JsonArray ja1=new JsonArray();
		JsonArray ja2=new JsonArray();
		JsonArray ja3=new JsonArray();
		for(int i=0,length=list1.size();i<length;i++){
			JsonObject jo=new JsonObject();
			jo.put("x", list1.get(i).getX());
			jo.put("y", list1.get(i).getY());
			ja1.add(jo);
		}
		for(int i=0,length=list2.size();i<length;i++){
			JsonObject jo=new JsonObject();
			jo.put("x", list2.get(i).getX());
			jo.put("y", list2.get(i).getY());
			ja2.add(jo);
		}
		for(int i=0,length=list3.size();i<length;i++){
			JsonObject jo=new JsonObject();
			jo.put("x", list3.get(i).getX());
			jo.put("y", list3.get(i).getY());
			ja3.add(jo);
		}
		result.put("OFO", ja1);
		result.put("HELLOBIKE", ja2);
		result.put("MOBAI", ja3);
		return result;
	}
	
	public JsonObject calcFrequency(){
		JsonObject result=new JsonObject();
		JsonObject jo=RealTimePositionTask.getresult();
		JsonArray ja=jo.getJsonArray("HELLOBIKE");
		GeometryFactory factory=new GeometryFactory();
		STRtree tree=RoadIndex.getTree();
		for(int i=0,size=ja.size();i<size;i++){
			JsonObject temp=ja.getJsonObject(i);
			double x=temp.getDouble("x");
			double y=temp.getDouble("y");
			Coordinate coor=new Coordinate(x, y);
			com.vividsolutions.jts.geom.Point point=factory.createPoint(coor);
			Envelope aEnvelope=point.getEnvelopeInternal();
			List<LineString> list=(List<LineString>)tree.query(aEnvelope);
			double minDis=1.0;
			int index=0;
			for(LineString line:list){
				double distance=point.distance(line);
				if(minDis>distance){
					minDis=distance;
					index=line.getSRID();
				}
			}
			if(result.containsKey(String.valueOf(index))){
				result.put(String.valueOf(index), result.getInteger(String.valueOf(index))+1);
			}else{
				result.put(String.valueOf(index), 1);
			}
		}
		return result;
	}
	
	public JsonObject calcWeight(){
		JsonObject result=new JsonObject();
		JsonObject jo=calcFrequency();
		Iterator<Map.Entry<String, Object>> it=jo.iterator();
		HashMap<String, Double> map=RoadIndex.getMap();
		while(it.hasNext()){
			Entry<String, Object> entry=it.next();
			String key=entry.getKey();
			int value=(Integer)entry.getValue();
			double weight=value/(map.get(key));
			result.put(key, (int)weight);
		}
		return result;
	}
}
