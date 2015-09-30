package com.movilhuejutla;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterPush extends BaseAdapter{

	private Context contexto;
	private ArrayList<Notificacion> notificaciones;
	
	public AdapterPush (Context contexto, ArrayList<Notificacion> notificaciones){
		this.contexto = contexto;
		this.notificaciones = notificaciones;
	}

	@Override
	public int getCount() {
		return notificaciones.size();
	}

	@Override
	public Object getItem(int position) {
		return notificaciones.get(position);
	}

	@Override
	public long getItemId(int position) {
		return notificaciones.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			LayoutInflater inf = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inf.inflate(R.layout.items_noticia, null);
			
			holder = new ViewHolder();
			holder.imagen = (ImageView) convertView.findViewById(R.id.noticia_url_image);
			holder.titulo = (TextView) convertView.findViewById(R.id.noticia_titulo);
			holder.descripcion = (TextView) convertView.findViewById(R.id.noticia_descripcion);
			holder.fecha = (TextView) convertView.findViewById(R.id.noticia_fecha);
			holder.negocio = (Button) convertView.findViewById(R.id.noticia_id_negocio);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}

		String url = notificaciones.get(position).imagen;
		if(url.equals("0")){
			holder.imagen.setVisibility(View.GONE);
		}else{
			holder.imagen.setVisibility(View.VISIBLE);
			Picasso.with(contexto)
			   .load(notificaciones.get(position).imagen)
			   .placeholder(R.drawable.ic_not_found)
			   .error(R.drawable.ic_not_found)
			   .into(holder.imagen);
		}
		
		long neg = notificaciones.get(position).negocio;
		
		if(neg == 9999){
			holder.negocio.setVisibility(View.GONE);
		}
		else{
			holder.negocio.setTag(notificaciones.get(position).negocio);
		}
		
		holder.titulo.setText(notificaciones.get(position).titulo);
		holder.descripcion.setText(notificaciones.get(position).descripcion);
		holder.fecha.setText(notificaciones.get(position).fecha);
		
		return convertView;
		
	}
	
	static class ViewHolder{
		public ImageView imagen;
		public TextView titulo;
		public TextView descripcion;
		public TextView fecha;
		public Button negocio;
	}

}
