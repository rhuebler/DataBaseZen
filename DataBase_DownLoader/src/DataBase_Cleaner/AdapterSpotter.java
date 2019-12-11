package DataBase_Cleaner;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import DatabaseDownloader.DatabaseEntry;

public class AdapterSpotter {
	//Check if A referencne contains an exact match to an Adaoter... might be insuffcient but a good start
	 private ArrayList<String> adapters = new  ArrayList<String>();
	 private boolean containsAdapter = false;
	 private DatabaseEntry entry; 
	 private int occurance = 0;
	
	 public AdapterSpotter() {
		 ArrayList<String> adapters = new  ArrayList<String>();
			adapters.add("AGATCGGAAGAG");
			adapters.add("ATGGAATTCTCGG");
			adapters.add("AGATCGTCGGACT");
			adapters.add("ACTGTCTCTTATA");
			adapters.add("ACGCCTTGGCCGT");
			this.adapters=adapters;
	 }
	public int getOccurance() {
		return this.occurance;
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
		
		HashMap<String, String> contigByHeader = 	new HashMap<String, String> ();
		if(new File(databaseEntry.getOutFile()).exists()) {
		 try (InputStream in = new FileInputStream(databaseEntry.getOutFile())) {
			   InputStream gzipStream = new GZIPInputStream(in);
			   Reader decoder = new InputStreamReader(gzipStream);
			   BufferedReader buffered = new BufferedReader(decoder);
			   String line;
			   String sequence="";
			   String header;
			   while((line = buffered.readLine())!=null) {
				   if(!line.startsWith(">")) {
					  sequence += line;
				   } else {
					   header = line;
					   if(sequence.length()>1) {
						   contigByHeader.put(header, sequence);
						   sequence="";
					   }
				   }
			   } 
			   buffered.close();
			   decoder.close();
			   gzipStream.close();  
		   }catch(Exception e) {
			e.printStackTrace();
		    }
		 
		 for(String key : contigByHeader.keySet()) {
			 String sequence = contigByHeader.get(key);
			 for(String adapter:adapters) {
				 if(sequence.contains(adapter)) {
					String [] parts = sequence.split(adapter);
					occurance+= parts.length-1;
					containsAdapter = true;
					String seqAd="";
					adapter.length();
					int i =0;
					for(String s :parts) {
						seqAd+=s;
						if(i<parts.length) {
							int k =1;
							while(k<adapter.length()) {
								seqAd+="N";
								k++;
							}
						}
					i++;
					}
					sequence = seqAd;
				 }
			 }
			 contigByHeader.replace(key, sequence);
		 }
		this.containsAdapter = containsAdapter;
		this.entry.setContainsAdapter(containsAdapter);
		this.entry.setAdapterOccurance(occurance);
		if(containsAdapter) {
			String fileName = entry.getOutFile();
			 try (   FileOutputStream outputStream = new FileOutputStream(fileName, false);
					 BufferedOutputStream buffered =  new BufferedOutputStream(outputStream);
		             Writer writer = new OutputStreamWriter(new GZIPOutputStream(buffered), "UTF-8")) {
				 	String line ="";
				 	for(String key : contigByHeader.keySet()) {
				 		String sequence =  contigByHeader.get(key);
				 		line+=key+"\n";
				 		line+=sequence.replaceAll("(.{80})", "$1\n");
				 	}
			
		            writer.write(line);
	     }catch(IOException io) {
	     	System.err.println("FileName "+fileName);
				io.printStackTrace();
			}
		}
		
		}
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
