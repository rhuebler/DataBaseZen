package DataBase_Cleaner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import DatabaseDownloader.DatabaseEntry;

public class AddPathPercentagesToIndex {
	private ArrayList<DatabaseEntry> entries;
	private HashMap<String,DatabaseEntry> entrieByName = new HashMap<String,DatabaseEntry>();
	private HashMap<String,ArrayList<String>> pathPercentByName = new HashMap<String,ArrayList<String>>();
	private String pathToMasign;
	public AddPathPercentagesToIndex (ArrayList<DatabaseEntry> entries, String pathToMasign) {
		this.entries = entries;
		this.pathToMasign = pathToMasign;
		for (DatabaseEntry entry : entries) {
			entrieByName.put(entry.getAsm_name(), entry);
		}
	}
	public ArrayList<DatabaseEntry> getUpdatedDatabaseEntries() {
		return this.entries;
	}
	public void process() {
		int unavailable = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(pathToMasign))){
			String line = reader.readLine();
			while (line != null) {
				if(line.contains("_")) {
					for(String key : entrieByName.keySet()) {
						if(line.contains(key)) {
							String[] parts =line.split("\t");
							ArrayList<String> percentages= new ArrayList<String>();
							if(parts[1].contains("NA")) {
								unavailable++;
								percentages.add("-1.0");
								percentages.add("-1.0");
								percentages.add("-1.0");
								percentages.add("-1.0");
								percentages.add("false");
								percentages.add("-1");
								for(int i=0;i<10;i++) {
									percentages.add("NA;NA");
								}
									
							}else{
								for(int i=1;i<parts.length;i++) {
									percentages.add(parts[i]); //add references here
								}
								
							}
							if(entrieByName.get(key).isWantToDownload()) // if do not want to Download that one, than ther should not be a simulated file indicating something weird with the assembly name
								pathPercentByName.put(key, percentages);
							
						}
					}
					
				//System.out.println(line);
//			
//				String name = parts[0].split("\\_")[(parts[0].split("\\_").length-2)];
//				if(name.matches("v[0-9].0")) {//try to get weird asmIDs here
//					name=parts[0].split("\\_")[(parts[0].split("\\_").length-3)]+"_"+name;
//					//System.out.println(name);
//				}
				
				}
				 line = reader.readLine();
				// read next line
			}
			System.out.println("Unavailable entries in File: "+unavailable);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int notDownloaded=0;
		for(String key : entrieByName.keySet()) {
			if(pathPercentByName.containsKey(key)) {
				DatabaseEntry entry = entrieByName.get(key);
				entry.setOnPathPercentageStrict(Double.parseDouble(pathPercentByName.get(key).get(0)));
				entry.setOffPathPercentageStrict(Double.parseDouble(pathPercentByName.get(key).get(1)));
				entry.setOnPathPercentageRelaxed(Double.parseDouble(pathPercentByName.get(key).get(2)));
				entry.setOffPathPercentageRelaxed(Double.parseDouble(pathPercentByName.get(key).get(3)));
				entry.setMonoCladic(Boolean.parseBoolean(pathPercentByName.get(key).get(4)));
				entry.setTotalReadsTaxon(Integer.parseInt(pathPercentByName.get(key).get(5)));
				entry.setOffPathReferences(pathPercentByName.get(key).subList(6, 16));
				entrieByName.replace(key, entry);
				
			}else {
				 notDownloaded++;
//				if(entrieByName.get(key).isWantToDownload())
//					System.out.println(key+"\t"+entrieByName.get(key).getOutFile());
				DatabaseEntry entry = entrieByName.get(key);
				entry.setOnPathPercentageStrict(-1);
				entry.setOffPathPercentageStrict(-1);
				entry.setOnPathPercentageRelaxed(-1);
				entry.setOffPathPercentageRelaxed(-1);
				entry.setMonoCladic(false);
				entry.setTotalReadsTaxon(-1);
				ArrayList<String> list = new ArrayList<String>();
				for(int i=0;i<10;i++)
					list.add("NA;NA");
				entry.setOffPathReferences(list);
				entrieByName.replace(key, entry);
			}
		}
		System.out.println("Not downoladed: "+notDownloaded);
	}
}
