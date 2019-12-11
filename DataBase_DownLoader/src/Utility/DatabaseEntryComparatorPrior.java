package Utility;

import java.util.Comparator;

import DatabaseDownloader.DatabaseEntry;

public class DatabaseEntryComparatorPrior implements Comparator<DatabaseEntry>{

	@Override
	public int compare(DatabaseEntry o1, DatabaseEntry o2) {
		// TODO Auto-generated method stub
		return (int) (o2.getNCBIQualityValue()-o1.getNCBIQualityValue());
	}

}
