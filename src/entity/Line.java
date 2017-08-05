package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import config.GeoConfig;
import util.SpatialUtil;
/**
 * 线，点串，描述整条轨迹
 * @author KingWang
 *
 */
public class Line {
	/**
	 * 轨迹的id
	 */
	private String id="";
	/**
	 * 按照顺序存储点轨迹
	 */
	private List<GPSPoint> coors=new ArrayList<>();
	/**
	 * 经过的网格，按照轨迹顺序存储
	 */
	private List<Grid> grids=new ArrayList<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<GPSPoint> getCoors() {
		return coors;
	}
	public void setCoors(List<GPSPoint> coors) {
		this.coors = coors;
	}
	public List<Grid> getGrids() {
		return grids;
	}
	public void setGrids(List<Grid> grids) {
		this.grids = grids;
	}
	public void addPoint(GPSPoint p){
		this.coors.add(p);
	}
	
	public void removePoint(int index){
		this.coors.remove(index);
	}
	
	public Line sort(boolean isTimeAsc){
		List<GPSPoint> list=this.getCoors();
		Collections.sort(list, (point1,point2)->{
			if(point1.getDate().after(point2.getDate())){
				if(isTimeAsc){
					return 1;
				}else{
					return -1;
				}
			}else{
				if(isTimeAsc){
					return -1;
				}else{
					return 1;
				}
			}
		});
		return this;
	}
	/**
	 * 对线坐标串进行粗处理,太密的点删掉，太远的点打断成两段
	 * @param config
	 * @return
	 */
	public List<Line> filter(GeoConfig config){
		List<Line> resultList=new ArrayList<>();
		List<GPSPoint> list=new CopyOnWriteArrayList<>(this.getCoors());
		Point lastPt=new Point();
		int i=0;
		int lastCutIndex=0;
		for(GPSPoint point:list){
			if(i>0&&SpatialUtil.inTolerance(lastPt,point,config)==CompareValue.GT){
				List<GPSPoint> list_temp=new ArrayList<>();
				list_temp.addAll(list.subList(lastCutIndex, i));
				Line line_temp=new Line();
				line_temp.setCoors(list_temp);
				line_temp.setId(String.valueOf(System.currentTimeMillis()+new Random().nextInt(10)));
				resultList.add(line_temp);
				lastCutIndex=i;
			}else if(i>0&&SpatialUtil.inTolerance(lastPt, point, config)==CompareValue.LT){
				list.remove(i);
				i--;
			}
			lastPt=point;
			i++;
		}
		if(lastCutIndex==i){
			Line line_temp=new Line();
			line_temp.setCoors(this.getCoors());
			line_temp.setId(String.valueOf(System.currentTimeMillis()+new Random().nextInt(10)));
			resultList.add(line_temp);
		}else{
			List<GPSPoint> list_temp=new ArrayList<>();
			list_temp.addAll(list.subList(lastCutIndex, i));
			Line line_temp=new Line();
			line_temp.setCoors(list_temp);
			line_temp.setId(String.valueOf(System.currentTimeMillis()+new Random().nextInt(10)));
			resultList.add(line_temp);
		}
		return resultList;
	}
}
