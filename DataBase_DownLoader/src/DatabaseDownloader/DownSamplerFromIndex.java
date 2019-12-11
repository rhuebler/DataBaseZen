package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import Utility.DatabaseEntryComparator;

public class DownSamplerFromIndex {
	taxIDGetter taxGetter = new taxIDGetter();
	ArrayList<DatabaseEntry>  entriesToRemove = new ArrayList<DatabaseEntry>();
	DatabaseEntryComparator comp = new  DatabaseEntryComparator();
	public ArrayList<DatabaseEntry> getEntriesToRemvoe(){
		return entriesToRemove;
	}
//	public void processPriorToDownload(String pathToIndex) {
//		taxGetter.processNCBIZipFile();;
//		HashMap<Integer,HashMap<Integer,DatabaseEntry>> entries = loadDatabaseIndex(pathToIndex);
//		for(Integer key: entries.keySet()) {
//			if(entries.get(key).size()>=20) {
//				ArrayList<DatabaseEntry> entriesToDownSample = new ArrayList<DatabaseEntry>();
//				entriesToDownSample.addAll(entries.get(key).values());
//				entriesToDownSample.sort(comp);	
//				entriesToDownSample.subList(0, 20);
//				//
//				entriesToRemove.addAll(entriesToDownSample.subList(25, entriesToDownSample.size()-1));	
//				System.out.println(taxGetter.getNcbiIdToNameMap().get(key)+"\t"+entries.get(key).size());
//			}
//			
////			for(DatabaseEntry e: entries.get(key))
////    			System.out.println(e.getIndexLine());
//		}
//	}
 	public void process(String pathToIndex) {
		taxGetter.processNCBIZipFile();;
		HashMap<Integer,HashMap<Integer,DatabaseEntry>> entries = loadDatabaseIndex(pathToIndex);
		for(Integer key: entries.keySet()) {
			if(entries.get(key).size()>=100) {
				ArrayList<DatabaseEntry> entriesToDownSample = new ArrayList<DatabaseEntry>();
				entriesToDownSample.addAll(entries.get(key).values());
				entriesToDownSample.sort(comp);	
				entriesToRemove.addAll(entriesToDownSample.subList(25, entriesToDownSample.size()-1));	
				System.out.println(taxGetter.getNcbiIdToNameMap().get(key)+"\t"+entries.get(key).size());
			}
			
//			for(DatabaseEntry e: entries.get(key))
//    			System.out.println(e.getIndexLine());
		}
	}
	private HashMap<Integer,HashMap<Integer,DatabaseEntry>> loadDatabaseIndex(String pathToIndex){
		HashMap<Integer,HashMap<Integer,DatabaseEntry>> indexEntries = new HashMap<Integer,HashMap<Integer,DatabaseEntry>>();
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
			    		HashMap<Integer,DatabaseEntry> underSpecies  = indexEntries.get(entry.getSpeciesTaxID());
//			    		if(underSpecies.containsKey(entry.getCode()))
//			    			System.out.println(line);
			    		underSpecies.put(entry.getCode(),entry);
			    		indexEntries.replace(entry.getSpeciesTaxID(), underSpecies);		    		
			    	}else {
			    		HashMap<Integer,DatabaseEntry> underSpecies  = new HashMap<Integer,DatabaseEntry>();
			    		underSpecies.put(entry.getCode(),entry);
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
