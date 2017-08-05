package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequences;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.index.strtree.STRtree;

import config.GeoConfig;
import dao.QueryTrail;
import entity.CompareValue;
import entity.GPSPoint;
import entity.Grid;
import entity.Line;
import entity.Point;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class SpatialUtil {
	static String result=new String();
	/**
	 * 求武汉的边界多边形
	 * @param args
	 */
	public static void getPolygonofWuhan(String[] args) {
		Vertx vertx = Vertx.vertx();
		WebClient client = WebClient.create(vertx);
		client.get(80, "restapi.amap.com",
				"/v3/config/district?keywords=%E6%AD%A6%E6%B1%89&subdistrict=0&key=9b8b15ddfc322497fac75160cc7e487b&extensions=all")
				.send(ar -> {
					if (ar.succeeded()) {
						// Obtain response
						HttpResponse<Buffer> response = ar.result();
						result = response.bodyAsString();
						JsonObject jo = new JsonObject(result);
						result = jo.getJsonArray("districts").getJsonObject(0).getString("polyline");
						GeometryFactory factory=new GeometryFactory();
						String[] xyStrings=result.replace("|", ";").split(";");
						List<Coordinate> list=new ArrayList<>();
						for(String xy:xyStrings){
							String[] s_arr=xy.split(",");
							double[] d_xy=new double[2];
							d_xy[0]=Double.parseDouble(s_arr[0]);
							d_xy[1]=Double.parseDouble(s_arr[1]);
							Coordinate coor=new Coordinate(d_xy[0], d_xy[1]);
							list.add(coor);
						}
						Coordinate[] coor_arr=list.toArray(new Coordinate[0]);
						MultiPoint multiPoint=factory.createMultiPoint(coor_arr);
						Geometry env=multiPoint.getEnvelope();
						Coordinate[] MBR=env.getCoordinates();
						for(int i=0;i<MBR.length;i++){
							System.out.println(MBR[i].x+","+MBR[i].y);
						}
						client.close();
						vertx.close();
					} else {
						System.out.println("Something went wrong " + ar.cause().getMessage());
					}
				});
	}
	/**
	 * 计算两点的距离差在哪个阈值范围内
	 * @param pt1
	 * @param pt2
	 * @param config	阈值的设置
	 * @return
	 */
	public static CompareValue inTolerance(Point pt1,Point pt2,GeoConfig config){
		double delta=Math.sqrt(Math.pow(pt1.getX()-pt2.getX(),2)+Math.pow(pt1.getY()-pt2.getY(), 2));
		double max=config.getMaxGeoRange();
		double min=config.getMinGeoRange();
		if(delta<min){
			return CompareValue.LT;
		}else if(delta<=max&&delta>=min){
			return CompareValue.IN;
		}else{
			return CompareValue.GT;
		}
	}
	/**
	 * 建立网格
	 * @return
	 */
	public static HashMap<String,Grid> createGrids(){
		HashMap<String,Grid> gridMap=new HashMap<>();
		double left_top_x=Double.parseDouble(PropertiesUtil.getProperties("common", "left-top").split(",")[0]);
		double left_top_y=Double.parseDouble(PropertiesUtil.getProperties("common", "left-top").split(",")[1]);
		double right_bottom_x=Double.parseDouble(PropertiesUtil.getProperties("common", "right-bottom").split(",")[0]);
		double right_bottom_y=Double.parseDouble(PropertiesUtil.getProperties("common", "right-bottom").split(",")[1]);
		int rows=Integer.parseInt(PropertiesUtil.getProperties("common", "rows"));
		int cols=Integer.parseInt(PropertiesUtil.getProperties("common", "cols"));
		double interval_x=Double.parseDouble(ParseDataType.parseD2s((right_bottom_x-left_top_x)/(cols*1.0),6));
		double interval_y=Double.parseDouble(ParseDataType.parseD2s((left_top_y-right_bottom_y)/(rows*1.0),6));
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				Grid grid=new Grid();
				grid.setCol(cols);
				grid.setRow(rows);
				grid.setIndex(i*cols+j+1);
				Point lefttop=new Point();
				Point rightbottom=new Point();
				lefttop.setX(left_top_x+j*interval_x);
				lefttop.setY(left_top_y-i*interval_y);
				if(j==cols-1){
					rightbottom.setX(right_bottom_x);
				}else{
					rightbottom.setX(left_top_x+(j+1)*interval_x);
				}
				if(i==rows-1){
					rightbottom.setY(right_bottom_y);
				}else{
					rightbottom.setY(left_top_y-(i+1)*interval_y);
				}
				grid.setLefttop(lefttop);
				grid.setRightbottom(rightbottom);
				gridMap.put(String.valueOf(grid.getRow())+"_"+String.valueOf(grid.getCol())+"_"+String.valueOf(grid.getIndex()),grid);
			}
		}
		return gridMap;
	}
	
	/**
	 * 建立网格索引
	 */
	public static HashMap<String, Grid> createGridIndex(){
		
		HashMap<String, Grid> gridmap=createGrids();
		
		int cols=Integer.parseInt(PropertiesUtil.getProperties("common", "cols"));
		int rows=Integer.parseInt(PropertiesUtil.getProperties("common", "rows"));
		String rbPt_s=PropertiesUtil.getProperties("common", "right-bottom");
		Point rbPt=new Point();
		rbPt.setX(Double.parseDouble(rbPt_s.split(",")[0]));
		rbPt.setY(Double.parseDouble(rbPt_s.split(",")[1]));
		String ltPt_s=PropertiesUtil.getProperties("common", "left-top");
		Point ltPt=new Point();
		ltPt.setX(Double.parseDouble(ltPt_s.split(",")[0]));
		ltPt.setY(Double.parseDouble(ltPt_s.split(",")[1]));
		double range_x=rbPt.getX()-ltPt.getX();
		double range_y=ltPt.getY()-rbPt.getY();
		QueryTrail query=new QueryTrail();
		HashMap<String, Line> map=query.getLine();
		GeoConfig config=new GeoConfig();
		config.setMaxGeoRange(Double.parseDouble(PropertiesUtil.getProperties("common", "maxGeoRange")));
		config.setMinGeoRange(Double.parseDouble(PropertiesUtil.getProperties("common", "minGeoRange")));
		GeometryFactory factory=new GeometryFactory();
		for(Entry<String, Line> entry:map.entrySet()){
			Line templine=entry.getValue().sort(true);
			List<Line> list=templine.filter(config);
			for(Line line:list){
				List<GPSPoint> gpslist=line.getCoors();
				List<Coordinate> coors=new ArrayList<>();
				for(GPSPoint xy:gpslist){
					double x=xy.getX();
					double y=xy.getY();
					Coordinate coor=new Coordinate(x, y);
					coors.add(coor);
				}
				Coordinate[] coor_arr=coors.toArray(new Coordinate[0]);
				if(coor_arr.length>1){
					LineString l_s=factory.createLineString(coor_arr);
					Envelope env=l_s.getEnvelopeInternal();
					double max_x=env.getMaxX();
					double min_x=env.getMinX();
					double max_y=env.getMaxY();
					double min_y=env.getMinY();
					int max_j=(int)((max_x-ltPt.getX())/range_x*cols);
					int max_i=(int)((ltPt.getY()-max_y)/range_y*rows);
					int min_j=(int)((min_x-ltPt.getX())/range_x*cols);
					int min_i=(int)((ltPt.getY()-min_y)/range_y*rows);
					a:for(int i=max_i;i<=min_i;i++){
						for(int j=min_j;j<=max_j;j++){
							Grid grid=gridmap.get(String.valueOf(rows)+"_"+String.valueOf(cols)+"_"+String.valueOf(i*cols+j+1));
							Coordinate[] coor_arr1=new Coordinate[5];
							coor_arr1[0]=new Coordinate(grid.getLefttop().getX(), grid.getLefttop().getY());
							coor_arr1[1]=new Coordinate(grid.getLefttop().getX(), grid.getRightbottom().getY());
							coor_arr1[2]=new Coordinate(grid.getRightbottom().getX(), grid.getRightbottom().getY());
							coor_arr1[3]=new Coordinate(grid.getRightbottom().getX(), grid.getLefttop().getY());
							coor_arr1[4]=new Coordinate(grid.getLefttop().getX(), grid.getLefttop().getY());
							CoordinateArraySequence seq=new CoordinateArraySequence(coor_arr1);
							LinearRing ring = new LinearRing(seq, new GeometryFactory());
							Polygon poly=new Polygon(ring, null, new GeometryFactory());
							if(l_s.crosses(poly)||poly.covers(l_s)){
								grid.addLine(line);
								break a;
							}
						}
					}
				}else{
					GPSPoint point=gpslist.get(0);
					int j=(int)((point.getX()-ltPt.getX())/range_x*cols);
					int i=(int)((ltPt.getY()-point.getY())/range_y*rows);
					Grid grid=gridmap.get(String.valueOf(rows)+"_"+String.valueOf(cols)+"_"+String.valueOf(i*cols+j+1));
					grid.addLine(line);
				}
			}
		}
		System.out.println("网格索引创建成功！");
		return gridmap;
	}
	

	/**
	 * 建立R树索引
	 * @return
	 */
	public static STRtree createTrailRtree(){
		QueryTrail query=new QueryTrail();
		HashMap<String, Line> map=query.getLine();
		GeoConfig config=new GeoConfig();
		config.setMaxGeoRange(Double.parseDouble(PropertiesUtil.getProperties("common", "maxGeoRange")));
		config.setMinGeoRange(Double.parseDouble(PropertiesUtil.getProperties("common", "minGeoRange")));
		STRtree tree=new STRtree();
		for(Entry<String, Line> entry:map.entrySet()){
			Line templine=entry.getValue().sort(true);
			List<Line> list=templine.filter(config);
			for(Line line:list){
				GeometryFactory factory=new GeometryFactory();
				List<Coordinate> coors=new ArrayList<>();
				List<GPSPoint> gpslist=line.getCoors();
				for(GPSPoint xy:gpslist){
					double x=xy.getX();
					double y=xy.getY();
					Coordinate coor=new Coordinate(x, y);
					coors.add(coor);
				}
				Coordinate[] coor_arr=coors.toArray(new Coordinate[0]);
				if(coor_arr.length>1){
					LineString lineStr=factory.createLineString(coor_arr);
					Envelope env=lineStr.getEnvelopeInternal();
					tree.insert(env, lineStr);
				}else{
					com.vividsolutions.jts.geom.Point point=factory.createPoint(coor_arr[0]);
					Envelope env=point.getEnvelopeInternal();
					tree.insert(env, point);
				}
			}
		}
		tree.build();
		System.out.println("R树索引创建成功！");
		return tree;
	}
	/**
	 * R树查询
	 * @param tree
	 * @param searchGeo
	 * @return
	 */
	public static List<Geometry> query(STRtree tree,Geometry searchGeo){
		List <Geometry> result=new ArrayList<>();
		@SuppressWarnings("rawtypes")
		List list=tree.query(searchGeo.getEnvelopeInternal());
		for(int i=0;i<list.size();i++){
			Geometry lineStr=(Geometry)list.get(i);
			if(lineStr.intersects(searchGeo)){
				result.add(lineStr);
			}
		}
		return result;
	}
	
	public static Geometry generateSearchGeo(double left_top_x,double left_top_y,double right_bottom_x,double right_bottom_y){
		Coordinate[] coors=new Coordinate[4];
		coors[0]=new Coordinate(left_top_x, left_top_y);
		coors[1]=new Coordinate(right_bottom_x, left_top_y);
		coors[2]=new Coordinate(left_top_x, right_bottom_y);
		coors[3]=new Coordinate(right_bottom_x, right_bottom_y);
		LinearRing ring=new LinearRing(new CoordinateArraySequence(coors),new GeometryFactory());
		return ring;
	}
	public static void main(String[] args) {
		System.out.println(WGS_Encrypt.WGS2Mars(30.1529, 113.8898)[1]+","+WGS_Encrypt.WGS2Mars(30.1529, 113.8898)[0]);
		System.out.println(WGS_Encrypt.WGS2Mars(30.9691, 114.6775)[1]+","+WGS_Encrypt.WGS2Mars(30.9691, 114.6775)[0]);
	}
	
}
