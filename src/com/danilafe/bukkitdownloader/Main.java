package com.danilafe.bukkitdownloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	final String DEVBUIL = "http://dl.bukkit.org/downloads/craftbukkit/get/latest-dev/craftbukkit.jar"; 
	final String RECBUILD = "http://dl.bukkit.org/latest-rb/craftbukkit.jar"; 
	final String BETABUILD = "http://dl.bukkit.org/downloads/craftbukkit/get/latest-beta/craftbukkit.jar"; 
	String system = System.getProperty("os.name");
	String downloadlocation;
	File path;
	String[] filetypes = new String[]{
			"Recommended", "Dev" , "Beta"
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
		System.out.println(selected);
		switch(selected.toLowerCase()){
		case "recommended":
			try {
				URL download = new URL(RECBUILD);
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
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
}
