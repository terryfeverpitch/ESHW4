package com.ESHW4;

import java.util.ArrayList;

import com.ESHW4.FIleExplorer.FileExplorerItem;
import com.ESHW4.FIleExplorer.FileExplorerListAdapter;
import com.ESHW4.FIleExplorer.FileExplorerManager;
import com.ESHW4.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class FileExplorerActivity extends Activity {
	private FileExplorerListAdapter listAdapter;
	private FileExplorerManager fileManager = new FileExplorerManager();
	private ArrayList<FileExplorerItem> list;
	private ListView file_explorer_lv_list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer_main);
		
		EventManager.setComponent(this);
		
		file_explorer_lv_list = (ListView) findViewById(R.id.explorer_lv_list);
		
		if(list != null)
			list.clear();
		list = fileManager.getPlayList();
		listAdapter = new FileExplorerListAdapter(this);
		
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).type == FileExplorerItem.FILE) {
				listAdapter.addItem(list.get(i));
			}
			else if(list.get(i).type == FileExplorerItem.DIR) {
				listAdapter.addSectionHeaderItem(list.get(i));
			}
		}
		setTitle(listAdapter.getItem(0).path);
		file_explorer_lv_list.setAdapter(listAdapter);
		file_explorer_lv_list.setOnItemClickListener(EventManager.genFileExplorerListOnItemClickListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		EventManager.doOnBackPressed();
	}
	
	public ArrayList<FileExplorerItem> getList() {
		return list;
	}
}
