package DatabaseDownloader;

import java.util.ArrayList;

public class ascp_downloader {
	private String asperIndexPath;
	public ascp_downloader(String index) {
		 asperIndexPath = index;
	 }
	void download(DatabaseEntry entry) {
		ArrayList<String> command= new ArrayList<String>();
		command.add("ascp");
		command.add("-i");
		command.add(asperIndexPath);
		command.add("k1");
		command.add("Tr");
		command.add("–l100m");
		command.add("anonftp@ftp.ncbi.nlm.nih.gov:/"+entry.getName());
		command.add(entry.getOutFile());
	}
}