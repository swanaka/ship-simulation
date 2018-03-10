package model;

import java.util.List;

import model.Status.CargoType;
import model.Status.FuelType;

abstract class PortFacility{
		protected Ship berthingShip;
		protected List<FuelType> fuelTypeList;
		protected CargoType loadingType;
		protected int occupiedFlag;
		protected List<Double> bunkeringCapacityList;
		protected double loadingCapacity;
		protected Port port;
		

		public abstract void accept(Ship ship);
		public abstract void berthing();
		public abstract void loading();
		public abstract void unloading();
		public abstract void bunkering();
		public abstract boolean match(Ship ship);
		public void setPort(Port port) {
			this.port = port;
		}

	}
