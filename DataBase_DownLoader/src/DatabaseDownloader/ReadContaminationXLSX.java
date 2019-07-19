package DatabaseDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadContaminationXLSX {
	 public ReadContaminationXLSX(String locationXLSX) {
		location =locationXLSX;
	 }
	 private String location="";
	private ArrayList<String> assemblyIDs = new  ArrayList<String>();
	public void process() {
		if(location.length()>0) {
			File excelFile = new File(location);
			try {
				FileInputStream fis = new FileInputStream(excelFile);
				XSSFWorkbook workbook = new XSSFWorkbook(fis);
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIt = sheet.iterator();
				while(rowIt.hasNext()) {
				   Row row = rowIt.next();
				   assemblyIDs.add(row.getCell(5).toString());
				    }
				   workbook.close();
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException io) {
				io.printStackTrace();
			}
		}else {
			System.err.println("No XSLX File present try to download");
//			if() {
//				
//			}else {
//				
//			}
		}
	}
	public  ArrayList<String> getAssemblyIDs() {
		return	assemblyIDs;
	}

}
