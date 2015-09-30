package com.movilhuejutla;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterAnuncio extends BaseAdapter {
	
	private Context contexto;
    private ArrayList<ObjectAnuncio> items;
    
    public AdapterAnuncio(Context contexto, ArrayList<ObjectAnuncio> items){
    	this.contexto = contexto;
    	this.items = items;
    }

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).id_anuncio;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		
		ViewHolder holder;
		if(v == null){
			LayoutInflater inf = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf.inflate(R.layout.gridview_anuncio, null);
			
			holder = new ViewHolder();
			holder.img = (ImageView) v.findViewById(R.id.imagen_anuncio);
			holder.titulo = (TextView) v.findViewById(R.id.titulo_anuncio);
			holder.descripcion = (TextView) v.findViewById(R.id.descripcion_anuncio);
			holder.tipo = (TextView) v.findViewById(R.id.tipo_anuncio);
			holder.fecha = (TextView) v.findViewById(R.id.fecha_anuncio);
			v.setTag(holder);
		}
		else{
			holder = (ViewHolder) v.getTag();
		}
		
		holder.titulo.setText(items.get(position).titulo);
		holder.descripcion.setText(items.get(position).descripcion);
		holder.tipo.setText(items.get(position).tipo);
		holder.fecha.setText(items.get(position).fecha);
		
		String url = items.get(position).url;
		if(url.equals("0")){
			holder.img.setVisibility(View.GONE);
		}else{
		Picasso.with(contexto)
		   .load(items.get(position).url)
		   .placeholder(R.drawable.ic_not_found)
		   .error(R.drawable.ic_not_found)
		   .into(holder.img);
		}
		return v;
	}
	
	static class ViewHolder{
		public TextView titulo;
		public TextView descripcion;
		public TextView precio;
		public TextView tipo;
		public TextView fecha;
		public ImageView img;
	}
}
