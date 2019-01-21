package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
public class EntryLoader {
	
	
	private ArrayList<String> references = new ArrayList<String>();
	private boolean replaceExisting = true;
	private boolean contigLengthFiltering = true;
	private int lengthThreshold = 1000000;
	private void downLoadAssembly(DatabaseEntry entry) {
		String url = entry.getLink();
		String fileName = entry.getOutFile();
		  ArrayList<String> output = new  ArrayList<String>();
		try{
			URLConnection conn = new URL(url).openConnection();
			 conn.setConnectTimeout(90*1000);
			 conn.setReadTimeout(90*1000);
			   try (InputStream in = URI.create(url).toURL().openStream()) {
				   InputStream gzipStream = new GZIPInputStream(in);
				   Reader decoder = new InputStreamReader(gzipStream);
				   BufferedReader buffered = new BufferedReader(decoder);
				   String line;
				   int length=0;
				   String header="";
				   int totalNumber = 0;
				   int numberKept = 0;
				   ArrayList<String> contig = new  ArrayList<String>();
				 
				   while((line = buffered.readLine())!=null) {
					   if(line.startsWith(">")) {
						   totalNumber++;
						   if(length >=lengthThreshold) {
							   output.add(header);
							   output.addAll(contig);
							   numberKept++;
							   length = 0;
							   contig.clear();
						   }
						   header = line;
					   }else {
						   length += line.length();
						   contig.add(line);
					   }
				   } 
				   buffered.close();
				   decoder.close();
				   gzipStream.close();
				   entry.setTotalContigs(totalNumber);
				   entry.setKeptContigs(numberKept);
			   }catch(Exception e) {
			    	System.err.println("FileName "+fileName+"\n"+"URL: "+url);
			    	e.printStackTrace();
			    }
		}catch(IOException io) {
			io.printStackTrace();
		}
		 try (   FileOutputStream outputStream = new FileOutputStream(fileName);
	                Writer writer = new OutputStreamWriter(new GZIPOutputStream(outputStream), "UTF-8")) {
			 for(String s : output)
	            writer.write(s);


	        }catch(IOException io) {
	        	System.err.println("FileName "+fileName+"\n"+"URL: "+url);
				io.printStackTrace();
			}
	}
	private void downLoadCompleteReference(DatabaseEntry entry) {
		String url = entry.getLink();
		String fileName = entry.getOutFile();
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
		if(url.contains("material_genomic")) {
			System.err.println(url);
		}
		if( contigLengthFiltering == true) {
			switch(entry.getAssembly_level()) {
				case COMPLETE:
					downLoadCompleteReference(entry);
					break;
				default:
					downLoadAssembly(entry);
				break;	
			}
			
		}else {
			downLoadCompleteReference(entry);
		}
		
	}
	public ArrayList<String> getDownLoadedReferences(){
		return references;
	}
}
