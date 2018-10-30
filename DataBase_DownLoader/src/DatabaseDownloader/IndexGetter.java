package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;




public class IndexGetter {
	private String url;
	private ArrayList<String> taxNames;
	private int secsConnectTimeout=300;
	private int secsReadTimeout=90;
	private String keyword="Complete Genome";//"Scaffold";//"Complete Genome"
	private String outdir;
	private String rep;
	private ArrayList<DatabaseEntry> entries = new ArrayList<DatabaseEntry>();
	public ArrayList<DatabaseEntry> getDatabaseEntries(){
		return entries;
	}
	public IndexGetter(String fileName, String keyword, String outdir, String rep) {
		this.url = fileName;
		this.keyword = keyword;
		this.outdir = outdir;
		this.rep = rep;
	}
	public IndexGetter(String fileName, ArrayList<String> taxNames, String keyword, String outdir, String rep) {
		this.url = fileName;
		this.taxNames = taxNames;
		this.keyword = keyword;
		this.outdir = outdir;
		this.rep = rep;
	}
	private static InputStream streamFromUrl(URL url,int secsConnectTimeout,int secsReadTimeout) throws IOException {
	    URLConnection conn = url.openConnection();
	    if(secsConnectTimeout>0) conn.setConnectTimeout(secsConnectTimeout*1000);
	    if(secsReadTimeout>0) conn.setReadTimeout(secsReadTimeout*1000);
	    return conn.getInputStream();
	}
	public void process() {
			try {
				BufferedReader in = new BufferedReader( new InputStreamReader(streamFromUrl(new URL(url),secsConnectTimeout,secsReadTimeout), "UTF8"));
				String line;
				
				while ((line = in.readLine()) != null) {
					if(!line.toString().startsWith("#") && !line.toString().contains("material_genomic")) {//ignore comments
							String[] parts = line.toString().split("\\t");
							if(parts.length>=20) {
								if(parts[11].contains(keyword) || keyword.equals("all")) {//here either we match the keyword or we don't care
									String link = parts[19]+"/";
									String[] scatter = link.split("/");
									String GI = scatter[scatter.length-1];
									String name = parts[7]+parts[8]+parts[9];
										name = name.toString().replaceAll("/", "");
										
								if(parts[4].equals(rep) || rep.equals("all")) {// either we want to use representative genomes and only those or we want to use all 
									if(taxNames!=null&&taxNames.size()>0) {
										//either we want only specific names or we don't care at all
										for(String taxName : taxNames)
											if(name.contains(taxName)) {
												//System.out.println(name);
												entries.add(new DatabaseEntry((parts[7]+"_"+parts[8]+"_"+parts[9]).toString(),(link+GI+"_genomic.fna.gz"), outdir));
										}
									}else {
										DatabaseEntry entry = 	new DatabaseEntry((parts[7]+"_"+parts[8]+"_"+parts[9]).toString(),(link+GI+"_genomic.fna.gz"), outdir);
										entries.add(entry);
									}
								}
							}
						}else {
							System.err.println("Misformed line "+line.toString());
						}
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}	
			
	}
}
