package com.movilhuejutla;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class AdapterPronostico extends ArrayAdapter<InterfacePronostico>{

	private LayoutInflater inflater;
	
	public enum RowType{
		ITEM_HORA, HEADER_DIA
	}
	
	public AdapterPronostico(Context contexto, List<InterfacePronostico> items){
		super(contexto, 0, items);
		inflater = LayoutInflater.from(contexto);
	}
	
	public int getViewTypeCount(){
		return RowType.values().length;
	}
	
	public int getItemViewType(int position){
		return getItem(position).getViewType();
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		return getItem(position).getView(inflater, convertView);
		
	}
	
}

