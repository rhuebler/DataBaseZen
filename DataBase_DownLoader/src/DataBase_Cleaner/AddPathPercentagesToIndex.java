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
			boolean first = true;
			String line = reader.readLine();
			while (line != null) {
				if(!first) {
				String[] parts =line.split("\t");
				String name = parts[0].split("_")[(parts[0].split("_").length-2)];
				ArrayList<Double> percentages= new ArrayList<Double>(2);
				percentages.add(Double.parseDouble( parts[1]));
				percentages.add(Double.parseDouble( parts[2]));
				pathPercentByName.put(name, percentages);
				}else {
					first = false;
				}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String key : entrieByName.keySet()) {
			if(pathPercentByName.containsKey(key)) {
				DatabaseEntry entry = entrieByName.get(key);
				entry.setOnPathPercentage(pathPercentByName.get(key).get(0));
				entry.setOffPathPercentage(pathPercentByName.get(key).get(1));
				entrieByName.replace(key, entry);
			}else {
				DatabaseEntry entry = entrieByName.get(key);
				entry.setOnPathPercentage(-1);
				entry.setOffPathPercentage(-1);
				entrieByName.replace(key, entry);
			}
		}
	}
}
