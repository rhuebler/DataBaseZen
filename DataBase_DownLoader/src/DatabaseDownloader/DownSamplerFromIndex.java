package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DownSamplerFromIndex {
	public void process(String pathToIndex) {
		HashMap<Integer,ArrayList<DatabaseEntry>> entries = loadDatabaseIndex(pathToIndex);
		for(Integer key: entries.keySet()) {
			System.out.println(key+"\t"+entries.get(key).size());
		}
	}
	private HashMap<Integer,ArrayList<DatabaseEntry>> loadDatabaseIndex(String pathToIndex){
		HashMap<Integer,ArrayList<DatabaseEntry>> indexEntries = new HashMap<Integer,ArrayList<DatabaseEntry>>();
		File indexFile = new File(pathToIndex) ;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(indexFile));
			String line; 
			int number = 0;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
			    if(number!=0) {
			    	DatabaseEntry entry = new DatabaseEntry(line);
			    	if(indexEntries.containsKey(entry.getSpeciesTaxID())) {
			    		ArrayList<DatabaseEntry> underSpecies = indexEntries.get(entry.getSpeciesTaxID());
			    		underSpecies.add(entry);
			    		indexEntries.replace(entry.getSpeciesTaxID(), underSpecies);
			    	}else {
			    		ArrayList<DatabaseEntry> underSpecies = new ArrayList<DatabaseEntry>();
			    		underSpecies.add(entry);
			    		indexEntries.put(entry.getSpeciesTaxID(), underSpecies);
			    	}
			    }
			    number++;
			} 	
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		
		return indexEntries;
	}
}
