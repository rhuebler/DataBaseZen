package DatabaseDownloader;

import java.util.concurrent.Callable;
public class ConcurrentDownloader implements Callable<DownLoader> {
	/**
	 * 
	 */
			
	private int maxLength;
	private DatabaseEntry entry;
	
	public ConcurrentDownloader(DatabaseEntry entry){
		this.entry = entry;
	}
	public ConcurrentDownloader(DatabaseEntry entry, int length){
		this.entry = entry;
		this.maxLength = length;
	}
	

	@Override
	public DownLoader call() throws Exception {
		DownLoader loader =  new DownLoader();
//		switch(entry.getAssembly_level()) {
//			case COMPLETE:{
//				loader.downLoadCompleteReference(entry);
//				break;
//			}
//			default:{
//				
//				loader.downLoadAssembly(entry, maxLength);
//				break;
//			}
//		}
		loader.downLoadCompleteReference(entry);
		return loader;
	}
}
