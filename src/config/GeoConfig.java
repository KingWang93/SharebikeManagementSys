package config;

public class GeoConfig {
	private double maxGeoRange;
	private double minGeoRange;
	private double gridRowInterval;
	private double gridColInterval;
	public GeoConfig setMaxGeoRange(double maxGeoRange){
		this.maxGeoRange=maxGeoRange;
		return this;
	}
	public GeoConfig setMinGeoRange(double minGeoRange){
		this.minGeoRange=minGeoRange;
		return this;
	}
	public double getMaxGeoRange() {
		return maxGeoRange;
	}
	public double getMinGeoRange() {
		return minGeoRange;
	}
	public GeoConfig setGridRowInterval(double gridRowInterval){
		this.gridRowInterval=gridRowInterval;
		return this;
	}
	public GeoConfig setGridColInterval(double gridColInterval){
		this.gridColInterval=gridColInterval;
		return this;
	}
	public double getGridRowInterval() {
		return gridRowInterval;
	}
	public double getGridColInterval() {
		return gridColInterval;
	}
}
