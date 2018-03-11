package model;

import java.util.ArrayList;
import java.util.List;

import model.Status.FuelType;

public class PortNetwork {
	
	private static List<Port> portList;
	private static double[][] routeMatrix;
	private static PortNetwork portnetwork = new PortNetwork();
	
	private PortNetwork(){
		portList = new ArrayList<Port>();
		routeMatrix = new double[0][0];
	}
	public static void reset(){
		portList = new ArrayList<Port>();
		routeMatrix = new double[0][0];
	}
	public static PortNetwork getInstance(){
		return portnetwork;
	}

	public static void timeNext(){
		
		for (Port port : portList){
			port.timeNext();
		}
				
	}
	public static Port getPort(String portName){
		for (Port port : portList){
			if (port.getName().equals(portName)) return port;
		}
		return null;
	}
	
	public static double getDistance(Port port1,Port port2){
		int i = portList.indexOf(port1);
		int j = portList.indexOf(port2);
		return routeMatrix[i][j];
	}
	

	public void add(Port port){
		//TO-DO
	}
	public void remove(Port port){
		//TO-DO
	}
	public static List<Port> getPorts() {
		return portList;
	}
	public static void setPortSettings(List<Port> pList,double[][] route){
		portList = pList;
		routeMatrix = route;
	}
	
	public static boolean avilablePort(String portname, FuelType type) {
		for(Port port : portList) {
			if(port.getName().equals(portname)) {
				for(PortFacility facility : port.facilities) {
					if(facility.fuelTypeList.contains(type)) {
						return true;
					}
				}
			}	
		}
		return false;
	}
}
