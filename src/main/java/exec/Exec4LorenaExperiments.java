package exec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Fleet;
import model.Market;
import model.PortNetwork;
import simulation.SimpleSimulation;
import simulation.Simulation;
import simulation.Simulation4Lorena;
import util.CAPEXCalculator;
import util.CAPEXCalculator4CESUN;
import util.CSVReader;
import util.CSVwriter;

public class Exec4LorenaExperiments {
	private static final String INPUT_DIR = "./data/";
	private static final String demandFile = INPUT_DIR + "demand_config.csv";
	private static final String freightFile = INPUT_DIR + "freight_config.csv";
	private static final String fuelpriceFile = INPUT_DIR + "fuelprice_config.csv";
	private static final String portFile = INPUT_DIR + "port_config.csv";
	private static final String DIR_PREFIX = "case";
	private final static String OUTPUT_OVERALL_RESULT = "result_overall.csv";
	private static String OUTPUT_DIR_ROOT = "./data/";
	private static final int numOfShips = 20;
	private static final int numOfPortfacilities = 5;
	private static boolean detourFlag;

	public static void main(String[] args){
		System.out.println("Simulation end time: "+ args[0]);
		int endTime = Integer.parseInt(args[0]);
		String input = "case_table4Lorena.csv";
		List<String> outputListAll = new ArrayList<String>();
		outputListAll.add("System's ilities");
		outputListAll.add("Fuel efficiency");
		outputListAll.add("Total CO2 Emission rate");
		outputListAll.add("Total NOx Emission rate");
		outputListAll.add("Total SOx Emission rate");
		outputListAll.add("Total Waiting Time rate");
		outputListAll.add("InitialCost");
		CSVwriter.write(OUTPUT_DIR_ROOT + OUTPUT_OVERALL_RESULT, outputListAll, false);

		List<String[]> data = CSVReader.forGeneral(INPUT_DIR + input);
		for(String[] elem : data){
			String caseNum = elem[0];
			String typeOfFuel = elem[1];
			String typeOfEngine = elem[2];
			String scrubber = elem[3];
			String location = elem[4];
			String LNGbunkeringMethod = elem[5];
			CAPEXCalculator calculator = new CAPEXCalculator4CESUN(typeOfFuel, typeOfEngine, scrubber, location, LNGbunkeringMethod);
			if(!caseNum.equals("CASE")){
				makeDir(caseNum);
				makeInputFile(caseNum, typeOfFuel, typeOfEngine, scrubber, location, LNGbunkeringMethod);
				if(location.equals("Singapore")) {
					detourFlag = true;
				}else {
					detourFlag = false;
				}
				execOneShot(INPUT_DIR + DIR_PREFIX+caseNum, endTime, calculator);
			}
		}
		
		System.out.println("-------------All of the Simulation End ----------");
	}
	private static void execOneShot(String inputDirPath, int endTime, CAPEXCalculator calculator){
		Fleet.reset();
		PortNetwork.reset();
		Market.reset();
		String outputDir = inputDirPath+"/";
		Main.loadInitialPorts(inputDirPath + "/port_config.csv");
		Main.loadMarketInfoFromCSV(inputDirPath + "/freight_config.csv",inputDirPath + "/fuelprice_config.csv",inputDirPath +"/demand_config.csv");
		Main.loadInitialFleetFromCSV(inputDirPath + "/fleet_config.csv");
		if(detourFlag) {
			Fleet.setDetour(true);
		}else {
			Fleet.setDetour(false);
		}
		Simulation simulation = new Simulation4Lorena(endTime,outputDir, calculator);
		System.out.println("Simulation Start");
		simulation.execute();
		System.out.println("Simulation End");
	}
	private static void makeDir(String caseNum){
		File dir = new File(INPUT_DIR + DIR_PREFIX + caseNum);
		if(dir.exists()){
			;
		}else{
			dir.mkdir();
		}
	}

	private static void makeInputFile(String caseNum, String typeOfFuel, String typeOfEngine, String scrubber, String location, String LNGbunkeringMethod) {
		File dir = new File(INPUT_DIR + DIR_PREFIX + caseNum);
		if(dir.exists()){
			makeFleetConfig(caseNum, typeOfFuel, typeOfEngine, scrubber);
			makePortConfig(caseNum, location, LNGbunkeringMethod);
			makeFreightConfig(caseNum);
			makeFuelpriceConfig(caseNum);
			makeDemandConfig(caseNum);
		}
	}

	private static void makeFleetConfig(String caseNum, String typeOfFuel, String typeOfEngine, String scrubber){

		boolean dualfuelFlag;
		if(typeOfFuel.equals("HFOLNG")) {
			dualfuelFlag = true;
			typeOfFuel = "LNG";
		}else {
			dualfuelFlag = false;
		}
		
		String fileName = "fleet_config.csv";
		String outputFileName = INPUT_DIR + DIR_PREFIX + caseNum +"/" + fileName;
		List<String[]> outputData = new ArrayList<String[]>();
		List<String[]> templateHFO = new ArrayList<String[]>();
		List<String[]> templateLSFO = new ArrayList<String[]>();
		List<String[]> templateLNG = new ArrayList<String[]>();
		String[] row1 = {"shipName"};
		String[] row2 = {"Ship_1"};
		String[] row3 = {"speed","cargoType","cargoAmount", "foc", "fuelCapacity","fuelType","initialType","OperatingCost","Scrubber"};
		String[] row4HFO;
		if(scrubber.equals("Yes")){
			String[] row4HFOe = {"28","OIL","300000","0.36","5000","HFO","Japan","15000","Yes","No"};
			row4HFO = row4HFOe;
		}else {
			String[] row4HFOe = {"28","OIL","300000","0.36","5000","HFO","Japan","15000","No","No"};
			row4HFO = row4HFOe;
		}
		String[] row4LSFO = {"28","OIL","300000","0.32","5000","LSFO","Japan","15000","No", "No"};
		String[] row4LNG = null;
		if(dualfuelFlag) {
			String[] row4;
			if(scrubber.equals("Yes")){
				String[] row4e = {"28","OIL","300000","0.29","5000","HFOLNG","Japan","15000","Yes","No"};
				row4 = row4e;
			}else {
				String[] row4e = {"28","OIL","300000","0.29","5000","HFOLNG","Japan","15000","No","No"};
				row4 = row4e;
			}
			row4LNG = row4;
		}else {
			String[] row4;
			if(scrubber.equals("Yes")){
				String[] row4e = {"28","OIL","300000","0.29","5000","LNG","Japan","15000","Yes","No"};
				row4 = row4e;
			}else {
				String[] row4e = {"28","OIL","300000","0.29","5000","LNG","Japan","15000","No","No"};
				row4 = row4e;
			}
			row4LNG = row4;
		}
		
		if(typeOfFuel.equals("HFO")) {
			templateHFO.add(row1);
			templateHFO.add(row2);
			templateHFO.add(row3);
			templateHFO.add(row4HFO);
			
			for(int i = 0; i< numOfShips; i++){
				outputData.addAll(templateHFO);
			}
		}
		else if (typeOfFuel.equals("LSFO")) {
			templateLSFO.add(row1);
			templateLSFO.add(row2);
			templateLSFO.add(row3);
			templateLSFO.add(row4LSFO);
			for(int i = 0; i< numOfShips; i++){
				outputData.addAll(templateLSFO);
			}
		}else if (typeOfFuel.equals("LNG")) {
			if(typeOfEngine.equals("Gas-diesel engine")) {
				row4LNG[9] = "Yes";
			}else {
				row4LNG[9] = "No";
			}
			templateLNG.add(row1);
			templateLNG.add(row2);
			templateLNG.add(row3);
			templateLNG.add(row4LNG);
			for(int i = 0; i< numOfShips; i++){
				outputData.addAll(templateLNG);
			}
		}
		CSVwriter.writeAll(outputFileName, outputData, false);
	}

	private static void makePortConfig(String caseNum, String location, String LNGbunkeringMethod){
		String fileName = "port_config.csv";
		String port1LNG = "0";
		String port1LSFO = String.valueOf(numOfPortfacilities);
		String port2LNG = String.valueOf(numOfPortfacilities);
		String port2LSFO = String.valueOf(numOfPortfacilities);
		String port3LNG = "0";
		String port3LSFO = String.valueOf(numOfPortfacilities);
		
		if(location.equals("Japan")) {
			port1LNG = String.valueOf(numOfPortfacilities);
		}else if (location.equals("Singapore")) {
			port3LNG = String.valueOf(numOfPortfacilities);
		}
		List<String[]> data = CSVReader.forGeneral(portFile);
		data.get(7)[0] = port1LNG;
		data.get(10)[0] = port1LSFO;
		data.get(18)[0] = port2LNG;
		data.get(21)[0] = port2LSFO;
		data.get(29)[0] = port3LNG;
		data.get(32)[0] = port3LSFO;
		if(LNGbunkeringMethod.equals("Truck to Ship")) {
			data.get(6)[2] = "10.4";
			data.get(17)[2] = "10.4";
			data.get(28)[2] = "10.4";
		}else {
			data.get(6)[2] = "104";
			data.get(17)[2] = "104";
			data.get(28)[2] = "104";
		}
		CSVwriter.writeAll(INPUT_DIR + DIR_PREFIX + caseNum +"/"+ fileName, data, false);
	}

	private static void makeFreightConfig(String caseNum){
		File in = new File(freightFile);
		File out = new File(INPUT_DIR + DIR_PREFIX + caseNum +"/freight_config.csv");
	    try {
            copyFile(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	private static void makeDemandConfig(String caseNum){
		File in = new File(demandFile);
		File out = new File(INPUT_DIR + DIR_PREFIX + caseNum + "/demand_config.csv");
	    try {
            copyFile(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	private static void makeFuelpriceConfig(String caseNum){
		String fileName = "fuelprice_config.csv";
		List<String[]> data = CSVReader.forGeneral(fuelpriceFile);
		CSVwriter.writeAll(INPUT_DIR + DIR_PREFIX + caseNum +"/" +fileName, data, false);
		
	}

	public static void copyFile(File in, File out) throws Exception {
        FileInputStream fis  = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }
	    
	}
}
