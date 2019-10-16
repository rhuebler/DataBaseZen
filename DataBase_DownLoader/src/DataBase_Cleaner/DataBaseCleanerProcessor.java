package DataBase_Cleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import CommandLineProcessor.InputParameterProcessor;
import DatabaseDownloader.DatabaseEntry;
import DatabaseDownloader.DownSamplerFromIndex;
import Utility.IndexWriter;
import Utility.ProcessExecutor;

public class DataBaseCleanerProcessor {
	private double contamaninationThreshold = 0.05;
	private double endogenousThreshold = 0.95;
	String pathToIndex;
	private int threads;
	private ArrayList<String> dustCommand;
	private IndexWriter writer = new IndexWriter();
	private ArrayList<DatabaseEntry> entries;
	private ThreadPoolExecutor executor;
	private String output;
	private String pathToMaltExAssignment;
	public DataBaseCleanerProcessor(InputParameterProcessor inProcessor){
		output = inProcessor.getOutDir();
		pathToIndex = inProcessor.getPathToIndex();
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
		this.pathToMaltExAssignment = inProcessor.getPathToMaltExAssignment();
	}
	public void progressPercentage(int remain, int total) {//Adapted from  https://stackoverflow.com/questions/852665/command-line-progress-bar-in-java
	    if (remain > total) {
	        throw new IllegalArgumentException();
	    }
	    int maxBareSize = 10; // 10unit for 100%
	    int remainProcent = ((100 * remain) / total) / maxBareSize;
	    char defaultChar = '-';
	    String icon = "*";
	    String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
	    StringBuilder bareDone = new StringBuilder();
	    bareDone.append("[");
	    for (int i = 0; i < remainProcent; i++) {
	        bareDone.append(icon);
	    }
	    String bareRemain = bare.substring(remainProcent, bare.length());
	    System.out.print("\r" + bareDone + bareRemain + " " + remainProcent * 10 + "%");
	    if (remain == total) {
	        System.out.print("\n");
	    }
	}
	private void setAdapterContaminatedSequences() {
		System.out.println("Using "+threads+" threads to spot adapters in Reference sequences");
		executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
		ArrayList<Future<AdapterSpotter>> futureList = new ArrayList<Future<AdapterSpotter>>();
		int i =1;
		for(DatabaseEntry entry : entries) {
			ConcurrentAdapterSpotter task = new ConcurrentAdapterSpotter(entry);
			Future<AdapterSpotter> future = executor.submit(task);
			futureList.add(future);
			progressPercentage(i,entries.size());
			i++;
		}
		executor.shutdown();
		ArrayList<DatabaseEntry> entriesToUpDate = new ArrayList<DatabaseEntry>();
		for (Future<AdapterSpotter> futureSpotter: futureList) {
			try{
			AdapterSpotter spotter = futureSpotter.get();
			entriesToUpDate.add(spotter.getEntry());
			}catch(InterruptedException iEx) {
				iEx.printStackTrace();
			}catch(ExecutionException eEx) {
				eEx.printStackTrace();
			}
			this.entries = entriesToUpDate;
		}
	}
	private void removeAdapterContaminatedSequencesFromFolder() {
		ArrayList<DatabaseEntry> entriesToRemove = new ArrayList<DatabaseEntry>();
		for(DatabaseEntry entry : entries) {
			if(entry.isContainsAdapter()) {
				entriesToRemove.add(entry);
				File file = new File(entry.getOutFile());
				file.delete();
			}
			entries.removeAll(entriesToRemove);
		}
	}
	
	private void dustDatabase() {
		ArrayList<String>command = new ArrayList<String>();
		command.add("rm");
		command.add("placeholder");
		ProcessExecutor executor = new ProcessExecutor();
		int i=1;
		for(DatabaseEntry entry : entries) {
			dustCommand.set(2, entry.getOutFile()); //change input to inputfile is index 2
			dustCommand.set(4, entry.getFilteredFile()); //change output to outputfile is index 4
			executor.run(dustCommand);
			command.set(1, entry.getFilteredFile());
			executor.run(command);
			progressPercentage(i,entries.size());
			i++;
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
	private void downsample() {
		loadDatabaseIndex();
		DownSamplerFromIndex downSampler = new DownSamplerFromIndex();
		downSampler.process(pathToIndex);
		ArrayList<DatabaseEntry> entriesToRemove = downSampler.getEntriesToRemvoe();
		//Here we update our DB Index
		entries.removeAll(entriesToRemove);
		writer.writeDatabaseIndex(entries);
	}
	private void updateReferencesIndexWithPathInformation() {
		AddPathPercentagesToIndex addPath = new  AddPathPercentagesToIndex(entries, pathToMaltExAssignment);
		addPath.process();
		this.entries = addPath.getUpdatedDatabaseEntries();
	}
	private void removeCompromisedReferences() {
		loadDatabaseIndex();
		ArrayList<DatabaseEntry> entriesToRemove = new ArrayList<DatabaseEntry>();
		for(DatabaseEntry entry: entries) {

			if(entry.getOffPathPercentage()>=contamaninationThreshold) {
				entriesToRemove.add(entry);
			}
			if(entry.getOnPathPercentage()<endogenousThreshold){
				entriesToRemove.add(entry);
			}
		}
		entries.removeAll(entriesToRemove);
		writer.writeDatabaseIndex(entries);
	}
	public void removeAdapterContaminatedSequences( ) {
		loadDatabaseIndex();
		System.out.println("Recreating database index");
		writer.setOutput(new File(pathToIndex).getParent()+"/");
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
		System.out.println(entries.size()+" written to Index");
		System.out.println("Finding Sequences containing Adapters");
		setAdapterContaminatedSequences();
		System.out.println("Updating Index with Adapter Information");
		writer.initializeDatabaseIndex();
		writer.writeDatabaseAdapterIndex(entries);
		System.out.println("Removing Adapter contained sequences");
//		removeAdapterContaminatedSequencesFromFolder();
//		writer.initializeDatabaseIndex();
//		writer.writeDatabaseIndex(entries);
	}
	
	public void cleanCompromisedSequencesDatabase() {
		loadDatabaseIndex();
		System.out.println("Update database index");
		updateReferencesIndexWithPathInformation();
		writer.setOutput(new File(pathToIndex).getParent()+"/");
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
		System.out.println(entries.size()+" written to Index");
		System.out.println("Remove contaminated reference Sequences");
		removeCompromisedReferences();
		System.out.println("Dust database");
		dustDatabase();
		writer.appendCleanEntriesToDatabaseIndex(entries);
		System.out.println("DownSample");
		downsample();
	}
	
}
