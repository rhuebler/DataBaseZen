package DataBase_Cleaner;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import DatabaseDownloader.DatabaseEntry;
/**
 * Designed to filter assemblies by contig length and remove short contigs
 * @author huebler
 *
 */
public class ReferenceLengthFilter {
	boolean result = false;
	private int lengthThreshold;
	private DatabaseEntry entry;
	public ReferenceLengthFilter(DatabaseEntry entry, int length){
		this.entry = entry;
		this.lengthThreshold = length;
	}
	public boolean getResult() {
		return this.result;
	}
	
	public DatabaseEntry getEntry() {
		return this.entry;
	}
	public void process() {   
		String fileName = entry.getOutFile();
		ArrayList<String> output = new  ArrayList<String>();
		try {
			if(new File(fileName).exists()) {
				FileInputStream stream = new FileInputStream(fileName);
				if(stream.available()>0) {
				GZIPInputStream zip = new GZIPInputStream(stream);            
		        InputStreamReader isr = new InputStreamReader(zip, "UTF8");
		        BufferedReader reader = new BufferedReader(isr);
			   String line;
			   int length=0;
			   String header="";
			   int totalNumber = 0;
			   int numberKept = 0;
			   int totalLength =0;
			   int keptLength=0;
			   ArrayList<String> contig = new  ArrayList<String>();
			   while((line = reader.readLine())!=null) {
				   //System.out.println(line);
				   if(line.startsWith(">")) {
					   totalNumber++;
					   if(length >=lengthThreshold) {
						   output.add(header);
						   output.addAll(contig);
						   numberKept++;
						   keptLength+=length;
					   }
					   totalLength+=length;
					   length = 0;
					   contig.clear();
					   header = line;
				   }else {
					   length += line.length();
					   contig.add(line);
				   }
			   } 
			   totalLength+=length;
			   if(length >=lengthThreshold) {
				   output.add(header);
				   output.addAll(contig);
				   numberKept++;
				   keptLength+=length;
			   }
			   isr.close();
			   reader.close();
			   zip.close();
				   entry.setTotalContigs(totalNumber);
				   entry.setKeptContigs(numberKept);
				   double fraction = (double)keptLength/(double)totalLength;
				   //System.out.println(keptLength+"\n"+totalLength +"\n"+fraction);
				   entry.setPercentageKeptContig(fraction);
			}
			}else {
				System.err.println(entry.getOutFile()+" not available");
			}
		   }catch(Exception e) {
			e.printStackTrace();
		    }
		
	if(output.size()>0) {

		 try (   FileOutputStream outputStream = new FileOutputStream(fileName, false);
				 BufferedOutputStream buffered =  new BufferedOutputStream(outputStream);
	             Writer writer = new OutputStreamWriter(new GZIPOutputStream(buffered), "UTF-8")) {
			 	String line ="";
			 	for(String s : output) {
			 		line+=s+"\n";
			 	}
		
	            writer.write(line);
     }catch(IOException io) {
     	System.err.println("FileName "+fileName);
			io.printStackTrace();
		}
	 result = true;
	}
	output.clear();
}	
}
