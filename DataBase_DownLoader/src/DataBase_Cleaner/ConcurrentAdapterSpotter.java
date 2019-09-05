package DataBase_Cleaner;

import java.util.concurrent.Callable;

import DatabaseDownloader.DatabaseEntry;

public class ConcurrentAdapterSpotter implements Callable<AdapterSpotter>{
	private DatabaseEntry databaseEntry;
	public ConcurrentAdapterSpotter(DatabaseEntry databaseEntry){
		this.databaseEntry = databaseEntry;
	}
	@Override
	public AdapterSpotter call() throws Exception {
		AdapterSpotter spotter = new AdapterSpotter();
		spotter.process(databaseEntry);
		return spotter;
	}

}
