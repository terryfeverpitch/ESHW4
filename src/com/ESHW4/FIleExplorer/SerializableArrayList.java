package com.ESHW4.FIleExplorer;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableArrayList implements Serializable{
	private static final long serialVersionUID = 1L;
	private static ArrayList<FileExplorerItem> serializableArrayList;
	
	public SerializableArrayList(final ArrayList<FileExplorerItem> arrayList) {
		serializableArrayList = new ArrayList<FileExplorerItem>();
		for(int i = 0; i < arrayList.size(); i++) {
			if(arrayList.get(i).type == FileExplorerItem.FILE) {
				serializableArrayList.add(arrayList.get(i));
			}
		}
	}
	
	public ArrayList<FileExplorerItem> getSerialzableArrayList() {
		return serializableArrayList;
	}
}
