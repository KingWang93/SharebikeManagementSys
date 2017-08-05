package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtil {

    public static String getProperties(String propertyname, String sKey) {
        Properties properties = new Properties();
        try {           
            InputStreamReader insReader = new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"\\"+propertyname+".properties"), "UTF-8");
            properties.load(insReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = properties.getProperty(sKey);
        return s;
    }

	public static String getProperties(String propertyname) {
		String url = Thread.currentThread().getContextClassLoader().getResource("").toString();
		url = url.substring(url.indexOf("/") + 1);
		url = url.replaceAll("%20", " ");
		Properties properties = new Properties();
		try {
			InputStreamReader insReader = new InputStreamReader(new FileInputStream(url + propertyname + ".properties"),
					"UTF-8");
			properties.load(insReader);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		String s = properties.toString();
		return s;
	}

	public static Set<Object> getKeyValue(String propertyname) {
		String url = Thread.currentThread().getContextClassLoader().getResource("").toString();
		url = url.substring(url.indexOf("/") + 1);
		url = url.replaceAll("%20", " ");
		Properties properties = new Properties();
		try {
			InputStreamReader insReader = new InputStreamReader(new FileInputStream(url + propertyname + ".properties"),
					"UTF-8");
			properties.load(insReader);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Set<Object> keyValue = properties.keySet();
		return keyValue;
	}

	public static String ClobToString(Clob clob) {
		String reString = "";
		Reader is = null;
		try {
			is = clob.getCharacterStream();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		BufferedReader br = new BufferedReader(is);
		String s = null;
		try {
			s = br.readLine();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		StringBuffer sb = new StringBuffer();
		while (s != null) {
			// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
			sb.append(s);
			try {
				s = br.readLine();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		reString = sb.toString();
		return reString;
	}
}