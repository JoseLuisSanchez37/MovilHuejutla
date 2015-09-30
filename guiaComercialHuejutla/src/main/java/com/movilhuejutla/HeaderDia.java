package com.movilhuejutla;

import com.movilhuejutla.AdapterPronostico.RowType;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class HeaderDia implements InterfacePronostico{

	private final String fecha; 
	
	public HeaderDia(String fecha){
		this.fecha = fecha;
	}
	
	@Override
	public int getViewType() {
		return RowType.HEADER_DIA.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		HeaderDiaViewHolder holderDia;
		if(convertView == null){
			holderDia = new HeaderDiaViewHolder();
			convertView = (View) inflater.inflate(R.layout.item_header_pronostico, null);
			holderDia.fecha = (TextView) convertView.findViewById(R.id.header_fecha_pronostico);
			convertView.setTag(holderDia);
		}else{
			holderDia = (HeaderDiaViewHolder) convertView.getTag();
		}
		
		holderDia.fecha.setText(fecha);
		return convertView;
	}
	
	static class HeaderDiaViewHolder{
		TextView fecha;
	}

}
