package ArtificalReadGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import CommandLineProcessor.InputParameterProcessor;
import DatabaseDownloader.DatabaseEntry;

/**
 * Is used to process downloaded references into aritifical pseudoreads
 * @author huebler
 *
 */
public class ArtificalDataControler {
	private ArrayList<DatabaseEntry> references = new ArrayList<DatabaseEntry>();
	private InputParameterProcessor processor;
	private static ThreadPoolExecutor executor;
	private ArrayList<String> referenceNames = new ArrayList<String>();
	public ArtificalDataControler(InputParameterProcessor processor) {
		this.processor = processor;
		referenceNames = processor.getReferenceFiles();
	}
	private static void destroy(){
		executor.shutdown();
	}
	public ArtificalDataControler(InputParameterProcessor processor, ArrayList<DatabaseEntry> references) {
		this.processor = processor;
		this.references = references;
	}
	public void process() {
		String outDir = processor.getOutDir();
		if(!outDir.endsWith("/")){
			outDir+="/";
		}
		outDir+="simulatedData/";
		new File(outDir).mkdir();
		executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(processor.getNumberOfThreads());//intialize concurrent thread executor 
		System.out.println("Using "+executor.getCorePoolSize()+" cores");
		if(referenceNames.size()>0) {
			for(String fileName : referenceNames) {
				ConcurrentLoadGenomicFile task = new ConcurrentLoadGenomicFile(fileName, processor.getMaximumRate(), processor.getMaximumLength(), processor.getMinimumLength(), processor.getTransversionRate(), processor.getTransitionRate(),
						processor.getNumberOfReads(), processor.getOutputFormat(), outDir);
						executor.execute(task);
	//			LoadGenomicFile file = new LoadGenomicFile(fileName, processor.getMaximumRate(), processor.getMaximumLength(), processor.getMinimumLength(), processor.getTransversionRate(), processor.getTransitionRate(),
	//					processor.getNumberOfReads(), processor.getOutputFormat(), outDir);
				
			}
		}
		
		if(references.size()>0) {
			for(DatabaseEntry entry : references) {
				ConcurrentLoadGenomicFile task = new ConcurrentLoadGenomicFile(entry.getOutFile(), processor.getMaximumRate(), processor.getMaximumLength(), processor.getMinimumLength(), processor.getTransversionRate(), processor.getTransitionRate(),
						processor.getNumberOfReads(), processor.getOutputFormat(), outDir);
						executor.execute(task);
	//			LoadGenomicFile file = new LoadGenomicFile(fileName, processor.getMaximumRate(), processor.getMaximumLength(), processor.getMinimumLength(), processor.getTransversionRate(), processor.getTransitionRate(),
	//					processor.getNumberOfReads(), processor.getOutputFormat(), outDir);
				
			}
		}
		destroy();
		System.out.println("Waiting for read creation");
	}
	
}
