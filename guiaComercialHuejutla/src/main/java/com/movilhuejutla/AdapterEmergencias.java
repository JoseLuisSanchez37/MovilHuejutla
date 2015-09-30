package com.movilhuejutla;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterEmergencias extends BaseAdapter{

	private Context contexto;
	private ArrayList<NumEmergencia> dependencias;
	
	public AdapterEmergencias(Context contexto, ArrayList<NumEmergencia> dependencias){
		this.contexto = contexto;
		this.dependencias = dependencias;
	}
	
	@Override
	public int getCount() {
		return dependencias.size();
	}

	@Override
	public Object getItem(int position) {
		return dependencias.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		 
        if(convertView == null){
       
            LayoutInflater inf = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.items_emergencia, null);
            
            holder = new ViewHolder();
            holder.nombre = (TextView) convertView.findViewById(R.id.nombre_emergencia);
            holder.telefono = (TextView) convertView.findViewById(R.id.llamar_emergencia);
            
            convertView.setTag(holder);
        }
        else{
        	holder = (ViewHolder) convertView.getTag();
        }
        
        holder.nombre.setText(dependencias.get(position).nombre);
        holder.telefono.setText(dependencias.get(position).telefono);
 
        return convertView;
	}
	
	static class ViewHolder{
		public TextView nombre;
		public TextView telefono;
	}
}