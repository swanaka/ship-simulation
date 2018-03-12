package simulation;

import model.Fleet;
import model.Market;
import model.PortNetwork;

public abstract class Simulation {
	//Configuration
	private int startTime;
	private int endTime;
	private boolean saveFlag;
	//Status
	protected int now = 0;
	
	//Function
	public abstract void save(int now);
	public abstract void save();
	
	public Simulation(int endTime){
		
		setEndTime(endTime);
	}
	
	public void execute(){
		save(-1);
		while(now <= endTime){
			timeNext();
			if(this.saveFlag) save(now);
			now++;
		}
		save();
	};
	
	public void timeNext() {
		//1 Update the market situation.
		Market.timeNext();
		//2 Check demand, and if new demand happen, make a new contract, schedule to ship.
		if(Market.checkDemand()){
			Market.addContract();
		}
		//3 Update ships' situation.
		Fleet.timeNext();
		//4 Update ports' situation
		PortNetwork.timeNext();
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
	
	public void setSaveFlag(boolean flag) {
		this.saveFlag = flag;
	}
}
