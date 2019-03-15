import java.io.IOException;

import org.apache.commons.cli.ParseException;

import ArtificalReadGenerator.ArtificalDataControler;
import CommandLineProcessor.InputParameterProcessor;
import DatabaseDownloader.DatabaseProcessor;
import DatabaseDownloader.EntryLoader;

public class Database_DownLoader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			InputParameterProcessor inProcessor = new InputParameterProcessor(args);
			inProcessor.getAllOptions();
			switch(inProcessor.getExecutionMode()) {
				default:{
					DatabaseProcessor processor = new DatabaseProcessor(inProcessor);
					processor.process();
					processor.loadDatabase();
					break; 
				}
				case BOTH:{
					DatabaseProcessor processor = new DatabaseProcessor(inProcessor);
					processor.process();
					processor.loadDatabase();
//					ArtificalDataControler controler = new ArtificalDataControler(inProcessor, processor.getReferences());
//					controler.process();
					break;
					}
				case DOWNLOAD:{
					DatabaseProcessor processor = new DatabaseProcessor(inProcessor);
					processor.process();
					processor.loadDatabase();
					break;
					}
				case CREATE:
					ArtificalDataControler controler = new ArtificalDataControler(inProcessor);
					controler.process();
					//System.err.println("Currently not implemented!");
					break;
				case UPDATE:
					DatabaseProcessor processor = new DatabaseProcessor(inProcessor);
					processor.process();
					processor.updateDatabase();
					break;	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
