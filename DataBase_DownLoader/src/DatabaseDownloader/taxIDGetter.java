package DatabaseDownloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class taxIDGetter {
	String location = "ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip";
	public void process() {
		try{
			byte[] array = null;
			URLConnection conn = new URL(location).openConnection();
			 conn.setConnectTimeout(90*1000);
			 conn.setReadTimeout(90*1000);
			   try (InputStream in = URI.create(location).toURL().openStream()) {
				   ZipInputStream zipStream = new ZipInputStream(in);
				   ZipEntry zipEntry = zipStream.getNextEntry();
			       while (zipEntry != null) {
			    	   byte[] btoRead = new byte[1024];
			    	   ByteArrayOutputStream bout = new ByteArrayOutputStream();; //<- I don't want this!
			            int len = 0;
			            while ((len = zipStream.read(btoRead)) != -1) {
			                bout.write(btoRead, 0, len);
			            }
			            bout.close();
			            array =  bout.toByteArray();
			            zipEntry = zipStream.getNextEntry();
			       }
				   zipStream.close();
				  
				 String lines = new String(array); 
				 System.out.println(lines);
			   }catch(Exception e) {
				   e.printStackTrace();
			    }
		}catch(IOException io) {
			io.printStackTrace();
		}
	}
}
