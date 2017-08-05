package util;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.text.DecimalFormat;

public class ParseDataType {
	/*
	 * 功能：将double类型的数据转换为String类型
	 * 传入参数：
	 * d：double类型的数据
	 * num：输出几位小数（四舍五入）
	 */
	public static String parseD2s(double d,int num){
		String pattern = "0.";
		for(int i=0;i<num;i++){
			pattern+="0";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		String s=df.format(d);
		return s;
	}
	/*
	 * 功能：将String类型的数字转换为String类型
	 * 传入参数：
	 * s：传入的字符串形式的数字
	 * num：输出几位小数（四舍五入）
	 */
	public static String parseS2s(String s,int num){
		String pattern = "0.";
		for(int i=0;i<num;i++){
			pattern+="0";
		}
		DecimalFormat df = new DecimalFormat(pattern);
		String string=df.format(Double.parseDouble(s));
		return string;
	}
	
	/*clob转string*/
	public static String ClobToString(Clob clob){
		String reString="";
		if(clob!=null)
		{
			try {
				Reader is = clob.getCharacterStream();
				BufferedReader br=new BufferedReader(is);
				String s=br.readLine();
				StringBuffer sb=new StringBuffer();
				while(s!=null){
					sb.append(s);
					s=br.readLine();
				}
				reString=sb.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return reString;
	}
}
