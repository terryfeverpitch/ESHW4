package com.ESHW4.FIleExplorer;

import java.util.ArrayList;
import java.util.TreeSet;

import com.ESHW4.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileExplorerListAdapter extends BaseAdapter {
	public static final int TYPE_ITEM = 0;
	public static final int TYPE_SEPARATOR = 1;

	private ArrayList<FileExplorerItem> mData = new ArrayList<FileExplorerItem>();
	private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

	private LayoutInflater mInflater;

	public FileExplorerListAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(final FileExplorerItem item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void addSectionHeaderItem(final FileExplorerItem item) {
		mData.add(item);
		sectionHeader.add(mData.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public FileExplorerItem getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int rowType = getItemViewType(position);

		if (convertView == null) {
			holder = new ViewHolder();
			switch (rowType) {
				case TYPE_ITEM:
					convertView = mInflater.inflate(R.layout.listview_item, null);
					holder.textView = (TextView) convertView.findViewById(R.id.file_explorer_list_tv_item);
					break;
				case TYPE_SEPARATOR:
					convertView = mInflater.inflate(R.layout.listview_header, null);
					holder.textView = (TextView) convertView.findViewById(R.id.file_explorer_lis_tv_header);
					break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(mData.get(position).name);
		return convertView;
	}

	public static class ViewHolder {
		public TextView textView;
	}

}
