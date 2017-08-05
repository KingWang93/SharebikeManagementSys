package test;

import io.vertx.core.json.JsonObject;
import util.ParseData;
import util.WGS_Encrypt;

public class WGS2GDFileTest {
	public static void main(String[] args) {
//		parseShpfile2GeojsonFile("H:\\LinkingMap\\数据\\路网数据（WGS84坐标系下的）\\MergeRoad.shp","H:\\LinkingMap\\数据\\路网数据（WGS84坐标系下的）\\MergeRoad.json");
//		parseShpfile2GeojsonFile("H:\\LinkingMap\\数据\\路网数据（WGS84坐标系下的）\\GDRoad.shp","H:\\LinkingMap\\数据\\路网数据（WGS84坐标系下的）\\GDRoad.json");
		System.out.println(WGS_Encrypt.WGS2Mars(30.59448948700026,114.42237143169632)[1]+","+WGS_Encrypt.WGS2Mars(30.59448948700026,114.42237143169632)[0]);
		String result=ParseData.readData("H:\\LinkingMap\\数据\\路网数据（WGS84坐标系下的）\\MergeRoad.json","GBK");
		//			result=new String(result.getBytes("GBK"),"UTF-8");
		result=new String(result);
		JsonObject jo=ParseData.parseGeoJsonFromWGS2GD1(result);
		ParseData.writeData2file("H:/LinkingMap/数据/路网数据（WGS84坐标系下的）/GD_MergeRoad.json", jo);
		System.out.println(jo);
		
		String result1=ParseData.readData("H:\\LinkingMap\\数据\\路网数据（WGS84坐标系下的）\\GDRoad.json","GBK");
		//			result=new String(result.getBytes("GBK"),"UTF-8");
		result1=new String(result1);
		JsonObject jo1=ParseData.parseGeoJsonFromWGS2GD1(result1);
		ParseData.writeData2file("H:/LinkingMap/数据/路网数据（WGS84坐标系下的）/GD_GDRoad.json", jo1);
//		JsonArray ja=jo.getJsonArray("features").getJsonObject(1).getJsonObject("geometry").getJsonArray("coordinates");
//		System.out.println(ja);
	}
}
