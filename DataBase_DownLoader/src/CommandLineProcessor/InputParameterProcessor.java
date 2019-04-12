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
 * This class is used To Parse Input Parameters for RMA Extractor to make input more flexible and less error prone
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
	
	private String dustFormat = "fasta";
	private int dustLevel = 20;
	private int dustWindow = 64;
	private int dustLinker = 1 ;
	//cite [1] Morgulis A, Gertz EM, Schaffer AA, Agarwala R. A Fast and Symmetric DUST Implementation to Mask Low-Complexity DNA Sequences. for dustMasker part
	// constructor
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
				// taxNames.add(in.nextLine().trim().replace('_', ' '));
				 String line = in.nextLine().trim();
		
				// System.out.println(line);
				 taxNames.add(line);
			 }
			 in.close();
		 	}catch (FileNotFoundException e) {
		 System.err.println("File Not Found");
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
		input +="\n--state	"+ sequenceState;
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
		return input;
	}
	private void process(String[] parameters) throws IOException, ParseException{	
    	 CommandLine commandLine;
    	 	// Short Flags Are necessary parameters that are necessary for any run
    	 	// here we describe all CLI options
    	    Option option_Database = Option.builder("r").longOpt("references").argName("String").hasArgs().desc("Specify where to locate directory with references downloaded outside from this program").build();
    	    Option option_Output = Option.builder("o").longOpt("output").argName("String").hasArg().desc("Specify out directory").build();
    	    Option optionMode = Option.builder("m").longOpt("mode").argName("String").hasArg().desc("full, download, create, update").build();
    	    Option optionPhylum = Option.builder("p").longOpt("phylum").argName("String").hasArg().desc("fullNT, bacteria, viral, eukaryotes").build();
    	    Option optionState = Option.builder("s").longOpt("state").argName("String").hasArg().desc("complete, plasmid, assembly, all").build();
    	    Option optionTaxonList = Option.builder("t").longOpt("taxonlist").argName("String").hasArg().desc("A List with all taxa for which an analysis is desired.\n Taxa List will also be added to download").build();
    	    Option optionRepresentativeGenomes = Option.builder("w").longOpt("wgs").hasArg().argName("String").desc("Accepted values reference_genome, representative_genome, all, na").build();
    	    Option option_Help = Option.builder("h").longOpt("help").optionalArg(true).desc("Print Help").build();
    	    Option optionMaxRate = Option.builder("").longOpt("maxRate").optionalArg(true).desc("Maximum Muatation Rate betwen 0.0 and 1.0").build();
    	    Option optionMinimumLength = Option.builder("").longOpt("minLength").optionalArg(true).desc("Set minimum ReadLength by default 35").build();
    	    Option optionMaximumLength = Option.builder("").longOpt("maxLength").optionalArg(true).desc("Set maximum ReadLength by default 75").build();
    	    Option optionTransversionRate = Option.builder("").longOpt("transversionRate").optionalArg(true).desc("Set transversion rate").build();
    	    Option optionTransRate = Option.builder("").longOpt("transitionRate").optionalArg(true).desc("Set transition Rate").build();
    	    Option optionNumReads = Option.builder("").longOpt("numReads").hasArg().optionalArg(true).desc("Set Number of Reads").build();
    	    Option optionFormat = Option.builder("").longOpt("format").optionalArg(true).desc("output either fastq or fasta format").build();
    	    Option optionThreads = Option.builder("").longOpt("threads").hasArg().optionalArg(true).desc("get number of threads for read creation").build();
    	    Option optionDustLevel = Option.builder("").longOpt("dustLevel").hasArg().optionalArg(true).desc("Set Level parameter for DustMasker").build();
    	    Option optionDustWindow = Option.builder("").longOpt("dustWindow").optionalArg(true).desc("Set window size for DustMasker").build();
    	    Option optionDustLinker = Option.builder("").longOpt("dustLinker").hasArg().optionalArg(true).desc("Set Linker Parameter for DustMasker").build();
    	    Option optionPathToIndex = Option.builder("i").longOpt("index").hasArg().optionalArg(true).desc("Set the path to index to update an index").build();
    	    
    	    Options options = new Options();
    	    
    	    // add all parameters to the parser
    	    CommandLineParser parser = new DefaultParser();
    	    options.addOption(optionThreads);
    	    options.addOption(optionRepresentativeGenomes);
    	    options.addOption(optionState);
    	    options.addOption(optionTaxonList);
    	    options.addOption(optionPhylum);
    	    options.addOption(option_Database);
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
    	    //parse arguments into the comandline parser
    	        commandLine = parser.parse(options, parameters);
 
    	        //check if mode is set and if allowed values are set
    	        if(commandLine.hasOption('m')){
    	        	 if(Pattern.compile(Pattern.quote("full"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()){
    	        		 mode = ExecutionMode.BOTH;
    	        	 }else if(Pattern.compile(Pattern.quote("download"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.DOWNLOAD;
    	        	 }else if(Pattern.compile(Pattern.quote("create"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.CREATE;
    	        	 }else if(Pattern.compile(Pattern.quote("update"), Pattern.CASE_INSENSITIVE).matcher(commandLine.getOptionValue("mode")).find()) {
    	        		 mode = ExecutionMode.UPDATE;
    	        	 }else {
    	        		 System.err.println("Unknown Mode Shutting down");	
    	        		 System.exit(1);
    	        	 }
    	        }else {
    	        	System.out.println("Setting Execution Mode to full");	
    	        	mode = ExecutionMode.BOTH;
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
    	        		new File(outDir).mkdir();
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
    	        		break;
    	        		}
    	        	case DOWNLOAD:{
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
	    	        		if (commandLine.hasOption("references")) {
	    	        			String arg  = commandLine.getOptionValue("references");
	    	        			File inFile = new File(arg);
	    	        			for(String name : inFile.list())//if file ends with RMA6 or is as a soft link at to files
	            					if(name.endsWith("fa")||name.endsWith("fq")||name.endsWith("fasta")||name.endsWith("fastq")||name.endsWith(".gz")) {
	            						referenceFiles.add(inFile.getPath()+"/" + name);
	            					}
	    	        		}
	    	        	    
	    	        	break;
    	        	}
    	        	case UPDATE:{
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
    	        }
    	        if(commandLine.hasOption("h")){////help
    	        	String header = "I still need a usefull-name-program";
    	    	    String footer = "In case you encounter an error drop an email with an useful description to huebler@shh.mpg.de";
    	    	    HelpFormatter formatter = new HelpFormatter();
    	    	    formatter.setWidth(500);
    	    	    formatter.printHelp("I have no name :(", header, options, footer, true);   
    	    	    System.exit(0);
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
    }
  