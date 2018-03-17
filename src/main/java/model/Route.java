package model;

import java.util.ArrayList;
import java.util.List;

import util.CSVReader;

public class Route {

	private Port origin;
	private Port destination;
	private double distance;
	private List<double[]> points; //data for lat, lon and dis
	
	public Route() {
		points = new ArrayList<double[]>();
	}
	
	public void importFromCSV(String filename) {
		List<String[]> dataList = CSVReader.forGeneral(filename);
		boolean flag = false; 
		for(String[] data : dataList) {
			if (!flag) {
				this.distance = Double.parseDouble(data[2]);
				flag = true;
			}else {
				double lat = Double.parseDouble(data[0]);
				double lon = Double.parseDouble(data[1]);
				double dis = Double.parseDouble(data[2]);
				double[] latlon = {lat,lon, dis};
				points.add(latlon);
			}
		}
	}

	public double[] getLatLon(double distance) {
		if(distance > this.distance || distance < 0) {
			System.out.println("Error: No points among them.");
			return null;
		}

		double[] latlon = new double[2];
		for(double point[] : points) {
			if (point[2] > distance) {
				int index = points.indexOf(point);
				double previous[] = new double[3];
				if (index == 0) {
					previous[0] = this.origin.getLoc().getLat();
					previous[1] = this.origin.getLoc().getLon();
					previous[2] = 0;
				}
				else{
					previous = points.get(index-1);
				}
				double progress = (distance - previous[2]) / (point[2] - previous[2]);
				latlon = calDividingPoint(previous, point, progress);
				return latlon;
			}
		}
		double[] previous = points.get(points.size()-1);
		double[] point = new double[3];
		point[0] = this.destination.getLoc().getLat();
		point[1] = this.destination.getLoc().getLon();
		point[2] = this.distance;
		double progress = (distance - previous[2]) / (point[2] - previous[2]);
		latlon = calDividingPoint(previous, point, progress);
		return latlon;
	}
	
	private double[] calDividingPoint(double[] point1, double[] point2, double progress) {
		double[] xyz0 = calXYZfromLatLon(point1);
		double[] xyz1 = calXYZfromLatLon(point2);
		
		double sum = 0;
		for(int i = 0; i < 3; i++) {
			sum += xyz0[i] * xyz1[i];
		}
		double theta = Math.acos(sum);
		double sin = Math.sin(theta);
		
		double v0 = Math.sin(theta * (1 - progress)) / sin;
		double v1 = Math.sin(theta * progress) / sin;
		double[] dividedxyz = new double[3];
		for(int i=0; i < 3; i++) {
			dividedxyz[i] = xyz0[i] * v0 + xyz1[i] * v1;
		}
		return calLatLonfromXYZ(dividedxyz);
	}
	
	private double[] calXYZfromLatLon(double[] latlon) {
		double[] xyz = new double[3];
		double rlat = Math.toRadians(latlon[0]);
		double rlon = Math.toRadians(latlon[1]);
		double coslat = Math.cos(rlat);
		xyz[0] = coslat * Math.cos(rlon);
		xyz[1] = coslat * Math.sin(rlon);
		xyz[2] = Math.sin(rlat);
		
		return xyz;
	}

	private double[] calLatLonfromXYZ(double[] xyz) {
		double[] latlon = new double[2];
		double rlat = Math.asin(xyz[2]);
		double coslat = Math.cos(rlat);
		
		latlon[0] = Math.toDegrees(rlat);
		latlon[1] = Math.toDegrees(Math.atan2(xyz[1]/coslat, xyz[0]/coslat));
		return latlon;
	}
////////////////////////
	
	public void setOrigin(Port port) {
		this.origin = port;
	}
	public Port getOrigin() {
		return this.origin;
	}

	public Port getDestination() {
		return destination;
	}

	public void setDestination(Port destination) {
		this.destination = destination;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
