package DatabaseDownloader;

import Utility.State;

public class DatabaseEntry {
	private String name;
	private String link;
	private String outDir;
	private int taxID;
	private int speciesTaxID;
	private State assembly_level;
	private String realm;
	
//	Column 12: "assembly_level"
//		   Assembly level: the highest level of assembly for any object in the genome 
//		   assembly.
//		   Values:
//		      Complete genome - all chromosomes are gapless and have no runs of 10 or 
//		                        more ambiguous bases (Ns), there are no unplaced or 
//		                        unlocalized scaffolds, and all the expected chromosomes
//		                        are present (i.e. the assembly is not noted as having 
//		                        partial genome representation). Plasmids and organelles
//		                        may or may not be included in the assembly but if 
//		                        present then the sequences are gapless.
//		      Chromosome      - there is sequence for one or more chromosomes. This 
//		                        could be a completely sequenced chromosome without gaps
//		                        or a chromosome containing scaffolds or contigs with 
//		                        gaps between them. There may also be unplaced or 
//		                        unlocalized scaffolds.
//		      Scaffold        - some sequence contigs have been connected across gaps to
//		                        create scaffolds, but the scaffolds are all unplaced or 
//		                        unlocalized.
//		      Contig          - nothing is assembled beyond the level of sequence 
//		                        contigs
	public DatabaseEntry(String name, String link, String outDir, String assemblyLevel, int taxID, int speciesTaxID){
		this.link = link;
		this.name = name.toString().replace("=", "_").replace("/", "_");
		this.outDir = outDir;
		switch (assemblyLevel) {
			case "Complete genome":
				assembly_level = State.COMPLETE;
				break;
			case "Chromosome":
				assembly_level = State.CHROMOSOME;
				break;
			case "Scaffold":
				assembly_level = State.SCAFFOLD;
				break;
			case "Contig":
				assembly_level = State.CONTIG;
				break;
		}
		this.taxID = taxID;
		this.speciesTaxID = speciesTaxID;
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
}
