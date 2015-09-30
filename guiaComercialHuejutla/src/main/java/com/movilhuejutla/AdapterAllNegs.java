package com.movilhuejutla;

import java.util.ArrayList;
import java.util.Arrays;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class AdapterAllNegs extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer{

	private String[] negocios;
	private Character[] sectionLetters;
	private int[] sectionIndices;
	
	private LayoutInflater inflater;
	private ArrayList<Negs> arrayNegocios;
	private Context contexto;
	
	public AdapterAllNegs(Context context, ArrayList<Negs> array){
		inflater = LayoutInflater.from(context);
		arrayNegocios = array;
		negocios = getStringArray();
		sectionIndices = getSectionIndices();
		sectionLetters = getSectionLetters();
		contexto = context;
	}
	
	private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        char lastFirstChar = negocios[0].charAt(0);
        sectionIndices.add(0);
        for (int i = 1; i < negocios.length; i++) {
            if (negocios[i].charAt(0) != lastFirstChar) {
                lastFirstChar = negocios[i].charAt(0);
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }
	
	private Character[] getSectionLetters() {
        Character[] letters = new Character[sectionIndices.length];
        for (int i = 0; i < sectionIndices.length; i++) {
            letters[i] = negocios[sectionIndices[i]].charAt(0);
        }
        return letters;
    }
	
	private String[] getStringArray(){
		int size = arrayNegocios.size();
		String[] nombres = new String[size];
		for(int i = 0; i < size; i++){
			nombres[i] = arrayNegocios.get(i).nombre;
		}
		Arrays.sort(nombres);
		return nombres;
	}
	
	@Override
	public int getViewTypeCount(){
		return 2;
	}
	
	@Override
	public int getItemViewType(int position){
		if(arrayNegocios.get(position).pagado == 1)
			return 1;
		else
			return 0;
	}
	
	@Override
	public int getCount() {
		return arrayNegocios.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayNegocios.get(position);
	}

	@Override
	public long getItemId(int position) {
		return arrayNegocios.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		int type = getItemViewType(position);
		
		ViewHolderPagado pagado;
		ViewHolderGratuito gratuito;
		
		if(type == 1){
	        if(convertView == null){
	            convertView = inflater.inflate(R.layout.items_lista, null);
	            pagado = new ViewHolderPagado();
	            pagado.foto = (ImageView) convertView.findViewById(R.id.foto);
	            pagado.nombre = (TextView) convertView.findViewById(R.id.nombre);
	            pagado.direccion = (TextView) convertView.findViewById(R.id.descripcion);
	            
	            convertView.setTag(pagado);
	            
	        }else{
	        	pagado = (ViewHolderPagado) convertView.getTag();
	        }
	        
	        String path = Utils.getPathApp(contexto)+"/Imagenes/"+arrayNegocios.get(position).foto+".jpg";
	        Bitmap bmp = BitmapFactory.decodeFile(path);
	        if(bmp != null){
	        	pagado.foto.setImageBitmap(bmp);
	        }else{
	        	pagado.foto.setImageResource(R.drawable.ic_launcher);
	        }
	        pagado.nombre.setText(arrayNegocios.get(position).nombre);
	        pagado.nombre.setSelected(true);
	        pagado.direccion.setText(arrayNegocios.get(position).direccion);
	        
		}else{
	          if(convertView == null) {
	            gratuito = new ViewHolderGratuito();
	            convertView = inflater.inflate(R.layout.items_emergencia, null);
	            gratuito.nombre = (TextView) convertView.findViewById(R.id.nombre_emergencia);
	            gratuito.direccion = (TextView) convertView.findViewById(R.id.llamar_emergencia);
	            convertView.setTag(gratuito);
	          }else {
	            gratuito = (ViewHolderGratuito) convertView.getTag();
	          }
		
	        gratuito.nombre.setText(arrayNegocios.get(position).nombre);
	        gratuito.nombre.setSelected(true);
	        gratuito.direccion.setText(arrayNegocios.get(position).direccion);
			}

        return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.items_allnegs_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.txt_neg_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = "" + negocios[position].subSequence(0, 1).charAt(0);
        holder.text.setText(headerText);
        return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return negocios[position].subSequence(0, 1).charAt(0);
	}
		
	static class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
        TextView pagado;
    }
    
    static class ViewHolderPagado{
    	public ImageView foto;
    	public TextView nombre;
    	public TextView direccion;
    }
    
    static class ViewHolderGratuito{
    	public TextView nombre;
    	public TextView direccion;
    }

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndices.length == 0) {
            return 0;
        }
        
        if (sectionIndex >= sectionIndices.length) {
            sectionIndex = sectionIndices.length - 1;
        } else if (sectionIndex < 0) {
            sectionIndex = 0;
        }
        return sectionIndices[sectionIndex];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < sectionIndices.length; i++) {
            if (position < sectionIndices[i]) {
                return i - 1;
            }
        }
        return sectionIndices.length - 1;
	}

	@Override
	public Object[] getSections() {
		return sectionLetters;
	}
	
	public void clear() {
        negocios = new String[0];
        sectionIndices = new int[0];
        sectionLetters = new Character[0];
        notifyDataSetChanged();
    }

    public void restore() {
        sectionIndices = getSectionIndices();
        sectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

}
