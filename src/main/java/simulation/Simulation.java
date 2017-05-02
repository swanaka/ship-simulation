package simulation;

import model.Fleet;
import model.Market;
import model.PortNetwork;

public abstract class Simulation {
	//Configuration
	private int startTime;
	private int endTime;
	//Status
	protected int now;
	
	//Function
	public abstract void save(int now);
	
	public Simulation(int endTime){
		
		setEndTime(endTime);
	}
	
	public void execute(){
		while(now <= endTime){
			//timeNext(now);
			save(now);
			now++;
		}
	};
	
	public void timeNext(int now) {
		//1 Update the market situation.
		Market.timeNext(now);
		//2 Check demand, and if new demand happen, make a new contract, schedule to ship.
		if(Market.checkDemand()){
			Market.addContract();
		}
		//3 Update ships' situation.
		Fleet.timeNext(now);
		//4 Update ports' situation
		PortNetwork.timeNext(now);
		
		
	}
	
	
	//Getter and Setter
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	

}
