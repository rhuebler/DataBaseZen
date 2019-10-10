package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class IndexGetterGeneBank {
	String url = "https://ftp.ncbi.nlm.nih.gov/genomes/genbank/assembly_summary_genbank.txt";
	private ArrayList<String> taxNames;
	private int secsConnectTimeout=300;
	private int secsReadTimeout=90;
	private String state="Complete Genome";//"Scaffold";//"Complete Genome"
	private String outdir;
	private String rep;
	private boolean keywordRemoval;
	private ArrayList<DatabaseEntry> entries = new ArrayList<DatabaseEntry>();
	public void removeHumanContaminatedAssemblies(ArrayList<String> contamiantedReferences) {
		ArrayList<DatabaseEntry> contained = new ArrayList<DatabaseEntry>();
		for(DatabaseEntry entry :getDatabaseEntries() ) {
			if(contamiantedReferences.contains(entry.getAsm_name())) {
				 contained.add(entry);
			}
		}
		if(contained.size()>0)
			entries.removeAll(contained);
	}
	public ArrayList<DatabaseEntry> getDatabaseEntries(){
		return entries;
	}
	public IndexGetterGeneBank(String state, String outdir, String rep, boolean keywordRemoval) {
		this.state = state;
		this.outdir = outdir;
		this.rep = rep;
		this.keywordRemoval = keywordRemoval;
	}
	public IndexGetterGeneBank(String fileName, ArrayList<String> taxNames, String state, String outdir, String rep, boolean keywordRemoval) {
		this.url = fileName;
		this.taxNames = taxNames;
		this.state = state;
		this.outdir = outdir;
		this.rep = rep;
		this.keywordRemoval = keywordRemoval;
	}
	private static InputStream streamFromUrl(URL url,int secsConnectTimeout,int secsReadTimeout) throws IOException {
	    URLConnection conn = url.openConnection();
	    if(secsConnectTimeout>0) conn.setConnectTimeout(secsConnectTimeout*1000);
	    if(secsReadTimeout>0) conn.setReadTimeout(secsReadTimeout*1000);
	    return conn.getInputStream();
	}
	private void processParts(String[] parts, String name, String link, String GI) {
		if(taxNames!=null&&taxNames.size()>0) {
			//either we want only specific names or we don't care at all
			for(String taxName : taxNames)
				if(name.contains(taxName)) {
					//System.out.println(name);
					entries.add(new DatabaseEntry(parts[0],(parts[7]+" "+parts[8]).toString(),(link+GI+"_genomic.fna.gz"), 
							outdir, parts[11],Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),parts[14],parts[15], parts[4]));
			}
		}else {
			entries.add(new DatabaseEntry(parts[0],(parts[7]+" "+parts[8]).toString(),(link+GI+"_genomic.fna.gz"), 
					outdir, parts[11],Integer.parseInt(parts[5]), Integer.parseInt(parts[6]),parts[14],parts[15], parts[4]));
		}
	}
	private void processLine(String line) {
		String[] parts = line.toString().split("\\t");
		if(parts.length>=20) {
			if(parts[11].contains(state) || state.equals("all")) {//here either we match the keyword or we don't care
				String link = parts[19]+"/";
				String[] scatter = link.split("/");
				String GI = scatter[scatter.length-1];
				String name = parts[7]+parts[8];
					name = name.toString().replaceAll("/", "");
					switch(rep) {
						default:
							if(parts[4].equals(rep)) {// either we want to use representative genomes and only those or we want to use all 
								processParts(parts,name, link, GI);
							}
							break;
						case "disable":
							processParts(parts,name, link, GI);
							break;
						case "strict":
							if(!parts[4].equals("na")) {// either we want to use representative genomes and only those or we want to use all 
								processParts(parts,name, link, GI);
							}
							break;	
						}			
					}
	}else {
		System.err.println("Misformed line "+line.toString());
		}
}
	public void process() {
			try {
				BufferedReader in = new BufferedReader( new InputStreamReader(streamFromUrl(new URL(url),secsConnectTimeout,secsReadTimeout), "UTF8"));
				String line;
				
				while ((line = in.readLine()) != null) {
					if(!line.toString().startsWith("#") && !line.toString().contains("material_genomic")) {//ignore comments
						if(keywordRemoval == true) {
							if(!line.contains("uncultured") && !line.contains("co-culture species") && !line.contains("synthetic")&& !line.contains("unknown source")){
								processLine(line);
							}
						}else{
							processLine(line);
						}
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}	
			
	}
}

