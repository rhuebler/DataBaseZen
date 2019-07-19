package DatabaseDownloader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
// ADD ascp support
public class DownLoader {
	boolean result = false;
	DatabaseEntry entry;
	public boolean getResult() {
		return result;
	}
	public DatabaseEntry getEntry() {
		return entry;
	}
		public boolean downLoadCompleteReference(DatabaseEntry entry) {
			this.entry = entry;
		String url = entry.getLink();
		String fileName = entry.getOutFile();
		try{
			if(!new File(fileName).exists()) {
				URLConnection conn = new URL(url).openConnection();
				conn.setConnectTimeout(30*1000);
				conn.setReadTimeout(90*1000);
				try (InputStream in = conn.getInputStream()) {
					CopyOption[] options = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
					Files.copy(in, Paths.get(fileName), options);		
					result = true;
		    	}catch(Exception e) {
		    		e.printStackTrace();
		    	}
			} 
		}catch( IOException io) {
			io.printStackTrace();
		}
		return result;
	}
		
		public void downLoadAssembly(DatabaseEntry entry, int lengthThreshold) {
			String url = entry.getLink();
			String fileName = entry.getOutFile();
			ArrayList<String> output = new  ArrayList<String>();
			try{
				URLConnection conn = new URL(url).openConnection();
				 conn.setConnectTimeout(90*1000);
				 conn.setReadTimeout(90*1000);
				   try (InputStream in = conn.getInputStream()) {
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
					e.printStackTrace();
				    }
			}catch(IOException io) {
				io.printStackTrace();
			}	
			if(output.size()>0) {
			 try (   FileOutputStream outputStream = new FileOutputStream(fileName, false);
					 BufferedOutputStream buffered =  new BufferedOutputStream(outputStream);
		             Writer writer = new OutputStreamWriter(new GZIPOutputStream(buffered), "UTF-8")) {
				 for(String s : output) {
		            writer.write(s);
				 }
		        }catch(IOException io) {
		        	System.err.println("FileName "+fileName+"\n"+"URL: "+url);
					io.printStackTrace();
				}
			 result = true;
			}
		}	
}
