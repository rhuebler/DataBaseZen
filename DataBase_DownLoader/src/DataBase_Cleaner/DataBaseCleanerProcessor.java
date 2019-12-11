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
import Utility.State;

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
	private int lengthThreshhold=5000;
	public DataBaseCleanerProcessor(InputParameterProcessor inProcessor){
		output = inProcessor.getOutDir();
		pathToIndex = inProcessor.getPathToIndex();
		threads = inProcessor.getNumberOfThreads();
		this.lengthThreshhold=inProcessor.getLengthTreshold();
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
			//System.out.println(i);
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
		System.out.println(pathToIndex);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(indexFile));
			String line; 
			int number = 0;
			while ((line = br.readLine()) != null) {
				
			    if(number!=0) {
			    	//System.out.println(line);
			    	DatabaseEntry entry = new DatabaseEntry(line.toString());
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
		 System.out.println(entries.size()+" Entries read from index");
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
		addPath.getUpdatedDatabaseEntries();
		this.entries = addPath.getUpdatedDatabaseEntries();
	}
	private void removeCompromisedReferences() {
		ArrayList<DatabaseEntry> entriesToRemove = new ArrayList<DatabaseEntry>();
		for(DatabaseEntry entry: entries) {

			if(entry.getOffPathPercentageRelaxed()>=contamaninationThreshold) {
				entriesToRemove.add(entry);
			}
			if(entry.getOnPathPercentageRelaxed()<endogenousThreshold){
				entriesToRemove.add(entry);
			}
		}
		entries.removeAll(entriesToRemove);
		writer.writeDatabaseIndex(entries);
	}
	
	public void contigLengthFiltering() {
		loadDatabaseIndex();
		System.out.println("Creating backup database index");
		writer.setOutput(new File(pathToIndex).getParent()+"/");
		writer.initializeDatabaseBackupIndex();
		writer.writeDatabaseBackupIndex(entries);
		executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
		ArrayList<Future<ReferenceLengthFilter>> futureList = new ArrayList<Future<ReferenceLengthFilter>>();
		ArrayList<DatabaseEntry> entriesToUpdate = new ArrayList<DatabaseEntry>();
		int i=1;
		for(DatabaseEntry entry : entries) {
			if(entry.getAssembly_level()!=State.COMPLETE) {
				if(entry.getTotalContigs()==0) {
					ConcurrentReferenceLengthFilter task = new ConcurrentReferenceLengthFilter(entry,lengthThreshhold);
					Future<ReferenceLengthFilter> future = executor.submit(task);
					futureList.add(future);
				}else if(entry.getTotalContigs()>0) {//Do not rerun this
					entriesToUpdate.add(entry);//You would not expect that
				
				}
			}else {
					entriesToUpdate.add(entry);
			}
				
			progressPercentage(i,entries.size());
			i++;
		}
		executor.shutdown();
		for(Future<ReferenceLengthFilter> future : futureList) {
			try {
				ReferenceLengthFilter filter = future.get();
				if(filter.getResult()&&filter.getEntry().getKeptContigs()>0) {//only if at leat one contig is kept do we keept the entry
					entriesToUpdate.add(filter.getEntry());
				}else {
					File file = new File(filter.getEntry().getOutFile());// if the file exists please delete to avoid problems downstream
					if(file.exists()) {
						file.delete();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(entriesToUpdate.size()<=entries.size()) {// if er get more here than something went wrong
			writer.setOutput(new File(pathToIndex).getParent()+"/");
			this.entries = entriesToUpdate;
			System.out.println("Updating Index with Reference Information");
			writer.initializeDatabaseIndex();
			writer.writeDatabaseIndex(entries);
			System.out.println("Wrote "+entries.size()+ " entries to Index");
		}
		
	}
	
	public void removeAdapterContaminatedSequences( ) {
		loadDatabaseIndex();
		System.out.println("Creating backup database index");
		writer.setOutput(new File(pathToIndex).getParent()+"/");
		writer.initializeDatabaseBackupIndex();
		writer.writeDatabaseBackupIndex(entries);
		System.out.println(entries.size()+" written to Index");
		System.out.println("Finding Sequences containing Adapters");
		setAdapterContaminatedSequences();
		System.out.println("Updating Index with Adapter Information");
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
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
		System.out.println(entries.size()+" written to Index "+ writer.geIndex());
		/*System.out.println("Remove contaminated reference Sequences");
		removeCompromisedReferences();
		System.out.println("Dust database");
		dustDatabase();
		writer.appendCleanEntriesToDatabaseIndex(entries);
		System.out.println("DownSample");
		downsample();*/
	}
	
}
