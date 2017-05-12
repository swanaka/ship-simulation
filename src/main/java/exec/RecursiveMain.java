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
import util.CSVReader;
import util.CSVwriter;

public class RecursiveMain {
	private static final String INPUT_DIR = "./data/";
	private static final String demandFile = INPUT_DIR + "demand_config.csv";
	private static final String freightFile = INPUT_DIR + "freight_config.csv";
	private static final String fuelpriceFile = INPUT_DIR + "fuelprice_config.csv";
	private static final String portFile = INPUT_DIR + "port_config.csv";
	private static final String DIR_PREFIX = "case";
	
	public static void main(String[] args){
		System.out.println(args[0]);
		int endTime = Integer.parseInt(args[0]);
		String input = "case_table.csv";
		List<String[]> data = CSVReader.forGeneral(INPUT_DIR + input);
		for(String[] elem : data){
			String caseNum = elem[0];
			String hfo = elem[1];
			String lsfo = elem[2];
			String lng = elem[3];
			String port1 = elem[4];
			String port2 = elem[5];
			String port3 = elem[6];
			String trend = elem[7];
			String volatility = elem[8];
			if(!caseNum.equals("CASE")){
				makeDir(caseNum);
				makeInputFile(caseNum,hfo,lsfo,lng,port1,port2,port3, trend, volatility);
				execOneShot(INPUT_DIR + DIR_PREFIX+caseNum, endTime);
			}
		}
	}
	
	private static void makeDir(String caseNum){
		File dir = new File(INPUT_DIR + DIR_PREFIX + caseNum);
		if(dir.exists()){
			;
		}else{
			dir.mkdir();
		}
	}
	
	private static void makeInputFile(String caseNum, String hfo, String lsfo,String lng,String port1,String port2,String port3,String trend, String volatility){
		File dir = new File(INPUT_DIR + DIR_PREFIX + caseNum);
		if(dir.exists()){
			makeFleetConfig(caseNum,hfo,lsfo,lng);
			makePortConfig(caseNum,port1, port2,port3);
			makeFreightConfig(caseNum);
			makeFuelpriceConfig(caseNum,trend,volatility);
			makeDemandConfig(caseNum);
		}
	}
	
	private static void execOneShot(String inputDirPath, int endTime){
		Fleet.reset();
		PortNetwork.reset();
		Market.reset();
		String outputDir = inputDirPath+"/";
		Main.loadInitialPorts(inputDirPath + "/port_config.csv");
		Main.loadMarketInfoFromCSV(inputDirPath + "/freight_config.csv",inputDirPath + "/fuelprice_config.csv",inputDirPath +"/demand_config.csv");
		Main.loadInitialFleetFromCSV(inputDirPath + "/fleet_config.csv");

		Simulation simulation = new SimpleSimulation(endTime,outputDir);
		System.out.println("Simulation Start");
		simulation.execute();
		System.out.println("Simulation End");
	}
	
	private static void makeFleetConfig(String caseNum, String hfo, String lsfo, String lng){
		String fileName = "fleet_config.csv";
		String outputFileName = INPUT_DIR + DIR_PREFIX + caseNum +"/" + fileName;
		List<String[]> outputData = new ArrayList<String[]>();
		List<String[]> templateHFO = new ArrayList<String[]>();
		List<String[]> templateLSFO = new ArrayList<String[]>();
		List<String[]> templateLNG = new ArrayList<String[]>();
		String[] row1 = {"shipName"};
		String[] row2 = {"Ship_1"};
		String[] row3 = {"speed","cargoType","cargoAmount", "foc", "fuelCapacity","fuelType","initialType","OperatingCost"};
		String[] row4HFO = {"28","OIL","300000","1.24","5000","HFO","China","16000"};
		String[] row4LSFO = {"28","OIL","300000","1.24","5000","LSFO","China","16000"};
		String[] row4LNG = {"28","OIL","300000","1.24","5000","LNG","China","16000"};
		templateHFO.add(row1);
		templateHFO.add(row2);
		templateHFO.add(row3);
		templateHFO.add(row4HFO);
		
		
		for(int i = 0; i< Integer.parseInt(hfo); i++){
			outputData.addAll(templateHFO);
		}
		templateLSFO.add(row1);
		templateLSFO.add(row2);
		templateLSFO.add(row3);
		templateLSFO.add(row4LSFO);
		for(int i = 0; i< Integer.parseInt(lsfo); i++){
			outputData.addAll(templateLSFO);
		}
		templateLNG.add(row1);
		templateLNG.add(row2);
		templateLNG.add(row3);
		templateLNG.add(row4LNG);
		for(int i = 0; i< Integer.parseInt(lng); i++){
			outputData.addAll(templateLNG);
		}
		CSVwriter.writeAll(outputFileName, outputData, false);
	}
	
	private static void makePortConfig(String caseNum, String port1, String port2, String port3){
		String fileName = "port_config.csv";
		
		List<String[]> data = CSVReader.forGeneral(portFile);
		data.get(7)[0] = port1;
		data.get(18)[0] = port2;
		data.get(29)[0] = port3;
		CSVwriter.writeAll(INPUT_DIR + DIR_PREFIX + caseNum +"/"+ fileName, data, false);
	}
	
	private static void makeFuelpriceConfig(String caseNum, String trend, String volatility){
		String fileName = "fuelprice_config.csv";
		
		List<String[]> data = CSVReader.forGeneral(fuelpriceFile);
		
		switch(trend){
		case "High":
			data.get(3)[0] = String.valueOf(144);
			data.get(3)[1] = String.valueOf(1.124);
			data.get(3)[2] = String.valueOf(0.888);
			data.get(3)[3] = String.valueOf(0.539);
			break;
		case "Normal":
			data.get(3)[0] = String.valueOf(144);
			data.get(3)[1] = String.valueOf(1.124);
			data.get(3)[2] = String.valueOf(0.888);
			data.get(3)[3] = String.valueOf(0.539);
			break;
		case "Low":
			data.get(3)[0] = String.valueOf(144);
			data.get(3)[1] = String.valueOf(1.124);
			data.get(3)[2] = String.valueOf(0.888);
			data.get(3)[3] = String.valueOf(0.539);
			break;
		}
		CSVwriter.writeAll(INPUT_DIR + DIR_PREFIX + caseNum +"/" +fileName, data, false);
		
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
