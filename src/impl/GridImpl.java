package impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import entity.GPSPoint;
import entity.Grid;
import entity.Line;
import io.vertx.core.json.JsonArray;
import util.PropertiesUtil;

public class GridImpl {
	public JsonArray getTrail(Grid grid){
		HashSet<Line> set=grid.getSet();
		Iterator<Line> it=set.iterator();
		int coorAbs=Integer.parseInt(PropertiesUtil.getProperties("common", "CoorAbs"));
		JsonArray result=new JsonArray();
		
		while(it.hasNext()){
			Line line=it.next();
			List<GPSPoint> list=line.getCoors();
			double x=0;
			double y=0;
			JsonArray eachline=new JsonArray();
			String temp="";
			for(int i=0;i<list.size();i++){
				GPSPoint point=list.get(i);
				double tnum=((point.getDate().getTime()%60000)/60000.0)*1800.0;
				String time=String.valueOf(tnum);
				String s_x="";
				String s_y="";
				if(i==0){
					x=point.getX();
					y=point.getY();
					s_x=String.valueOf((int)(x*coorAbs));
					s_y=String.valueOf((int)(y*coorAbs));
				}else{
					s_x=String.valueOf((int)((point.getX()-x)*coorAbs));
					s_y=String.valueOf((int)((point.getY()-y)*coorAbs));
				}
				if(i==list.size()-1){
					temp+=(s_x+","+s_y+","+time);
				}else{
					temp+=(s_x+","+s_y+","+time+";");
				}
			}
			eachline.add(temp);
			result.add(eachline);
		}
		return result;
	}

}
