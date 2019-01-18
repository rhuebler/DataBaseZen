package DatabaseDownloader;

import java.time.ZonedDateTime;

import Utility.State;

public class DatabaseEntry {
	private String name;
	private String link;
	private String outDir;
	private int taxID;
	private int speciesTaxID;
	private State assembly_level;
	private String realm;
	private String seq_rel_date;
	private String asm_name;



	public DatabaseEntry(String name, String link, String outDir, String assemblyLevel, int taxID, int speciesTaxID, String seq_rel_date, String asm_name){
		this.link = link;
		this.name = name.toString().replace("=", "_").replace("/", "_");
		this.outDir = outDir;
		switch (assemblyLevel) {
			case "Complete genome":
				setAssembly_level(State.COMPLETE);
				break;
			case "Chromosome":
				setAssembly_level(State.CHROMOSOME);
				break;
			case "Scaffold":
				setAssembly_level(State.SCAFFOLD);
				break;
			case "Contig":
				setAssembly_level(State.CONTIG);
				break;
		}
		this.setTaxID(taxID);
		this.setSpeciesTaxID(speciesTaxID);
		this.setSeq_rel_date(seq_rel_date);
		this.setAsm_name(asm_name);
	}
	public String getOutFile() {
		if(!outDir.endsWith("/")){
			outDir+="/";
		}
		return outDir+name+".fna.gz";
	}
	public String getFilteredFile() {
		return outDir+name+"_dusted.fna.gz";
	}
	public String getName() {
		return name;
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
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+ZonedDateTime.now();
	
	}
}
