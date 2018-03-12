package exec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Fleet;
import model.Market;
import model.PortNetwork;
import simulation.Simulation;
import simulation.Simulation4Lorena;
import util.CAPEXCalculator;
import util.CAPEXCalculator4Workshop;
import util.CSVReader;
import util.CSVwriter;

public class Exec4ComputerAidedWorkshop {

	private static final String INPUT_DIR = "./data/";
	private static final String demandFile = INPUT_DIR + "demand_config.csv";
	private static final String freightFile = INPUT_DIR + "freight_config.csv";
	private static final String fuelpriceFile = INPUT_DIR + "fuelprice_config.csv";
	private static final String portFile = INPUT_DIR + "port_config.csv";
	private static final String DIR_PREFIX = "case";
	private final static String OUTPUT_OVERALL_RESULT = "result_overall.csv";
	private static String OUTPUT_DIR_ROOT = "./data/";

	private static final String casefile = "case_table4Workshop.csv";
	private static boolean detourFlag;
	////CASE SETTING
	private static final int numOfShips = 20;
	private static final int numOfPortfacilities = 5;
	
	public static void main(String[] args){
		int endTime = 0;
		boolean saveFlag = false;
		if(args.length == 0) {
			endTime = 8760 * 100;
		}else if(args.length == 1){
			System.out.println("Simulation end time: "+ args[0]);
			endTime = Integer.parseInt(args[0]);
		}else if(args.length == 2) {
			System.out.println("Simulation end time: "+ args[0]);
			endTime = Integer.parseInt(args[0]);
			saveFlag = true;
		}
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
			CAPEXCalculator calculator = new CAPEXCalculator4Workshop(numOfHFO,numOfLSFO, numOfLNG, numOfHFOLNG,elem[5], elem[6],elem[7],elem[8],elem[9],elem[10]);
			if(!caseNum.equals("CASE")){
				makeDir(caseNum);
				makeInputFile(caseNum, numOfHFO, numOfLSFO, numOfLNG, numOfHFOLNG, persianGulfSetting, japanSetting, singaporeSetting);
				if(Integer.parseInt(elem[9]) > 0) {
					detourFlag = true;
				}else {
					detourFlag = false;
				}
				execOneShot(INPUT_DIR + DIR_PREFIX+caseNum, endTime, calculator, saveFlag);
			}
		}
		
		System.out.println("-------------All of the Simulation End ----------");
	}
	private static void execOneShot(String inputDirPath, int endTime, CAPEXCalculator calculator, boolean saveFlag){
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
		simulation.setSaveFlag(saveFlag);
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
	
	private static void makeInputFile(String caseNum, String numOfHFO, String numOfLSFO, String numOfLNG, String numOfHFOLNG, String[] persianGulfSetting, String[] japanSetting, String[] singaporeSetting) {
		File dir = new File(INPUT_DIR + DIR_PREFIX + caseNum);
		if(dir.exists()){
			makeFleetConfig(caseNum, numOfHFO, numOfLSFO, numOfLNG, numOfHFOLNG);
			makePortConfig(caseNum, persianGulfSetting, japanSetting, singaporeSetting);
			makeFreightConfig(caseNum);
			makeFuelpriceConfig(caseNum);
			makeDemandConfig(caseNum);
		}
	}

	private static void makeFleetConfig(String caseNum, String numOfHFO, String numOfLSFO, String numOfLNG, String numOFHFOLNG){

		int hfo = Integer.parseInt(numOfHFO);
		int lsfo = Integer.parseInt(numOfLSFO);
		int lng = Integer.parseInt(numOfLNG);
		int hfolng = Integer.parseInt(numOFHFOLNG);

		String fileName = "fleet_config.csv";
		String outputFileName = INPUT_DIR + DIR_PREFIX + caseNum +"/" + fileName;
		List<String[]> outputData = new ArrayList<String[]>();
		List<String[]> templateHFO = new ArrayList<String[]>();
		List<String[]> templateLSFO = new ArrayList<String[]>();
		List<String[]> templateLNG = new ArrayList<String[]>();
		List<String[]> templateHFOLNG = new ArrayList<String[]>();
		String[] row1 = {"shipName"};
		String[] row2 = {"Ship_1"};
		String[] row3 = {"speed","cargoType","cargoAmount", "foc", "fuelCapacity","fuelType","initialType","OperatingCost","Scrubber"};
		String[] row4HFO = {"28","OIL","300000","0.124","5000","HFO","Japan","15000","Yes","No"};
		
		String[] row4LSFO = {"28","OIL","300000","0.11","5000","LSFO","Japan","15000","No", "No"};
		String[] row4LNG = {"28","OIL","300000","0.10","5000","LNG","Japan","15000","No","No"};
		String[] row4HFOLNG = {"28","OIL","300000","0.10","5000","HFOLNG","Japan","15000","Yes","No"};
		
		templateHFO.add(row1);
		templateHFO.add(row2);
		templateHFO.add(row3);
		templateHFO.add(row4HFO);
			
		for(int i = 0; i< hfo; i++){
			outputData.addAll(templateHFO);
		}
		templateLSFO.add(row1);
		templateLSFO.add(row2);
		templateLSFO.add(row3);
		templateLSFO.add(row4LSFO);
		for(int i = 0; i < lsfo; i++){
			outputData.addAll(templateLSFO);
		}
		templateLNG.add(row1);
		templateLNG.add(row2);
		templateLNG.add(row3);
		templateLNG.add(row4LNG);
		for(int i = 0; i < lng; i++){
			outputData.addAll(templateLNG);
		}
		templateHFOLNG.add(row1);
		templateHFOLNG.add(row2);
		templateHFOLNG.add(row3);
		templateHFOLNG.add(row4HFOLNG);
		for(int i = 0; i < hfolng; i++){
			outputData.addAll(templateHFOLNG);
		}
		CSVwriter.writeAll(outputFileName, outputData, false);
	}
	
	private static void makePortConfig(String caseNum, String[] persianGulfSetting, String[] japanSetting, String[] singaporeSetting){
		String fileName = "port_config.csv";
		///JAPAN
		String port1LNG = japanSetting[0];
		String port1LSFO = String.valueOf(numOfPortfacilities);
		//Persian Gulf
		String port2LNG = persianGulfSetting[0];
		String port2LSFO = String.valueOf(numOfPortfacilities);
		//Singapore
		String port3LNG = singaporeSetting[0];
		String port3LSFO = String.valueOf(numOfPortfacilities);
		
		List<String[]> data = CSVReader.forGeneral(portFile);
		data.get(7)[0] = port1LNG;
		data.get(10)[0] = port1LSFO;
		data.get(18)[0] = port2LNG;
		data.get(21)[0] = port2LSFO;
		data.get(29)[0] = port3LNG;
		data.get(32)[0] = port3LSFO;
		data.get(6)[2] = "104";
		data.get(17)[2] = "104";
		data.get(28)[2] = "104";

		if(japanSetting[1].equals("Truck to Ship")) {
			data.get(6)[5] = "TRUE";
			data.get(6)[2] = "10.4";
		}
		if(japanSetting[1].equals("Ship to Ship")) {
			data.get(6)[5] = "TRUE";
		}
		if(persianGulfSetting[1].equals("Truck to Ship")) {
			data.get(17)[5] = "TRUE";
			data.get(17)[2] = "10.4";
		}
		if(persianGulfSetting[1].equals("Ship to Ship")) {
			data.get(17)[5] = "TRUE";
		}
		if(singaporeSetting[1].equals("Truck to Ship")) {
			data.get(28)[5] = "TRUE";
			data.get(28)[2] = "10.4";
		}
		if(singaporeSetting[1].equals("Ship to Ship")) {
			data.get(28)[5] = "TRUE";
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
