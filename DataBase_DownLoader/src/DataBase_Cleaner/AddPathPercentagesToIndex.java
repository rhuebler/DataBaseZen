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
	private HashMap<String,ArrayList<Double>> pathPercentByName = new HashMap<String,ArrayList<Double>>();
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
		try (BufferedReader reader = new BufferedReader(new FileReader(pathToMasign))){
			String line = reader.readLine();
			while (line != null) {
				if(line.contains("_")) {
					for(String key : entrieByName.keySet()) {
						if(line.contains(key)) {
							String[] parts =line.split("\t");
							ArrayList<Double> percentages= new ArrayList<Double>(2);
							percentages.add(Double.parseDouble( parts[1]));
							percentages.add(Double.parseDouble( parts[2]));
							percentages.add(Double.parseDouble( parts[3]));
							percentages.add(Double.parseDouble( parts[4]));
							percentages.add(Double.parseDouble( parts[5]));
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
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String key : entrieByName.keySet()) {
			if(pathPercentByName.containsKey(key)) {
				DatabaseEntry entry = entrieByName.get(key);
				entry.setOnPathPercentageStrict(pathPercentByName.get(key).get(0));
				entry.setOffPathPercentageStrict(pathPercentByName.get(key).get(1));
				entry.setOnPathPercentageRelaxed(pathPercentByName.get(key).get(2));
				entry.setOffPathPercentageRelaxed(pathPercentByName.get(key).get(3));
				entry.setTotalReadsTaxon(pathPercentByName.get(key).get(4));
				entrieByName.replace(key, entry);
			}else {
				System.out.println(key);
				DatabaseEntry entry = entrieByName.get(key);
				entry.setOnPathPercentageStrict(-1);
				entry.setOffPathPercentageStrict(-1);
				entry.setOnPathPercentageRelaxed(-1);
				entry.setOffPathPercentageRelaxed(-1);
				entry.setTotalReadsTaxon(-1.0);
				entrieByName.replace(key, entry);
			}
		}
	}
}
