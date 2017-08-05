package util;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import entity.GPSPoint;
import entity.Grid;
import entity.Line;
import proto.GridProto;

public class ProtoBuilder {
	public static byte[] getGridProto(Grid grid){
		GridProto.Grid.Builder gridBuiler=GridProto.Grid.newBuilder();
		gridBuiler.setCol(grid.getCol());
		gridBuiler.setRow(grid.getRow());
		gridBuiler.setIndex(grid.getIndex());
		HashSet<Line> set=grid.getSet();
		Iterator<Line> it=set.iterator();
		
		GridProto.Point.Builder ptBuilder=GridProto.Point.newBuilder();
		GridProto.Line.Builder lBuilder=GridProto.Line.newBuilder();
		SimpleDateFormat sd=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		while(it.hasNext()){
			Line line=it.next();
			List<GPSPoint> list=line.getCoors();
			for(GPSPoint point:list){
				ptBuilder.setX(point.getX());
				ptBuilder.setY(point.getY());
				ptBuilder.setDate(sd.format(point.getDate()));
				lBuilder.addCoors(ptBuilder);
			}
			gridBuiler.addLines(lBuilder);
		}
		
		return gridBuiler.build().toByteArray();
	}
}
