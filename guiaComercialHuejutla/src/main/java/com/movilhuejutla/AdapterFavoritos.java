package com.movilhuejutla;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterFavoritos extends BaseAdapter{

	private Context contexto;
	private ArrayList<ObjectNegocio> items;
	
	public AdapterFavoritos(Context contexto, ArrayList<ObjectNegocio> items){
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
		return items.get(position).id;
	}

	@Override
    public View getView(int position, View v, ViewGroup parent) {
 
    	ViewHolder holder;
 
        if(v == null){
        	
            LayoutInflater inf = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.items_lista, null);
            
            holder = new ViewHolder();
            holder.foto = (ImageView) v.findViewById(R.id.foto);
            holder.nombre = (TextView) v.findViewById(R.id.nombre);
            holder.direccion = (TextView) v.findViewById(R.id.descripcion);
            
            v.setTag(holder);
        }
        else{
        	holder = (ViewHolder) v.getTag();
        }       
        
        String path = Utils.getPathApp(contexto)+"/Imagenes/"+items.get(position).foto+".jpg";
        Bitmap logo = BitmapFactory.decodeFile(path);
        holder.foto.setImageBitmap(logo);
        //holder.foto.setImageResource(contexto.getResources().getIdentifier(items.get(position).foto, "drawable", contexto.getPackageName()));
        holder.nombre.setText(items.get(position).nombre);
        holder.nombre.setSelected(true);
        holder.direccion.setText(items.get(position).direccion);
 
        return v;
    }
    
    static class ViewHolder{
    	public ImageView foto;
    	public TextView nombre;
    	public TextView direccion;
    }
	
}
