package DataBase_Cleaner;

import java.util.concurrent.Callable;

import DatabaseDownloader.DatabaseEntry;

public class ConcurrentReferenceLengthFilter implements Callable<ReferenceLengthFilter> {
	private int maxLength;
	private DatabaseEntry entry;
	
	public ConcurrentReferenceLengthFilter(DatabaseEntry entry){
		this.entry = entry;
	}
	public ConcurrentReferenceLengthFilter(DatabaseEntry entry, int length){
		this.entry = entry;
		this.maxLength = length;
	}
	public ReferenceLengthFilter call() throws Exception {
		ReferenceLengthFilter filter =  new ReferenceLengthFilter(entry, maxLength);
		filter.process();
		return filter;
	}
}
