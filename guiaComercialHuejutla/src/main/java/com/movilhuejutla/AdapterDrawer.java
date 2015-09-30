package com.movilhuejutla;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterDrawer extends ArrayAdapter<String>{

	private Context contexto;
	private String[] opciones;
	private int layoutResourceId;
	
	public AdapterDrawer(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		 this.contexto = context;
		 this.opciones = objects;
		 this.layoutResourceId = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutResourceId, null);
		}
		
		TextView text = (TextView) convertView.findViewById(R.id.drawer_text);
		text.setText(opciones[position]);
		
		ImageView img = (ImageView) convertView.findViewById(R.id.drawer_img);
		
		switch(position){
		case 0:
			img.setImageDrawable(contexto.getResources().getDrawable(R.drawable.p1));
			break;
		case 1:
			img.setImageDrawable(contexto.getResources().getDrawable(R.drawable.ic_action_call));
			break;
		case 2:
			img.setImageDrawable(contexto.getResources().getDrawable(R.drawable.ic_anuncio));
			break;
		case 3:
			img.setImageDrawable(contexto.getResources().getDrawable(R.drawable.ic_favorito));
			break;
		case 4:
			img.setImageDrawable(contexto.getResources().getDrawable(R.drawable.ic_push));
			break;
		case 5:
			img.setImageDrawable(contexto.getResources().getDrawable(R.drawable.ic_google_play));
			break;
		case 6:
			img.setImageDrawable(contexto.getResources().getDrawable(R.drawable.ic_support));
			break;
		}
		
		return convertView;
	}

}
