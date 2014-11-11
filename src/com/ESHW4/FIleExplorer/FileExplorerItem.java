package com.ESHW4.FIleExplorer;

import java.io.Serializable;


public class FileExplorerItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1641574056785586804L;
	public static final int FILE = 0;
	public static final int DIR = 1;
	public String name;
	public String path;
	public int type;
	
	public FileExplorerItem(String name, String path, int type) {
		this.name = name;
		this.path = path;
		this.type = type;
	}
}
