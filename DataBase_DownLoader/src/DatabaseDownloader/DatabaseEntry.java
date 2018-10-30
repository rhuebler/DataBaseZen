package DatabaseDownloader;
public class DatabaseEntry {
	private String name;
	private String link;
	private String outDir;
	public DatabaseEntry(String name, String link, String outDir){
		this.link = link;
		this.name = name.toString().replace(" ", "_").replace("=", "_").replace("/", "_");
		this.outDir = outDir;
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
