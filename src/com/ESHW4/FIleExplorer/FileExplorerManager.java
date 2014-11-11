package com.ESHW4.FIleExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.ESHW4.EventManager;
import com.ESHW4.R;

public class FileExplorerManager {
	private final static String DEFAULTPATH = "/sdcard";
	private static String ROOTPATH = "/sdcard";
	private static ArrayList<FileExplorerItem> list = new ArrayList<FileExplorerItem>();
	private String pattern = ".mp4";
	
	public ArrayList<FileExplorerItem> getPlayList() {
		list.clear();
	    if(ROOTPATH != null && !ROOTPATH.equals("/")) {
	        File home = new File(ROOTPATH);
	        if(ROOTPATH.equals(DEFAULTPATH))
	        	list.add(new FileExplorerItem("/..", home.getPath(), FileExplorerItem.DIR));
	        else
	        	list.add(new FileExplorerItem("/..", home.getParent(), FileExplorerItem.DIR));
	        File[] listFiles = home.listFiles();
	        if(listFiles != null && listFiles.length > 0) {
	        	Arrays.sort(listFiles);
	        	for(File file : listFiles) {
	                if(file.isDirectory()) {
	                	list.add(new FileExplorerItem(file.getName(), file.getPath(), FileExplorerItem.DIR));
	                }
	            }
	            for(File file : listFiles) {
	            	if(file.isFile() && file.getName().endsWith(pattern)) {
	            		list.add(new FileExplorerItem(file.getName(), file.getPath(), FileExplorerItem.FILE));
	            	}
	            }
	        }
	    }
	    return list;
	}
	
	public void clearList() {
		list.clear();
	}
	
	public void setRootPath(String path) {
		clearList();
		if(!path.equals("/"))
			ROOTPATH = path;
		else
			ROOTPATH = DEFAULTPATH;
	}
}
