package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
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
			String url = "https://raw.githubusercontent.com/rhuebler/DatabaseDownloader/master/AssemblyNames.txt?token=ALG76J3SKTVWHRFJU3ZVUAS6NSKVC";
			try{
				URLConnection conn = new URL(url).openConnection();
				conn.setConnectTimeout(30*1000);
				conn.setReadTimeout(90*1000);
				try (BufferedReader buffIn = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					assemblyIDs.add(buffIn.readLine());
				}catch(IOException io) {
					io.printStackTrace();
				}
			}catch(IOException io) {
				io.printStackTrace();
			}
		}
	}
	public  ArrayList<String> getAssemblyIDs() {
		return	assemblyIDs;
	}

}
/**
are likely to form relatively small contigs.
Consistent with this expectation, we found that 99.7% of
contaminated contigs and scaffolds are shorter than 10 kbp,
99.3% are below5 kbp, and 92% are below 1 kbp (Fig. 2A). The median
We selected 219 high-quality samples for further analysis,
choosing those with at least 20× coverage (see Supplemental
Fig. S4), and found that contaminated scaffolds had significantly
lower coverage than the genome-wide average (Fig. 2B, red box).
*/