package exec;

import java.util.ArrayList;
import java.util.List;

import simulation.InputData;
import simulation.Simulation;
import simulation.Simulation4Workshop;
import util.CSVReader;
import util.CSVwriter;

public class Exec4Workshop {

	private final static String OUTPUT_OVERALL_RESULT = "result_overall.csv";
	private static String OUTPUT_DIR_ROOT = "./data/";
	private static final String INPUT_DIR = "./data/";

	public static void main(String[] args) {
		int endTime = 8760 * 100;
		if(args.length == 0) {
			System.out.println("You need to designate the input file name");
			return ;
		}
		String casefile = args[0];
		List<String> outputListAll = new ArrayList<String>();
		outputListAll.add("System's ilities");
		outputListAll.add("Fuel efficiency");
		outputListAll.add("Total CO2 Emission rate");
		outputListAll.add("Total NOx Emission rate");
		outputListAll.add("Total SOx Emission rate");
		outputListAll.add("Total Waiting Time rate");
		outputListAll.add("InitialCost");
		CSVwriter.write(OUTPUT_DIR_ROOT + OUTPUT_OVERALL_RESULT, outputListAll, false);

		List<String[]> data = CSVReader.forGeneral(INPUT_DIR + casefile);
		int count = 0;
		for(String[] elem : data){
			if(count == 0) {
				count ++;
				continue;
			}
			
			String caseNum = elem[0];
			String numOfHFO = elem[1];
			String numOfLSFO = elem[2];
			String numOfLNG = elem[3];
			String numOfHFOLNG = elem[4];
			String[] persianGulfSetting = {elem[5], elem[6]};
			String[] japanSetting = {elem[7], elem[8]};
			String[] singaporeSetting = {elem[9], elem[10]};
			
			InputData input = new InputData();
			input.setNumOfHFO(numOfHFO);
			input.setNumOfLSFO(numOfLSFO);
			input.setNumOfLNG(numOfLNG);
			input.setNumOfHFOLNG(numOfHFOLNG);
			input.setNumOfBunkeringFacilitiesAtPersianGulf(persianGulfSetting[0]);
			input.setBunkeringMethodAtPersianGulf(persianGulfSetting[1]);
			input.setNumOfBunkeringFacilitiesAtJapan(japanSetting[0]);
			input.setBunkeringMethodAtJapan(japanSetting[1]);
			input.setNumOfBunkeringFacilitiesAtSingapore(singaporeSetting[0]);
			input.setBunkeringMethodAtSingapore(singaporeSetting[1]);
			
			execOneshot(endTime, input, caseNum);
		}
		
		System.out.println("-------------All of the Simulation End ----------");
	}
	
	public static void execOneshot(int endTime, InputData data, String casenum) {
		if(Integer.parseInt(casenum) % 1000 == 0) {
			System.out.println(casenum);
		}
		Simulation simulation = new Simulation4Workshop(endTime, data, casenum);
		simulation.setSaveFlag(true);
		simulation.execute();
	}
}
