package simulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Fleet;
import model.Market;
import model.Port;
import model.PortNetwork;
import model.Ship;

public class SimpleSimulation extends Simulation{
	
	private final String OUTPUT_DIR = "./data/";
	private final String OUTPUT_FILE = "result.xlsx";

	public SimpleSimulation(int endTime) {
		super(endTime);
	}

	@Override
	public void save(int now) {
		//TO-DO
		List<String> outputList = new ArrayList<String>();
		List<Ship> ships = Fleet.getShips();
		for(Ship ship : ships){
			outputList.add(String.valueOf(ship.getAmountOfCargo()));
			outputList.add(String.valueOf(ship.getAmountOfFuel()));
			outputList.add(String.valueOf(ship.getRemainingDistance()));
			outputList.add(String.valueOf(ship.getCargoType()));
			outputList.add(ship.getShipStatus().name());
		}
		//this.portNetwork.save(now);
		double freight = Market.getFreight().get(0).getPrice();
		outputList.add(String.valueOf(freight));
		double fuelPrice = Market.getFuels().get(0).getPrice();
		outputList.add(String.valueOf(fuelPrice));

		try{
			Workbook wb = null;
			Sheet sheet = null;
			if(new File(OUTPUT_DIR+OUTPUT_FILE).exists()){
				FileInputStream filein = new FileInputStream(OUTPUT_DIR + OUTPUT_FILE);
				wb = WorkbookFactory.create(filein);
				String safeName = WorkbookUtil.createSafeSheetName("reseult");
	            sheet = wb.getSheet(safeName);
				filein.close();
			}else{
				wb = new XSSFWorkbook();
				String safeName = WorkbookUtil.createSafeSheetName("reseult");
				sheet = wb.createSheet(safeName);
			}
			
			
            
            //CreationHelper createHelper = wb.getCreationHelper();
            
            //Rows(行にあたる)を作る。Rowsは0始まり。
            Row row = sheet.createRow(now);
            //cell(列にあたる)を作って、そこに値を入れる。
            int i = 0;
            for (String elem : outputList){
            	row.createCell(i).setCellValue(elem);
            	i++;
            }
            FileOutputStream fileOut = new FileOutputStream(OUTPUT_DIR + OUTPUT_FILE);
            wb.write(fileOut);
          
			fileOut.close();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			
			
		}
		
		System.out.println("Now: " + now + " Saved!");
		
	}

}
