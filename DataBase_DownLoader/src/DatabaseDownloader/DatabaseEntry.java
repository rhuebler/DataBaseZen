package DatabaseDownloader;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

import Utility.State;

public class DatabaseEntry {
	/**
	 * This class represents an entry out of NCBI
	 */
	private String assembly_accession; 
	
	private String name;
	private String link;
	private String outDir;
	private int taxID;
	private int speciesTaxID;
	private State assembly_level = null;
	private String realm;
	private String seq_rel_date;
	private String asm_name;
	private int totalContigs = 0;
	private int keptContigs = 0;
	private String fileName ="";
	private boolean cleanDB = false;
	private String time;
	private double qualityValue;
	private boolean containsAdapter = false;
	private double onPathPercentage = 0.0;
	private double offPathPercentage = 0.0;
	public String getAssembly_accession() {
		return assembly_accession;
	}
	public void setAssembly_accession(String assembly_accession) {
		this.assembly_accession = assembly_accession;
	}
	public double getOnPathPercentage() {
		return onPathPercentage;
	}
	public void setOnPathPercentage(double onPathPercentage) {
		this.onPathPercentage = onPathPercentage;
	}
	
	public double getOffPathPercentage() {
		return offPathPercentage;
	}
	public void setOffPathPercentage(double offPathPercentage) {
		this.offPathPercentage = offPathPercentage;
	}
	public boolean isContainsAdapter() {
		return containsAdapter;
	}
	public void setContainsAdapter(boolean containsAdapter) {
		this.containsAdapter = containsAdapter;
	}
	
	public DatabaseEntry(String assembly_accession, String name, String link, String outDir, String assemblyLevel, int taxID, int speciesTaxID, String seq_rel_date, String asm_name){
		this.assembly_accession =  assembly_accession;
		this.link = link;
		this.name = name.toString().replace("=", "_").replace("/", "_");
		this.outDir = outDir;
		State state = null;
		if(assemblyLevel.equals("Complete genome")){
				state = State.COMPLETE;
		}
		else if(assemblyLevel.equals("Chromosome")) {
				state = State.CHROMOSOME;
			}
		else if(assemblyLevel.equals("Scaffold")) {
				state =State.SCAFFOLD;
			}
		else if(assemblyLevel.equals("Contig")) {
				state = State.CONTIG;
			}
		else {
			//System.out.println(assemblyLevel);
			state = State.COMPLETE;
		}
		setAssembly_level(state);
		this.setTaxID(taxID);
		this.setSpeciesTaxID(speciesTaxID);
		this.setSeq_rel_date(seq_rel_date);
		this.setAsm_name(asm_name);
	}
	public DatabaseEntry(String line) throws IOException {
		String[]parts =	line.split("\t");
		switch(parts.length) {
			default:{
				name = parts[0];
				outDir = new File(parts[6]).getCanonicalPath();
				State state = null;
				if(parts[3].equals("COMPLETE")){
						state = State.COMPLETE;
				}
				else if(parts[3].equals("CHROMOSOME")) {
						state = State.CHROMOSOME;
					}
				else if(parts[3].equals("SCAFFOLD")) {
						state =State.SCAFFOLD;
					}
				else if(parts[3].equals("CONTIG")) {
						state = State.CONTIG;
					}
				else {
					state = State.COMPLETE;
				}
				setAssembly_level(state);
				taxID = Integer.parseInt(parts[1]);
				speciesTaxID = Integer.parseInt(parts[2]); 
				seq_rel_date = parts[4];
				asm_name = parts[5];
				setTotalContigs(Integer.parseInt(parts[8]));
		    	setKeptContigs(Integer.parseInt(parts[9]));
		    	setFileName(parts[6]);
		    	time = parts[7];}
			break;
			case 11:{
				name = parts[0];
				outDir = new File(parts[6]).getCanonicalPath();
				State state = null;
				if(parts[3].equals("COMPLETE")){
						state = State.COMPLETE;
				}
				else if(parts[3].equals("CHROMOSOME")) {
						state = State.CHROMOSOME;
					}
				else if(parts[3].equals("SCAFFOLD")) {
						state =State.SCAFFOLD;
					}
				else if(parts[3].equals("CONTIG")) {
						state = State.CONTIG;
					}
				else {
					state = State.COMPLETE;
				}
				setAssembly_level(state);
				taxID = Integer.parseInt(parts[1]);
				speciesTaxID = Integer.parseInt(parts[2]); 
				seq_rel_date = parts[4];
				asm_name = parts[5];
				setTotalContigs(Integer.parseInt(parts[8]));
		    	setKeptContigs(Integer.parseInt(parts[9]));
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	setContainsAdapter(Boolean.parseBoolean(parts[10]));
				break;
			}
			case 13:{
				name = parts[0];
				outDir = new File(parts[6]).getCanonicalPath();
				State state = null;
				if(parts[3].equals("COMPLETE")){
						state = State.COMPLETE;
				}
				else if(parts[3].equals("CHROMOSOME")) {
						state = State.CHROMOSOME;
					}
				else if(parts[3].equals("SCAFFOLD")) {
						state =State.SCAFFOLD;
					}
				else if(parts[3].equals("CONTIG")) {
						state = State.CONTIG;
					}
				else {
					state = State.COMPLETE;
				}
				setAssembly_level(state);
				taxID = Integer.parseInt(parts[1]);
				speciesTaxID = Integer.parseInt(parts[2]); 
				seq_rel_date = parts[4];
				asm_name = parts[5];
				setTotalContigs(Integer.parseInt(parts[8]));
		    	setKeptContigs(Integer.parseInt(parts[9]));
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	setContainsAdapter(Boolean.parseBoolean(parts[10]));
		    	setOnPathPercentage(Double.parseDouble(parts[11]));
		    	setOffPathPercentage(Double.parseDouble(parts[12]));
				break;
			}
		}
	
	}
	public void setQualityValue() {
		switch(assembly_level){
			case COMPLETE:
				qualityValue = 100*onPathPercentage;
				break;
			case CHROMOSOME:
				qualityValue = (90-(totalContigs-keptContigs)/2)*onPathPercentage;
				
				break;
			case SCAFFOLD:
				qualityValue = (80-(totalContigs-keptContigs)/2)*onPathPercentage;
				break;	
			case CONTIG:
				qualityValue = (70-(totalContigs-keptContigs)/2)*onPathPercentage;
				break;
		default:
			qualityValue=0;
			break;
		}
	}
	public double getQualityValue() {
		return qualityValue;
	}
	public void setCleanDB(boolean b) {
		cleanDB =b;
	}
	public void setFileName(String s) {
		fileName=s;
	}
	public String getOutFile() {
		if(outDir!= null) {
			if(!outDir.endsWith("/")){
			outDir+="/";
			}
			return outDir+name+"_"+asm_name+"_"+taxID+".fna.gz";
		}else 
			return fileName;
	}
	
	public String getFilteredFile() {
		return outDir+name+"_"+asm_name+"_dusted_"+taxID+".fna.gz";
	}
	public String getName() {
		return name;
	}
	public int getCode() {
		String s = name+"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t";
		return s.hashCode();
	}
	public String getLink() {
		return link;
	}
	public String getAsm_name() {
		return asm_name;
	}
	public void setAsm_name(String asm_name) {
		this.asm_name = asm_name;
	}
	public String getSeq_rel_date() {
		return seq_rel_date;
	}
	public void setSeq_rel_date(String seq_rel_date) {
		this.seq_rel_date = seq_rel_date;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public State getAssembly_level() {
		return assembly_level;
	}
	public void setAssembly_level(State assembly_level) {
		this.assembly_level = assembly_level;
	}
	public int getSpeciesTaxID() {
		return speciesTaxID;
	}
	public void setSpeciesTaxID(int speciesTaxID) {
		this.speciesTaxID = speciesTaxID;
	}
	public int getTaxID() {
		return taxID;
	}
	public void setTaxID(int taxID) {
		this.taxID = taxID;
	}
	public String getIndexLine() {
			if(time!=null)
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
			else	
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
	}
	public String getAdapterContainedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+containsAdapter;
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+containsAdapter;
	}
	public String getPathContainedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+ "\t"+containsAdapter+ "\t"+ onPathPercentage+"\t"+ offPathPercentage;
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+"\t"+containsAdapter+ "\t"+ offPathPercentage;
	}
	public String getCleanedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+time+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+ZonedDateTime.now()+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
}
	public String failedState() {
		return name + "\t" +link + "\t"+outDir;
	}
	public int getTotalContigs() {
		return totalContigs;
	}
	public void setTotalContigs(int totalContigs) {
		this.totalContigs = totalContigs;
	}
	public int getKeptContigs() {
		return keptContigs;
	}
	public void setKeptContigs(int keptContigs) {
		this.keptContigs = keptContigs;
	}
}
