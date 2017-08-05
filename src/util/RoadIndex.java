package util;

import java.util.HashMap;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class RoadIndex {
	private static STRtree r_tree;
	private static HashMap<String, Double> map;
	static {
		r_tree=null;
		map=new HashMap<>();
	}
	public static void createRtree_Road() {
		STRtree tree=new STRtree();
		GeometryFactory factory=new GeometryFactory();
		String data=ParseData.readData("GD_MergeRoad.json", "UTF-8");
		JsonObject result = new JsonObject(data);
		JsonArray ja = result.getJsonArray("features");
		for (int i = 0; i < ja.size(); i++) {
			JsonObject temp = ja.getJsonObject(i).getJsonObject("geometry");
			double roadlength=ja.getJsonObject(i).getJsonObject("properties").getDouble("Shape_Le_1");
			map.put(String.valueOf(i), roadlength);
			JsonArray ja1=null;
			if(temp.getString("type").equals("LineString")){
				ja1 = temp.getJsonArray("coordinates");
			}else if(temp.getString("type").equals("MultiLineString")){
				ja1 = temp.getJsonArray("coordinates").getJsonArray(0);//这里舍弃了后面的元素，因为MultiLineString比较少,使之从多线变成单线
			}
			List list = ja1.getList();
			int size=list.size();
			Coordinate[] coors=new Coordinate[size];
			for (int m = 0; m < size; m++) {
				List<Double> xylist = (List<Double>) list.get(m);
				double x = xylist.get(0);
				double y = xylist.get(1);
				coors[m]=new Coordinate(x, y);
			}
			LineString line=factory.createLineString(coors);
			line.setSRID(i);
			Envelope env=line.getEnvelopeInternal();
			tree.insert(env, line);
		}
		tree.build();
		r_tree=tree;
		System.out.println();
	}
	
	public static STRtree getTree(){
		return r_tree;
	}
	public static HashMap<String, Double> getMap(){
		return map;
	}
	
}
