import java.util.ArrayList;

import ArtificalReadGenerator.LoadGenomicFile;
import DatabaseDownloader.DatabaseEntry;
import DatabaseDownloader.IndexGetter;
import Utility.Phylum;
import Utility.ProcessExecutor;

public class test {

	public static void main(String[] args) {
			ArrayList<String> dustCommand  = new ArrayList<String>();
			dustCommand.add("gzcat") ;
			dustCommand.add("placeholder");dustCommand.add("|");
			dustCommand.add("/usr/local/ncbi/blast/bin/dustmasker");
			dustCommand.add("-in");dustCommand.add("-");
			dustCommand.add("-out");dustCommand.add("placeholder");
			dustCommand.add("-infmt");dustCommand.add("fasta");
			dustCommand.add("-outfmt");dustCommand.add("fasta");
			dustCommand.add("-window");dustCommand.add(""+64);
			dustCommand.add("-level");dustCommand.add(""+20);
			dustCommand.add("-linker"); dustCommand.add(""+1);
		
			ArrayList<String>command=new ArrayList<String>();
			command.add("rm");
			command.add("placeholder");
			ProcessExecutor executor = new ProcessExecutor();
			String inFile = "/Users/huebler/Desktop/DownLoadDatabase_16S/testDust/Acinetobacter_baumannii__R2090.fna.gz";
			String outFile = "/Users/huebler/Desktop/DownLoadDatabase_16S/testDust/Acinetobacter_baumannii__R2090_dusted.fasta";
				dustCommand.set(1, inFile); //change input to inputfile is index 8 
				dustCommand.set(7, outFile); //change output to outputfile is index 10
				executor.run(dustCommand);
				//command.set(1,inFile);
				//executor.run(command);
		}
}
//assembly_accession	bioproject	biosample	wgs_master	refseq_category	taxid	species_taxid	organism_name	infraspecific_name	isolate	version_status	assembly_level	release_type	genome_rep	seq_rel_date	asm_name	submitter	gbrs_paired_asm	paired_asm_comp	ftp_path	excluded_from_refseq	relation_to_type_material
//assembly_accession	bioproject	biosample	wgs_master	refseq_category	taxid	species_taxid	organism_name	infraspecific_name	isolate	version_status	assembly_level	release_type	genome_rep	seq_rel_date	asm_name	submitter	gbrs_paired_asm	paired_asm_comp	ftp_path	excluded_from_refseq	relation_to_type_material