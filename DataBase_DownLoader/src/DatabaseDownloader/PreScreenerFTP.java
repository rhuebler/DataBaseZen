package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.net.ftp.FTPClient;

import CommandLineProcessor.InputParameterProcessor;
import Utility.DatabaseEntryComparatorPrior;
import Utility.State;

public class PreScreenerFTP {
	//private ArrayList<DatabaseEntry> allEntries = new ArrayList<DatabaseEntry>();
	private int removed =0;
	private Integer priorToScreenValue= 25;
	private int cutOffL50 = 0;
	private int cutOffN50 = 0;
	private int cutOffScore = 0;
	private int cutOffTotalGapLength = 0;
	
	private int cutOffTotalLengthAssembly= 0; 
	private int cutOffSpannedGaps= 0; 
	private int cutOffRegionCount= 0; 
	private int cutOffContigCount= 0; 
	private int cutOffUnspannedGaps= 0; 

	private int cutOffMoleculeCount= 0;  
	private int cutOffTopLevelCount= 0;  
	private int cutOffComponentCount= 0;
	
	
	
	
	
	private ArrayList<DatabaseEntry> failedEntries = new ArrayList<DatabaseEntry>();
	private HashMap<Integer, ArrayList<DatabaseEntry>> entriesByID = new HashMap<Integer, ArrayList<DatabaseEntry>>();
	public HashMap<Integer, Integer> entriesByAmount = new HashMap<Integer, Integer>();
	public ArrayList<DatabaseEntry> getFailedEntries() {
		return this.failedEntries;
	}
	public int getRemoved() {
		return this.removed;
	}
	public  PreScreenerFTP(InputParameterProcessor input) {
		this.priorToScreenValue = input.getPriorToScreenValue();
		this.cutOffL50 = input.getCutOffL50();
		this.cutOffN50 = input.getCutOffN50();
		this.cutOffScore = input.getCutOffScore();
		this.cutOffTotalGapLength = input.getCutOffTotalGapLength();
		cutOffTotalLengthAssembly = input.getCutOffTotalLengthAssembly(); 
		cutOffSpannedGaps = input.getCutOffSpannedGaps(); 
		cutOffRegionCount = input.getCutOffRegionCount(); 
		cutOffContigCount = input.getCutOffContigCount(); 
		cutOffUnspannedGaps = input.getCutOffUnspannedGaps();
		cutOffMoleculeCount = input.getCutOffMoleculeCount();  
		cutOffTopLevelCount = input.getCutOffTopLevelCount();  
		cutOffComponentCount = input.getCutOffComponentCount();
		
	}
	public void preScreenEntriesFTP(ArrayList<DatabaseEntry> entries) {
		failedEntries.clear();
		System.out.println("Prescreen Index");
		DatabaseEntryComparatorPrior prior = new DatabaseEntryComparatorPrior();

		FTPClient client = new FTPClient();
	    try {
	    	client.connect("ftp.ncbi.nlm.nih.gov");
		    client.login("anonymous", "");
		    client.enterLocalPassiveMode();
		    int i = 0;
	    for(DatabaseEntry entry : entries) {
		    	if(!entry.getAsssemblySummaryLink().contains("na/na_assembly_stats.txt")){
		    	boolean result = false;
				String line;
				String fileName =entry.getAsssemblySummaryLink().toString().replace("ftp://ftp.ncbi.nlm.nih.gov/", "");
				if(client.isAvailable()&&client.isConnected()) {
				//System.out.println(client.getReplyCode());
				try{
					InputStream is =client.retrieveFileStream(fileName);
					InputStreamReader in = new InputStreamReader(is);
					BufferedReader reader = new BufferedReader(in);
				   while((line = reader.readLine())!= null) {
						if (line.contains("all")) {
							if (line.contains("total-length")) {
								String parts[] = line.toString().split("\\t");
								if(parts[0].equals("all")) {
									entry.setTotalLengthAssembly(Integer.parseInt(parts[parts.length-1]));
								}	
							}
							if (line.contains("spanned-gaps")) {
								String parts[] = line.toString().split("\\t");
								entry.setSpannedGaps(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("unspanned-gaps")) {
								String parts[] = line.toString().split("\\t");
								entry.setUnspannedGaps(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("region-count")) {
								String parts[] = line.toString().split("\\t");
								entry.setRegionCount(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("contig-count")) {
								String parts[] = line.toString().split("\\t");
								entry.setContigCount(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("contig-N50")) {
								
								String parts[] = line.toString().split("\\t");
								entry.setContigCountN50(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("contig-L50")) {
								
								String parts[] = line.toString().split("\\t");
								entry.setContigCountL50(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("total-gap-length")) {
								String parts[] = line.toString().split("\\t");
								entry.setTotalGapLength(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("molecule-count")) {
								String parts[] = line.toString().split("\\t");
								entry.setMoleculeCount(Integer.parseInt(parts[parts.length-1]));
							}
							if (line.contains("top-level-count")) { 
								String parts[] = line.toString().split("\\t");
								entry.setTopLevelCount(Integer.parseInt(parts[parts.length-1]));			
							}
							if (line.contains("component-count")){
								String parts[] = line.toString().split("\\t");
								entry.setComponentCount(Integer.parseInt(parts[parts.length-1]));
							}
				}
			}
			is.close();
			in.close();	   
			reader.close();	
			client.completePendingCommand();
			result = true;
			}catch(SocketException s) {
				System.err.println(entry.getAsssemblySummaryLink() + " cannot be read");
				i++;
				client.disconnect();
				client.connect("ftp.ncbi.nlm.nih.gov");
			    client.login("anonymous", "");
				client.enterLocalPassiveMode();
				System.err.println("Reset Connection to Server"+i+"times");
				//client.setDataTimeout(99999999);
			}catch(IOException io) {
				
				io.printStackTrace();
			}catch(NullPointerException io) {
					System.err.println(entry.getAsssemblySummaryLink() + " cannot be read");
					i++;
					client.disconnect();
					client.connect("ftp.ncbi.nlm.nih.gov");
				    client.login("anonymous", "");
					client.enterLocalPassiveMode();
					System.err.println("Reset Connection to Server"+i+"times");
					//client.setDataTimeout(99999999);
				}
		}else {
					System.out.print("No longer connected to server");
					}
				
				if(result) {
					if(entriesByID.containsKey(entry.getSpeciesTaxID())) {
					ArrayList<DatabaseEntry>list = entriesByID.get(entry.getSpeciesTaxID());
					list.add(entry);
					entriesByID.replace(entry.getSpeciesTaxID(), list);
					entriesByAmount.replace(entry.getSpeciesTaxID(), (entriesByAmount.get(entry.getSpeciesTaxID())+1));
					}else {
					ArrayList<DatabaseEntry>list = new ArrayList<DatabaseEntry>();
					list.add(entry);
					entriesByID.put(entry.getSpeciesTaxID(), list);
					entriesByAmount.put(entry.getSpeciesTaxID(), 1);
					}
				}else {
					failedEntries.add(entry); 
				}
		    }
	    }	
		}catch(IOException io) {
			io.printStackTrace();
			
		}finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
	}
	public ArrayList<DatabaseEntry> getModifiedList(){
		System.out.println("Filter Index by prescreen criteria");
		DatabaseEntryComparatorPrior prior = new DatabaseEntryComparatorPrior();
		ArrayList<DatabaseEntry> modifiedList = new ArrayList<DatabaseEntry>();
		for(int key:entriesByAmount.keySet()) {
				ArrayList<DatabaseEntry> completes = new ArrayList<DatabaseEntry>();
				ArrayList<DatabaseEntry> assemblies = new ArrayList<DatabaseEntry>();
				ArrayList<DatabaseEntry> entriesToDownsample = entriesByID.get(key);
				entriesToDownsample.sort(prior);
			
				//System.out.println("For key: "+key);
				for(DatabaseEntry entry : entriesToDownsample) {
				//	System.out.println(entry.getNCBIQualityValue());
					if(entry.getAssembly_level()!=State.COMPLETE) {
						if(entry.getTotalGapLength() > cutOffTotalGapLength || cutOffTotalGapLength == 0) {
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getContigCountL50() < cutOffL50 || cutOffL50 == 0) {
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getContigCountN50() < cutOffN50 || cutOffN50 == 0) {
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getTotalLengthAssembly()<cutOffTotalLengthAssembly || cutOffTotalLengthAssembly == 0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						} 
						if(entry.getSpannedGaps()<cutOffSpannedGaps || cutOffSpannedGaps == 0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getRegionCount()<cutOffRegionCount || cutOffRegionCount == 0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getComponentCount()<cutOffContigCount || cutOffContigCount ==0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
			
						if(entry.getUnspannedGaps()<cutOffUnspannedGaps || cutOffUnspannedGaps == 0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getMoleculeCount()<cutOffMoleculeCount || cutOffMoleculeCount == 0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getTopLevelCount()<cutOffTopLevelCount || cutOffTopLevelCount == 0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.getComponentCount()<cutOffComponentCount || cutOffComponentCount==0){
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						if(entry.isWantToDownload())
							assemblies.add(entry);
						else
							modifiedList.add(entry);
						
					}else {
						completes.add(entry);
					}
					if(entry.isWantToDownload()==false)
						removed++;
//					modifiedList.add(entry);
//					if(entry.isWantToDownload())
//						i++;
				}
				Collections.shuffle(completes);
				Collections.shuffle(assemblies);
				if(completes.size()>=priorToScreenValue) {
					int i = 0;
					for(DatabaseEntry entry : completes) {
						while(i>=priorToScreenValue) {
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
						}
						i++;
						if(entry.isWantToDownload()==false)
							removed++;
						modifiedList.add(entry);
					}
					for(DatabaseEntry entry: assemblies) {
						entry.setWantToDownload(false);
						entry.setWantToKeep(false);
						removed++;
						modifiedList.add(entry);
					}
				}else {
					int i = 0;
					for(DatabaseEntry entry : completes) {
						modifiedList.add(entry);
						i++;
					}
					for(DatabaseEntry entry: assemblies) {
						while(i>=priorToScreenValue) {
							entry.setWantToDownload(false);
							entry.setWantToKeep(false);
							removed++;
						}
						modifiedList.add(entry);
						i++;
					}
				}
			}
		return modifiedList;
	}
}
