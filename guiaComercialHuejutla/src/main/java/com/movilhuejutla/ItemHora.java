package com.movilhuejutla;

import com.movilhuejutla.AdapterPronostico.RowType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemHora implements InterfacePronostico{

	private final String hora, temp, icon, desc;
	private Context cont;
	
	public ItemHora(String hora, String temp, String icon, String desc, Context cont){
		this.hora = hora;
		this.temp = temp;
		this.icon = icon;
		this.desc = desc;
		this.cont = cont;
	}
	
	@Override
	public int getViewType() {
		return RowType.ITEM_HORA.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		ViewHolderHora holder;
		if(convertView == null){
			holder = new ViewHolderHora();
			convertView = inflater.inflate(R.layout.item_content_pronostico, null);
			holder.hora = (TextView) convertView.findViewById(R.id.content_hora_pronostico);
			holder.temperatura = (TextView) convertView.findViewById(R.id.content_temperatura_pronostico);
			holder.icono = (ImageView) convertView.findViewById(R.id.content_icono_pronostico);
			holder.descripcion = (TextView) convertView.findViewById(R.id.content_descripcion_pronostico);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolderHora) convertView.getTag();
		}
		
		String ic = "p"+icon;
		holder.hora.setText(hora);
		holder.temperatura.setText(temp+"°");
		holder.icono.setImageResource(cont.getResources().getIdentifier(ic, "drawable", cont.getPackageName()));
		holder.descripcion.setText(desc);
		return convertView;
	}
	
	static class ViewHolderHora{
		ImageView icono;
		TextView descripcion;
		TextView temperatura;
		TextView hora;
	}

}
