package entity;

import java.util.HashSet;


public class Grid {
	private int index=0;
	private int col=0;
	private int row=0;
	private Point lefttop=new Point();
	private Point rightbottom=new Point();
	private HashSet<Line> set=new HashSet<>();
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public Point getLefttop() {
		return lefttop;
	}
	public void setLefttop(Point lefttop) {
		this.lefttop = lefttop;
	}
	public Point getRightbottom() {
		return rightbottom;
	}
	public void setRightbottom(Point rightbottom) {
		this.rightbottom = rightbottom;
	}
	public HashSet<Line> getSet() {
		return set;
	}
	public void setSet(HashSet<Line> set) {
		this.set = set;
	}
	public void addLine(Line line){
		this.set.add(line);
	}
}
