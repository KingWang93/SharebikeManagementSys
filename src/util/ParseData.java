package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.gdal.gdal.gdal;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.ogr;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ParseData {
	// sourcefilepath:buildings.json
	/**
	 * 从文件读取数据
	 * 
	 * @param sourcefilepath
	 * @return
	 */
	public static String readData(String sourcefilepath,String charset) {
		JsonObject result = new JsonObject();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcefilepath), charset));
//			reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcefilepath), "UTF-8"));
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String line = "";
		StringBuffer filecontent = new StringBuffer();
		try {
			while ((line = reader.readLine()) != null) {
				filecontent.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return filecontent.toString();
	}

	// transfer_building.json
	/**
	 * 写数据到文件中
	 * 
	 * @param outputfilepath
	 * @param data
	 */
	public static void writeData2file(String outputfilepath, Object data) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfilepath), "UTF-8"));
			writer.write(data.toString());
			writer.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * 将GeoJson数据从WGS转换到高德坐标系
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JsonObject parseGeoJsonFromWGS2GD(String data) {
		JsonObject result = new JsonObject();
		result = new JsonObject(data.toString());
		JsonArray ja = result.getJsonArray("features");
		for (int i = 0; i < ja.size(); i++) {
			JsonObject temp = ja.getJsonObject(i).getJsonObject("geometry");
			JsonArray ja1 = temp.getJsonArray("rings").getJsonArray(0);
			List list = ja1.getList();
			for (int m = 0; m < list.size(); m++) {
				List<Double> xylist = (List<Double>) list.get(m);
				double x = xylist.get(0);
				double y = xylist.get(1);
				double[] mars = WGS_Encrypt.WGS2Mars(y, x);
				y = mars[0];
				x = mars[1];
				xylist.set(0, x);
				xylist.set(1, y);
			}
		}
		return result;
	}
	/**
	 * 将shp文件转换为json数据文件输出，这个json后面缺少反括号，要自己加上去，是gdal的bug还是？
	 * @param shpfilePath
	 * @param outGeojsonPath
	 */
	public static void parseShpfile2GeojsonFile(String shpfilePath,String outGeojsonPath) {
		// 注册所有的驱动
		ogr.RegisterAll();
		// 为了支持中文路径，请添加下面这句代码
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
		// 为了使属性表字段支持中文，请添加下面这句
		gdal.SetConfigOption("SHAPE_ENCODING", "");

		// 打开文件
		DataSource ds = ogr.Open(shpfilePath, 0);
		if (ds == null) {
			System.out.println("打开文件失败！");
			return;
		}
		System.out.println("打开文件成功！");
		Driver dv = ogr.GetDriverByName("GeoJSON");
		if (dv == null) {
			System.out.println("打开驱动失败！");
			return;
		}
		System.out.println("打开驱动成功！");
		dv.CopyDataSource(ds, outGeojsonPath);
		System.out.println("转换成功！");
	}
	
//	
//	public static void main(String[] args) {
//		parseShpfile2GeojsonFile("","");
//	}
	/**
	 * 该方法和上面的data格式不太一样
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JsonObject parseGeoJsonFromWGS2GD1(String data){
		JsonObject result = new JsonObject();
		result = new JsonObject(data);
		JsonArray ja = result.getJsonArray("features");
		for (int i = 0; i < ja.size(); i++) {
			System.out.println(i);
			JsonObject temp = ja.getJsonObject(i).getJsonObject("geometry");
			JsonArray ja1=null;
			if(temp.getString("type").equals("LineString")){
				ja1 = temp.getJsonArray("coordinates");
			}else if(temp.getString("type").equals("MultiLineString")){
				ja1 = temp.getJsonArray("coordinates").getJsonArray(0);
			}
			List list = ja1.getList();
			for (int m = 0; m < list.size(); m++) {
				List<Double> xylist = (List<Double>) list.get(m);
				double x = xylist.get(0);
				double y = xylist.get(1);
				double[] mars = WGS_Encrypt.WGS2Mars(y, x);
				y = mars[0];
				x = mars[1];
				xylist.set(0, x);
				xylist.set(1, y);
			}
		}
		return result;
	}
	
}
