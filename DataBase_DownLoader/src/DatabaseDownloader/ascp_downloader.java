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
/**
are likely to form relatively small contigs.
Consistent with this expectation, we found that 99.7% of
contaminated contigs and scaffolds are shorter than 10 kbp,
99.3% are below5 kbp, and 92% are below 1 kbp (Fig. 2A). The median
We selected 219 high-quality samples for further analysis,
choosing those with at least 20× coverage (see Supplemental
Fig. S4), and found that contaminated scaffolds had significantly
lower coverage than the genome-wide average (Fig. 2B, red box).
*/