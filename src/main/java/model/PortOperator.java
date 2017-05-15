package model;

public abstract class PortOperator {
	//Configuration
	private String name;
	private double fixedCost;

	//Status
	private double cashFlow;
	private double bunkeringCash;
	
	private double berthingCash;
	
	//Function
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getFixedCost() {
		return fixedCost;
	}

	public void setFixedCost(double fixedCost) {
		this.fixedCost = fixedCost;
	}

	public double getCashFlow() {
		return cashFlow;
	}

	public void setCashFlow(double cashFlow) {
		this.cashFlow = cashFlow;
	}
	public void addBunkeringCash(double cash){
		this.bunkeringCash += cash;
	}
	public double getBunkeringCash() {
		return bunkeringCash;
	}

	public void setBunkeringCash(double bunkeringCash) {
		this.bunkeringCash = bunkeringCash;
	}
	
	public void addBerthingCash(double berthingCash){
		this.berthingCash += berthingCash;
	}

	public double getBerthingCash() {
		return berthingCash;
	}

	public void setBerthingCash(double berthingCost) {
		this.berthingCash = berthingCost;
	}
}

