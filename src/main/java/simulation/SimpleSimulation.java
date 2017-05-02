package simulation;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Fleet;
import model.Market;
import model.Port;
import model.PortNetwork;
import model.Ship;

public class SimpleSimulation extends Simulation{
	
	private final String OUTPUT_DIR = "./data/";

	public SimpleSimulation(int endTime) {
		super(endTime);
	}

	@Override
	public void save(int now) {
		//TO-DO
		List<Ship> ships = Fleet.getShips();
		for(Ship ship : ships){
			ship.getAmountOfCargo();
			ship.getAmountOfFuel();
			ship.getRemainingDistance();
			ship.getCargoType();
			//ship.getShipStatus().name();
		}
		//this.portNetwork.save(now);
		double freight = Market.getFreight().get(0).getPrice();
		double fuelPrice = Market.getFuels().get(0).getPrice();
		
		try{
			Workbook wb = new XSSFWorkbook();
			FileOutputStream fileOut = new FileOutputStream(OUTPUT_DIR + "result.xlsx");
			String safeName = WorkbookUtil.createSafeSheetName("['aaa's test*?]");
            Sheet sheet1 = wb.createSheet(safeName);
            
            CreationHelper createHelper = wb.getCreationHelper();
            
            //Rows(行にあたる)を作る。Rowsは0始まり。
            Row row = sheet1.createRow((short)0);
            //cell(列にあたる)を作って、そこに値を入れる。
            Cell cell = row.createCell(0);
            cell.setCellValue(1);
 
            row.createCell(1).setCellValue(1.2);
            row.createCell(2).setCellValue(
                 createHelper.createRichTextString("sample string"));
            row.createCell(3).setCellValue(true);
             
            wb.write(fileOut);
            fileOut.close();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			
		}
		
		System.out.println("Now: " + now + " Saved!");
		
	}

}
