package DataBase_Cleaner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import DatabaseDownloader.DatabaseEntry;

public class AdapterSpotter {
	 private ArrayList<String> adapters = new  ArrayList<String>();
	 private boolean clean = true;
	 private DatabaseEntry entry; 
	 public AdapterSpotter() {
		 ArrayList<String> adapters = new  ArrayList<String>();
			adapters.add("AGATCGGAAGAG");
			adapters.add("ATGGAATTCTCGG");
			adapters.add("AGATCGTCGGACT");
			adapters.add("ACTGTCTCTTATA");
			adapters.add("ACGCCTTGGCCGT");
			this.adapters=adapters;
	 }
	public boolean isClean() {
		return this.clean;
	}
	public  DatabaseEntry getEntry(){
		return entry;
	}
	public void process(DatabaseEntry databaseEntry) {
		this.entry =databaseEntry;
		boolean clean = true;
		String sequence="";
		 try (InputStream in = new FileInputStream(databaseEntry.getOutFile())) {
			   InputStream gzipStream = new GZIPInputStream(in);
			   Reader decoder = new InputStreamReader(gzipStream);
			   BufferedReader buffered = new BufferedReader(decoder);
			   String line;
			   while((line = buffered.readLine())!=null) {
				   if(!line.startsWith(">")) {
					  sequence += line;
				   } 
			   } 
			   buffered.close();
			   decoder.close();
			   gzipStream.close();  
		   }catch(Exception e) {
			e.printStackTrace();
		    }
		 for(String adapter:adapters) {
			 if(sequence.contains(adapter)) {
				 clean = false;
			 }
		 }
		this.clean = clean;
	}
}
