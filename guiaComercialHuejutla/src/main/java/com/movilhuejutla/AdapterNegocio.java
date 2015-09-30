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
public class AdapterNegocio extends BaseAdapter{
;
	private Context contexto;
    private ArrayList<ObjectNegocio> items;
 
    public AdapterNegocio(Context contexto, ArrayList<ObjectNegocio> items){
        this.items = items;
        this.contexto = contexto;
    }
    
    @Override
    public int getCount() {
        return items.size();
    }
 
    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
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
        if(logo != null)
        	holder.foto.setImageBitmap(logo);
        else
        	holder.foto.setImageResource(R.drawable.ic_launcher);
        	
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
