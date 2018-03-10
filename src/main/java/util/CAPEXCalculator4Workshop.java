package util;

public class CAPEXCalculator4Workshop extends CAPEXCalculator{

	private int numOfHFO;
	private int numOfLSFO;
	private int numOfLNG;
	private int numOfHFOLNG;
	private int numOfLNGbunkeringAtPersianGulf;
	private String bunkeringMethodAtPersianGulf;
	private int numOfLNGbunkeringAtJapan;
	private String bunkeringMethodAtJapan;
	private int numOfLNGbunkeringAtSingapore;
	private String bunkeringMethodAtSingapore;
	
	
	private final double cost4HFOShip = 10.9275;
	private final double cost4LSFOShip = 0.1612;
	private final double cost4LNG4stroke = 18.3582;
	private final double cost4LNGdualfuel = 17.484;
	private final double cost4BunkeringShip = 50;
	private final double cost4LNGterminal = 940;
	private final double cost4LNGtruck = 0.5;
	private final double cost4LSFObunkering = 10;
	
	public CAPEXCalculator4Workshop(String numOfHFO, String numOfLSFO, String numOfLNG, String numOfHFOLNG, String numAtPersianGulf, String methodAtPersianGulf, String numAtJapan, String methodAtJapan, String numAtSingapore, String methodAtSinapore){
		this.numOfHFO = Integer.parseInt(numOfHFO);
		this.numOfLSFO = Integer.parseInt(numOfLSFO);
		this.numOfLNG = Integer.parseInt(numOfLNG);
		this.numOfHFOLNG = Integer.parseInt(numOfHFOLNG);
		this.numOfLNGbunkeringAtPersianGulf = Integer.parseInt(numAtPersianGulf);
		this.numOfLNGbunkeringAtJapan = Integer.parseInt(numAtJapan);
		this.numOfLNGbunkeringAtSingapore = Integer.parseInt(numAtSingapore);
		this.bunkeringMethodAtPersianGulf = methodAtPersianGulf;
		this.bunkeringMethodAtJapan = methodAtJapan;
		this.bunkeringMethodAtSingapore = methodAtSinapore;
	}
	
	@Override
	public double calculate(){
		double capex = 0;
		capex += cost4HFOShip * numOfHFO + cost4LSFOShip * numOfLSFO + cost4LNG4stroke * numOfLNG + cost4LNGdualfuel * numOfHFOLNG;
		if(numOfLSFO > 0) {
			capex += 2 * cost4LSFObunkering;
		}
		if(this.bunkeringMethodAtPersianGulf.equals("Truck to Ship")) {
			capex += cost4LNGtruck * numOfLNGbunkeringAtPersianGulf;
		}else if(this.bunkeringMethodAtPersianGulf.equals("Ship to Ship")) {
			capex += cost4BunkeringShip * numOfLNGbunkeringAtPersianGulf;
		}else if(this.bunkeringMethodAtPersianGulf.equals("Shore to Ship")) {
			capex += cost4LNGterminal;
		}
		if(this.bunkeringMethodAtJapan.equals("Truck to Ship")) {
			capex += cost4LNGtruck * numOfLNGbunkeringAtJapan;
		}else if(this.bunkeringMethodAtJapan.equals("Ship to Ship")) {
			capex += cost4BunkeringShip * numOfLNGbunkeringAtJapan;
		}else if(this.bunkeringMethodAtJapan.equals("Shore to Ship")) {
			capex += cost4LNGterminal;
		}
		if(this.bunkeringMethodAtSingapore.equals("Truck to Ship")) {
			capex += cost4LNGtruck * numOfLNGbunkeringAtSingapore;
		}else if(this.bunkeringMethodAtSingapore.equals("Ship to Ship")) {
			capex += cost4BunkeringShip * numOfLNGbunkeringAtSingapore;
		}else if(this.bunkeringMethodAtSingapore.equals("Shore to Ship")) {
			capex += cost4LNGterminal;
		}
		
		return capex;
	}
}
