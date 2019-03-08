package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import CommandLineProcessor.InputParameterProcessor;
import Utility.Phylum;
import Utility.ProcessExecutor;
import Utility.ReferenceGenome;
import Utility.State;

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
		writeDatabaseIndex();
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
		getter.process();
	}
	public ArrayList<DatabaseEntry> getEntries(){
		return getter.getDatabaseEntries();
	}
	public void loadDatabase() {
		new File(output).mkdir();
		EntryLoader loader = new EntryLoader() ;
		loader.setCleanDB(cleanDatabase);
		for(DatabaseEntry entry : getEntries()) {
			try{
				loader.download(entry);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(cleanDatabase) {
			cleanDatabase(); 
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
	
	private void writeDatabaseIndex() {
		String header = "Name\ttaxID\tspeciesTaxID\tassembly_level\tseq_rel_date\tasm_name\tFileName\tTime\tNumberTotalContigs\tNumberKeptContigs\tNumberRemovedContigs";
		if(references!=null) {
			 try (   FileOutputStream outputStream = new FileOutputStream(output+"index.txt");
		                Writer writer = new OutputStreamWriter(new GZIPOutputStream(outputStream), "UTF-8")) {
				 writer.write(header);
				 for(DatabaseEntry entry : references) {
						 writer.write(entry.getIndexLine());
				 }

		        }catch(IOException io) {
					io.printStackTrace();
				}
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
			    if(number!=0) {
			    String[]parts =	line.split("\t");
			    //(String name, String link, String outDir, String assemblyLevel, int taxID, int speciesTaxID, String seq_rel_date, String asm_name)
			    	DatabaseEntry entry = new DatabaseEntry(parts[0],null,null,parts[3],Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),parts[4],parts[5]);
			    	entry.setTotalContigs(Integer.parseInt(parts[8]));
			    	entry.setKeptContigs(Integer.parseInt(parts[9]));
			    	entry.setFileName(parts[6]);
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
	private void updateDatabase(String pathToIndex) {
		ArrayList<DatabaseEntry> entriesToUpdate = new ArrayList<DatabaseEntry>();
		HashMap<Integer, DatabaseEntry> map = new HashMap<Integer, DatabaseEntry>();
		for (DatabaseEntry entry : loadDatabaseIndex(pathToIndex)) {
			map.put(entry.getCode(), entry);
		}
		for(DatabaseEntry entry :references) {
			if(!map.containsKey(entry.getCode())) {
				entriesToUpdate.add(entry);
			}
			else {
				map.remove(entry.getCode());
			}
			if(!map.isEmpty()) {//do something clever
				
			}
		}
	}
}
