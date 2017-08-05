package util;

import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class JDBCUtil {
	
	
	public static ComboPooledDataSource getPool(String db){
		ComboPooledDataSource pool=new ComboPooledDataSource(db);
		return pool;
	}
	
	public static void close(Connection conn) throws SQLException{
		if(conn!=null){
			conn.close();
		}
	}
}
