package com.danilafe.bukkitdownloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.plaf.FileChooserUI;

public class Main {
	
	int bytecount = 0;
	int buffer = 0;
	HttpURLConnection con;
	int filecontent = -1;
	final String DEVBUILD = "http://dl.bukkit.org/downloads/craftbukkit/get/latest-dev/craftbukkit.jar"; 
	final String RECBUILD = "http://dl.bukkit.org/latest-rb/craftbukkit.jar"; 
	final String BETABUILD = "http://dl.bukkit.org/downloads/craftbukkit/get/latest-beta/craftbukkit.jar"; 
	String system = System.getProperty("os.name");
	String downloadlocation;
	File path;
	String[] filetypes = new String[]{
			"Recommended", "Dev" , "Beta"
	};
	String[] yesno = new String[]{
		"Yes", "No"	
	};
	
	
	public Main(){
		JFileChooser j = new JFileChooser();
		j.setFileSelectionMode(j.DIRECTORIES_ONLY);
		int opt = j.showDialog(null, "Select Folder");
		if(opt == j.APPROVE_OPTION){
			generateServer(j);
		}
		
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	public void downloadfile(){
		
	}
	
	void generateServer(JFileChooser j){
		File direcotry = j.getSelectedFile();
		String selected = (String)JOptionPane.showInputDialog(null,"Select Bukkit Version", "Version Select", JOptionPane.QUESTION_MESSAGE, null,filetypes,filetypes[1]);
		if(selected != null){
			System.out.println(selected);
			URL download = null;
			switch(selected.toLowerCase()){
			case "recommended":
				try {
					download = new URL(RECBUILD);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "dev":
				try {
					download = new URL(DEVBUILD);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "beta":
				try {
					download = new URL(BETABUILD);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			
			/*
			 * Download file and generate Start.command
			 */
			try{
				InputStream is = download.openStream();
				con = (HttpURLConnection)download.openConnection();
				
				if(con.getResponseCode() / 100 == 2)  
				{  
				    // This should get you the size of the file to download (in bytes)  
				    int contentLength = con.getContentLength();  
				    System.out.println(contentLength);
				    filecontent = contentLength;
				} 
				
				FileOutputStream fos = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "craftbukkit.jar");
				BufferedInputStream br = new BufferedInputStream(is);
				
				JUpdateFrame updateframe = new JUpdateFrame(this);
				byte[] data = new byte[1024];
				while((buffer = br.read(data, 0, 1024)) != -1){
					fos.write(data,0, buffer);
					bytecount += buffer;
				}
				is.close();
				fos.close();
				br.close();
				updateframe.doclose();
				
				int ram = getRam();
				System.out.println(system);
				if(system.startsWith("Windows")){
					FileOutputStream fos2 = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "start.bat");
					PrintWriter pw = new PrintWriter(fos2);
					pw.println("java -Xmx" + ram + "G -jar craftbukkit.jar -o true");
					pw.println("PAUSE");
					pw.close();
					fos2.close();
							
				}
				else if(system.contains("Unix")){
					FileOutputStream fos2 = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "start.sh");
					PrintWriter pw = new PrintWriter(fos2);
					pw.println("#!/bin/sh");
					pw.println(" BINDIR=$(dirname \"$(readlink -fn \"$0\")\")");
					pw.println(" cd \"$BINDIR\"");
					pw.println("java -Xmx" + ram + "G -jar craftbukkit.jar -o true");
					pw.close();
					fos2.close();
					Runtime.getRuntime().exec("chmod +x " + direcotry.getAbsolutePath() + "/" + "start.sh");
				} 
				else if(system.contains("Mac")){
					FileOutputStream fos2 = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "start.command");
					PrintWriter pw = new PrintWriter(fos2);
					pw.println("#!/bin/bash");
					pw.println("cd \"$( dirname \"$0\" )\"");
					pw.println("java -Xmx" + ram + "G -jar craftbukkit.jar -o true");
					pw.close();
					fos2.close();
					Runtime.getRuntime().exec("chmod a+x " + direcotry.getAbsolutePath() + "/" + "start.command");
				}
				
				String manual;
			} catch(IOException e){
				
			}
		}
	}
	
	public int getRam(){
		String ram = JOptionPane.showInputDialog("How many Gigabytes of memory would you like the server to allocate?" + 2);
		try{
			int ramint = Integer.parseInt(ram);	
			return ramint;
		} catch(Exception e){
			return -1;
		}

	}
	
}
