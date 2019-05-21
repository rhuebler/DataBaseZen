package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import CommandLineProcessor.InputParameterProcessor;
import Utility.IndexWriter;
import Utility.Phylum;
import Utility.ProcessExecutor;
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
	private boolean cleanDatabase;
	private ArrayList<String> dustCommand;
	private boolean keywordFiltering = true;
	private String pathToIndex="";
	private int length;
	private IndexWriter writer = new IndexWriter();
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
		if(!output.endsWith("/"))
			output+="/";
		if(inProcessor.getTaxonNames() == null && inProcessor.getPhylum() == null) {
			System.err.println("Provide either Phylum or Taxon List. Shutting Down!");
			System.exit(1);
		}
		reference = inProcessor.getRepresentativeGenomes();
		cleanDatabase = inProcessor.iscleanDB();
		if(cleanDatabase) {
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
		}
		pathToIndex = inProcessor.getPathToIndex();
		length = inProcessor.getLengthTreshold();
		keywordFiltering = inProcessor.isKeywordRemoval();
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
		new File(output).mkdir();
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
			new File(output).mkdir();
			getter = new IndexGetter(fileName, taxNames, keyword, output, rep,keywordFiltering );
		}else {
			getter = new IndexGetter(fileName, keyword, output, rep,keywordFiltering );
		}
		System.out.println("Reading NCBI Index file");
		getter.process();
	}
	public ArrayList<DatabaseEntry> getEntries(){
		return getter.getDatabaseEntries();
	}
	public void loadDatabase() {
		new File(output).mkdir();
		EntryLoader loader = new EntryLoader() ;
		loader.setCleanDB(cleanDatabase);
		loader.setLengthThreshold(length);
		writer.setOutput(output);
		writer.initializeDatabaseIndex();
		for(DatabaseEntry entry : getEntries()) {
			try{
				boolean result = loader.download(entry);
				if(result)
					writer.appendEntryToDatabaseIndex(entry);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(cleanDatabase) {
			cleanDatabase(); 
			writer.writeDatabaseIndex(loader.getDownLoadedReferences());
		}
		references = loader.getDownLoadedReferences();
	}
	
	public void loadDatabase(ArrayList<DatabaseEntry> entriesToUpdate) {
		new File(output).mkdir();
		EntryLoader loader = new EntryLoader() ;
		loader.setCleanDB(cleanDatabase);
		System.out.println("Downloading Entries");
		for(DatabaseEntry entry : entriesToUpdate) {
			try{
				boolean result = loader.download(entry);
				if(result)
					writer.appendEntryToDatabaseIndex(entry);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		//if we fail to download something we collect those entries and try again at the end
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
		// if we still fail to download them we write an extra index containing them
		if(loader.getFailedReferences().size()>0) {
			writer.writeFailedEntires(loader.getFailedReferences());
		}
		references = loader.getDownLoadedReferences();	
	}
	
	
	private void cleanDatabase() {
		ArrayList<String>command = new ArrayList<String>();
		command.add("rm");
		command.add("placeholder");
		ProcessExecutor executor = new ProcessExecutor();
		for(DatabaseEntry entry : references) {
			dustCommand.set(2, entry.getOutFile()); //change input to inputfile is index 2
			dustCommand.set(4, entry.getFilteredFile()); //change output to outputfile is index 4
			executor.run(dustCommand);
			command.set(1, entry.getFilteredFile());
			executor.run(command);
		}
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
	public void downsample() {
		DownSamplerFromIndex downSampler = new DownSamplerFromIndex();
		downSampler.process(pathToIndex);
		ArrayList<DatabaseEntry> entriesToRemove = downSampler.getEntriesToRemvoe();
		
		
		
		
		
		//Here we update our DB Index
		ArrayList<DatabaseEntry> allEntries = loadDatabaseIndex(pathToIndex);
		allEntries.removeAll(entriesToRemove);
		writer.writeDatabaseIndex(allEntries);
	}
	public void updateDatabase() {
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
		if(!entriesToUpdate.isEmpty()) {
			System.out.println("Updating database");
			if(cleanDatabase) {
				System.out.println("Cleaning database");
				loadDatabase(entriesToUpdate);
				//recreate index after cleaning database
				writer.initializeDatabaseIndex();
				writer.writeDatabaseIndex(currentEntries);
				cleanDatabase();
				writer.appendEntriesToDatabaseIndex(references);
			}else {
				loadDatabase(entriesToUpdate);
			}
		}else {
			System.out.println("Database up to date under current parameters");
		}
		
	}
}
