package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVReader {
	public static HashMap<String,String> forParam(String filePath){
 
		HashMap<String, String> returnMap = new HashMap<String, String>();
		String[] key = null;
		String[] val = null;
		try {
			File f =  new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			String line;
			int index = 0;
			while ((line =br.readLine()) != null){
				String[] data = line.split(",",0);
				if (index == 0 ) key = data;
				else if (index == 1) val = data;
				index++;
			}
			br.close();
			
		}catch (IOException e){
			System.out.println(e);
		}
		for (int i=0;i<key.length;i++){
			returnMap.put(key[i], val[i]);
		}
		return returnMap;
	}
	
	public static List<String[]> forGeneral(String filePath){
		List<String[]> returnList = new ArrayList<String[]>(); 
		try {
			String path = new File(".").getAbsoluteFile().getParent();
			File f =  new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(f));
			
			String line;

			while ((line =br.readLine()) != null){
				String[] data = line.split(",",0);
				returnList.add(data);
			}
			br.close();
			
		}catch (IOException e){
			System.out.println(e);
		}
		return returnList;
	}
}
