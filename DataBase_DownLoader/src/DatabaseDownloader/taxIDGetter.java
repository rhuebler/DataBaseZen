package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class taxIDGetter {
	 HashMap<String,Integer> ncbiNameToId;
	 HashMap<Integer,String> ncbiIdToName;
	String location = "https://raw.githubusercontent.com/danielhuson/megan-ce/master/resources/files/ncbi.map";
	public HashMap<String,Integer>  getNcbiNameToIdMap(){
		return this.ncbiNameToId;
	}
	
	public HashMap<Integer,String>  getNcbiIdToNameMap(){
		return this.ncbiIdToName;
	}
	
	public void  setNcbiNameToIdMap(HashMap<String,Integer> map){
		this.ncbiNameToId = map;
	}
	
	private void  setNcbiIdToNameMap(HashMap<Integer,String> map){
		this.ncbiIdToName = map;
	}
	public void process() {
		try{
			
			HashMap<String,Integer> ncbiNameMap = new HashMap<String,Integer>();
			HashMap<Integer, String> ncbiIDMap = new HashMap<Integer, String>();
			URLConnection conn = new URL(location).openConnection();
			 conn.setConnectTimeout(90*1000);
			 conn.setReadTimeout(90*1000);
			   try (InputStream in = conn.getInputStream()) {
				   InputStreamReader reader = new InputStreamReader(in);
				   BufferedReader buffered = new BufferedReader(reader);
				   String line;
				   while((line = buffered.readLine()) != null) {
						String[] frags = line.split("\\t");
						ncbiNameMap.put(frags[1], Integer.parseInt(frags[0]));
						ncbiIDMap.put(Integer.parseInt(frags[0]), frags[1]);
						// do something with line
			       }
				 buffered.close();
				setNcbiNameToIdMap(ncbiNameMap);
				setNcbiIdToNameMap(ncbiIDMap);
			   }catch(Exception e) {
				   e.printStackTrace();
			    }
		}catch(IOException io) {
			io.printStackTrace();
		}
	}
	public void processNCBIZipFile() {
		HashMap<Integer,String> nameToID = new HashMap<Integer,String>();
        HashMap<Integer,ArrayList<String>> IDsToNames = new HashMap<Integer,ArrayList<String>>();
        try{
			byte[] array = null;
			URLConnection conn = new URL(location).openConnection();
			 conn.setConnectTimeout(90*1000);
			 conn.setReadTimeout(90*1000);
			   try (InputStream in = conn.getInputStream()) {
				   ZipInputStream zipStream = new ZipInputStream(in);
				   ZipEntry zipEntry = zipStream.getNextEntry();
			       while (zipEntry != null) {
			    	   if(zipEntry.getName().equals("names.dmp")) {
			    		   byte[] btoRead = new byte[1024];
				    	   ByteArrayOutputStream bout = new ByteArrayOutputStream();; //<- I don't want this!
				            int len = 0;
				            while ((len = zipStream.read(btoRead)) != -1) {
				                bout.write(btoRead, 0, len);
				            }
				            bout.close();
				            array =  bout.toByteArray();
			    	   }
			    	
			            zipEntry = zipStream.getNextEntry();
			       }
				   zipStream.close();
				  
				 String lines = new String(array); 
				 
				 for(String line:lines.split("\\n")) {
					String[] parts = line.split("\\t");
					//System.out.println(parts[0]+"\t"+parts[2]);
					if(!IDsToNames.containsKey(Integer.parseInt(parts[0]))) {
						ArrayList<String> entries = new ArrayList<String>();
						entries.add(parts[2]);
						IDsToNames.put(Integer.parseInt(parts[0]), entries);
					}	
					else {
						ArrayList<String> entries = IDsToNames.get(Integer.parseInt(parts[0]));
						entries.add(parts[2]);
						IDsToNames.replace(Integer.parseInt(parts[0]),entries);
					}
				 }
				 
				for(int ID : IDsToNames.keySet()) {
					nameToID.put(ID, IDsToNames.get(ID).get(0));
				}
				ncbiIdToName = nameToID;
			   }catch(Exception e) {
				   e.printStackTrace();
			    }
		}catch(IOException io) {
			io.printStackTrace();
		}
	}	
	
}
