package DatabaseDownloader;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
public class EntryLoader {
	
	
	private ArrayList<String> references = new ArrayList<String>();
	private boolean replaceExisting = true;
	public void download(String fileName, String url) throws Exception {
		if(url.contains("material_genomic"))
			System.err.println(url);
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
	}
	public ArrayList<String> getDownLoadedReferences(){
		return references;
	}
}
