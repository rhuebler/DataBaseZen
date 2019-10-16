import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import DatabaseDownloader.DatabaseEntry;

public class test {
	public static void downLoadAssembly(String url, int lengthThreshold) {

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
					   //System.out.println(line);
					   if(line.startsWith(">")) {
						   totalNumber++;
						   if(length >=lengthThreshold) {
							   output.add(header);
							   output.addAll(contig);
							   numberKept++;
						   }
						   length = 0;
						   contig.clear();
						   header = line;
					   }else {
						   length += line.length();
						   contig.add(line);
					   }
				   } 
				   if(length >=lengthThreshold) {
					   output.add(header);
					   output.addAll(contig);
					   numberKept++;
				   }
				   buffered.close();
				   decoder.close();
				   gzipStream.close();
			//	 System.out.println(output.size());
				 for(String l : output) {
					// System.out.println(l);
				 }
				  
			   }catch(Exception e) {
				e.printStackTrace();
			    }
		}catch(IOException io) {
			io.printStackTrace();
		}	
		String fileName = "/Users/huebler/Desktop/Zunongwangia mangrovi strain=DSM 24499_IMG-taxon 2622736505 annotated assembly_1334022_2.fna.gz";
		if(output.size()>0) {

			 try (   FileOutputStream outputStream = new FileOutputStream(fileName, false);
					 BufferedOutputStream buffered =  new BufferedOutputStream(outputStream);
		             Writer writer = new OutputStreamWriter(new GZIPOutputStream(buffered), "UTF-8")) {
				 	String line ="";
				 	for(String s : output) {
				 		line+=s+"\n";
				 	}
			
		            writer.write(line);
				 //}
				// writer.flush();
				 writer.close();
		        }catch(IOException io) {
		        	System.err.println("FileName "+fileName+"\n"+"URL: "+url);
					io.printStackTrace();
				}
			
			}
		}	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path ="/Users/huebler/Desktop/Zunongwangia mangrovi strain=DSM 24499_IMG-taxon 2622736505 annotated assembly_1334022_2.fna.gz";
				
		String content;
		String reference="";
		
		 try {                 
			 	String url = "ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCA/900/112/105/GCA_900112105.1_IMG-taxon_2622736505_annotated_assembly/GCA_900112105.1_IMG-taxon_2622736505_annotated_assembly_cds_from_genomic.fna.gz";	
			 	downLoadAssembly(url, 1000);

			 	GZIPInputStream zip = new GZIPInputStream(new FileInputStream(path));            
		        InputStreamReader isr = new InputStreamReader(zip, "UTF8");
		        BufferedReader reader = new BufferedReader(isr);
		        while ((content = reader.readLine()) != null) {
		        	if(!content.startsWith(">"))//ignore header
		        	{
		        		
		        		reference+=content;
		        	}
		        }
		        reader.close();
		        isr.close();
		        zip.close();
		    	
		    } catch (FileNotFoundException e) {
		        System.out.println(e);
		    } catch (IOException e) {
		        System.out.println(e);
		    }
	}

}
