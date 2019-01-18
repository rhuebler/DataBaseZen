package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
public class EntryLoader {
	
	
	private ArrayList<String> references = new ArrayList<String>();
	private boolean replaceExisting = true;
	private boolean contigLengthFiltering = true;
	private void downLoadAssembly(String url, String fileName) {
		try{
			URLConnection conn = new URL(url).openConnection();
			 conn.setConnectTimeout(90*1000);
			 conn.setReadTimeout(90*1000);
			   try (InputStream in = URI.create(url).toURL().openStream()) {
				   InputStream gzipStream = new GZIPInputStream(in);
				   Reader decoder = new InputStreamReader(gzipStream);
				   BufferedReader buffered = new BufferedReader(decoder);
				   String line;
				   while((line = buffered.readLine())!=null) {
			 
				   } 
				   buffered.close();
				   decoder.close();
				   gzipStream.close();
			   }catch(Exception e) {
			    	System.err.println("FileName "+fileName+"\n"+"URL: "+url);
			    	e.printStackTrace();
			    }
		}catch(IOException io) {
			io.printStackTrace();
		}
	}
	private void downLoadFile(String url, String fileName) {
		try{
			if(!new File(fileName).exists() || replaceExisting) {
				URLConnection conn = new URL(url).openConnection();
				conn.setConnectTimeout(90*1000);
				conn.setReadTimeout(90*1000);
				try (InputStream in = URI.create(url).toURL().openStream()) {
					CopyOption[] options = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
					Files.copy(in, Paths.get(fileName), options);
					references.add(fileName);
		    	}catch(Exception e) {
		    		System.err.println("FileName "+fileName+"\n"+"URL: "+url);
		    		e.printStackTrace();
		    	}
			} 
		}catch( IOException io) {
			io.printStackTrace();
		}
	}
	public void download(DatabaseEntry entry) throws Exception {
		String url = entry.getLink();
		String fileName = entry.getOutFile();
		if(url.contains("material_genomic")) {
			System.err.println(url);
		}
		if( contigLengthFiltering == true) {
			switch(entry.getAssembly_level()) {
				case COMPLETE:
					downLoadFile(url, fileName);
					break;
				default:
					downLoadAssembly(url, fileName);
				break;	
			}
			
		}else {
			downLoadFile(url, fileName);
		}
		
	}
	public ArrayList<String> getDownLoadedReferences(){
		return references;
	}
}
