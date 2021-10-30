package DatabaseDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DownSamplerFromIndex {
	//private taxIDGetter taxGetter = new taxIDGetter();
	private ArrayList<DatabaseEntry>  entriesToRemove = new ArrayList<DatabaseEntry>();
	private int downSampleThreshold=10;
	private  ArrayList<DatabaseEntry> list;
	public DownSamplerFromIndex(int threshold, ArrayList<DatabaseEntry> list) {
		downSampleThreshold = threshold;
		this.list = list;
	}
	public ArrayList<DatabaseEntry> getEntriesToRemvoe(){
		return entriesToRemove;
	}

 	public void process() {
 		int deleted = 0;
		//taxGetter.processNCBIZipFile();
		HashMap<Integer,HashMap<Integer,DatabaseEntry>> entries = loadDatabaseIndex();
		for(Integer key: entries.keySet()) {
			if(entries.get(key).size()>=downSampleThreshold) {
				int i = 0;
				ArrayList<DatabaseEntry> entriesToDownSample = new ArrayList<DatabaseEntry>();
				entriesToDownSample.addAll(entries.get(key).values());
				Collections.shuffle(entriesToDownSample);
				for(DatabaseEntry entry:entriesToDownSample) {
					if(i>=downSampleThreshold) {
						if(entry.isWantToDownload()&&entry.isWantToKeep()) {
							deleted++;
							if(new File(entry.getOutFile()).exists())
								new File(entry.getOutFile()).delete();
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);	
						}
					}
						
				i++;
				entriesToRemove.add(entry);
				}		
				//System.out.println(taxGetter.getNcbiIdToNameMap().get(key)+"\t"+entries.get(key).size());
			}else {
				entriesToRemove.addAll(entries.get(key).values());
			}
		}
		System.out.println("Removed "+deleted +" Files from database");
	}
	private HashMap<Integer,HashMap<Integer,DatabaseEntry>> loadDatabaseIndex(){ //Sort References by Species ID
		HashMap<Integer,HashMap<Integer,DatabaseEntry>> indexEntries = new HashMap<Integer,HashMap<Integer,DatabaseEntry>>();
		for(DatabaseEntry entry : list) {
			if(entry.isWantToKeep() && entry.isWantToDownload()) {
				if(indexEntries.containsKey(entry.getSpeciesTaxID())) {
				    HashMap<Integer,DatabaseEntry> underSpecies  = indexEntries.get(entry.getSpeciesTaxID());
				    underSpecies.put(entry.getCode(),entry);
				    indexEntries.replace(entry.getSpeciesTaxID(), underSpecies);		    		
				}else {
				    HashMap<Integer,DatabaseEntry> underSpecies  = new HashMap<Integer,DatabaseEntry>();
				    underSpecies.put(entry.getCode(),entry);
				    indexEntries.put(entry.getSpeciesTaxID(), underSpecies);	
				    	}
			    	}else {
			    		entriesToRemove.add(entry);
			    	}
			    }
		return indexEntries;
	}
}
