package Utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import DatabaseDownloader.DatabaseEntry;

public class IndexWriter {
	private String header = "Name\ttaxID\tspeciesTaxID\tassembly_level\tseq_rel_date\tasm_name\tFileName\tDownLoadDate\tNumberTotalContigs\tNumberKeptContigs\tNumberRemovedContigs";
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

	public void setOutput(String output) {
		this.output = output;
	}

	public void appendEntriesToDatabaseIndex(ArrayList<DatabaseEntry> entries) {
		 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),true)))
		 	{		if(entries.size()>0) {
		 			for(DatabaseEntry entry : entries) {	
		 				System.out.println(entry.getIndexLine());
		 				br.write(entry.getIndexLine());
		 				br.newLine();
		 			}
		 		}
	        }catch(IOException io) {
				io.printStackTrace();
		}
}
	
	public void appendEntryToDatabaseIndex(DatabaseEntry entry) {
		 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),true)))
		 	{		System.out.println(entry.getIndexLine());
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
		if(!entriesToIndex.isEmpty()) {
			 try ( BufferedWriter br  = new BufferedWriter( new FileWriter(new File(output+"index.txt"),false)))
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