package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.net.ftp.FTPClient;

import CommandLineProcessor.InputParameterProcessor;
import Utility.DatabaseEntryComparatorPrior;
import Utility.IndexWriter;
import Utility.Phylum;
import Utility.ReferenceGenome;
import Utility.State;
/**
 * iS used to Download and update reference databses 
 * @author huebler
 *
 */
public class DatabaseProcessor {

	
	private IndexGetter getter;
	private Phylum phylum;
	private State sequenceState;
	private ArrayList<String> taxNames;
	private ReferenceGenome reference;
	private String output="";
	private ArrayList<DatabaseEntry> references;
	private ArrayList<DatabaseEntry> entries;
	private boolean cleanDatabase;
	private boolean keywordFiltering = true;
	private String pathToIndex="";
	private int length;
	private int threads;
	private IndexWriter writer = new IndexWriter();
	private boolean contaminatedRemoval = false;
	private ArrayList<String> humanContaminatedAssemblies =  new  ArrayList<String>();
	private InputParameterProcessor inPro;

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
	
	
	public String getIndex() {
		return this.pathToIndex;
	}
	public  ArrayList<DatabaseEntry> getReferences() {
		return references;
	}
	public DatabaseProcessor(InputParameterProcessor inProcessor) {
		if(inProcessor.getTaxonNames() != null && inProcessor.getTaxonNames().size()>0) {
			taxNames = inProcessor.getTaxonNames();
		}	
		if(inProcessor.getPhylum() != null) {
			phylum = inProcessor.getPhylum();
		}else{
			phylum=Phylum.FULLNT;
		}
		System.out.println(inProcessor.getSequenceState());
		if(inProcessor.getSequenceState() != null) {
			sequenceState = inProcessor.getSequenceState();
		}else {
			sequenceState = State.COMPLETE;
		}
		output = inProcessor.getOutDir();
		new File(output).mkdirs();
		if(!output.endsWith("/"))
			output+="/";
		if(inProcessor.getTaxonNames() == null && inProcessor.getPhylum() == null) {
			System.err.println("Provide either Phylum or Taxon List. Shutting Down!");
			System.exit(1);
		}
		threads = inProcessor.getNumberOfThreads();
		reference = inProcessor.getRepresentativeGenomes();
		cleanDatabase = inProcessor.iscleanDB();
		
		pathToIndex = inProcessor.getPathToIndex();
		length = inProcessor.getLengthTreshold();
		keywordFiltering = inProcessor.isKeywordRemoval();
		contaminatedRemoval = inProcessor.isRemoveHumanContaminated();
		if(contaminatedRemoval) {
			if(inProcessor.getAssembliesToIgnore().size()==0) {
				ReadContaminationXLSX readContamination = new ReadContaminationXLSX(inProcessor.getPathToXLSXFiles());
				readContamination.process();
				humanContaminatedAssemblies = readContamination.getAssemblyIDs();
			}else {
				humanContaminatedAssemblies =inProcessor.getAssembliesToIgnore();
			}
		}
		//Prior screen value
		inPro = inProcessor;
	}
	
	public void process() {
		String fileName="";
		String keyword="";
		//which index do we need
		switch(phylum){
			case FULLNT:{
				output += "fullNT/";
				fileName = "ftp://ftp.ncbi.nlm.nih.gov/genomes/refseq/assembly_summary_refseq.txt";
				break;
			}
			case EUKARYOTES:{
				output += "eukaryotes/";
				fileName = "ftp://ftp.ncbi.nlm.nih.gov/genomes/GENOME_REPORTS/eukaryotes.txt";
				break;
			}
			case VIRAL:{
				output += "viral/";
				fileName = "ftp://ftp.ncbi.nlm.nih.gov/genomes/refseq/viral/assembly_summary.txt";
				break;
			}
			case BACTERIA:{
				output += "bacteria/";
				fileName = "ftp://ftp.ncbi.nlm.nih.gov/genomes/refseq/bacteria/assembly_summary.txt";
				break;
			}
			
		}
		
		new File(output).mkdirs();
		
		switch(sequenceState) {
			case ALL:
				keyword="all";
				break;
			case CONTIG:
				keyword="Contig";
				break;
			case SCAFFOLD:
				keyword="Scaffold";
				break;
			case COMPLETE:
				keyword="Complete";
				break;
			case CHROMOSOME:
				keyword="Chromosome";
				break;
		}
		String rep ="";
		switch(reference){
			case DISABLE:
				rep = "disable";
				break;
			case REFERENCE_GENOME:
				rep = "reference genome";
				break;
			case REPRESENTATIVE_GENOME:
				rep = "representative genome";
				break;
			case NA:
				rep="na";
				break;
			case STRICT:
				rep="strict";
				break;	
		}
		if(taxNames!=null && taxNames.size()>0) {
			output += "list/";
			new File(output).mkdirs();
			getter = new IndexGetter(fileName, taxNames, keyword, output, rep,keywordFiltering );
		}else {
			getter = new IndexGetter(fileName, keyword, output, rep,keywordFiltering );
		}
		System.out.println("Reading NCBI Index file");
	
		getter.process();
		if(contaminatedRemoval) {
			getter.removeHumanContaminatedAssemblies(humanContaminatedAssemblies);
		}
		entries = getter.getDatabaseEntries();
		System.out.println("Downloaded NCBI Index");
		
		PreScreenerFTP ftpScreener = new PreScreenerFTP(inPro);
		ftpScreener.preScreenEntriesFTP(getEntries());
		if(ftpScreener.getFailedEntries().size()>0) {
			System.out.println("Downloading failed entries");
			ftpScreener.preScreenEntriesFTP(ftpScreener.getFailedEntries());
		}
	
		this.entries=ftpScreener.getModifiedList();
		System.out.println(ftpScreener.getRemoved()+" entries will be removed");
//		if(ftpScreener.getRemoved()==0) {
//			for(int key:ftpScreener.entriesByAmount.keySet())
//				System.out.println(key+"	"+ftpScreener.entriesByAmount.get(key));
//			
//			System.exit(1);
//		}
		//System.out.println("Original size: "+originalSize+" down sampled Size: "+ getEntries().size());
		if (threads>1) {
			if(threads>4) {
				System.out.println("Maximum number of Threads for downloading set to 4 as higher number results in frequent Server Exceptions");
				threads=4;
			}
			loadDatabaseConcurrently();
		}else {
			loadDatabase();
			}
	}
	public ArrayList<DatabaseEntry> getEntries(){
		return entries;
	}

		
	
	

	
	
	
	
	
	
	
	public void loadDatabase() {
		new File(output).mkdirs();
		EntryLoader loader = new EntryLoader() ;
		loader.setCleanDB(cleanDatabase);
		loader.setLengthThreshold(length);
		writer.setOutput(output);
		writer.initializeDatabaseIndex();
		pathToIndex = writer.getOutput();
		System.out.println("Downloading Entries sequencly");
		int i=0;
		for(DatabaseEntry entry : getEntries()) {
			try{
				if(entry.isWantToDownload()) {
					boolean result = loader.download(entry);
					if(result) {
						writer.appendEntryToDatabaseIndex(entry);
					}
				}else {
					writer.appendEntryToDatabaseIndex(entry);
				}
				progressPercentage(i, getEntries().size());
				i++;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(loader.getFailedReferences().size()>0) {
			ArrayList<DatabaseEntry> list = loader.getFailedReferences();
			loader.clearFailedReferences();
			for(DatabaseEntry entry : list) {
				try{
					boolean result = loader.download(entry);
					if(result)
						writer.appendEntryToDatabaseIndex(entry);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(loader.getFailedReferences().size()>0) {
			writer.writeFailedEntires(loader.getFailedReferences());
		}
//		if(cleanDatabase) {
//			cleanDatabase(); 
//			writer.writeCleanDatabaseIndex(loader.getDownLoadedReferences());
//		}
		references = loader.getDownLoadedReferences();
	}
	
	public void loadDatabaseConcurrently() {
		new File(output).mkdir();
		EntryLoader loader = new EntryLoader() ;
		loader.setCleanDB(cleanDatabase);
		loader.setLengthThreshold(length);
		loader.intiliazeExecutor(threads);
		writer.setOutput(output);
		writer.initializeDatabaseIndex();
		System.out.println("Downloading Entries Concurrently");
		int i=1;
		int k =0;
		for(DatabaseEntry entry : getEntries()) {
			try{
				if(entry.isWantToDownload()) {
					loader.downloadConcurrently(entry);
				}else {
					k++;
					writer.appendEntryToDatabaseIndex(entry);
				}
				//progressPercentage(i, getEntries().size());
				i++;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Appended "+k+" entries");
		loader.destroy();
		loader.getResults();
		
		
		//if we fail to download something we collect those entries and try again at the end
		if(loader.getFailedReferences().size()>0) {
			i=1;
			loader.intiliazeExecutor(threads);
			ArrayList<DatabaseEntry> list = loader.getFailedReferences();
			loader.clearFailedReferences();
			for(DatabaseEntry entry : list) {
				try{
					loader.downloadConcurrently(entry);
					if((((i*100)/list.size()) % 10)==0)
						System.out.println(((i*100)/list.size()) +" done");
					i++;
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			loader.destroy();
			loader.getResults();
			//writer.appendEntriesToDatabaseIndex(loader.getDownLoadedReferences());
		}
		// if we still fail to download them we write an extra index containing them
		if(loader.getFailedReferences().size()>0) {
			writer.writeFailedEntires(loader.getFailedReferences());
		}
		System.out.println("Sucessfull downloaded references were: "+loader.getDownLoadedReferences().size());
		writer.appendEntriesToDatabaseIndex(loader.getDownLoadedReferences());
		references = loader.getDownLoadedReferences();	
	}
	
	public void loadDatabase(ArrayList<DatabaseEntry> entriesToUpdate) {
		new File(output).mkdir();
		EntryLoader loader = new EntryLoader() ;
		loader.setCleanDB(cleanDatabase);
		System.out.println("Downloading Entries sequencely");
		int i=0;
		for(DatabaseEntry entry : entriesToUpdate) {
			try{
				if(entry.isWantToDownload()) {
				boolean result = loader.download(entry);
				if(result)
					writer.appendEntryToDatabaseIndex(entry);
				}else {
					writer.appendEntryToDatabaseIndex(entry);
				}
				progressPercentage(i, getEntries().size());
				i++;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(loader.getFailedReferences().size()>0) {
			ArrayList<DatabaseEntry> list = loader.getFailedReferences();
			loader.clearFailedReferences();
			for(DatabaseEntry entry : list) {
				try{
					boolean result = loader.download(entry);
					if(result)
						writer.appendEntryToDatabaseIndex(entry);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(loader.getFailedReferences().size()>0) {
			writer.writeFailedEntires(loader.getFailedReferences());
		}
		
		
	}
		public void loadDatabaseConcurrently(ArrayList<DatabaseEntry> entriesToUpdate) {
			new File(output).mkdir();
			EntryLoader loader = new EntryLoader() ;
			loader.setCleanDB(cleanDatabase);
			loader.intiliazeExecutor(8);
			System.out.println("Downloading Entries Concurrently");
			int i=1;
			for(DatabaseEntry entry : entriesToUpdate) {
				try{
					if(entry.isWantToDownload())
						loader.downloadConcurrently(entry);
					else {
						writer.appendEntryToDatabaseIndex(entry);
					}
					progressPercentage(i, getEntries().size());
					i++;
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			loader.destroy();
			loader.getResults();
			//writer.appendEntriesToDatabaseIndex(loader.getDownLoadedReferences());
		
		//if we fail to download something we collect those entries and try again at the end
		if(loader.getFailedReferences().size()>0) {
			System.out.println("Trying to redownload failed References");
			i=1;
			loader.intiliazeExecutor(threads);
			ArrayList<DatabaseEntry> list = loader.getFailedReferences();
			loader.clearFailedReferences();
			for(DatabaseEntry entry : list) {
				try{
					loader.downloadConcurrently(entry);
					if((((i*100)/list.size()) % 10)==0)
						System.out.println(((i*100)/list.size()) +" done");
					i++;
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			loader.destroy();
			loader.getResults();
			writer.appendEntriesToDatabaseIndex(loader.getDownLoadedReferences());
		}
		// if we still fail to download them we write an extra index containing them
		if(loader.getFailedReferences().size()>0) {
			writer.writeFailedEntires(loader.getFailedReferences());
		}
		references = loader.getDownLoadedReferences();	
	}
	
	
	

	private ArrayList<DatabaseEntry> loadDatabaseIndex(String pathToIndex){
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
		
		return indexEntries;
	}
	
	public void updateDatabase() { //TODO have to change this
		ArrayList<DatabaseEntry> entriesToUpdate = new ArrayList<DatabaseEntry>();
		ArrayList<DatabaseEntry> currentEntries = new ArrayList<DatabaseEntry>();
		HashMap<Integer, DatabaseEntry> map = new HashMap<Integer, DatabaseEntry>();
		System.out.println("Reading Database index");
		for (DatabaseEntry entry : loadDatabaseIndex(pathToIndex)) {
			map.put(entry.getCode(), entry);
		}
		System.out.println("Checking if Database is up to date");
		
		for(DatabaseEntry entry :getter.getDatabaseEntries()) {
			if(!map.containsKey(entry.getCode())) {
				entriesToUpdate.add(entry);
			}else if(map.containsKey(entry.getCode())){
				currentEntries.add(entry);
				map.remove(entry.getCode());
			}	
		}
		if(!map.isEmpty()) {//do something clever
			System.out.println("Unadressed entries left");
		}
		System.out.println("Recreating database index");
		writer.setOutput(output);
		writer.initializeDatabaseIndex();
		writer.appendEntriesToDatabaseIndex(currentEntries);
		System.out.println(currentEntries.size()+" written to Index");
		if(!entriesToUpdate.isEmpty()) {
			System.out.println("Updating database loading "+entriesToUpdate.size()+" entries");
			PreScreenerFTP ftpScreener = new PreScreenerFTP(inPro);
			ftpScreener.preScreenEntriesFTP(entriesToUpdate);//TODO what to do?
			if(threads>1) {
				loadDatabaseConcurrently(entriesToUpdate);
			}else {
				loadDatabase(entriesToUpdate);
			}
		}else {
			System.out.println("Database up to date under current parameters");
		}
		
	}
}
