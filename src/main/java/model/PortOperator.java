package model;

public abstract class PortOperator {
	//Configuration
	private String name;
	private double fixedCost;

	//Status
	private double cashFlow;
	
	//Function
	public void addCashFlow(double add){
		this.cashFlow += add;
	}

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
}
