package DataBase_Cleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import CommandLineProcessor.InputParameterProcessor;
import DatabaseDownloader.DatabaseEntry;
import Utility.IndexWriter;
import Utility.ProcessExecutor;

public class DataBaseCleanerProcessor {
	String pathToIndex;
	private int length;
	private int threads;
	private ArrayList<String> dustCommand;
	private IndexWriter writer = new IndexWriter();
	private ArrayList<DatabaseEntry> entries;
	String output;
	public DataBaseCleanerProcessor(InputParameterProcessor inProcessor){
		output = inProcessor.getOutDir();
		pathToIndex = inProcessor.getPathToIndex();
		length  = inProcessor.getLengthTreshold();
		threads = inProcessor.getNumberOfThreads();
		ArrayList<String> command  = new ArrayList<String>();
		command.add("gzcat") ;
		command.add("placeholder");command.add("|");
		command.add("dustmasker");
		command.add("-in");command.add("-");
		command.add("-out");command.add("placeholder");
		command.add("-window");command.add(""+inProcessor.getDustWindow());
		command.add("-level");command.add(""+inProcessor.getDustLevel());
		command.add("-infmt");command.add(inProcessor.getDustFormat());
		command.add("-outfmt");command.add("fasta");
		command.add("-linker"); command.add(""+inProcessor.getDustLinker());
		dustCommand = command;
	}
	private void dustDatabase() {
		ArrayList<String>command = new ArrayList<String>();
		command.add("rm");
		command.add("placeholder");
		ProcessExecutor executor = new ProcessExecutor();
		for(DatabaseEntry entry : entries) {
			dustCommand.set(2, entry.getOutFile()); //change input to inputfile is index 2
			dustCommand.set(4, entry.getFilteredFile()); //change output to outputfile is index 4
			executor.run(dustCommand);
			command.set(1, entry.getFilteredFile());
			executor.run(command);
		}
	}	
	private void loadDatabaseIndex(){
		ArrayList<DatabaseEntry> indexEntries= new ArrayList<DatabaseEntry>();
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
			    	indexEntries.add(entry);
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
		
		 entries=indexEntries;
	}
	
	public void cleanDatabase() {
		loadDatabaseIndex();
		System.out.println("Recreating database index");
		writer.setOutput(output);
		System.out.println(entries.size()+" written to Index");
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
		System.out.println("Cleaning database");			
		dustDatabase();
		writer.appendCleanEntriesToDatabaseIndex(entries);
	}
	
}
