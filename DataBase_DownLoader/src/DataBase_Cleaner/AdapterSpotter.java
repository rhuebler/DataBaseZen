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
	//Check if A referencne contains an exact match to an Adaoter... might be insuffcient but a good start
	 private ArrayList<String> adapters = new  ArrayList<String>();
	 private boolean containsAdapter = false;
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
	public boolean isAdapterContained() {
		return this.containsAdapter;
	}
	public  DatabaseEntry getEntry(){
		return entry;
	}
	public void process(DatabaseEntry databaseEntry) {
		this.entry =databaseEntry;
		boolean containsAdapter = false;
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
				 containsAdapter = true;
			 }
		 }
		this.containsAdapter = containsAdapter;
		this.entry.setContainsAdapter(containsAdapter);
	}
	public void processAsParts(DatabaseEntry databaseEntry) {
		this.entry =databaseEntry;
		boolean containsAdapter = false;
		String first="";
		String second="";
		int i=0;
		 try (InputStream in = new FileInputStream(databaseEntry.getOutFile())) {
			   InputStream gzipStream = new GZIPInputStream(in);
			   Reader decoder = new InputStreamReader(gzipStream);
			   BufferedReader buffered = new BufferedReader(decoder);
			   String line;
			   while((line = buffered.readLine())!=null) {
				   if(!line.startsWith(">")) {
					 if(i>0) {
						 second =line;
						 for(String adapter:adapters) {
							 if((first+second).contains(adapter)) {
								 containsAdapter = true;
							 }
						 }
						 first = second;
					 }else {
						 first =line;
					 }
					 i++;
				   } 
			   } 
			   buffered.close();
			   decoder.close();
			   gzipStream.close();  
		   }catch(Exception e) {
			e.printStackTrace();
		    }
		
		this.containsAdapter = containsAdapter;
		this.entry.setContainsAdapter(this.containsAdapter);
	}
}
