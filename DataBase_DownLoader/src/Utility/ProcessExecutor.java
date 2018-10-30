package Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProcessExecutor {
	public boolean run(ArrayList<String> command){
		if(command!=null&&command.size()!=0) {
			String l ="";
			for(String s : command){
				System.out.println(s);
				l+=s+" ";
			}
			System.out.println(l);
			ProcessBuilder builder = new ProcessBuilder (command);
			boolean continueRun = false;
			//Map<String, String> environ = builder.environment();
			try {
				final Process process = builder.start();
			    if(process.isAlive()){
			    	 	BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				    String line;
				    while ((line = br.readLine()) != null) {
				      System.out.println(line);
				    }
				    BufferedReader be = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				    while ((line = be.readLine()) != null) {
				    	System.err.println(line);
				    }
				    int status = process.waitFor();
				    System.out.println("Process Exited with status: " + status);
			    }
				continueRun = true;
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(continueRun)
				return continueRun;
			else{
				System.out.println("Process interuppted");
				System.exit(1);
				return continueRun;
			}	
		}
		else {
			System.out.println("Process interuppted");
			System.exit(1);
			return false;
		}
	}

}
