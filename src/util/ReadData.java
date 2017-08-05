package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import io.vertx.core.json.JsonObject;

public class ReadData {
	public static void main(String[] args) {
		Connection conn=null;
		ComboPooledDataSource pool=JDBCUtil.getPool("gjc");
		try {
			conn=pool.getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("ga_gps_history_2017_03_28_0.txt"));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

		PreparedStatement pre=null;
		try {
			pre=conn.prepareStatement("insert into GPS values(?,?,?,?,?,?,?,?,?)");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			int i=0;
			String line;
			while((line=reader.readLine())!=null){
				JsonObject jo=new JsonObject(line);
				pre.setString(1, jo.getString("vehicle_no"));
				pre.setString(2, jo.getString("vehicle_line"));
				pre.setString(3, jo.getString("Vec"));
				pre.setString(4, jo.getString("Direction"));
				pre.setString(5, jo.getString("linedirection"));
				pre.setTimestamp(6, Timestamp.valueOf(jo.getString("Time")));
				pre.setString(7, jo.getString("LON"));
				pre.setString(8, jo.getString("LAT"));
				pre.setString(9, jo.getString("status"));
				pre.addBatch();
				if(++i%1000==0){
					pre.executeBatch();
					conn.commit();
				}
			}
			pre.executeBatch();
			conn.commit();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		pool.close();
	}
}
