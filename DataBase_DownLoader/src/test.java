import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import DatabaseDownloader.DatabaseEntry;

public class test {
	private static HashMap<String,ArrayList<Double>> pathPercentByName = new HashMap<String,ArrayList<Double>>();
	private static String pathToMasign ="/Users/huebler/Desktop/AssingedNodes.txt";
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
		/* TODO Auto-generated method stub
		String path = "/Users/huebler/Desktop/Zunongwangia mangrovi strain=DSM 24499_IMG-taxon 2622736505 annotated assembly_1334022_2.fna.gz";
		//path= "/Users/huebler/Desktop/ASM187452v1_2125990.fna.gz";		
		String content;
		String reference="";
		
		 try {                 
			 	String url = "ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCA/900/112/105/GCA_900112105.1_IMG-taxon_2622736505_annotated_assembly/GCA_900112105.1_IMG-taxon_2622736505_annotated_assembly_cds_from_genomic.fna.gz";	
			 	//downLoadAssembly(url, 1000);

			 	GZIPInputStream zip = new GZIPInputStream(new FileInputStream(path));            
		        InputStreamReader isr = new InputStreamReader(zip, "UTF8");
		        BufferedReader reader = new BufferedReader(isr);
		        while ((content = reader.readLine()) != null) {
		        	if(content.startsWith(">"))//ignore header
		        	{
		        		
		        		reference+=content;
		        	}
		        	if(!content.startsWith(">"))//ignore header
		        	{
		        		
		        		reference+=content;
		        	}
		        	System.out.println(content);
		        }
		        reader.close();
		        isr.close();
		        zip.close();
		    	
		    } catch (FileNotFoundException e) {
		        System.out.println(e);
		    } catch (IOException e) {
		        System.out.println(e);
		    }*/
		process();
	}
	public static void process() {
		try (BufferedReader reader = new BufferedReader(new FileReader(pathToMasign))){
			String line = reader.readLine();
			while (line != null) {
				
				if(line.contains("_")) {
				System.out.println(line);
				String[] parts =line.split("\t");
				String name = parts[0].split("\\_")[(parts[0].split("\\_").length-2)];
				ArrayList<Double> percentages= new ArrayList<Double>(2);
				percentages.add(Double.parseDouble( parts[1]));
				percentages.add(Double.parseDouble( parts[2]));
				pathPercentByName.put(name, percentages);
				}
				 line = reader.readLine();
				// read next line
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
