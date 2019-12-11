package ArtificalReadGenerator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;

import DatabaseDownloader.DatabaseEntry;
import Utility.SimulateFormat;

public class LoadGenomicFile { 
	private double maximumrate= 0.1;
	private DatabaseEntry entry;
	private String referenceName;
	private int minimumLength = 35;
	private int maximumLength = 75;
	private double transversion = 0.1;
	private double transition = 0.1;
	private int numberOfReads = 1000;
	private ArrayList<String> simulatedReads = new ArrayList<String>();
	private SimulateFormat format = SimulateFormat.FASTQ;
	private ArrayList<Double> mutationRate = new ArrayList<Double>();
	private ArrayList<Integer> transversions= new ArrayList<Integer>();
	private ArrayList<Integer> transitions= new ArrayList<Integer>();
	private ArrayList<Integer> lengths = new ArrayList<Integer>();
	private String outDir;
	private boolean mutations = false;
	public LoadGenomicFile(DatabaseEntry entry, double maxMutationRate, int maximumLength, int minimumLength, double transversionRate, 
			double transitionRate, int numberOfReads, SimulateFormat format, String outDir){
		maximumrate=maxMutationRate;
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		transversion = transversionRate;
		transition = transitionRate;
		this.numberOfReads = numberOfReads;
		this.format = format;
		this.entry = entry;
		String[]parts =entry.getOutFile().split("/");
		referenceName = parts[parts.length-1];
		this.outDir = outDir;
	}
	private String mutateRead(String read) {// I want to save drawing some random numbers therefore we choose the number of positions mutate than the postitions and lastly the mutation to aplly
		 int transitions = 0;
		 int transversions = 0;
		 double rate = ThreadLocalRandom.current().nextDouble(0,maximumrate);
		 int numberOfPositions =  (int) Math.round(read.length()*rate);
		 mutationRate.add(rate);
		 lengths.add(read.length());
		 HashSet<Integer> positions = new HashSet<Integer>();
		 while(positions.size() <= numberOfPositions) {
			 positions.add(ThreadLocalRandom.current().nextInt(read.length()));
		 }
		 char[] array =read.toCharArray();
		 for(int position: positions) {
			Double choice = ThreadLocalRandom.current().nextDouble(2*transversion+transition);
			 switch(array[position]){
				 case 'A':
					 if(choice<transversion) {
						 array[position] = 'T';
						 transversions++;
					 }else if(transversion <= choice || choice < transversion+transition) {
						 array[position] = 'G';
						 transitions++;
					 }else if(transversion+transition<=choice) {
						 transitions++;
						 array[position] = 'C';
					 } 
					 break;
				 case 'G':
					 if(choice<transversion) {
						 array[position] = 'T';
						 transversions++;
					 }else if(transversion <= choice || choice < transversion+transition) {
						 transitions++;
						 array[position] = 'A';
					 }else if(transversion+transition<=choice) {
						 transitions++;
						 array[position] = 'C';
					 }
				 	break;
				 case 'C':
					 if(choice<transversion) {
						 transversions++;
						 array[position] = 'A';
					 }else if(transversion <= choice || choice < transversion+transition) {
						 array[position] = 'T';
						 transitions++;
					 }else if(transversion+transition<=choice) {
						 array[position] = 'G';
						 transitions++;
					 }
					break;
				 case 'T':
					 if(choice<transversion) {
						 transversions++;
						 array[position] = 'A';
					 }else if(transversion <= choice || choice < transversion+transition) {
						 array[position] = 'C';
						 transitions++;
					 }else if(transversion+transition<=choice) {
						 array[position] = 'G';
						 transitions++;
					 }
					 break;
				default:
					System.err.println("Error unknown chracter for sequence");
					break;	 
			 }
		 }
		 String mutated = "";
		this.transitions.add(transitions);
		this.transversions.add(transversions);
		 for(char c : array)
			 mutated+=c;
		return mutated;
	}
	private String mutateSequencePerBasePair(String read) {
//		 int transitions = 0;
//		 int transversions = 0;
		// lengths.add(read.length());
		char[] array =read.toCharArray();
		int i = 0;
		while(i< array.length) {
			Double choice = ThreadLocalRandom.current().nextDouble(0.0,1.0);
			switch(array[i]){
			 case 'A':
				 if(choice<transversion) {
					 array[i] ='G';
					 //transversions++;
				 }else if(transversion<=choice && choice<transition+transversion) {
					 //transitions++;
					 switch(ThreadLocalRandom.current().nextInt(1, 2)) {
					 	case 1:
					 		array[i] ='T';
					 		break;
					 	case 2:
					 		array[i] ='C';
					 		break;
					 }
				 }else {
					 break;
				 } 
				 break;
			 case 'G':
				 if(choice<transversion) {
					 //transversions++;
					 array[i] ='A';
				 }else if(transversion<=choice && choice<transition+transversion) {
					// transitions++;
						 switch(ThreadLocalRandom.current().nextInt(1, 2)) {
						 	case 1:
						 		array[i] ='T';
						 		break;
						 	case 2:
						 		array[i] ='C';
						 		break;
						 }
					 }else {
						 break;
					 }
			 	break;
			 case 'C':
				 if(choice<transversion) {
					 array[i] ='T';
					 //transversions++;
				 }else if(transversion<=choice && choice<transition+transversion) {
					// transitions++;
						 switch(ThreadLocalRandom.current().nextInt(1, 2)) {
						 	case 1:
						 		array[i] ='G';
						 		break;
						 	case 2:
						 		array[i] ='A';
						 		break;
						 }
						 
				 }else {
					 break;
				 }
				break;
			 case 'T':
				 if(choice<transversion) {
					// transversions++;
					 array[i] ='C';
				 }else if(transversion<=choice && choice<transition+transversion) {
					// transitions++;
					 switch(ThreadLocalRandom.current().nextInt(1, 2)) {
					 	case 1:
					 		array[i] ='G';
					 		break;
					 	case 2:
					 		array[i] ='A';
					 		break;
					 }
				 }else {
					 break;
				 }
				 break;
			default:
				System.err.println("Error unknown chracter for sequence");
				break;	 
		 }
			i++;
		}
		 String mutated = "";
//		 this.transitions.add(transitions);
//			this.transversions.add(transversions);
		 for(char c : array)
			 mutated+=c;
		return mutated;
	}
	public String getReferenceName() {
		return referenceName;
	}
	public ArrayList<String> getSimulatedReads(){
		return simulatedReads;
	}
	public void processFile() {
		String content;
		String reference="";
		//String header="";// currently the bottleneck is loading the file
		 try {                           
		        GZIPInputStream zip = new GZIPInputStream(new FileInputStream(entry.getOutFile()));            
		        InputStreamReader isr = new InputStreamReader(zip, "UTF8");
		        BufferedReader reader = new BufferedReader(isr);
		        while ((content = reader.readLine()) != null) {
		        	if(!content.startsWith(">"))//ignore header
		        	{
		        		reference+=content;
		        	}
		        }
		        reader.close();
		        isr.close();
		        zip.close();
		    	System.out.println("loaded file "+ getReferenceName());
		    } catch (FileNotFoundException e) {
		        System.out.println(e);
		    } catch (IOException e) {
		        System.out.println(e);
		    }
	
		int referenceLength = reference.length();
		int k = 0;
		//System.out.println("Generate reads");
		
		if(mutations) {
			System.out.println("Generate pseudo reads with mutations");
			while(k<numberOfReads) {// relativly fast
				int randomLength = ThreadLocalRandom.current().nextInt(minimumLength, maximumLength);
				int position =  ThreadLocalRandom.current().nextInt(0, referenceLength - randomLength);
				if(format == SimulateFormat.FASTA ) {// allow reads to be generated in fasta and fastq format
					simulatedReads.add(">read"+(k+1));
					//simulatedReads.add(mutateRead(reference.substring(position, position+randomLength)));
					simulatedReads.add(mutateSequencePerBasePair(reference.substring(position, position+randomLength)));
					
				}else if(format == SimulateFormat.FASTQ) {//not yet implemented
					simulatedReads.add("@read"+(k+1));
					//String mutatedSequence = reference.substring(position, position+randomLength);
					String mutatedSequence = mutateSequencePerBasePair(reference.substring(position, position+randomLength));
					simulatedReads.add(mutatedSequence);
					simulatedReads.add("+");
					String quality ="";
					for(int i =0;i<mutatedSequence.length();i++)
						quality +="~";
					simulatedReads.add(quality);
				}
				//simulatedReads.add(mutateSequencePerBasePair(reference.substring(position, position+randomLength)));
				k++;
			}
		}else {
			System.out.println("Generate pseudo reads without mutations");
			for(int position=0;position<=reference.length()-100;position+=100)//TODO get remainder
			{	if(format == SimulateFormat.FASTA ) {// allow reads to be generated in fasta and fastq format
					simulatedReads.add(">read"+(k+1));
					//simulatedReads.add(mutateRead(reference.substring(position, position+randomLength)));
					simulatedReads.add(reference.substring(position, position+100));
					
				}else if(format == SimulateFormat.FASTQ) {//not yet implemented
					simulatedReads.add("@read"+(k+1));
					//String mutatedSequence = reference.substring(position, position+randomLength);
					String sequence = reference.substring(position, position+99);
					simulatedReads.add(sequence);
					simulatedReads.add("+");
					String quality ="";
					for(int i =0;i<sequence.length();i++)
						quality +="~";
					simulatedReads.add(quality);
				}
				k++;
			}
		}
//		System.out.println("Generated Reads");
//		System.out.println("Mutationrate "+maximumrate+"\t"+"Transversionrate "+transversion+ "\t"+"TransitionRate "+transition+"\tlength");
//		for (int i = 0; i<transitions.size();i++) {
//			//System.out.println(mutationRate.get(i)+"\t"+transversions.get(i)+"\t"+transitions.get(i)+"\t"+lengths.get(i));
//			System.out.println(transversions.get(i)+"\t"+transitions.get(i)+"\t"+lengths.get(i));
//		}
	}
	public void writeFile() {
		System.out.println("Write File");
		try{
			String f="";
			if(format == SimulateFormat.FASTA )
				f="fasta";
			else if(format == SimulateFormat.FASTQ)
				f="fastq";
			Path file = Paths.get(outDir+getReferenceName()+".simulated."+f);
			System.out.println(outDir+getReferenceName()+".simulated."+f);
			//System.out.println(file.getFileName()+" "+simulatedReads.size());
			Files.write(file, simulatedReads, Charset.forName("UTF-8"));
		}catch(IOException io){
			io.printStackTrace();
		}
	}
}
