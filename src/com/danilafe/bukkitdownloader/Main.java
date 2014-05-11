package com.danilafe.bukkitdownloader;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
		//Created the file chooser
		JFileChooser j = new JFileChooser();
		
		//Make sure you can only select folders.
		j.setFileSelectionMode(j.DIRECTORIES_ONLY);
		
		//If something was selected generate the server.
		int opt = j.showDialog(null, "Select Folder");
		if(opt == j.APPROVE_OPTION){
			generateServer(j);
		}
		
	}
	
	public static void main(String[] args) {
		new Main();
	}
	

	
	void generateServer(JFileChooser j){
		//Gets the selected folder
		File direcotry = j.getSelectedFile(); 
		
		//Gets the selected option  - the release version
		String selected = (String)JOptionPane.showInputDialog(null,"Select Bukkit Version", "Version Select", JOptionPane.QUESTION_MESSAGE, null,filetypes,filetypes[1]);
		
		//If it wasn't cancelled
		if(selected != null){
			
			//Init URL
			URL download = null;
			
			//Set the URL to the corresponding constant
			switch(selected.toLowerCase()){
			
			//If it's recommended set it to RECBUILD, the link to the recommended download.
			case "recommended":
				try {
					download = new URL(RECBUILD);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			//If it's dev set it to RECBUILD, the link to the development download.
			case "dev":
				try {
					download = new URL(DEVBUILD);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			
			//If it's beta set it to RECBUILD, the link to the beta download.
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
				
				//Open the stream from the URL
				InputStream is = download.openStream();
				
				//Open the connection (for the progress frame)
				con = (HttpURLConnection)download.openConnection();
				
				//Get the size of the file.
				if(con.getResponseCode() / 100 == 2)  
				{  
				    int contentLength = con.getContentLength();  
				    System.out.println(contentLength);
				    filecontent = contentLength;
				} 
				
				
				//Create the file output stream to download bukkit.jar
				FileOutputStream fos = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "craftbukkit.jar");
				
				//Create the buffered input stream to read data
				BufferedInputStream br = new BufferedInputStream(is);
				
				//Create progress frame.
				JUpdateFrame updateframe = new JUpdateFrame(this);
				
				//Create the buffer.
				byte[] data = new byte[1024];
				
				//Download the jar!
				while((buffer = br.read(data, 0, 1024)) != -1){
					fos.write(data,0, buffer);
					bytecount += buffer;
				}
				
				//Close all the buffers.
				is.close();
				fos.close();
				br.close();
				
				//Close the frame.
				updateframe.doclose();
				
				//Get the ram.
				int ram = getRam();
				
				//Depending on the OS, generate the start file.
				if(system.startsWith("Windows")){
					//Make the output stream
					FileOutputStream fos2 = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "start.bat");
					
					//Make a writer to write to the stream
					PrintWriter pw = new PrintWriter(fos2);
					
					//Write the start.bat
					pw.println("java -Xmx" + ram + "G -jar craftbukkit.jar -o true");
					pw.println("PAUSE");
					
					//Close the writers
					pw.close();
					fos2.close();
							
				}
				else if(system.contains("Unix")){
					//Make the output stream
					FileOutputStream fos2 = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "start.sh");
					
					//Make a writer to write to the stream
					PrintWriter pw = new PrintWriter(fos2);
					pw.println("#!/bin/sh");
					
					//Write the start.sh
					pw.println(" BINDIR=$(dirname \"$(readlink -fn \"$0\")\")");
					pw.println(" cd \"$BINDIR\"");
					pw.println("java -Xmx" + ram + "G -jar craftbukkit.jar -o true");
					
					//Close the writers
					pw.close();
					fos2.close();
					
					//Change run permissions.
					Runtime.getRuntime().exec("chmod +x " + direcotry.getAbsolutePath() + "/" + "start.sh");
				} 
				else if(system.contains("Mac")){
					//Make the output stream
					FileOutputStream fos2 = new FileOutputStream(direcotry.getAbsolutePath() + "/" + "start.command");
					
					//Make a writer to write to the stream
					PrintWriter pw = new PrintWriter(fos2);
					
					//Write the start.command
					pw.println("#!/bin/bash");
					pw.println("cd \"$( dirname \"$0\" )\"");
					pw.println("java -Xmx" + ram + "G -jar craftbukkit.jar -o true");
					
					//Close the writers
					pw.close();
					fos2.close();
					
					//Change run permissions.
					Runtime.getRuntime().exec("chmod a+x " + direcotry.getAbsolutePath() + "/" + "start.command");
				}
				
				String way = (String)JOptionPane.showInputDialog(null,"Generate config automatically?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,yesno,yesno[0]);
				
				//Generate config
				if(way.toLowerCase().equals("yes")){
					autoConfig(direcotry);
				}else {
					generateConfig(direcotry);
				}
				
				//Get computer's IP
				String ip;
				URL icanhazip = new URL("http://icanhazip.com");
				BufferedReader r = new BufferedReader(new InputStreamReader(icanhazip.openStream()));
				ip = r.readLine();
				
				
				//Copy ip to clipboard
				StringSelection selection = new StringSelection(ip);
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents(selection, selection);
				
			    //Show "Keep in mind" message
				JOptionPane.showMessageDialog(null, "Your server has been set up! \n \n Connect to your server with \n <localhost> \n \n Others can use: \n <" + ip + "> \n (Copied to your clipboard) \n \n Things to keep in mind: \n \n 1.Always stop the server by typing \"stop\" \n \n 2.Start it by running start.command / start.sh / start.bat \n \n 3.Download plugins and put them in the plugins folder.");
				
				//Port forwarding guide
				int i = JOptionPane.showOptionDialog(null, "Next step: \n Port forward your ports!", "Port Forward", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{
						"Port forwarding guide!", "Skip"
				}, "Port forwarding guide!");
				if(i == JOptionPane.YES_OPTION){
					 Desktop.getDesktop().browse(new URI("http://portforward.com/english/applications/port_forwarding/Minecraft_Server/"));
				}
				
				//Ask for donation
				 i = JOptionPane.showOptionDialog(null, "Thank you for using AutoBukkitServer. This tool is completely free to use and enjoy.\n Although, time isn't. I take hours of time out of my life to make and update this tool for you,\n and donations keep me going!", "Donate!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{
						 "Donate!", "Finish"
				 }, "Donate!");
				 
				 if(i == JOptionPane.YES_OPTION){
					 Desktop.getDesktop().browse(new URI("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=MVENJVD6Y6EXJ&lc=US&item_name=hawkfalcon&item_number=hawkfalcon&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted"));
				 }
				 
				 //Open start command.
				 File[] files = direcotry.listFiles();
				 for(File f: files){
					 if(f.getName().startsWith("start.")){
						 Desktop.getDesktop().open(f);
					 }
				 }
				 
				 System.exit(0);
				
				
				
			} catch(IOException | URISyntaxException e){
				
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
	
	void autoConfig(File maindir){
		try {
			//Create the file
			File config = new File(maindir.getAbsolutePath() + "/server.properties");
			
			//Created the print writer
			PrintWriter pwr = new PrintWriter(config);
			
			//Print configuration properties. Those are a mix between the 1.6 and 1.7 servers.
			pwr.println("#Minecraft server properties");
			pwr.println("allow-nether=" + (String) JOptionPane.showInputDialog(null,"Allow nether?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("level-name=" + (String) JOptionPane.showInputDialog("World name:"));
			pwr.println("allow-flight=" + (String) JOptionPane.showInputDialog(null,"Allow flight?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("announce-player-achievements=" + (String) JOptionPane.showInputDialog(null,"Announce player achievements in chat?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("level-seed=" + (String) JOptionPane.showInputDialog("World seed"));
			pwr.println("max-build-height=" + (String) JOptionPane.showInputDialog("Max Build Height"));
			pwr.println("spawn-npcs=" + (String) JOptionPane.showInputDialog(null,"Spawn NPC's?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("white-list=" +(String) JOptionPane.showInputDialog(null,"Enable white-list?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("spawn-animals=" + (String) JOptionPane.showInputDialog(null,"Spawn animals?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("hardcore=" +(String) JOptionPane.showInputDialog(null,"Enable HARDCORE mode?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("online-mode=true");
			pwr.println("pvp=" + (String) JOptionPane.showInputDialog(null,"Enable PVP?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("difficulty=" + (String) JOptionPane.showInputDialog(null,"Server Difficulty: \n 0 = Peaceful \n 1 = Easy \n 2 = Normal \n 3 = Hard", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"0","1","2","3"
			},"true"));
			pwr.println("gamemode=" + (String) JOptionPane.showInputDialog(null,"Server Difficulty: \n 0 = Survivial \n 1 = Creative \n 2 = Adventure", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"0","1","2","3"
			},"true"));
			pwr.println("enable-command-block=" + (String) JOptionPane.showInputDialog(null,"Enable Command Blocks?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("max-players=" + (String) JOptionPane.showInputDialog("Max players"));
			pwr.println("spawn-monsters=" + (String) JOptionPane.showInputDialog(null,"Allow monsters?", "Config settings", JOptionPane.QUESTION_MESSAGE, null,new String[] {
					"true", "false"
			},"true"));
			pwr.println("motd=" + (String) JOptionPane.showInputDialog("Server MOTD."));
			
			pwr.close();
			
			//Opens the config.
			Desktop.getDesktop().open(config);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	void generateConfig(File maindir){
		try {
			//Create the file
			File config = new File(maindir.getAbsolutePath() + "/server.properties");
			
			//Created the print writer
			PrintWriter pwr = new PrintWriter(config);
			
			//Print configuration properties. Those are a mix between the 1.6 and 1.7 servers.
			pwr.println("#Minecraft server properties");
			pwr.println("generator-settings=");
			pwr.println("op-permission-level=4");
			pwr.println("allow-nether=true");
			pwr.println("level-name=world");
			pwr.println("enable-query=false");
			pwr.println("allow-flight=false");
			pwr.println("announce-player-achievements=true");
			pwr.println("server-port=25565");
			pwr.println("level-type=DEFAULT");
			pwr.println("enable-rcon=false");
			pwr.println("force-gamemode=false");
			pwr.println("level-seed=");
			pwr.println("server-ip=");
			pwr.println("max-build-height=256");
			pwr.println("spawn-npcs=true");
			pwr.println("white-list=false");
			pwr.println("spawn-animals=true");
			pwr.println("texture-pack=");
			pwr.println("snooper-enabled=true");
			pwr.println("hardcore=false");
			pwr.println("online-mode=true");
			pwr.println("resource-pack=");
			pwr.println("pvp=true");
			pwr.println("difficulty=1");
			pwr.println("enable-command-block=false");
			pwr.println("player-idle-timeout=0");
			pwr.println("gamemode=0");
			pwr.println("max-players=20");
			pwr.println("spawn-monsters=true");
			pwr.println("view-distance=10");
			pwr.println("generate-structures=true");
			pwr.println("motd=A Minecraft Server");
			
			pwr.close();
			
			//Opens the config.
			Desktop.getDesktop().open(config);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
}
