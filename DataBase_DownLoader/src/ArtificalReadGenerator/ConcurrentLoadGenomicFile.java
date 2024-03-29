package ArtificalReadGenerator;




import DatabaseDownloader.DatabaseEntry;
import Utility.SimulateFormat;

public class ConcurrentLoadGenomicFile implements Runnable {
	private double maximumRate= 0.1;
	private int minimumLength = 35;
	private int maximumLength = 75;
	private double transversion = 0.1;
	private double transition = 0.1;
	private int numberOfReads = 1000;
	private SimulateFormat format = SimulateFormat.FASTQ;
	private DatabaseEntry entry;
	private String outDir;
	public ConcurrentLoadGenomicFile(DatabaseEntry entry, double maxMutationRate, int maximumLength, int minimumLength, double transversionRate, 
			double transitionRate, int numberOfReads, SimulateFormat format, String outDir) {
		this.entry = entry;
		this.maximumRate =  maxMutationRate;
		this.maximumLength = maximumLength;
		this.minimumLength = minimumLength;
		this.transversion = transversionRate;
		this.transition = transitionRate;
		this.numberOfReads = numberOfReads;
		this.format = format;
		this.outDir=outDir;
	}
	public void run(){
		LoadGenomicFile file = new LoadGenomicFile(entry, maximumRate, maximumLength, minimumLength, transversion, 
				transition, numberOfReads, format, outDir);
		file.processFile();
		file.writeFile();
	}
	
}
