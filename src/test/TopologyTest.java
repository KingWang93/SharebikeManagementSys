package test;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class TopologyTest {
	/**
	 * 实验说明，LinearRing是线，polygon是多边形，线内部和多边形内部不一样，线的子集是在线上，也是内部（within），多边形是面
	 * @param args
	 */
	public static void main(String[] args) {
		Coordinate[] coor_arr1=new Coordinate[5];
//		coor_arr1[0]=new Coordinate(114.254398, 30.665175);
//		coor_arr1[1]=new Coordinate(114.254398, 30.525956);
//		coor_arr1[2]=new Coordinate(114.392427, 30.525956);
//		coor_arr1[3]=new Coordinate(114.392427, 30.665175);
//		coor_arr1[4]=new Coordinate(114.254398, 30.665175);
		coor_arr1[0]=new Coordinate(114.254398, 30.665175);
		coor_arr1[1]=new Coordinate(114.392427, 30.665175);
		coor_arr1[2]=new Coordinate(114.392427, 30.525956);
		coor_arr1[3]=new Coordinate(114.254398, 30.525956);
		coor_arr1[4]=new Coordinate(114.254398, 30.665175);
		CoordinateArraySequence seq=new CoordinateArraySequence(coor_arr1);
		LinearRing ring = new LinearRing(seq, new GeometryFactory());
		String string="114.254398 30.665175, 114.392427 30.665175";
//		String string="114.287 30.5964, 114.2877 30.5971, 114.2885 30.5979, 114.2894 30.5988, 114.2912 30.6002, 114.292 30.6009, 114.2925 30.6013, 114.2942 30.6028, 114.2949 30.6032, 114.296 30.6046, 114.2971 30.6059, 114.2975 30.6065, 114.298 30.6072, 114.2985 30.608, 114.2991 30.6089";
		String[] arr=string.split(", ");
		List<Coordinate> coors=new ArrayList<>();
		for(String temp:arr){
			String[] xy=temp.split(" ");
			Coordinate coor=new Coordinate(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
			coors.add(coor);
		}
		Coordinate[] coor_arr=coors.toArray(new Coordinate[0]);
		LineString l_s=new GeometryFactory().createLineString(coor_arr);
//		Polygon poly=new Polygon(ring, null, new GeometryFactory());
//		System.out.println(poly.contains(l_s));
//		System.out.println(poly.touches(l_s));
//		System.out.println(poly.covers(l_s));
//		System.out.println(poly.within(l_s));
//		System.out.println(poly.crosses(l_s));
//		System.out.println(poly.intersects(l_s));
//		System.out.println(poly.equals(l_s));
//		System.out.println(poly.overlaps(l_s));
//		System.out.println(poly.coveredBy(l_s));
		System.out.println("-----------------");
		System.out.println(l_s.touches(ring));
		System.out.println(l_s.covers(ring));
		System.out.println(l_s.within(ring));
		System.out.println(l_s.crosses(ring));
		System.out.println(l_s.intersects(ring));
		System.out.println(l_s.disjoint(ring));
		System.out.println(l_s.equals(ring));
		System.out.println(l_s.overlaps(ring));
		System.out.println(l_s.contains(ring));
		System.out.println(l_s.coveredBy(ring));
		System.out.println("---------");
		System.out.println(ring.touches(l_s));
		System.out.println(ring.covers(l_s));
		System.out.println(ring.within(l_s));
		System.out.println(ring.crosses(l_s));
		System.out.println(ring.intersects(l_s));
		System.out.println(ring.disjoint(l_s));
		System.out.println(ring.equals(l_s));
		System.out.println(ring.overlaps(l_s));
		System.out.println(ring.contains(l_s));
		System.out.println(ring.coveredBy(l_s));
		System.out.println(ring.relate(l_s));
//		LINEARRING (114.254398 30.665175, 114.254398 30.525956, 114.392427 30.525956, 114.392427 30.665175, 114.254398 30.665175)
//		LINESTRING (114.3013 30.5228, 114.3026 30.5233, 114.3042 30.5246, 114.305 30.5261, 114.3058 30.5265, 114.3069 30.5266, 114.3083 30.5268, 114.3091 30.5261, 114.3104 30.525, 114.311 30.5242, 114.312 30.5234, 114.313 30.523, 114.3144 30.5226, 114.3156 30.5224, 114.3163 30.5221, 114.3168 30.522, 114.3176 30.522, 114.3181 30.5217)
	}

}
