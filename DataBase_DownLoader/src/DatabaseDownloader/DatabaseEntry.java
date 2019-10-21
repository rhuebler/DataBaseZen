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
	private String outFile;
	private int taxID;
	private int speciesTaxID;
	private State assembly_level = null;
	private String realm;
	private String seq_rel_date;
	private String asm_name;
	private String representative="na";
	private int totalContigs = 0;
	private int keptContigs = 0;
	private String fileName ="";
	private boolean cleanDB = false;
	private String time;
	private double qualityValue;
	private boolean containsAdapter = false;
	private int adapterOccurance = 0;
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
	
	public DatabaseEntry(String assembly_accession, String name, String link, String outDir, String assemblyLevel, 
			int taxID, int speciesTaxID, String seq_rel_date, String asm_name, String representative){
		this.assembly_accession =  assembly_accession;
		this.link = link;
		this.name = name.toString();
		this.name = this.name.replace("=", "_").replace("/", "_").replace(" ", "_");
		if(!outDir.endsWith("/")){
			this.outDir= outDir+"/";
			
		}
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
		this.outFile = outDir+getName()+"_"+asm_name+"_"+taxID+".fna.gz";
		this.representative = representative;
	}
	public DatabaseEntry(String line) throws IOException {
		String[]parts =	line.split("\t");
		switch(parts.length) {
			default:{
				name = parts[0];
				outFile = new File(parts[6]).getCanonicalPath();
				this.outDir = new File(this.outFile).getParent();
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
				
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	representative = parts[8];
		    	setTotalContigs(Integer.parseInt(parts[9]));
		    	setKeptContigs(Integer.parseInt(parts[10]));
			break;
			}
			case 11:{
				name = parts[0];
				taxID = Integer.parseInt(parts[1]);
				speciesTaxID = Integer.parseInt(parts[2]); 
				outFile = new File(parts[6]).getCanonicalPath();
				this.outDir = new File(this.outFile).getParent();
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
				
				seq_rel_date = parts[4];
				asm_name = parts[5];
				
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	setTotalContigs(Integer.parseInt(parts[8]));
		    	setKeptContigs(Integer.parseInt(parts[9]));
				break;
			}
			case 12:{
				name = parts[0];
				outFile = new File(parts[6]).getCanonicalPath();
				this.outDir = new File(this.outFile).getParent();
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
				
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	representative = parts[8];
		    	setTotalContigs(Integer.parseInt(parts[9]));
		    	setKeptContigs(Integer.parseInt(parts[10]));
		    	setContainsAdapter(Boolean.parseBoolean(parts[11]));
				break;
			}
			case 13:{
				name = parts[0];
				outFile = new File(parts[6]).getCanonicalPath();
				this.outDir = new File(this.outFile).getParent();
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
				
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	representative = parts[8];
		    	setTotalContigs(Integer.parseInt(parts[9]));
		    	setKeptContigs(Integer.parseInt(parts[10]));
		    	setContainsAdapter(Boolean.parseBoolean(parts[11]));
		    	setAdapterOccurance(Integer.parseInt(parts[12]));
				break;
			}
			case 14:{
				name = parts[0];
				outFile = new File(parts[6]).getCanonicalPath();
				this.outDir = new File(this.outFile).getParent();
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
			
		    
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	representative = parts[8];
		    	setTotalContigs(Integer.parseInt(parts[9]));    
		    	setKeptContigs(Integer.parseInt(parts[10]));     
		    	setContainsAdapter(Boolean.parseBoolean(parts[11]));
		    	setOnPathPercentage(Double.parseDouble(parts[12]));
		    	setOffPathPercentage(Double.parseDouble(parts[13]));
				break;
			}
			case 15:{
				name = parts[0];
				outFile = new File(parts[6]).getCanonicalPath();
				this.outDir = new File(this.outFile).getParent();
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
			
		    
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	representative = parts[8];
		    	setTotalContigs(Integer.parseInt(parts[9]));    
		    	setKeptContigs(Integer.parseInt(parts[10]));     
		    	setContainsAdapter(Boolean.parseBoolean(parts[11]));
		    	setAdapterOccurance(Integer.parseInt(parts[12]));
		    	setOnPathPercentage(Double.parseDouble(parts[13]));
		    	setOffPathPercentage(Double.parseDouble(parts[14]));
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
		return this.outFile;
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
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
			else	
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
	}
	public String getAdapterContainedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+"\t"+containsAdapter+"\t"+ adapterOccurance;
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+"\t"+containsAdapter+"\t"+ adapterOccurance;
	}
	public String getPathContainedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+ "\t"+containsAdapter+ "\t"+ adapterOccurance +"\t"+ onPathPercentage+"\t"+ offPathPercentage;
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs)+"\t"+containsAdapter+ "\t"+ adapterOccurance +"\t"+ onPathPercentage+"\t"+ offPathPercentage;
	}
	public String getCleanedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+time+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+ZonedDateTime.now()+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
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
	public int getAdapterOccurance() {
		return adapterOccurance;
	}
	public void setAdapterOccurance(int adapterOccurance) {
		this.adapterOccurance = adapterOccurance;
	}
}
