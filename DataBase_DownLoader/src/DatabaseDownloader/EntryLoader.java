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
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class EntryLoader {
	/**
	 * This class is used to download genomes from NCBI. It's subfunctions return true if the download was successfull
	 */
	private ArrayList<DatabaseEntry> references = new ArrayList<DatabaseEntry>();
	private boolean replaceExisting = true;
	private boolean contigLengthFiltering = true;
	private int lengthThreshold = 100000;
	private boolean cleanDB = false;
	private ThreadPoolExecutor executor;
	private int numThreads=4;
	private ArrayList<DatabaseEntry> failedReferences = new ArrayList<DatabaseEntry>();
	private ArrayList<Future<DownLoader>> results = new ArrayList<Future<DownLoader>>();
	public void destroy(){
		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void intiliazeExecutor(int threads){
		this.numThreads = threads;
		executor=(ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
	}
	public void setCleanDB(boolean b) {
		cleanDB =b;
	}
	public void clearFailedReferences() {
		failedReferences.clear();
	}
	private boolean downLoadAssembly(DatabaseEntry entry) {
		String url = entry.getLink();
		String fileName = entry.getOutFile();
		ArrayList<String> output = new  ArrayList<String>();
		boolean result = false;
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
				   references.add(entry);
				  
			   }catch(Exception e) {
				   	failedReferences.add(entry);
			    }
		}catch(IOException io) {
			failedReferences.add(entry);
			io.printStackTrace();
		}	
		if(output.size()>0) {
		 try (   FileOutputStream outputStream = new FileOutputStream(fileName, false);
	                Writer writer = new OutputStreamWriter(new GZIPOutputStream(outputStream), "UTF-8")) {
			 for(String s : output) {
	            writer.write(s);
			 }
	        }catch(IOException io) {
	        	failedReferences.add(entry);
	        	System.err.println("FileName "+fileName+"\n"+"URL: "+url);
				io.printStackTrace();
			}
		 result = true;
		}
		return result;
	}
	private boolean downLoadCompleteReference(DatabaseEntry entry) {
		String url = entry.getLink();
		String fileName = entry.getOutFile();
		boolean result = false;
		try{
			if(!new File(fileName).exists() || replaceExisting) {
				URLConnection conn = new URL(url).openConnection();
				conn.setConnectTimeout(30*1000);
				conn.setReadTimeout(90*1000);
				try (InputStream in = conn.getInputStream()) {
					CopyOption[] options = new CopyOption[] {StandardCopyOption.REPLACE_EXISTING};
					Files.copy(in, Paths.get(fileName), options);
					entry.setCleanDB(cleanDB);
					references.add(entry);
					result = true;
		    	}catch(Exception e) {
		    		failedReferences.add(entry);
		    	}
			} 
		}catch( IOException io) {
			io.printStackTrace();
		}
		return result;
	}
	public boolean download(DatabaseEntry entry) throws Exception {
		String url = entry.getLink();
		boolean result = false;
		if(url.contains("material_genomic")) {
			System.err.println(url);
		}
		if( contigLengthFiltering == true) {
			switch(entry.getAssembly_level()) {
				case COMPLETE:
					result = downLoadCompleteReference(entry);
					break;
				default:
					result = downLoadAssembly(entry);
				break;	
			}
		}else {
		   result = downLoadCompleteReference(entry);
		}
		return result;
	}
	
	public void downloadConcurrently(DatabaseEntry entry) throws Exception {
		String url = entry.getLink();
		if(url.contains("material_genomic")) {
			System.err.println(url);
		}
		if( contigLengthFiltering == true) {
			switch(entry.getAssembly_level()) {
				case COMPLETE:{
					ConcurrentDownloader task = new ConcurrentDownloader(entry);
					Future<DownLoader> future=executor.submit(task);
					results.add(future);
					break;
					}
				default:{
					ConcurrentDownloader task = new ConcurrentDownloader(entry,lengthThreshold);
					Future<DownLoader> future=executor.submit(task);
					results.add(future);
				}
				break;	
			}
		}else {
			ConcurrentDownloader task = new ConcurrentDownloader(entry);
			Future<DownLoader> future=executor.submit(task);
			results.add(future);
		}
		
	}
	public void getResults() {
		for(Future<DownLoader> future: results) {
			DownLoader loader;
			try {
				loader = future.get();
				if(loader.getResult()) {
					references.add(loader.getEntry());
				}	else {
					failedReferences.add(loader.getEntry());
				}	
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public ArrayList<DatabaseEntry> getDownLoadedReferences(){
		return references;
	}
	public ArrayList<DatabaseEntry> getFailedReferences() {
		return failedReferences;
	}
	public void setFailedReferences(ArrayList<DatabaseEntry> failedReferences) {
		this.failedReferences = failedReferences;
	}
	public void setLengthThreshold(int length) {
		this.lengthThreshold = length;
		
	}
}
