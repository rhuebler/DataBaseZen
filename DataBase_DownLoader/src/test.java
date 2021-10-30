import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import DatabaseDownloader.DatabaseEntry;

public class test {
	private static HashMap<String,ArrayList<Double>> pathPercentByName = new HashMap<String,ArrayList<Double>>();
	private static String pathToMasign ="/Users/huebler/Desktop/AssingedNodes.txt";
	private static int totalLengthAssembly= 0; 
	private static int spannedGaps= 0; 
	private static int unspannedGaps= 0; 
	private static int regionCount= 0; 
	private static int contigCount= 0; 
	private static int contigCountN50= 0; 
	private static int contigCountL50= 0;  
	private static int totalGapLength= 0;  
	private static int moleculeCount= 0;  
	private static int topLevelCount= 0;  
	private static int componentCount= 0; 
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

		String line = " Vibrio_sp._624788_strain_624788_Draft_1234362.fna.gz";
		System.out.println(line.substring(0,line.length()-3));
		
		// downloadFromFTP();
//		getAssemblyStatistics("ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/000/238/775/GCF_000238775.1_ASM23877v2/GCF_000238775.1_ASM23877v2_assembly_stats.txt");
//		System.out.println(
//				"totalLengthAssembl: "+totalLengthAssembly +"\n"+
//				"spannedGaps: "+spannedGaps+"\n"+
//				"unspannedGaps: "+ unspannedGaps+"\n"+ 
//				"regionCount:"+ regionCount+"\n"+
//				"contigCount: "+ contigCount+"\n"+
//				"contigCountN50: "+ contigCountN50+"\n"+
//				"contigCountL50: " + contigCountL50+"\n"+
//				"totalGapLength "+ totalGapLength+"\n"+
//				"moleculeCount: "+ moleculeCount+"\n"+ 
//				"topLevelCount: "+ topLevelCount+"\n"+
//				"ComponentCount: "+ componentCount);
//		getAssemblyStatistics("ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/005/042/105/GCF_005042105.1_ASM504210v1/GCF_005042105.1_ASM504210v1_assembly_stats.txt");
//		
//		System.out.println(
//		"totalLengthAssembl: "+totalLengthAssembly +"\n"+
//		"spannedGaps: "+spannedGaps+"\n"+
//		"unspannedGaps: "+ unspannedGaps+"\n"+ 
//		"regionCount:"+ regionCount+"\n"+
//		"contigCount: "+ contigCount+"\n"+
//		"contigCountN50: "+ contigCountN50+"\n"+
//		"contigCountL50: " + contigCountL50+"\n"+
//		"totalGapLength "+ totalGapLength+"\n"+
//		"moleculeCount: "+ moleculeCount+"\n"+ 
//		"topLevelCount: "+ topLevelCount+"\n"+
//		"ComponentCount: "+ componentCount);
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
//		String s ="Clostridium_bornimense_strain_type_strain:_M2_40_M2/40_1216932.fna.gz";
//		s =s.replace("=", "_").replace("/", "_").replace(" ", "_").replace("'", "_").replace("\"", "_").replace("\\","_");
//		System.out.println(s);
//		//process();
//		loadDatabaseIndex("/Users/huebler/Desktop/index.txt");
	}
	public static void process() {
		
//		try (BufferedReader reader = new BufferedReader(new FileReader(pathToMasign))){
//			String line = reader.readLine();
//			while (line != null) {
//				
//				if(line.contains("_")) {
//				System.out.println(line);
//				String[] parts =line.split("\t");
//				String name = parts[0].split("\\_")[(parts[0].split("\\_").length-2)];
//				ArrayList<Double> percentages= new ArrayList<Double>(2);
//				percentages.add(Double.parseDouble( parts[1]));
//				percentages.add(Double.parseDouble( parts[2]));
//				pathPercentByName.put(name, percentages);
//				}
//				 line = reader.readLine();
//				// read next line
//			}
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	public static void getAssemblyStatistics(String link) {
		
		String line;
		try {
			URLConnection conn = new URL(link).openConnection();
			 conn.setConnectTimeout(90*1000);
			 conn.setReadTimeout(90*1000);
		   try (BufferedReader reader =new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			   while((line = reader.readLine())!= null)
			if (line.contains("all")) {
				if (line.contains("total-length")) {
					String parts[] = line.toString().split("\\t");
					if(parts[parts.length-3].equals("all"))
						totalLengthAssembly =Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("spanned-gaps")) {
					String parts[] = line.toString().split("\\t");
					spannedGaps=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("unspanned-gaps")) {
					String parts[] = line.toString().split("\\t");
					unspannedGaps=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("region-count")) {
					String parts[] = line.toString().split("\\t");
					regionCount=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("contig-count")) {
					String parts[] = line.toString().split("\\t");
					contigCount=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("contig-N50")) {
					String parts[] = line.toString().split("\\t");
					contigCountN50=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("contig-L50")) {
					String parts[] = line.toString().split("\\t");
					contigCountL50=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("total-gap-length")) {
					String parts[] = line.toString().split("\\t");
					totalGapLength=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("molecule-count")) {
					String parts[] = line.toString().split("\\t");
					moleculeCount=Integer.parseInt(parts[parts.length-1]);
				}
				if (line.contains("top-level-count")) { 
					String parts[] = line.toString().split("\\t");
					topLevelCount=Integer.parseInt(parts[parts.length-1]);			
				}
				if (line.contains("component-count")){
					String parts[] = line.toString().split("\\t");
					componentCount=Integer.parseInt(parts[parts.length-1]);
				}
			}
		}catch(IOException io) {
			System.out.println(link + " cannot be read");
			io.printStackTrace();
			}
		}catch(IOException io) {
			System.out.println(link + " cannot be read");
			io.printStackTrace();
			
		}
		   
	}
	private static ArrayList<DatabaseEntry> loadDatabaseIndex(String pathToIndex){
		ArrayList<DatabaseEntry> indexEntries= new ArrayList<DatabaseEntry>();
		File indexFile = new File(pathToIndex) ;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(indexFile));
			String line; 
			int number = 0;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			    if(number!=0) {
			    	DatabaseEntry entry = new DatabaseEntry(line.toString());
			    	indexEntries.add(entry);
			    }
			    number++;
			} 	
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		
		return indexEntries;
	}
	public static void downloadFromFTP() {
		FTPClient client = new FTPClient();
		String line;
		try {
			String fileName ="ftp://ftp.ncbi.nlm.nih.gov/genomes/all/GCF/000/010/525/GCF_000010525.1_ASM1052v1/GCF_000010525.1_ASM1052v1_assembly_stats.txt";
			URL url = new URL(fileName);
		            client.connect("ftp.ncbi.nlm.nih.gov");
		            System.out.println("here");
		            client.enterRemotePassiveMode();
		            client.login("anonymous", "");
		          
		           try( BufferedReader in = new BufferedReader(new InputStreamReader( client.retrieveFileStream(fileName.replace("ftp://ftp.ncbi.nlm.nih.gov/", ""))))){;
		            // Download file from FTP server.
		           
		            while((line = in.readLine())!= null) {
		            	System.out.println(line);
		            }
		            
		           }
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            try {
		                client.disconnect();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
			
	}
}
