package DataBase_Cleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

import CommandLineProcessor.InputParameterProcessor;
import DatabaseDownloader.DatabaseEntry;
import DatabaseDownloader.DownSamplerFromIndex;
import Utility.IndexWriter;
import Utility.ProcessExecutor;
import Utility.State;

public class DataBaseCleanerProcessor {
	private double contamaninationThreshold = 0.05;
	private double endogenousThreshold = 0.5;
	String pathToIndex;
	private int threads;
	private ArrayList<String> dustCommand;
	private IndexWriter writer = new IndexWriter();
	private ArrayList<DatabaseEntry> entries;
	private ThreadPoolExecutor executor;
	private String output;
	private String pathToMaltExAssignment;
	private int lengthThreshhold=5000;
	private int adapterThreshold =10;
	private int speciesThreshold =10;
	public DataBaseCleanerProcessor(InputParameterProcessor inProcessor){
		output = inProcessor.getOutDir();
		pathToIndex = inProcessor.getPathToIndex();
		threads = inProcessor.getNumberOfThreads();
		lengthThreshhold=inProcessor.getLengthTreshold();
		ArrayList<String> command  = new ArrayList<String>();
	
		command.add("dustmasker");
		command.add("-in");command.add("placeholder");
	
		command.add("-window");command.add(""+inProcessor.getDustWindow());
		command.add("-level");command.add(""+inProcessor.getDustLevel());
		command.add("-infmt");command.add(inProcessor.getDustFormat());
		command.add("-outfmt");command.add("fasta");
		command.add("-linker"); command.add(""+inProcessor.getDustLinker());
		command.add( "-out" ); command.add( "placeholder" );
		dustCommand = command;
		this.pathToMaltExAssignment = inProcessor.getPathToMaltExAssignment();
	}
	public DataBaseCleanerProcessor(InputParameterProcessor inProcessor, String index){
		output = inProcessor.getOutDir();
		pathToIndex = index;
		threads = inProcessor.getNumberOfThreads();
		lengthThreshhold=inProcessor.getLengthTreshold();
		ArrayList<String> command  = new ArrayList<String>();

		command.add("dustmasker");
		command.add("-in");command.add("placeholder");
	
		command.add("-window");command.add(""+inProcessor.getDustWindow());
		command.add("-level");command.add(""+inProcessor.getDustLevel());
		command.add("-infmt");command.add(inProcessor.getDustFormat());
		command.add("-outfmt");command.add("fasta");
		command.add("-linker"); command.add(""+inProcessor.getDustLinker());
		command.add( "-out" ); command.add( "placeholder" );
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
	    System.out.print("\\r" + bareDone + bareRemain + " " + remainProcent * 10 + "%");
	    if (remain == total) {
	        System.out.print("\n");
	    }
	}
	private void setAdapterContaminatedSequences() {
		System.out.println("Using "+threads+" threads to spot adapters in Reference sequences");
		executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
		ArrayList<DatabaseEntry> presentList = new ArrayList<DatabaseEntry>();
		ArrayList<Future<AdapterSpotter>> futureList = new ArrayList<Future<AdapterSpotter>>();
		int i =1;
		for(DatabaseEntry entry : entries) {
			if(entry.isWantToDownload()) {//check if we even downloaded it
				ConcurrentAdapterSpotter task = new ConcurrentAdapterSpotter(entry);
				Future<AdapterSpotter> future = executor.submit(task);
				futureList.add(future);
				progressPercentage(i,entries.size());
				//System.out.println(i);
					
			}	else {
				presentList.add(entry);
			}		
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
			
		}
		entriesToUpDate.addAll(presentList);
		if(entriesToUpDate.size() == entries.size())
			this.entries = entriesToUpDate;
		else {
			System.err.println("Internal index corrupted! Aborting");
			System.exit(1);
		}
	}
	private void removeAdapterContaminatedSequencesFromFolder() {
		int deleted = 0;
		ArrayList<DatabaseEntry> entriesToUpdate = new ArrayList<DatabaseEntry>();
		for(DatabaseEntry entry : entries) {
			if(entry.getAdapterOccurance()>=adapterThreshold) {
				File file = new File(entry.getOutFile());
				if(file.exists()) {
					file.delete();
					deleted++;
				}
				entry.setWantToDownload(false);
				entry.setWantToKeep(false);
		
			}
			
			entriesToUpdate.add(entry);
		}
		entries = entriesToUpdate;
		System.out.println("AdapterFilter removed: "+deleted+" files");
	}
	
	private void dustDatabase() {
		System.out.println("Dusting database");
		ArrayList<String>commandGunzip = new ArrayList<String>();
		commandGunzip.add("gunzip");
		commandGunzip.add("placeholder");
		ArrayList<String>commandGzip = new ArrayList<String>();
		commandGzip.add("gzip");
		commandGzip.add("placeholder");
		ProcessExecutor executor = new ProcessExecutor();

		ArrayList<DatabaseEntry> currentEntries = new ArrayList<DatabaseEntry> ();
		for(DatabaseEntry entry : entries) {
			if(entry.isWantToKeep() && entry.getOutFile() != entry.getFilteredFile()) {
				String shortOutFileName = entry.getOutFile().substring(0, entry.getOutFile().length() - 3);
				String shortFileName = entry.getFilteredFile().substring(0, entry.getFilteredFile().length() - 3);
				dustCommand.set(2, shortOutFileName); //change input to inputfile is index 2
				dustCommand.set(dustCommand.size()-1, shortFileName); //change output to outputfile is index 4
				commandGunzip.set(1,entry.getOutFile());
			
				
				try {
					System.out.println("Run Gunzip for " + commandGunzip.get(0) +" "+commandGunzip.get(1));
					executor.run(commandGunzip);
					ProcessBuilder builder = new ProcessBuilder (dustCommand);
					String line;
						final Process process = builder.start();//get JobID here
					    if(process.isAlive()){
					    	 	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
						    line = null;
						    while ((line = br.readLine()) != null) {
						    	System.out.println(line);
						    }
						    BufferedReader be = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						    while ((line = be.readLine()) != null) {
						    	System.err.println(line);
						    }
						    int status = process.waitFor();
							System.out.println("Dusted file with  with status: " + status);
					    }
					//progressPercentage(i,entries.size());
					//gzip but first check if the previous step even suceeded god damn
					if(new File(shortFileName).exists()){
						System.out.println("Run Gzip and remove");
						commandGzip.set(1, shortFileName);
						executor.run(commandGzip);
						//remove orignal
						if( new File(entry.getFilteredFile()).exists()) {
							new File(shortFileName).delete();
							entry.setOutFile(entry.getFilteredFile());
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			currentEntries.add(entry);
		}
		//entries = currentEntries;
	}	
	private void loadDatabaseIndex(){
		int duplicates=0;
		HashMap<String,DatabaseEntry> indexEntries= new HashMap<String,DatabaseEntry>();
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
			    	if(indexEntries.containsKey(entry.getAsm_name())) {
			    		//System.err.println(line);
			    		duplicates++;
			    	}else {
			    		indexEntries.put(entry.getAsm_name(), entry);
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
		 entries = new ArrayList<DatabaseEntry>();
		 for(String key:indexEntries.keySet())
			 entries.add(indexEntries.get(key));
		 System.out.println(entries.size()+" Entries read from index\n");
		 System.err.println(duplicates+" duplicates in Index have been removed\n");
	}
	private void downsample() {
		DownSamplerFromIndex downSampler = new DownSamplerFromIndex(speciesThreshold, entries);
		downSampler.process();
		ArrayList<DatabaseEntry> entriesToRemove = downSampler.getEntriesToRemvoe();
		//Here we update our DB Index
		if(entries.size() == entriesToRemove.size())
			this.entries = entriesToRemove;
		else{
			System.err.println("Index File corrupted! Aborting");
			System.exit(1);
		}
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
	}
	private void updateReferencesIndexWithPathInformation() {
		AddPathPercentagesToIndex addPath = new  AddPathPercentagesToIndex(entries, pathToMaltExAssignment);
		addPath.process();
		addPath.getUpdatedDatabaseEntries();
		ArrayList<DatabaseEntry> entriesToUpdate = addPath.getUpdatedDatabaseEntries();
		if(entries.size() == entriesToUpdate.size())
			this.entries = entriesToUpdate;
		else{
			System.err.println("Internal index corrupted! Aborting");
			System.exit(1);
		}
	}
	private void removeCompromisedReferences() {
		int deleted = 0;
		removeAdapterContaminatedSequencesFromFolder();
		ArrayList<DatabaseEntry> currentEntries = new ArrayList<DatabaseEntry>();
		for(DatabaseEntry entry : entries) {
			if(entry.getOnPathPercentageRelaxed() <= endogenousThreshold && entry.getOnPathPercentageRelaxed() > -1){//remove references that are non monocladic and have low endogenous DNA and do not have unassigned_reads as first
				if( entry.isMonoCladic()==false) {
					if(!entry.getoffPathReferences().split(";")[0].contains("unassigned_reads")) {
							if(new File(entry.getOutFile()).exists())
								new File(entry.getOutFile()).delete();
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
							//entry.setOutFile("");
							deleted++;
					}
				}	
			}
			currentEntries.add(entry);
		}
		if(entries.size() == currentEntries.size())
			entries = currentEntries;
		else {
			System.err.println("Index Corrupted");
			System.exit(1);
		}
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
		System.out.println("CrossContamination Filter removed: "+ deleted + " files")
;	}
	
	public void contigLengthFiltering() {
		loadDatabaseIndex();
		System.out.println("Creating backup database index");
		writer.setOutput(new File(pathToIndex).getParent()+"/");
		writer.initializeDatabaseBackupIndex();
		writer.writeDatabaseBackupIndex(entries);
		executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
		ArrayList<Future<ReferenceLengthFilter>> futureList = new ArrayList<Future<ReferenceLengthFilter>>();
		ArrayList<DatabaseEntry> entriesToUpdate = new ArrayList<DatabaseEntry>();

		for(DatabaseEntry entry : entries) {
			if(entry.isWantToDownload()) {
				if(entry.getAssembly_level() != State.COMPLETE) {
					//System.out.println("here");
					if(entry.getTotalContigs() == 0) {
						ConcurrentReferenceLengthFilter task = new ConcurrentReferenceLengthFilter(entry,lengthThreshhold);
						Future<ReferenceLengthFilter> future = executor.submit(task);
						futureList.add(future);
					}else if(entry.getTotalContigs()>0) {//Do not rerun this
						entriesToUpdate.add(entry);//You would not expect that
					
					}
				}else if(entry.getAssembly_level() == State.COMPLETE){
						entriesToUpdate.add(entry);
				}
			}else {
				entriesToUpdate.add(entry);
			}
		}
		executor.shutdown();
		for(Future<ReferenceLengthFilter> future : futureList) {
			try {
				ReferenceLengthFilter filter = future.get();
				if(filter.getResult()&&filter.getEntry().getKeptContigs()>0) {//only if at leat one contig is kept do we keep the entry
					entriesToUpdate.add(filter.getEntry());
				}else if (!filter.getResult()&&filter.getEntry().getKeptContigs()==0){
					File file = new File(filter.getEntry().getOutFile());// if the file exists please delete to avoid problems downstream
					if(file.exists()) {
						file.delete();
					}
					DatabaseEntry entry = filter.getEntry();
					entry.setWantToKeep(false);
					entry.setWantToDownload(false);
					entriesToUpdate.add(filter.getEntry());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(entriesToUpdate.size()==entries.size()) {// if er get more here than something went wrong
			writer.setOutput(new File(pathToIndex).getParent()+"/");
			this.entries = entriesToUpdate;
			System.out.println("Updating Index with Reference Information");
			writer.initializeDatabaseIndex();
			writer.writeDatabaseIndex(entries);
			System.out.println("Wrote "+entries.size()+ " entries to Index");
		}else {
			System.err.println("Internal index seems to be corrupted!Aborting!");
			System.exit(1);
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
		removeAdapterContaminatedSequencesFromFolder();
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
	}
	
	public void cleanCompromisedSequencesDatabase() {
		loadDatabaseIndex();
		System.out.println("Writing backup database index");
		//writer.initializeDatabaseBackupIndex();
		//writer.writeDatabaseBackupIndex(entries);
		System.out.println("Update database index");
		updateReferencesIndexWithPathInformation();
		writer.setOutput(new File(pathToIndex).getParent()+"/");
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
		System.out.println(entries.size()+" written to Index "+ writer.geIndex());
		System.out.println("Remove contaminated Reference Sequences");
		removeCompromisedReferences();
		writer.writeDatabaseIndex(entries);
		System.out.println("DownSample");
		downsample();
		dustDatabase();
		writer.initializeDatabaseIndex();
		writer.writeDatabaseIndex(entries);
	}
	
}
