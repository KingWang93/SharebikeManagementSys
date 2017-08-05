package test;

import java.io.FileWriter;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.index.strtree.STRtree;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import util.ParseData;

public class GenerateRoad {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void createRtree_Road() {
//	public static void main(String[] a){
		JsonArray jArray=new JsonArray();
		
		String data=ParseData.readData("H:/LinkingMap/数据/路网数据（高德坐标系下）/GD_MergeRoad.json", "UTF-8");
		JsonObject result = new JsonObject(data);
		JsonArray ja = result.getJsonArray("features");
		for (int i = 0; i < ja.size(); i++) {
			JsonObject jo1=new JsonObject();
			JsonObject temp = ja.getJsonObject(i).getJsonObject("geometry");
			JsonArray ja1=null;
			if(temp.getString("type").equals("LineString")){
				ja1 = temp.getJsonArray("coordinates");
			}else if(temp.getString("type").equals("MultiLineString")){
				ja1 = temp.getJsonArray("coordinates").getJsonArray(0);//这里舍弃了后面的元素，因为MultiLineString比较少,使之从多线变成单线
			}
			List list = ja1.getList();
			int size=list.size();
			StringBuffer sb=new StringBuffer();
			for (int m = 0; m < size; m++) {
				List<Double> xylist = (List<Double>) list.get(m);
				double x = xylist.get(0);
				double y = xylist.get(1);
				if(m!=size-1){
					sb.append(String.valueOf(x)+","+String.valueOf(y)+";");
				}else{
					sb.append(String.valueOf(x)+","+String.valueOf(y));
				}
			}
			jo1.put(String.valueOf(i),sb.toString());
			jArray.add(jo1);
		}
//		System.out.println(jArray);
		ParseData.writeData2file("H:/LinkingMap/数据/路网数据（高德坐标系下）/高德路网融合后.json", jArray);
		//114.16039106901454,30.65061790654755
//		Coordinate coor=new Coordinate(114.16039106901454, 30.65061790654755);
//		Point point=factory.createPoint(coor);
//		Envelope aEnvelope=point.getEnvelopeInternal();
//		List list=tree.query(aEnvelope);
//		return;
	}

}
