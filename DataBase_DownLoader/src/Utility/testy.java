package Utility;

import DatabaseDownloader.taxIDGetter;

public class testy {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//			taxIDGetter getter = new taxIDGetter();
//			getter.processNCBIZipFile();
		
		
		for(int i=1;i<=20000;i++) {
			//Math.round(((i/20000)*100) % 100)
			System.out.println(i+" "+((i*100)/20000)%10);
			if(((Math.round(i/20000)*100) % 100)==10)
				System.out.println((Math.round(i/20000)*100) +" done");
		}
	}

}
