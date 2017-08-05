package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import entity.BikeType;
import entity.GPSPoint;
import entity.Line;
import main.Main;
import util.JDBCUtil;
import util.PropertiesUtil;
import util.WGS_Encrypt;

public class QueryTrail {
	
	@SuppressWarnings("unused")
	public HashMap<String,Line> getLine(){
		System.out.println("开始查询轨迹");
		System.out.println(System.currentTimeMillis());
		HashMap<String,Line> lines=new HashMap<>();
		
		Connection conn=null;
		try {
			conn=Main.getPool(BikeType.HELLOBIKE).getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				JDBCUtil.close(conn);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		double max_x=Double.parseDouble(PropertiesUtil.getProperties("common", "right-bottom").split(",")[0]);
		double max_y=Double.parseDouble(PropertiesUtil.getProperties("common", "left-top").split(",")[1]);
		double min_x=Double.parseDouble(PropertiesUtil.getProperties("common", "left-top").split(",")[0]);
		double min_y=Double.parseDouble(PropertiesUtil.getProperties("common", "right-bottom").split(",")[1]);
		PreparedStatement pre=null;
		ResultSet res=null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String sql="";
			String tname=getTableName();
			String max_record=PropertiesUtil.getProperties("common", "MaxRecordNum");
			SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String interval="";
			interval = PropertiesUtil.getProperties("common","GJCTrackTimeInterval");
			String formated_date="";
			formated_date=sd.format(getstartTime(Long.parseLong(interval)));
			if(!hasData()){
				sql="select hp,x,y,gettime from GA_GPS_HISTORY_2017_06_08 where gettime between to_date('2017/06/08 17:10:00','yyyy/mm/dd hh24:mi:ss') and to_date('2017/06/08 17:20:00','yyyy/mm/dd hh24:mi:ss')";
			}else{
				sql = "select hp,x,y,gettime from "+tname+" where gettime>to_date('"+formated_date+"','yyyy/mm/dd hh24:mi:ss')";
			}
			pre = conn.prepareStatement(sql);
			res=pre.executeQuery();
			while(res.next()){
				String vehicle_no=res.getString("hp");
				double LON=res.getDouble("x");
				double LAT=res.getDouble("y");
				double[] xy=WGS_Encrypt.WGS2Mars(LAT,LON);
				LON=xy[1];
				LAT=xy[0];
				if(!in(LON, LAT, max_x, max_y, min_x, min_y)){
					continue;
				}
				String Time=res.getString("gettime").replace(".0", "");
				GPSPoint point=new GPSPoint();
				Line line=new Line();
				if(lines.containsKey(vehicle_no)){
					line=lines.get(vehicle_no);
					line.getCoors().add(point);
				}else{
					line.setId(vehicle_no);
					line.getCoors().add(point);
				}
				point.setLine(line);
				point.setX(LON);
				point.setY(LAT);
				try {
					point.setDate(sdf.parse(Time));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				lines.put(vehicle_no, line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				res.close();
				pre.close();
				JDBCUtil.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("轨迹查询完毕");
		System.out.println(System.currentTimeMillis());
		return lines;
	}
	public boolean in(double x,double y,double max_x,double max_y,double min_x,double min_y){
		if(x>max_x||x<min_x||y>max_y||y<min_y){
			return false;
		}
		return true;
	}
	public boolean hasData() throws Exception{
		boolean has = true;
		Connection conn = null;
		conn=Main.getPool(BikeType.HELLOBIKE).getConnection();
		PreparedStatement pre=null;
		ResultSet res=null;
		SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String interval="";
		interval = PropertiesUtil.getProperties("common","GJCTrackTimeInterval");
		
		String tname=getTableName();
		if(!isTableExist(tname)){
			System.out.println("今天没有相应轨迹表！");
			has=false;
			return has;
		}
//		String formated_date="2017/04/05 00:00:00";//模拟没有数据的情况
		String formated_date=sd.format(getstartTime(Long.parseLong(interval)));
		try {
			String max_record=PropertiesUtil.getProperties("common", "MaxRecordNum");
			String sql="";
			sql ="select count(*) from "+tname+" where gettime>to_date('"+formated_date+"','yyyy/mm/dd hh24:mi:ss')";
			pre=conn.prepareStatement(sql);
			res=pre.executeQuery();
			while(res.next()){
				if(res.getInt(1)==0){
					has=false;
					return has;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				res.close();
				pre.close();
				JDBCUtil.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return has;
	}
	/**
	 * 获取当天的轨迹表，各个用户的轨迹表名字相同（渣土车，公交车，出租车）
	 * 每台公交车每15秒一个点
	 * @return 当天的轨迹表名
	 */
	public String getTableName(){
		Date date = new Date();
		SimpleDateFormat sd=new SimpleDateFormat("yyyy_MM_dd");
		String tname="GA_GPS_HISTORY_"+sd.format(date);
		return tname;
	}
	/**
	 * 获取起始时间
	 * @param interval	时间间隔
	 * @return 当前时间减去时间间隔返回查询的起始时间
	 */
	public Date getstartTime(long interval){
		long cur_time=System.currentTimeMillis();
		long t1=cur_time-interval;//起始时间
		Date date=new Date(t1);
		return date;
	}
	/**
	 * 判断某用户下的表是否存在
	 * @param tablename	表名
	 * @param username	不同用户
	 * @return	存在为真，不存在为假
	 */
	public boolean isTableExist(String tablename){
		boolean isExist=true;
		String sql ="select * from user_tables where table_name='"+tablename+"'";
		PreparedStatement pre=null;
		Connection conn=null;
		ResultSet res=null;
		try {
			conn=Main.getPool(BikeType.HELLOBIKE).getConnection();
			pre=conn.prepareStatement(sql);
			res=pre.executeQuery();
			if(!res.next()){
				isExist=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isExist=false;
		}finally {
			try {
				res.close();
				pre.close();
				JDBCUtil.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isExist;
	}

}
