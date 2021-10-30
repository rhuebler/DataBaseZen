package ArtificalReadGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	private InputParameterProcessor processor;
	private static ThreadPoolExecutor executor;
	private ArrayList<String> referenceNames = new ArrayList<String>();
	private String pathToIndex = "";
	private ArrayList<DatabaseEntry> entries;
	private void loadDatabaseIndex(){
		ArrayList<DatabaseEntry> indexEntries= new ArrayList<DatabaseEntry>();
		File indexFile = new File(pathToIndex) ;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(indexFile));
			String line; 
			int number = 0;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
			    if(number!=0) {
			    	DatabaseEntry entry = new DatabaseEntry(line);
			    	indexEntries.add(entry);
			    }
			    number++;
			} 	
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		
		 entries=indexEntries;
		 System.out.println(entries.size()+"Entries read from Index");
	}
	public ArtificalDataControler(InputParameterProcessor processor) {
		this.processor = processor;
		//referenceNames = processor.getReferenceFiles();
		pathToIndex = processor.getPathToIndex();
	}
	private static void destroy(){
		executor.shutdown();
	}
	public ArtificalDataControler(InputParameterProcessor processor,String pathToIndex) {
		this.processor = processor;
		this.pathToIndex = pathToIndex;
	}
	public void process() {
		loadDatabaseIndex();
		String outDir = processor.getOutDir();
		if(!outDir.endsWith("/")){
			outDir+="/";
		}
		outDir+="simulatedData/";
		new File(outDir).mkdir();
		executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(processor.getNumberOfThreads());//intialize concurrent thread executor 
		System.out.println("Using "+executor.getCorePoolSize()+" cores");
		for(DatabaseEntry entry: entries) {
			if(entry.isWantToDownload()) {
				ConcurrentLoadGenomicFile task = new ConcurrentLoadGenomicFile(entry, processor.getMaximumRate(), processor.getMaximumLength(), processor.getMinimumLength(), processor.getTransversionRate(), processor.getTransitionRate(),
						processor.getNumberOfReads(), processor.getOutputFormat(), outDir);
						executor.submit(task);
			}
		}
		System.out.println("Waiting for read creation");
		destroy();
		System.out.println("All Reads generated");
	}

}
