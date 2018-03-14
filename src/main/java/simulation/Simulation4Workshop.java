package simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Fleet;
import model.Ship;
import util.CAPEXCalculator;

public class Simulation4Workshop extends Simulation {
	private CAPEXCalculator calcultaor;
	public Simulation4Workshop(int endTime, CAPEXCalculator calculator) {
		super(endTime);
		this.calcultaor = calculator;
	}
	@Override
	public void save(int now) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}
	
	public Map<String, String> getResult(){
		Map<String, String> result = new HashMap<String, String>();
		List<Ship> ships = Fleet.getShips();
		double TTTM = 0;
		double TECU = 0;
		double totalCo2 = 0;
		double totalNox = 0;
		double totalSox = 0;
		double totalWaitingTime = 0;


		double totalFuelCost = 0;
		for(Ship ship : ships){

			double totalCargo = ship.getTotalCargo();
			double totalDistance = ship.getTotalDistance();
			double totalFuel = ship.getTotalFuelPricise();
			double co2 = ship.getCo2();
			double sox = ship.getSox();
			double nox = ship.getNox();
			double waitingTime = ship.getWaitingTime();

			TTTM += ship.getTotalTonKm();
			if(totalCargo * totalDistance != 0) TECU += totalFuel / (totalCargo * totalDistance);
			totalFuelCost += ship.getTotalFuelPricise();
			totalCo2 += co2;
			totalSox += sox;
			totalNox += nox;
			totalWaitingTime += waitingTime;

		}
		result.put("tttk", String.valueOf(TTTM));
		result.put("fe", String.valueOf(totalFuelCost/TTTM));
		result.put("co", String.valueOf(totalCo2/TTTM));
		result.put("no", String.valueOf(totalNox/TTTM));
		result.put("so", String.valueOf(totalSox/TTTM));
		result.put("wait", (String.valueOf(totalWaitingTime/(this.getEndTime()-8760)/20*100)));
		result.put("ic", String.valueOf(this.calcultaor.calculate()));
		return result;
	}

}
