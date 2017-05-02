package util;

public class Location {
	private double lat;
	private double lon;
	
	public Location(double lat, double lon){
		this.lat = lat;
		this.lon = lon;
	}
	
	public double[] getloc(){
		double[] returnArray = {this.lat, this.lon};
		return returnArray;
	}
}
