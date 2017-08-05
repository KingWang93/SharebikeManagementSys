package entity;

import java.util.Date;

/**
 * 点，描述点的位置，所属网格和所属线条
 * @author KingWang
 *
 */
public class GPSPoint extends Point{
	private Date date=new Date();
	private Grid grid=new Grid();
	private Line line=new Line();

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Grid getGrid() {
		return grid;
	}
	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	public Line getLine() {
		return line;
	}
	public void setLine(Line line) {
		this.line = line;
	}

}
