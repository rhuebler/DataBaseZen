package DatabaseDownloader;

import java.io.File;
import java.util.ArrayList;

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
	private ArrayList<String> references;
	private boolean cleanDatabase;
	private ArrayList<String> dustCommand;
	public  ArrayList<String> getReferences() {
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
			case ALL:
				rep = "all";
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
		}
		if(taxNames!=null && taxNames.size()>0) {
			output += "list/";
			new File(output).mkdir();
			getter = new IndexGetter(fileName, taxNames, keyword, output, rep);
		}else {
			getter = new IndexGetter(fileName, keyword, output, rep);
		}
		getter.process();
	}
	public ArrayList<DatabaseEntry> getEntries(){
		return getter.getDatabaseEntries();
	}
	public void loadDatabase() {
		new File(output).mkdir();
		EntryLoader loader = new EntryLoader() ;
		for(DatabaseEntry entry : getEntries()) {
			try{
				loader.download(entry.getOutFile(),entry.getLink());
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
		references.clear();
		ArrayList<String>command=new ArrayList<String>();
		command.add("rm");
		command.add("placeholder");
		ProcessExecutor executor = new ProcessExecutor();
		for(DatabaseEntry entry : getEntries()) {
			dustCommand.set(2, entry.getOutFile()); //change input to inputfile is index 2
			dustCommand.set(4, entry.getFilteredFile()); //change output to outputfile is index 4
			executor.run(dustCommand);
			command.set(1, entry.getFilteredFile());
			executor.run(command);
			references.add(entry.getFilteredFile());
		}
	}	
}
