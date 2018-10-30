package ArtificalReadGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

import CommandLineProcessor.InputParameterProcessor;
import Utility.SimulateFormat;

public class ArtificalDataControler {
	private ArrayList<String> references = new ArrayList<String>();
	private InputParameterProcessor processor;
	private static ThreadPoolExecutor executor;
	public ArtificalDataControler(InputParameterProcessor processor) {
		this.processor = processor;
		references = processor.getReferenceFiles();
	}
	private static void destroy(){
		executor.shutdown();
	}
	public ArtificalDataControler(InputParameterProcessor processor, ArrayList<String> references) {
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
		for(String fileName : references) {
			ConcurrentLoadGenomicFile task = new ConcurrentLoadGenomicFile(fileName, processor.getMaximumRate(), processor.getMaximumLength(), processor.getMinimumLength(), processor.getTransversionRate(), processor.getTransitionRate(),
					processor.getNumberOfReads(), processor.getOutputFormat(), outDir);
					executor.execute(task);
//			LoadGenomicFile file = new LoadGenomicFile(fileName, processor.getMaximumRate(), processor.getMaximumLength(), processor.getMinimumLength(), processor.getTransversionRate(), processor.getTransitionRate(),
//					processor.getNumberOfReads(), processor.getOutputFormat(), outDir);
			
		}
		destroy();
		System.out.println("Waiting for read creation");
	}
	
}
