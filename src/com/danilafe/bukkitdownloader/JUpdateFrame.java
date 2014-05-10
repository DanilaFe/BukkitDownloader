package com.danilafe.bukkitdownloader;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class JUpdateFrame extends JFrame {

	Main parent;
	JProgressBar bar;
	JUpdateFrame us = this;
	
	public Thread update = new Thread() {
		public void run() {
			System.out.println("Max = " + parent.filecontent);
			System.out.println("Current = " + parent.bytecount);
			bar = new JProgressBar(0, parent.filecontent);
			us.add(bar);
			us.setSize(500, 50);
			us.setVisible(true);
			while(true){
				try {
					Thread.sleep(500);
					bar.setValue(parent.bytecount);
					bar.repaint();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	public JUpdateFrame(Main parent){
		this.parent = parent;
		update.start();
	}
	
	public void doclose(){
		update.stop();
		this.dispose();
	}
	
}
