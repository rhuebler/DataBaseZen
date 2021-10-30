package DatabaseDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.net.ftp.FTPClient;
import Utility.State;

public class DatabaseEntry {
	private boolean monoCladic = false;

	/**
	 * This class represents an entry out of NCBI
	 * 
	 * Maybe you should be a SQL Lite DB???
	 */
	private String assembly_accession; 
	
	private String name;
	private String link;
	private String outDir;
	private String outFile;
	private int taxID;
	private int speciesTaxID;
	private State assembly_level = null;
	private String realm;
	private String seq_rel_date;
	private String asm_name;
	private String representative="na";
	private int totalContigs = 0;
	private int keptContigs = 0;
	private String fileName ="";
	private boolean cleanDB = false;
	private String time;
	private double qualityValue;
	private boolean containsAdapter = false;
	private int adapterOccurance = 0;
	private double onPathPercentageStrict = 0.0;
	private double offPathPercentageStrict = 0.0;
	private double onPathPercentageRelaxed = 0.0;
	private double offPathPercentageRelaxed = 0.0;
	private double totalReadsTaxon =0;
	private double percentageKeptContig = 1.0;
	private int totalLengthAssembly= 0; 
	private int  spannedGaps= 0; 
	private int unspannedGaps= 0; 
	private int regionCount= 0; 
	private int contigCount= 0; 
	private int contigCountN50= 0; 
	private int contigCountL50= 0;  
	private int totalGapLength= 0;  
	private int moleculeCount= 0;  
	private int topLevelCount= 0;  
	private int componentCount= 0; 
	private boolean wantToDownload = true;
	private boolean wantToKeep =  true;
	private String asssemblySummaryLink;
	private String offPathReferences ="NA;NA\tNA;NA\tNA;NA\tNA;NA\tNA;NA\tNA;NA\tNA;NA\tNA;NA\tNA;NA\tNA;NA\t";
	
	public String getAsssemblySummaryLink() {
		return asssemblySummaryLink;
	}
	public void setAsssemblySummaryLink(String asssemblySummaryLink) {
		this.asssemblySummaryLink = asssemblySummaryLink;
	}
	private DecimalFormat df = new DecimalFormat("#.####");
	
	
	
	public String getAssembly_accession() {
		return assembly_accession;
	}
	public void setAssembly_accession(String assembly_accession) {
		this.assembly_accession = assembly_accession;
	}
	public boolean isContainsAdapter() {
		return containsAdapter;
	}
	public void setContainsAdapter(boolean containsAdapter) {
		this.containsAdapter = containsAdapter;
	}
	
	public DatabaseEntry(String assembly_accession, String name, String link, String outDir, String assemblyLevel, 
			int taxID, int speciesTaxID, String seq_rel_date, String asm_name, String representative ,String asssemblySummaryLink)
	{
		Locale loc= Locale.ENGLISH;
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("#.####");
		this.df =df;
		this.assembly_accession =  assembly_accession;
		this.link = link;
		this.name = name.toString();
		this.name = this.name.replaceAll("[\\W]", "_");
		if(!outDir.endsWith("/")){
			this.outDir= outDir+"/";
			
		}
		State state = null;
		if(assemblyLevel.equals("Complete genome")){
				state = State.COMPLETE;
		}
		else if(assemblyLevel.equals("Chromosome")) {
				state = State.CHROMOSOME;
			}
		else if(assemblyLevel.equals("Scaffold")) {
				state =State.SCAFFOLD;
			}
		else if(assemblyLevel.equals("Contig")) {
				state = State.CONTIG;
			}
		else {
			System.out.println(assemblyLevel);
			//state = State.COMPLETE;
		}
		setAssembly_level(state);
		this.setTaxID(taxID);
		this.setSpeciesTaxID(speciesTaxID);
		this.setSeq_rel_date(seq_rel_date);
		this.setAsm_name(asm_name.toString().replaceAll("[\\W]", ""));
		//this.setAsm_name(asm_name.toString().replaceAll("[\\W]", "_");
		this.outFile = outDir+getName()+"_"+getAsm_name()+"_"+taxID+".fna.gz";
		this.representative = representative;
		this.asssemblySummaryLink = asssemblySummaryLink;
		
	}
	public DatabaseEntry(String line) throws IOException {
		Locale loc= Locale.ENGLISH;
		NumberFormat nf = NumberFormat.getNumberInstance(loc);
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("#.####");
		this.df =df;
		String[]parts =	line.split("\t");
//		int i=0;
//		for(String part: parts) {
//			System.out.println(part+"\t"+i);
//			i++;
//		}
		try{
				name = parts[0];
				taxID = Integer.parseInt(parts[1]);
				speciesTaxID = Integer.parseInt(parts[2]); 
				State state = null;
				if(parts[3].equals("COMPLETE")){
						state = State.COMPLETE;
				}
				else if(parts[3].equals("CHROMOSOME")) {
						state = State.CHROMOSOME;
					}
				else if(parts[3].equals("SCAFFOLD")) {
						state =State.SCAFFOLD;
					}
				else if(parts[3].equals("CONTIG")) {
						state = State.CONTIG;
					}
				else {
					state = State.COMPLETE;
				}
				setAssembly_level(state);
				
				seq_rel_date = parts[4];
				asm_name = parts[5];
				outFile = new File(parts[6]).getCanonicalPath();
				this.outDir = new File(this.outFile).getParent();
		    	setFileName(parts[6]);
		    	time = parts[7];
		    	representative = parts[8];
		    	setTotalContigs(Integer.parseInt(parts[9]));    
		    	setKeptContigs(Integer.parseInt(parts[10]));
		    	setPercentageKeptContig(Double.parseDouble(parts[12]));
		    	setContainsAdapter(Boolean.parseBoolean(parts[13]));
		    	setAdapterOccurance(Integer.parseInt(parts[14]));
		    	//Taxonomic Stuff
		    	setOnPathPercentageStrict(Double.parseDouble(parts[15]));
		    	setOffPathPercentageStrict(Double.parseDouble(parts[16]));
		    	setOnPathPercentageRelaxed(Double.parseDouble(parts[17]));
		    	setOffPathPercentageRelaxed(Double.parseDouble(parts[18]));
		    	setTotalReadsTaxon((int) Double.parseDouble(parts[19]));
		    	//NCBI stuff
		    	setTotalLengthAssembly(Integer.parseInt(parts[20]));
		    	setSpannedGaps(Integer.parseInt(parts[21]));
		    	setUnspannedGaps(Integer.parseInt(parts[22]));
		    	setRegionCount(Integer.parseInt(parts[23]));
		    	setContigCount(Integer.parseInt(parts[24]));
		    	setContigCountN50(Integer.parseInt(parts[25]));
		    	setContigCountL50(Integer.parseInt(parts[26]));
		    	setTotalGapLength(Integer.parseInt(parts[27]));
		    	setMoleculeCount(Integer.parseInt(parts[28]));
		    	setTopLevelCount(Integer.parseInt(parts[29]));
		    	setComponentCount(Integer.parseInt(parts[30]));
		    	setWantToDownload(Boolean.parseBoolean(parts[31]));
		    	setWantToKeep(Boolean.parseBoolean(parts[32]));
		    	ArrayList<String> references = 	new ArrayList<String>();
		    	if(parts.length>33) {
			    	for(int i = 33;i<43;i++) {
			    		 references.add(parts[i]);
			    	}
		    	}
		    	setOffPathReferences(references);
		    	}catch(ArrayIndexOutOfBoundsException outOfBound) {
		    		System.err.println(line);
		    		outOfBound.printStackTrace();
		    	}
//		    	if(!wantToDownload&&wantToKeep)
//		    		System.out.println(name+"\t"+state + "\t"+ wantToDownload+"\t"+ wantToKeep);
		}
	public int getNCBIQualityValue() {//TODO rethink this
		int value = 0;
		switch(assembly_level) {
			default:
				value =500+ getContigCountL50();
				break;
			case COMPLETE :{
				value = 10000 ;
				break;
			}	
		}
		
		 //TODO probably to simple
		return value;
	}
	public void getAssemblyStatistics() {

		String line;
			
//			URLConnection conn = new URL(asssemblySummaryLink).openConnection();
//			conn.setUseCaches(true);
//			 conn.setConnectTimeout(90*1000);
//			 conn.setReadTimeout(90*1000);
//		//System.out.println(assembly_level);
//		   try (BufferedReader reader =new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			FTPClient client = new FTPClient();
			System.out.println("here");
			try {
				String fileName = asssemblySummaryLink.toString().replace("ftp://ftp.ncbi.nlm.nih.gov/", "");
			     client.connect("ftp.ncbi.nlm.nih.gov");
			    client.enterRemotePassiveMode();
			    client.login("anonymous", "");
			
			try( BufferedReader reader = new BufferedReader(new InputStreamReader(client.retrieveFileStream(fileName)))){
			   while((line = reader.readLine())!= null) {
			if (line.contains("all")) {

				if (line.contains("total-length")) {
					String parts[] = line.toString().split("\\t");
					if(parts[0].equals("all")) {
						totalLengthAssembly =Integer.parseInt(parts[parts.length-1]);
					}	
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
		}
		//System.out.println(totalLengthAssembly);	   
		}catch(IOException io) {
			System.err.println(asssemblySummaryLink + " cannot be read");
			io.printStackTrace();
			}
		}catch(IOException io) {
			System.err.println(asssemblySummaryLink + " cannot be read");
			io.printStackTrace();
			
		}finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		   
	}
	public void setQualityValue() {
		switch(assembly_level){
			case COMPLETE:
				qualityValue = 100;
				break;
			case CHROMOSOME:
				qualityValue = (90-(totalContigs-keptContigs)/2);
				break;
			case SCAFFOLD:
				qualityValue = (80-(totalContigs-keptContigs)/2);
				break;	
			case CONTIG:
				qualityValue = (70-(totalContigs-keptContigs)/2);
				break;
		default:
			qualityValue=0;
			break;
		}
	}
	public double getQualityValue() {
		return qualityValue;
	}
	public void setCleanDB(boolean b) {
		cleanDB =b;
	}
	public void setFileName(String s) {
		fileName=s;
	}
	public String getOutFile() {
		return this.outFile;
	}
	
	public String getFilteredFile() {
		return outDir+name+"_"+asm_name+"_dusted_"+taxID+".fna.gz";
	}
	public String getName() {
		return name;
	}
	public int getCode() {
		String s = name+"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t";
		return s.hashCode();
	}
	public String getLink() {
		return link;
	}
	public String getAsm_name() {
		return asm_name;
	}
	public void setAsm_name(String asm_name) {
		this.asm_name = asm_name;
	}
	public String getSeq_rel_date() {
		return seq_rel_date;
	}
	public void setSeq_rel_date(String seq_rel_date) {
		this.seq_rel_date = seq_rel_date;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public State getAssembly_level() {
		return assembly_level;
	}
	public void setAssembly_level(State assembly_level) {
		this.assembly_level = assembly_level;
	}
	public int getSpeciesTaxID() {
		return speciesTaxID;
	}
	public void setSpeciesTaxID(int speciesTaxID) {
		this.speciesTaxID = speciesTaxID;
	}
	public int getTaxID() {
		return taxID;
	}
	public void setTaxID(int taxID) {
		this.taxID = taxID;
	}
	public String getIndexLine() {
			if(time!=null) {
				//System.out.println("I have the time of my Download and I owe it all to DSL");
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getOutFile()+"\t"+time
						+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs
						+"\t"+(totalContigs-keptContigs)+"\t"+df.format(percentageKeptContig)
						+"\t"+containsAdapter+ "\t"+ adapterOccurance +"\t"+ df.format(onPathPercentageStrict)
						+"\t"+df.format(offPathPercentageStrict)+"\t"+ df.format(onPathPercentageRelaxed)
						+"\t"+df.format(offPathPercentageRelaxed)+"\t"+totalReadsTaxon
						+"\t"+totalLengthAssembly+"\t"+ spannedGaps+"\t"+ unspannedGaps
						+"\t"+regionCount+"\t"+contigCount+"\t"+ contigCountN50+"\t"+contigCountL50+"\t"+ totalGapLength 
						+"\t"+ moleculeCount+"\t"+ topLevelCount+"\t"+ componentCount+"\t"+ wantToDownload+"\t"+ wantToKeep
						+"\t"+offPathReferences+monoCladic;
			}else	{
				return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date
						+"\t"+asm_name+"\t"+getOutFile()+"\t"+ZonedDateTime.now()
						+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs
						+"\t"+(totalContigs-keptContigs)+"\t"+df.format(percentageKeptContig)+
						"\t"+containsAdapter+ "\t"+ adapterOccurance +"\t"+ df.format(onPathPercentageStrict)
						+"\t"+ df.format(offPathPercentageStrict)+"\t"+ df.format(onPathPercentageRelaxed)+"\t"
						+ df.format(offPathPercentageRelaxed)+"\t"+totalReadsTaxon
						+"\t"+totalLengthAssembly+"\t"+ spannedGaps+"\t"+ unspannedGaps+"\t"+regionCount+"\t"
						+contigCount+"\t"+ contigCountN50+"\t"+contigCountL50+"\t"+ totalGapLength 
						+"\t"+ moleculeCount+"\t"+ topLevelCount+"\t"+ componentCount+"\t"+ wantToDownload+"\t"+ wantToKeep
						+"\t"+offPathReferences+monoCladic;
				
			}
	}

	public String getCleanedIndexLine() {
		if(time!=null)
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+time+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
		else	
			return name +"\t"+taxID+"\t"+speciesTaxID+"\t"+assembly_level+"\t"+seq_rel_date+"\t"+asm_name+"\t"+getFilteredFile()+"\t"+ZonedDateTime.now()+"\t"+representative+"\t"+totalContigs+"\t"+keptContigs+"\t"+(totalContigs-keptContigs);
}
	public String failedState() {
		return name + "\t" +link + "\t"+outDir;
	}
	public int getTotalContigs() {
		return totalContigs;
	}
	public void setTotalContigs(int totalContigs) {
		this.totalContigs = totalContigs;
	}
	public int getKeptContigs() {
		return keptContigs;
	}
	public void setKeptContigs(int keptContigs) {
		this.keptContigs = keptContigs;
	}
	public int getAdapterOccurance() {
		return adapterOccurance;
	}
	public void setAdapterOccurance(int adapterOccurance) {
		this.adapterOccurance = adapterOccurance;
	}
	public double getPercentageKeptContig() {
		return percentageKeptContig;
	}
	public void setPercentageKeptContig(double percentageKeptContig) {
		this.percentageKeptContig = percentageKeptContig;
	}
	public int getTotalReadsTaxon() {
		return (int) totalReadsTaxon;
	}
	public void setTotalReadsTaxon(int i) {
		this.totalReadsTaxon = i;
	}
	public double getOffPathPercentageRelaxed() {
		return offPathPercentageRelaxed;
	}
	public void setOffPathPercentageRelaxed(double offPathPercentageRelaxed) {
		this.offPathPercentageRelaxed = offPathPercentageRelaxed;
	}
	public double getOffPathPercentageStrict() {
		return offPathPercentageStrict;
	}
	public void setOffPathPercentageStrict(double offPathPercentageStrict) {
		this.offPathPercentageStrict = offPathPercentageStrict;
	}
	public double getOnPathPercentageStrict() {
		return onPathPercentageStrict;
	}
	public void setOnPathPercentageStrict(double onPathPercentageStrict) {
		this.onPathPercentageStrict = onPathPercentageStrict;
	}
	public double getOnPathPercentageRelaxed() {
		return onPathPercentageRelaxed;
	}
	public void setOnPathPercentageRelaxed(double onPathPercentageRelaxed) {
		this.onPathPercentageRelaxed = onPathPercentageRelaxed;
	}
	public int getTotalLengthAssembly() {
		return totalLengthAssembly;
	}
	public void setTotalLengthAssembly(int totalLengthAssembly) {
		this.totalLengthAssembly = totalLengthAssembly;
	}
	public int getSpannedGaps() {
		return spannedGaps;
	}
	public void setSpannedGaps(int spannedGaps) {
		this.spannedGaps = spannedGaps;
	}
	public int getUnspannedGaps() {
		return unspannedGaps;
	}
	public void setUnspannedGaps(int unspannedGaps) {
		this.unspannedGaps = unspannedGaps;
	}
	public int getRegionCount() {
		return regionCount;
	}
	public void setRegionCount(int regionCount) {
		this.regionCount = regionCount;
	}
	public int getContigCount() {
		return contigCount;
	}
	public void setContigCount(int contigCount) {
		this.contigCount = contigCount;
	}
	public int getContigCountN50() {
		return contigCountN50;
	}
	public void setContigCountN50(int contigCountN50) {
		this.contigCountN50 = contigCountN50;
	}
	public int getContigCountL50() {
		return contigCountL50;
	}
	public void setContigCountL50(int contigCountL50) {
		this.contigCountL50 = contigCountL50;
	}
	public int getTotalGapLength() {
		return totalGapLength;
	}
	public void setTotalGapLength(int totalGapLength) {
		this.totalGapLength = totalGapLength;
	}
	public int getMoleculeCount() {
		return moleculeCount;
	}
	public void setMoleculeCount(int moleculeCount) {
		this.moleculeCount = moleculeCount;
	}
	public int getTopLevelCount() {
		return topLevelCount;
	}
	public void setTopLevelCount(int topLevelCount) {
		this.topLevelCount = topLevelCount;
	}
	public int getComponentCount() {
		return componentCount;
	}
	public void setComponentCount(int componentCount) {
		this.componentCount = componentCount;
	}
	public boolean isWantToDownload() {
		return wantToDownload;
	}
	public void setWantToDownload(boolean wantToDownload) {
		this.wantToDownload = wantToDownload;
	}
	public boolean isWantToKeep() {
		return wantToKeep;
	}
	public void setWantToKeep(boolean wantToKeep) {
		this.wantToKeep = wantToKeep;
	}
	public void setOffPathReferences(List<String> list) {
		String line="";
		for(String part:list) {
			line+=part+"\t";
		}
		offPathReferences = line;
	}
	public String getoffPathReferences() {
		return this.offPathReferences;
	}
	public void setMonoCladic(boolean parseBoolean) {
		monoCladic = parseBoolean;
		
	}
	public boolean isMonoCladic() {
		return this.monoCladic;
	}
	public void setOutFile(String string) {
		this.outFile = string;
		
	}
}
