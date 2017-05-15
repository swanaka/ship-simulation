package model;

public class Status {
	public enum ShipStatus{
		TRANSPORT("Transporting"),
		WAIT("Waiting"),
		BERTH("Berthing");

		private final String name;
		private ShipStatus(final String name){
			this.name = name;
		}
	}

	public enum BunkeringStatus{
		YES,
		NO,
	}

	public enum LoadingStatus{
		LOADING,
		UNLOADING,
		NO,
	}
	public enum MaintenanceStatus{
		YES,
		NO,
	}
	
	public enum FuelType{ 
		LNG("LNG"), 
		METHANOL("METHANOL"),
		HFO("HFO"),
		LSFO("LSFO");
		
		private final String name;
		private FuelType(final String name){
			this.name = name;
		}
	}
	
	public enum CargoType{Container, OIL}
}
