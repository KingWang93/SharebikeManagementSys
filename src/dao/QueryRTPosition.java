package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.BikeType;
import entity.Point;
import main.Main;
import util.JDBCUtil;

public class QueryRTPosition {
	public List<Point> getRealTimePositon(BikeType type){
		switch (type) {
		case HELLOBIKE:
			return getRealTimePositionofHello();
		case MOBAI:
			return getRealTimePositionofMobai();
		case OFO:
			return getRealTimePositionofOfo();
		default:
			return null;
		}
	}
	public List<Point> getRealTimePositionofMobai(){
		Connection conn=null;
		PreparedStatement pre=null;
		ResultSet res=null;
		List<Point> list=new ArrayList<>();
		try {
			conn =Main.getPool(BikeType.MOBAI).getConnection();
			String sql="select x,y from T_GPS_REALTIME";
			pre=conn.prepareStatement(sql);
			res=pre.executeQuery();
			while(res.next()){
				Point pt=new Point();
				pt.setX(res.getDouble(1));
				pt.setY(res.getDouble(2));
				list.add(pt);
			}
		} catch (SQLException e) {
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
		return list;
	}
	
	public List<Point> getRealTimePositionofOfo(){
		Connection conn=null;
		PreparedStatement pre=null;
		ResultSet res=null;
		List<Point> list=new ArrayList<>();
		try {
			conn=Main.getPool(BikeType.OFO).getConnection();
			String sql="select x,y from T_GPS_REALTIME";
			pre=conn.prepareStatement(sql);
			res=pre.executeQuery();
			while(res.next()){
				Point pt=new Point();
				pt.setX(res.getDouble(1));
				pt.setY(res.getDouble(2));
				list.add(pt);
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
		return list;
	}
	public List<Point> getRealTimePositionofHello(){
		Connection conn=null;
		PreparedStatement pre=null;
		ResultSet res=null;
		List<Point> list=new ArrayList<>();
		try {
			conn=Main.getPool(BikeType.HELLOBIKE).getConnection();
			String sql="select x,y from T_GPS_REALTIME";
			pre=conn.prepareStatement(sql);
			res=pre.executeQuery();
			while(res.next()){
				Point pt=new Point();
				pt.setX(res.getDouble(1));
				pt.setY(res.getDouble(2));
				list.add(pt);
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
		return list;
	}
}
