package CommandLineProcessor;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import Utility.ExecutionMode;
import Utility.Phylum;
import Utility.ReferenceGenome;
import Utility.SimulateFormat;
import Utility.State;

/**
 * This class is used To Parse Input Parameters for the database to make input more flexible and less error prone
 * uses the console parameters as input and fill up all Parameter slots to control subsequent functions
 * @author huebler
 *
 */
public class InputParameterProcessor {
	/**
	 * @param String[] args all commandline parameters
	 * @throws none thrown all caught
	 * @return return values and parameters to run and control the program
	 */
	// initialize class with standard values;	
	private String outDir;
	private ReferenceGenome reference = ReferenceGenome.DISABLE;
	private ExecutionMode mode;
	private Phylum phylum;
	private State sequenceState;
	private ArrayList<String> referenceFiles= new ArrayList<String>();
	private ArrayList<String> taxNames = new ArrayList<String>();
	
	private double maximumRate= 0.01;
	private int minimumLength = 35;
	private int maximumLength = 75;
	private double transversion = 0.01;
	private double transition = 0.01;
	private int numberOfReads = 1000;
	private SimulateFormat format = SimulateFormat.FASTA;
	private  int threads = 1;
	private boolean cleanDB =false;
	private String pathToIndex = "";
	private boolean keywordRemoval = false;
	
	private String dustFormat = "fasta";
	private int dustLevel = 20;
	private int dustWindow = 64;
	private int dustLinker = 1 ;
	private int lengthTreshold = 5000;
	private boolean ignoreHumanAssemblies =false;
	private String assembliesToIgnoreFile="";
	private ArrayList<String> assembliesToIgnore;
	private String pathToXLSX="";
	private String pathToMaltExAssignment;
	private int cutOffTotalGapLength = 0;
	private Integer priorToScreenValue= 25;
	private int cutOffL50 = 10;
	private int cutOffN50 = 25000;
	private int cutOffScore =100;
	
	private int cutOffTotalLengthAssembly= 0; 
	private int cutOffSpannedGaps= 0; 
	private int cutOffRegionCount= 0; 
	private int cutOffContigCount= 0; 
	private int cutOffUnspannedGaps= 0; 

	private int cutOffMoleculeCount= 0;  
	private int cutOffTopLevelCount= 0;  
	private int cutOffComponentCount= 0;
	private double cutOffOnPathPercentage = 0.0;
	// constructor
	public String getPathToMaltExAssignment() {
		return this.pathToMaltExAssignment;
	}
	public String getPathToXLSXFiles() {
		return pathToXLSX;
	}
	public boolean isRemoveHumanContaminated() {
		return ignoreHumanAssemblies;
	}
	public String getDustFormat() {
		return dustFormat;
	}
	public int getDustLevel(){
		return dustLevel;
	}
	public int getDustWindow() {
		return dustWindow;
	}
	public int getDustLinker() {
		return dustLinker;
	}
	public InputParameterProcessor(String[] params) throws IOException, ParseException{
		process(params);
	}
	public String getPathToIndex() {
		return pathToIndex;
	}
	public boolean iscleanDB() {
		return cleanDB;
	}
	public int getNumberOfThreads() {
		return threads;
	}
	public double getMaximumRate() {
		return maximumRate;
	}
	public int getMinimumLength() {
		return minimumLength;
	}
	public int getMaximumLength() {
		return maximumLength;
	}
	public double getTransversionRate() {
		return transversion;
	}
	public double getTransitionRate() {
		return transition;
	}
	public int getNumberOfReads() {
		return numberOfReads;
	}
	public SimulateFormat getOutputFormat() {
		return format;
	}
	// getters for parameters
	public ReferenceGenome getRepresentativeGenomes() {
		return reference;
	}
	public ArrayList<String> getTaxonNames(){
		return taxNames;
	}
	public State getSequenceState() {
		return sequenceState;
	}
	public ArrayList<String> getReferenceFiles(){
		return referenceFiles;
	}
	public Phylum getPhylum(){
		return phylum;
	}
	public ExecutionMode getExecutionMode() {
		return mode;
	}
	private void readTaxList(File f) throws IOException{
		try {
			 Scanner	in = new Scanner(f.getCanonicalFile());
			 while(in.hasNext()){
				 String line = in.nextLine().trim();
				 if(!line.startsWith("#"))
					 taxNames.add(line);
			 }
			 in.close();
		 	}catch (FileNotFoundException e) {
		 System.err.println("TaxaListFile Not Found");
		 e.printStackTrace();
		}		
	}
	
	public String getOutDir(){
		return this.outDir;
	}
	public String getAllOptions(){
		String input = "All Parameters:\n -r";
		for(String d: referenceFiles)
			input+= " "+d;
		input += "\n-o "+outDir;
		input +="\n--mode	"+mode;
		input +="\n--phylum	"+phylum;
		input +="\n--state	"+sequenceState;
		input+="\n--wgs	"+reference;
		String s = "";
		for(String tax:taxNames) {
			s+=tax+"\t";
		}
		input+="\n--taxonList	"+s;
		input+="\n--maxRate	"+maximumRate;
		input+="\n--minLength	"+minimumLength;
		input+="\n--maxLength	"+maximumLength;
		input+="\n--transversionRate	"+transversion;
		input+="\n--transitionRate	"+transition;
		input+="\n--numReads	"+numberOfReads;
		input+="\n--format	"+format;
		input+="\n--threads	"+threads;
		input+="\n--dustLevel	"+dustLevel;
		input+="\n--dustWindow	"+dustWindow;
		input+="\n--dustLinker	"+dustLinker;
		input+="\n--index	"+pathToIndex;
		input+="\n--length	"+lengthTreshold;
		input+="\n--pathToMaltExAssignment	"+pathToMaltExAssignment;
		if(ignoreHumanAssemblies)
			input+="\n--contaminatedRemoval "+assembliesToIgnoreFile+" ";
		if(keywordRemoval)
			input+="\n--keywordRemoval";
		input+="\n--priorToScreenValue	"+priorToScreenValue;
		input+="\n--cutOffTotalGapLength	"+ cutOffTotalGapLength;
		input+="\n--cutOffL50	"+ cutOffL50;
		input+="\n--cutOffN50	"+ cutOffN50;
		input+="\n--cutOffScore	"+cutOffScore;
		//cite [1] Morgulis A, Gertz EM, S
		return input;
	}
	private void process(String[] parameters) throws IOException, ParseException{	
    	 CommandLine commandLine;
    	 	// Short Flags Are necessary parameters that are necessary for any run
    	 	// here we describe all CLI options
    	   // Option option_Database = Option.builder("r").longOpt("references").argName("String").hasArgs().desc("Specify where to locate directory with references downloaded outside from this program").build();
    	    Option option_Output = Option.builder("o").longOpt("output").argName("String").hasArg().desc("Specify out directory").build();
    	    Option optionMode = Option.builder("m").longOpt("mode").argName("String").hasArg().desc("full, download, create, update").build();
    	    Option optionPhylum = Option.builder("p").longOpt("phylum").argName("String").hasArg().desc("fullNT, bacteria, viral, eukaryotes, adapter_clean, taxonomic_clean").build();
    	    Option optionState = Option.builder("s").longOpt("state").argName("String").hasArg().desc("complete, plasmid, assembly, all").build();
    	    Option optionTaxonList = Option.builder("t").longOpt("taxonlist").argName("String").hasArg().desc("A List with all taxa for which an analysis is desired.\n Taxa List will also be added to download").build();
    	    Option optionRepresentativeGenomes = Option.builder("w").longOpt("wgs").hasArg().argName("String").desc("Accepted values reference_genome, representative_genome, all, strict, na").build();
    	    Option option_Help = Option.builder("h").longOpt("help").optionalArg(true).desc("Print Help").build();
    	    Option optionMaxRate = Option.builder("").longOpt("maxRate").optionalArg(true).desc("Maximum Muatation Rate betwen 0.0 and 1.0").build();
    	    Option optionMinimumLength = Option.builder("").longOpt("minLength").optionalArg(true).desc("Set minimum ReadLength by default 35").build();
    	    Option optionMaximumLength = Option.builder("").longOpt("maxLength").optionalArg(true).desc("Set maximum ReadLength by default 75").build();
    	    Option optionTransversionRate = Option.builder("").longOpt("transversionRate").optionalArg(true).desc("Set transversion rate").build();
    	    Option optionTransRate = Option.builder("").longOpt("transitionRate").optionalArg(true).desc("Set transition Rate").build();
    	    Option optionNumReads = Option.builder("").longOpt("numReads").hasArg().optionalArg(true).desc("Set Number of Reads").build();
    	    Option optionFormat = Option.builder("").longOpt("format").hasArg().optionalArg(true).desc("output either fastq or fasta format").build();
    	    Option optionThreads = Option.builder("").longOpt("threads").hasArg().optionalArg(true).desc("get number of threads for read creation").build();
    	    Option optionDustLevel = Option.builder("").longOpt("dustLevel").hasArg().optionalArg(true).desc("Set Level parameter for DustMasker").build();
    	    Option optionDustWindow = Option.builder("").longOpt("dustWindow").optionalArg(true).desc("Set window size for DustMasker").build();
    	    Option optionDustLinker = Option.builder("").longOpt("dustLinker").hasArg().optionalArg(true).desc("Set Linker Parameter for DustMasker").build();
    	    Option optionPathToIndex = Option.builder("i").longOpt("index").hasArg().optionalArg(true).desc("Set the path to index to update an index").build();
    	    Option optionLengthThreshold = Option.builder("l").longOpt("length").hasArg().optionalArg(true).desc("Set length threshold for assemblies exclude parts of assemblies that are shorter").build();
    	    Option optionKeywordRemoval = Option.builder("k").longOpt("keywordRemoval").optionalArg(true).desc("Key word removal, exclude entries that contain uncultured, co-culture species, synthetic").build();
    	    Option optionIngoreContaminatedAssemblies = Option.builder("c").longOpt("contaminatedRemoval").optionalArg(true).hasArg().desc("Ignore Assembly IDs provided in List with contaminated sequences").build();
    	    Option optionPathToMaltExAssignment = Option.builder().longOpt("pathToMaltExAssignment").optionalArg(true).hasArg().desc("Specify path to MaltExtract read assignment File").build();
    	    Option optionCutOffGapLength = Option.builder().longOpt("cutOffTotalGapLength").optionalArg(true).hasArg().desc("Cutoff value for total gap length").build();
    	    Option optionNumberOfReadsPrior = Option.builder().longOpt("priorToScreenValue").optionalArg(true).hasArg().desc("How many references should be kept at max after pree screening").build();
    	    Option optionCutOffL50 = Option.builder().longOpt("cutOffL50").optionalArg(true).hasArg().desc("Cutoff value for contig L50 value").build();
    	    Option optionCutOffN50 = Option.builder().longOpt("cutOffN50").optionalArg(true).hasArg().desc("Cutoff value for contig N50 value").build();
    	    Option optionCutOffScore = Option.builder().longOpt("cutOffScore").optionalArg(true).hasArg().desc("Cutoff value for prior Score").build();
    	    Option optionCutOffOnPath = Option.builder().longOpt("cutOffOnPathPercentage").optionalArg(true).hasArg().desc("Cutoff value for on Path percentage").build();
    	    
    	    Options options = new Options();
    	    
    	    // add all parameters to the parser
    	    CommandLineParser parser = new DefaultParser();
    	    options.addOption(optionThreads);
    	    options.addOption(optionRepresentativeGenomes);
    	    options.addOption(optionState);
    	    options.addOption(optionTaxonList);
    	    options.addOption(optionPhylum);
    	  //  options.addOption(option_Database);
    	    options.addOption(option_Output);
    	    options.addOption(optionMode);
    	    options.addOption(option_Help);
    	    options.addOption(optionFormat);
    	    options.addOption(optionNumReads);
    	    options.addOption(optionTransRate);
    	    options.addOption(optionTransversionRate);
    	    options.addOption(optionMaximumLength);
    	    options.addOption(optionMinimumLength);
    	    options.addOption(optionMaxRate);
    	    options.addOption(optionDustLinker);
    	    options.addOption(optionDustWindow);
    	    options.addOption(optionDustLevel);
    	    options.addOption(optionPathToIndex);
    	    options.addOption(optionLengthThreshold);
    	    options.addOption(optionKeywordRemoval);
    	    options.addOption(optionIngoreContaminatedAssemblies);
    	    options.addOption(optionPathToMaltExAssignment);
    	    options.addOption(optionCutOffScore);
    	    options.addOption(optionCutOffN50);
    	    options.addOption(optionCutOffL50);
    	    options.addOption(optionNumberOfReadsPrior);
    	    options.addOption(optionCutOffGapLength);
    	    options.addOption(optionCutOffOnPath);
    	    //parse arguments into the comandline parser
    	        commandLine = parser.parse(options, parameters);
 
    	        
    	        if(commandLine.hasOption("h")){////help
    	        	String header = "I still need a usefull-name-program";
    	    	    String footer = "In case you encounter an error drop an email with an useful description to huebler@shh.mpg.de";
    	    	    HelpFormatter formatter = new HelpFormatter();
    	    	    formatter.setWidth(500);
    	    	    formatter.printHelp("I have no name :(", header, options, footer, true);   
    	    	    System.exit(0);
    	        }
    	        //check if mode is set and if allowed values are set
    	        if(commandLine.hasOption('m')){
    	        	 if(Pattern.compile(Pattern.quote("download"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.DOWNLOAD;
    	        	 }else if(Pattern.compile(Pattern.quote("create"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.CREATE;
    	        	 }else if(Pattern.compile(Pattern.quote("update"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.UPDATE;
    	        	 }else if(Pattern.compile(Pattern.quote("taxonomic_clean"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.CLEAN_TAXONOMIC;
    	        		  
    	        	 }else if(Pattern.compile(Pattern.quote("adapter_clean"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.CLEAN_ADAPTERS;
    	        		 
    	        	 }else if(Pattern.compile(Pattern.quote("reference_length"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.CLEAN_REFERENCE;
    	        		 
    	        	 }else {
    	        		 System.err.println("Unknown Mode Shutting down");	
    	        		 System.exit(1);
    	        	 }
    	        }

    	        if (commandLine.hasOption("wgs")) {
    	        	if(Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("wgs")).find()){
    	        		reference =ReferenceGenome.DISABLE;
   	        	 	}else if(Pattern.compile(Pattern.quote("reference_genome"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("wgs")).find()) {
   	        	 		reference = ReferenceGenome.REFERENCE_GENOME;
   	        	 	}else if(Pattern.compile(Pattern.quote("representative_genome"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("wgs")).find()) {
   	        	 		reference = ReferenceGenome.REPRESENTATIVE_GENOME;
   	        	 	}else if(Pattern.compile(Pattern.quote("na"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("wgs")).find()) {
   	        	 		reference = ReferenceGenome.NA;
   	        	 	}else if(Pattern.compile(Pattern.quote("strict"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("wgs")).find()) {
   	        	 		reference = ReferenceGenome.STRICT;
   	        	 	}
    	        }
    	        if (commandLine.hasOption("output"))//set output directorty
    	        {
    	        	try{
    	        		String path = commandLine.getOptionValue("output");
    	        		if(path.contains("\n")||path.contains("$")||path.contains("\'")||path.contains("=")|| path.contains("\"")){ // check if path contains illegal characters
    	        			System.err.println("Illegal Character detected");
    	        			System.exit(1);
    	        		}
    	        		File f = new File(path); // get canonical path
    	        		outDir = f.getCanonicalPath()+"/";
    	        		//if(!new File(outDir).getAbsoluteFile().exists())
    	        			//new File(outDir).mkdir();
    	        		}catch(IOException io){
    	        			System.err.println(io);
    	        		}
    	        }
        		if(commandLine.hasOption("threads")) {
	        	    	this.threads = Integer.parseInt(commandLine.getOptionValue("threads"));
	        	    }
        		if (commandLine.hasOption("maxRate")) {
	        			this.maximumRate = Double.parseDouble(commandLine.getOptionValue("maxRate"));
	        		}
	        		if(commandLine.hasOption("minLength")) {
	        			this.minimumLength = Integer.parseInt(commandLine.getOptionValue("minLength"));
	        		}
	        		if(commandLine.hasOption("maxLength")) {
	        			this.maximumLength = Integer.parseInt(commandLine.getOptionValue("maxLength"));
	        		}
	        		if(commandLine.hasOption("transversionRate")) {
	        			this.transversion = Double.parseDouble(commandLine.getOptionValue("transversionRate"));
	        		}
	        		if(commandLine.hasOption("transitionRate")) {
	        			this.transition = Double.parseDouble(commandLine.getOptionValue("transitionRate"));
	        		}
	        		if(commandLine.hasOption("numReads")) {
	        			this.numberOfReads = Integer.parseInt(commandLine.getOptionValue("numReads"));
	        		}
	        		if(commandLine.hasOption("format")) {
	        			String value = commandLine.getOptionValue("format");
	        			if(value.equals("fa")||value.equals("fasta"))
	        				this.format = SimulateFormat.FASTA;
	        			else if(value.equals("fq")||value.equals("fastq"))
	        				this.format = SimulateFormat.FASTQ;
	        		}
	        		if(commandLine.hasOption("dustLevel")) {
	        			this.dustLevel = Integer.parseInt(commandLine.getOptionValue("dustLevel"));
	        		}
	        		if(commandLine.hasOption("dustWindow")) {
	        			this.dustWindow = Integer.parseInt(commandLine.getOptionValue("dustWindow"));
	        		}
	        		if(commandLine.hasOption("dustLinker")) {
	        			this.dustLinker = Integer.parseInt(commandLine.getOptionValue("dustLinker"));
	        		}
	        		
    	        switch (mode) {
    	        	case BOTH:{
    	        		if (commandLine.hasOption("keywordRemoval")) {
    	        			this.keywordRemoval=true;
    	        		}
    	        		   if (commandLine.hasOption("phylum")) {
    	        			   if(Pattern.compile(Pattern.quote("bacteria"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()){
    	        				   phylum = Phylum.BACTERIA;
    	      	        	 	}else if(Pattern.compile(Pattern.quote("eukaryotes"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
    	      	        		   phylum = Phylum.EUKARYOTES;
    	      	        	 	}else if(Pattern.compile(Pattern.quote("fullnt"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
    	      	        	 		phylum = Phylum.FULLNT;
    	      	        	 	}else if(Pattern.compile(Pattern.quote("viral"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
    	      	        	 		phylum = Phylum.VIRAL;
    	      	        	 	}else {
    	      	        	 		System.err.println("unspecified input for phylum");
    	      	        	 	}
    	        		   }
    	        		   if (commandLine.hasOption("taxonlist")) {
    	        			   try{
    	        				   String tax = commandLine.getOptionValue("taxonlist");
    	        				  
    	        				   File f = new File(tax);
    	        				   if(f.getCanonicalFile().exists()){
    	    	     					readTaxList(f);
    	        				   }
    	        			   }catch (IOException e) {
    							System.err.println("IOException taxa list cannot be resolved");
    							e.printStackTrace();
    							System.exit(1);
    						}
    	        		   }   
    	        		   if(commandLine.hasOption("state")) {
    	        			   String state = commandLine.getOptionValue("state");
    	        			   if(Pattern.compile(Pattern.quote("complete"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState = State.COMPLETE;
    	        			   }else if(Pattern.compile(Pattern.quote("scaffold"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState = State.SCAFFOLD;
    	        			   }else if(Pattern.compile(Pattern.quote("contig"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState =  State.CONTIG;
    	        			   }else if(Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState = State.ALL;
    	        			   }else {
    	        				   System.out.println("State "+state +" not specified");
    	        				   System.exit(1);
    	        			   }
    	        		   }
    	        		   
    	        		   if(commandLine.hasOption("contaminatedRemoval")) {
    	        			   ignoreHumanAssemblies = true;
    	        			   File file=new File(commandLine.getOptionValue("contaminatedRemoval"));
    	        			   if(file.exists() && file.canRead()) {
    	        				   assembliesToIgnoreFile = file.getCanonicalPath();
    	        				  try { Scanner	in = new Scanner(file.getCanonicalFile());
    	        					 while(in.hasNext()){
    	        						 String line = in.nextLine().trim();
    	        						 if(!line.startsWith("#"))
    	        							 assembliesToIgnore.add(line);
    	        					 }
    	        					 in.close();
    	        				 	}catch (FileNotFoundException e) {
		    	        				 System.err.println("contaminatedRemoval File Not Found");
		    	        				 e.printStackTrace();
		    	        				}		   
    	        			   	} 
    	        		   }
    	        			   
    	        		break;
    	        		}
    	        	case DOWNLOAD:{
    	        		if (commandLine.hasOption("keywordRemoval")) {
    	        			this.keywordRemoval=true;
    	        		}
    	        		
    	        		if (commandLine.hasOption("phylum")) {
    	        				if(Pattern.compile(Pattern.quote("bacteria"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()){
    	        					phylum = Phylum.BACTERIA;
 	      	        	 		}else if(Pattern.compile(Pattern.quote("eukaryotes"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
	 	      	        		   phylum = Phylum.EUKARYOTES;
	 	      	        	 	}else if(Pattern.compile(Pattern.quote("fullnt"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
	 	      	        	 		phylum = Phylum.FULLNT;
	 	      	        	 	}else if(Pattern.compile(Pattern.quote("viral"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
	 	      	        	 		phylum = Phylum.VIRAL;
	 	      	        	 	}else {
	 	      	        	 		System.err.println("unspecified input for phylum");
	 	      	        	 	}
    	        			}
	        			   if(commandLine.hasOption("state")) {
    	        			   String state = commandLine.getOptionValue("state");
    	        			   if(Pattern.compile(Pattern.quote("complete"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState = State.COMPLETE;
    	        			   }else if(Pattern.compile(Pattern.quote("scaffold"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState = State.SCAFFOLD;
    	        			   }else if(Pattern.compile(Pattern.quote("contig"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState =  State.CONTIG;
    	        			   }else if(Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
    	        				   sequenceState = State.ALL;
    	        			   }else {
    	        				   
    	        			   }
	        			   }
	        		   if (commandLine.hasOption("taxonlist")) {
	        			   try{
	        				   String tax = commandLine.getOptionValue("taxonlist");
	        				   File f = new File(tax);
	        				   System.out.println(f.getCanonicalPath());
	        				   if(f.getCanonicalFile().exists()){
	    	     					readTaxList(f);
	        				   }else {
	        					System.err.println("IOException taxa list cannot be resolved");
	   							System.exit(1);
	        				   }
	        			   }catch (IOException e) {
							System.err.println("IOException taxa list cannot be resolved");
							e.printStackTrace();
							System.exit(1);
	        			   }
	        		   }   
	        		   if(commandLine.hasOption("contaminatedRemoval")) {
	        			   ignoreHumanAssemblies = true;
	        			   File file=new File(commandLine.getOptionValue("contaminatedRemoval"));
	        			   if(file.exists() && file.canRead()) {
	        				   assembliesToIgnoreFile = file.getCanonicalPath();
	        				  try { Scanner	in = new Scanner(file.getCanonicalFile());
	        					 while(in.hasNext()){
	        						 String line = in.nextLine().trim();
	        						 if(!line.startsWith("#"))
	        							 assembliesToIgnore.add(line);
	        					 }
	        					 in.close();
	        				 	}catch (FileNotFoundException e) {
	    	        				 System.err.println("contaminatedRemoval File Not Found");
	    	        				 e.printStackTrace();
	    	        				}		   
	        			   	} 
	        		   }
	        		  if (commandLine.hasOption("cutOffTotalGapLength")) {
	        			  cutOffTotalGapLength=Integer.parseInt(commandLine.getOptionValue("cutOffTotalGapLength"));
	        		  }
	        		  if (commandLine.hasOption("cutOffL50")) {
	        			  cutOffL50=Integer.parseInt(commandLine.getOptionValue("cutOffL50"));
	        		  }
	        		  if (commandLine.hasOption("cutOffN50")) {
	        			  cutOffN50=Integer.parseInt( commandLine.getOptionValue("cutOffN50"));
	        		  }
	        		  if (commandLine.hasOption("cutOffScore")) {
	        			  cutOffScore=Integer.parseInt(commandLine.getOptionValue("cutOffScore"));
	        		  }
	        		  if (commandLine.hasOption("priorToScreenValue")) {
	        			  priorToScreenValue=Integer.parseInt(commandLine.getOptionValue("priorToScreenValue"));
	        		  }
	        		  
	        		break;
	        		}
    	        	case CREATE:{
    	        		   if (commandLine.hasOption("taxonlist")) {
    	        			   try{
    	        				   String tax = commandLine.getOptionValue("taxonlist");
    	        				   File f = new File(tax);
    	        				   if(f.getCanonicalFile().exists()){
    	    	     					readTaxList(f);
    	        				   }
    	        			   }catch (IOException e) {
    							System.err.println("IOException taxa list cannot be resolved");
    							e.printStackTrace();
    							System.exit(1);
    	        			   }
    	        		   }   
    	        		   if (commandLine.hasOption("index")) {
   	        			    pathToIndex = commandLine.getOptionValue("index");
   	        				} else {
   	        					System.err.println("No database index provided!!! Cannot create reads!!!");
   	        					System.exit(1);
   	        				}     
	    	        	break;
    	        	}
    	        	case UPDATE:{
    	        		if (commandLine.hasOption("keywordRemoval")) {
    	        			this.keywordRemoval=true;
    	        		}
	    	        		if (commandLine.hasOption("phylum")) {
		        				if(Pattern.compile(Pattern.quote("bacteria"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()){
		        					phylum = Phylum.BACTERIA;
		      	        	 		}else if(Pattern.compile(Pattern.quote("eukaryotes"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
	 	      	        		   phylum = Phylum.EUKARYOTES;
	 	      	        	 	}else if(Pattern.compile(Pattern.quote("fullnt"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
	 	      	        	 		phylum = Phylum.FULLNT;
	 	      	        	 	}else if(Pattern.compile(Pattern.quote("viral"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
	 	      	        	 		phylum = Phylum.VIRAL;
	 	      	        	 	}else {
	 	      	        	 		System.err.println("unspecified input for phylum");
	 	      	        	 	}
		        			}
	        			   if(commandLine.hasOption("state")) {
		        			   String state = commandLine.getOptionValue("state");
		        			   if(Pattern.compile(Pattern.quote("complete"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
		        				   sequenceState = State.COMPLETE;
		        			   }else if(Pattern.compile(Pattern.quote("scaffold"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
		        				   sequenceState = State.SCAFFOLD;
		        			   }else if(Pattern.compile(Pattern.quote("contig"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
		        				   sequenceState =  State.CONTIG;
		        			   }else if(Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
		        				   sequenceState = State.ALL;
		        			   }else {
		        				   System.out.println(state);
		        			   }
	        			   }
	        		   if (commandLine.hasOption("taxonlist")) {
	        			   try{
	        			
	        				   String tax = commandLine.getOptionValue("taxonlist");
	        				   File f = new File(tax);
	        				   if(f.getCanonicalFile().exists()){
	    	     					readTaxList(f);
	        				   }else {
	        					System.err.println("IOException taxa list cannot be resolved");
	   							System.exit(1);
	        				   }
	        			   }catch (IOException e) {
							System.err.println("IOException taxa list cannot be resolved");
							e.printStackTrace();
							System.exit(1);
	        			   }
	        		   	}   
	        		   if (commandLine.hasOption("index")) {
	        			    pathToIndex = commandLine.getOptionValue("index");
	        				} else {
	        					System.err.println("No database index provided!!! Cannot update Index!!!");
	        					System.exit(1);
	        				}  
    	        		break;
    	        	}
    	        	
    	        	case CLEAN_TAXONOMIC:{
    	        		if(commandLine.hasOption("index")) {
	        			    pathToIndex = commandLine.getOptionValue("index");
	        				} else {
	        					System.err.println("No database index provided!!! Cannot clean DB!!!");
	        					System.exit(1);
	        				}  
    	        		if(commandLine.hasOption("pathToMaltExAssignment")) {
    	        			pathToMaltExAssignment = commandLine.getOptionValue("pathToMaltExAssignment");
	        				} else {
	        					System.err.println("No MaltExtractAssignment file provided!!! Cannot clean DB!!!");
	        					System.exit(1);
	        				}  
    	        		
    	        		break;
    	        	}
    	        	case CLEAN_ADAPTERS:{
    	        		if(commandLine.hasOption("index")) {
	        			    pathToIndex = commandLine.getOptionValue("index");
	        				} else {
	        					System.err.println("No database index provided!!! Cannot clean Index!!!");
	        					System.exit(1);
	        				}  
    	        		if(commandLine.hasOption("cutOffOnPathPercentage")) {
    	        			cutOffOnPathPercentage = Double.parseDouble(commandLine.getOptionValue("cutOffOnPathPercentage"));
    	        		}
    	        		break;
    	        	}
    	        	case CLEAN_REFERENCE:{
    	        		if(commandLine.hasOption("index")) {
	        			    pathToIndex = commandLine.getOptionValue("index");
	        				} else {
	        					System.err.println("No database index provided!!! Cannot clean Index!!!");
	        					System.exit(1);
	        				}  
    	        		if(commandLine.hasOption("length")) {
 	        			   String length = commandLine.getOptionValue("length");
 	        			  lengthTreshold = Integer.parseInt(length);
 	        		   }
    	        		break;
    	        	}
				case COMPLETE:
					if (commandLine.hasOption("keywordRemoval")) {
	        			this.keywordRemoval=true;
	        		}
	        		
	        		if (commandLine.hasOption("phylum")) {
	        				if(Pattern.compile(Pattern.quote("bacteria"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()){
	        					phylum = Phylum.BACTERIA;
	      	        	 		}else if(Pattern.compile(Pattern.quote("eukaryotes"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
 	      	        		   phylum = Phylum.EUKARYOTES;
 	      	        	 	}else if(Pattern.compile(Pattern.quote("fullnt"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
 	      	        	 		phylum = Phylum.FULLNT;
 	      	        	 	}else if(Pattern.compile(Pattern.quote("viral"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("phylum")).find()) {
 	      	        	 		phylum = Phylum.VIRAL;
 	      	        	 	}else {
 	      	        	 		System.err.println("unspecified input for phylum");
 	      	        	 	}
	        			}
        			   if(commandLine.hasOption("state")) {
	        			   String state = commandLine.getOptionValue("state");
	        			   if(Pattern.compile(Pattern.quote("complete"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
	        				   sequenceState = State.COMPLETE;
	        			   }else if(Pattern.compile(Pattern.quote("scaffold"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
	        				   sequenceState = State.SCAFFOLD;
	        			   }else if(Pattern.compile(Pattern.quote("contig"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
	        				   sequenceState =  State.CONTIG;
	        			   }else if(Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE).matcher(state).find()){
	        				   sequenceState = State.ALL;
	        			   }else {
	        				   
	        			   }
        			   }
        		   if (commandLine.hasOption("taxonlist")) {
        			   try{
        				   String tax = commandLine.getOptionValue("taxonlist");
        				   File f = new File(tax);
        				   System.out.println(f.getCanonicalPath());
        				   if(f.getCanonicalFile().exists()){
    	     					readTaxList(f);
        				   }else {
        					System.err.println("IOException taxa list cannot be resolved");
   							System.exit(1);
        				   }
        			   }catch (IOException e) {
						System.err.println("IOException taxa list cannot be resolved");
						e.printStackTrace();
						System.exit(1);
        			   }
        		   }   
        		   if(commandLine.hasOption("contaminatedRemoval")) {
        			   ignoreHumanAssemblies = true;
        			   File file=new File(commandLine.getOptionValue("contaminatedRemoval"));
        			   if(file.exists() && file.canRead()) {
        				   assembliesToIgnoreFile = file.getCanonicalPath();
        				  try { Scanner	in = new Scanner(file.getCanonicalFile());
        					 while(in.hasNext()){
        						 String line = in.nextLine().trim();
        						 if(!line.startsWith("#"))
        							 assembliesToIgnore.add(line);
        					 }
        					 in.close();
        				 	}catch (FileNotFoundException e) {
    	        				 System.err.println("contaminatedRemoval File Not Found");
    	        				 e.printStackTrace();
    	        				}		   
        			   	} 
        		   }
        		  if (commandLine.hasOption("cutOffTotalGapLength")) {
        			  cutOffTotalGapLength=Integer.parseInt(commandLine.getOptionValue("cutOffTotalGapLength"));
        		  }
        		  if (commandLine.hasOption("cutOffL50")) {
        			  cutOffL50=Integer.parseInt(commandLine.getOptionValue("cutOffL50"));
        		  }
        		  if (commandLine.hasOption("cutOffN50")) {
        			  cutOffN50=Integer.parseInt( commandLine.getOptionValue("cutOffN50"));
        		  }
        		  if (commandLine.hasOption("cutOffScore")) {
        			  cutOffScore=Integer.parseInt(commandLine.getOptionValue("cutOffScore"));
        		  }
        		  if (commandLine.hasOption("priorToScreenValue")) {
        			  priorToScreenValue=Integer.parseInt(commandLine.getOptionValue("priorToScreenValue"));
        		  }
					   if (commandLine.hasOption("taxonlist")) {
	        			   try{
	        				   String tax = commandLine.getOptionValue("taxonlist");
	        				   File f = new File(tax);
	        				   if(f.getCanonicalFile().exists()){
	    	     					readTaxList(f);
	        				   }
	        			   }catch (IOException e) {
							System.err.println("IOException taxa list cannot be resolved");
							e.printStackTrace();
							System.exit(1);
	        			   }
	        		   }   
					if(commandLine.hasOption("pathToMaltExAssignment")) {
	        			pathToMaltExAssignment = commandLine.getOptionValue("pathToMaltExAssignment");
        				} else {
        					System.err.println("No MaltExtractAssignment file provided!!! Cannot clean DB!!!");
        					System.exit(1);
        				}  
	        		if(commandLine.hasOption("cutOffOnPathPercentage")) {
	        			cutOffOnPathPercentage = Double.parseDouble(commandLine.getOptionValue("cutOffOnPathPercentage"));
	        		}
					if(commandLine.hasOption("length")) {
	        			   String length = commandLine.getOptionValue("length");
	        			  lengthTreshold = Integer.parseInt(length);
	        		   }
 	        		
					break;
    	        }
    	  
    	        if(!commandLine.hasOption("o")){
	    	        	System.err.println("output has to be specified -h for help \n Shutting down");
	    	        	System.exit(1);
    	        }
//    	        if ((commandLine.hasOption("phylum") == false && commandLine.hasOption("taxonlist") == false)==true) {
//      			   	System.err.println("Specify either Phylum or Taxonlist! Shutting Down!");
//      			   System.exit(1);
//      		   }
    	    }
	public int getLengthTreshold() {
		return lengthTreshold;
	}
	public void setLengthTreshold(int lengthTreshold) {
		this.lengthTreshold = lengthTreshold;
	}
	public boolean isKeywordRemoval() {
		return keywordRemoval;
	}
	public void setKeywordRemoval(boolean keywordRemoval) {
		this.keywordRemoval = keywordRemoval;
	}
	public int getCutOffTotalGapLength() {
		return cutOffTotalGapLength;
	}
	public void setCutOffTotalGapLength(int cutOffTotalGapLength) {
		this.cutOffTotalGapLength = cutOffTotalGapLength;
	}
	public Integer getPriorToScreenValue() {
		return priorToScreenValue;
	}
	public void setPriorToScreenValue(Integer priorToScreenValue) {
		this.priorToScreenValue = priorToScreenValue;
	}
	public int getCutOffL50() {
		return cutOffL50;
	}
	public void setCutOffL50(int cutOffL50) {
		this.cutOffL50 = cutOffL50;
	}
	public int getCutOffN50() {
		return cutOffN50;
	}
	public void setCutOffN50(int cutOffN50) {
		this.cutOffN50 = cutOffN50;
	}
	public int getCutOffScore() {
		return cutOffScore;
	}
	public void setCutOffScore(int cutOffScore) {
		this.cutOffScore = cutOffScore;
	}
	public int getCutOffTotalLengthAssembly() {
		return cutOffTotalLengthAssembly;
	}
	public void setCutOffTotalLengthAssembly(int cutOffTotalLengthAssembly) {
		this.cutOffTotalLengthAssembly = cutOffTotalLengthAssembly;
	}
	public int getCutOffSpannedGaps() {
		return cutOffSpannedGaps;
	}
	public void setCutOffSpannedGaps(int cutOffSpannedGaps) {
		this.cutOffSpannedGaps = cutOffSpannedGaps;
	}
	public int getCutOffRegionCount() {
		return cutOffRegionCount;
	}
	public void setCutOffRegionCount(int cutOffRegionCount) {
		this.cutOffRegionCount = cutOffRegionCount;
	}
	public int getCutOffContigCount() {
		return cutOffContigCount;
	}
	public void setCutOffContigCount(int cutOffContigCount) {
		this.cutOffContigCount = cutOffContigCount;
	}
	public int getCutOffUnspannedGaps() {
		return cutOffUnspannedGaps;
	}
	public void setCutOffUnspannedGaps(int cutOffUnspannedGaps) {
		this.cutOffUnspannedGaps = cutOffUnspannedGaps;
	}
	public int getCutOffMoleculeCount() {
		return cutOffMoleculeCount;
	}
	public void setCutOffMoleculeCount(int cutOffMoleculeCount) {
		this.cutOffMoleculeCount = cutOffMoleculeCount;
	}
	public int getCutOffTopLevelCount() {
		return cutOffTopLevelCount;
	}
	public void setCutOffTopLevelCount(int cutOffTopLevelCount) {
		this.cutOffTopLevelCount = cutOffTopLevelCount;
	}
	public int getCutOffComponentCount() {
		return cutOffComponentCount;
	}
	public void setCutOffComponentCount(int cutOffComponentCount) {
		this.cutOffComponentCount = cutOffComponentCount;
	}
	public ArrayList<String> getAssembliesToIgnore() {
		return assembliesToIgnore;
	}
	public void setAssembliesToIgnore(ArrayList<String> assembliesToIgnore) {
		this.assembliesToIgnore = assembliesToIgnore;
	}
	public double getCutOffOnPathPercentage() {
		return cutOffOnPathPercentage;
	}
	public void setCutOffOnPathPercentage(double cutOffOnPathPercentage) {
		this.cutOffOnPathPercentage = cutOffOnPathPercentage;
	}
    }
  