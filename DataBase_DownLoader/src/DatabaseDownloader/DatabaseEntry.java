package DatabaseDownloader;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

import Utility.State;

public class DatabaseEntry {
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
	public DatabaseEntry(String name, String link, String outDir, String assemblyLevel, int taxID, int speciesTaxID, String seq_rel_date, String asm_name){
		this.link = link;
		this.name = name.toString().replace("=", "_").replace("/", "_");
		this.outDir = outDir;
		//System.out.println(name +"\t"+ link +"\t"+ outDir+"\t"+assemblyLevel+"\t"+ taxID+"\t"+ speciesTaxID+"\t"+ seq_rel_date+"\t"+ asm_name);
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
		name = parts[0];
		outDir = new File(parts[6]).getCanonicalPath();
		State state = null;
		if(parts[3].equals("Complete genome")){
				state = State.COMPLETE;
		}
		else if(parts[3].equals("Chromosome")) {
				state = State.CHROMOSOME;
			}
		else if(parts[3].equals("Scaffold")) {
				state =State.SCAFFOLD;
			}
		else if(parts[3].equals("Contig")) {
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
			return outDir+name+"_"+asm_name+".fna.gz";
		}else 
			return fileName;
	}
	
	public String getFilteredFile() {
		return outDir+name+"_"+asm_name+"_dusted.fna.gz";
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
		if(cleanDB)
			return getCleanedIndexLine();
		else {
			if(time!=null)
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
			else	
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
		}
	}
	private String getCleanedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+time+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+ZonedDateTime.now()+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
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
