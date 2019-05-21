package Utility;

import java.util.Comparator;

import DatabaseDownloader.DatabaseEntry;


public class DatabaseEntryComparator implements Comparator<DatabaseEntry>{

	@Override
	public int compare(DatabaseEntry o1, DatabaseEntry o2) {
		// TODO Auto-generated method stub
		return (int) (o2.getQualityValue()-o1.getQualityValue());
	}

}
