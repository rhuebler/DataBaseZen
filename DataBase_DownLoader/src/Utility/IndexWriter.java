package Utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import DatabaseDownloader.DatabaseEntry;
/**
 * Writes and updates Database indeices 
 * @author huebler
 *
 */
public class IndexWriter {
	private String header = "Name\ttaxID\tspeciesTaxID\tassembly_level\tseq_rel_date\tasm_name\tFileName\tDownLoadDate\treference\tNumberTotalContigs"
			+ "\tNumberKeptContigs\tNumberRemovedContigs\tPercentageKept\tAdapter\tAdapterOccurance"
			+ "\tOnPathStrict\tOffPathStrict\tOnPathRelaxed\tOffPathRelaxed\tTotalReads"
			+"\ttotalLengthAssembly\tspannedGaps\tunspannedGaps\t"
			+ "regionCount\tcontigCount\tcontigCountN50\tcontigCountL50\ttotalGapLength" 
			+"\tmoleculeCount\ttopLevelCount\tcomponentCount\twantToDownload\twantToKeep\t"
			+"off_reference_01\toff_reference_02\toff_reference_03\toff_reference_04\toff_reference_05\t"
			+ "off_reference_06\toff_reference_07\toff_reference_08\toff_reference_09\toff_reference_10\tmonoCladic";
	private ArrayList<DatabaseEntry> references;
	public ArrayList<DatabaseEntry> getReferences() {
		return references;
	}

	public void setReferences(ArrayList<DatabaseEntry> references) {
		this.references = references;
	}
	private String output;
	public String getOutput() {
		return output;
	}
	public String geIndex() {
		return output+"index.txt";
	}
	public void setOutput(String output) {
		this.output = output;
	}

	public void appendEntriesToDatabaseIndex(ArrayList<DatabaseEntry> entries) {
		 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),true)))
		 	{		if(entries.size()>0) {
		 			for(DatabaseEntry entry : entries) {	
		 			//	System.out.println(entry.getIndexLine());
		 				br.write(entry.getIndexLine());
		 				br.newLine();
		 			}
		 		}
	        }catch(IOException io) {
				io.printStackTrace();
		}
}
	public void appendCleanEntriesToDatabaseIndex(ArrayList<DatabaseEntry> entries) {
		 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),true)))
		 	{		if(entries.size()>0) {
		 			for(DatabaseEntry entry : entries) {	
		 			//	System.out.println(entry.getIndexLine());
		 				br.write(entry.getCleanedIndexLine());
		 				br.newLine();
		 			}
		 		}
	        }catch(IOException io) {
				io.printStackTrace();
		}
}
	public void appendEntryToDatabaseIndex(DatabaseEntry entry) {
		 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),true)))
		 	{		//System.out.println(entry.getIndexLine());
		 			br.write(entry.getIndexLine());
		 			br.newLine();	
	        }catch(IOException io) {
				io.printStackTrace();
		}
	}
	public void initializeDatabaseIndex() {
		try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),false)))
		 {
			br.write(header);
			br.newLine();
		 }catch(IOException io) {
				io.printStackTrace();
		}
	}
	public void initializeDatabaseBackupIndex() {
		try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index_backup.txt"),false)))
		 {
			br.write(header);
			br.newLine();
		 }catch(IOException io) {
				io.printStackTrace();
		}
	}
	public void writeDatabaseIndex() {
	if(references!=null) {
		 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),false)))
		 {
			br.write(header);
			br.newLine();
			 for(DatabaseEntry entry : references) {
					br.write(entry.getIndexLine());
					br.newLine();
			 }
	        }catch(IOException io) {
				io.printStackTrace();
			}
		}
	}
	public void writeDatabaseIndex(ArrayList<DatabaseEntry> entriesToIndex) {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		int i =1;
		if(!entriesToIndex.isEmpty()) {
			 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),false)))
			 {
				br.write(header);
				br.newLine();
				
				 for(DatabaseEntry entry : entriesToIndex) {
//					String[] headerP = header.split("\t");
//					String[] lineP = entry.getIndexLine().split("\t");
//					int i=0;
//					while(i<headerP.length)
//						System.out.println(headerP[i]+"\t"+ lineP[i] );
					 	if(entry.getIndexLine().split("\t").length != header.split("\t").length)
					 			numbers.add(i);
						br.write(entry.getIndexLine());
						br.newLine();
						i++;
				 }
		        }catch(IOException io) {
					io.printStackTrace();
				}
			}
			if(numbers.size()>0) {
				String line = "Index corrptet at row(s):";
				for(int number : numbers)
					line+= "\n" +number;
				System.err.println(line);	
			}
		}
	public void writeDatabaseBackupIndex(ArrayList<DatabaseEntry> entriesToIndex) {
		if(!entriesToIndex.isEmpty()) {
			 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index_backup.txt"),false)))
			 {
				br.write(header);
				br.newLine();
				 for(DatabaseEntry entry : entriesToIndex) {
						br.write(entry.getIndexLine());
						br.newLine();
				 }
		        }catch(IOException io) {
					io.printStackTrace();
				}
			}
		}
//	public void writeDatabaseTaxonomicIndex(ArrayList<DatabaseEntry> entriesToIndex) {
//		if(!entriesToIndex.isEmpty()) {
//			 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),false)))
//			 {
//				br.write(header);
//				br.newLine();
//				 for(DatabaseEntry entry : entriesToIndex) {
//						br.write(entry.getPathContaminedIndexLine());
//						br.newLine();
//				 }
//		        }catch(IOException io) {
//					io.printStackTrace();
//				}
//			}
//		}
//	public void writeDatabaseAdapterIndex(ArrayList<DatabaseEntry> entriesToIndex) {
//		if(!entriesToIndex.isEmpty()) {
//			 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),false)))
//			 {
//				br.write(header);
//				br.newLine();
//				 for(DatabaseEntry entry : entriesToIndex) {
//						br.write(entry.getAdapterContainedIndexLine());
//						br.newLine();
//				 }
//		        }catch(IOException io) {
//					io.printStackTrace();
//				}
//			}
//		}
	public void writeCleanDatabaseIndex(ArrayList<DatabaseEntry> entriesToIndex) {
		if(!entriesToIndex.isEmpty()) {
			 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),false)))
			 {
				br.write(header);
				br.newLine();
				 for(DatabaseEntry entry : entriesToIndex) {
						br.write(entry.getCleanedIndexLine());
						br.newLine();
				 }
		        }catch(IOException io) {
					io.printStackTrace();
				}
			}
		}
	public void writeFailedEntires(ArrayList<DatabaseEntry> entriesToIndex) {
		String header = "Name\tlink\toutput_directory";

		if(!entriesToIndex.isEmpty()) {
			 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"failedEntries.txt"),false)))
			 {
				br.write(header);
				br.newLine();
				 for(DatabaseEntry entry : entriesToIndex) {
						br.write(entry.getIndexLine());
						br.newLine();
				 }
		        }catch(IOException io) {
					io.printStackTrace();
				}
			}
		}
}
