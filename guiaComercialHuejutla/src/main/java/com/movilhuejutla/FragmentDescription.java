package com.movilhuejutla;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentDescription extends Fragment implements OnClickListener{
	
	private DB_MH db;
	private ObjectNegocio negocio;
	private Context contexto;
	private Bundle bundle;
	private DB_F fv;
	private int f;
	private ImageButton favorito;
	
	private ImageView imagen;
	private String[] imagenes;
	private int index = 0;
	Timer tiempo;
	TimerTask tarea;	
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		contexto = activity.getApplication().getApplicationContext();
		bundle = getArguments();
		
		db = DB_MH.getInstance(contexto);
		fv = DB_F.getInstance(contexto);
		negocio = db.obtenerNegocio(bundle.getLong("id_negocio"));
		f = fv.isFavorito(bundle.getLong("id_negocio"));
	}
			
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		final View v = inflater.inflate(R.layout.layout_detalle_negocio, container,false);
				
		imagenes = db.obtenerImagenes(bundle.getLong("id_negocio"));
		imagen = (ImageView) v.findViewById(R.id.img_negocio);
		
		if(imagenes.length > 1){
			Timer timer = new Timer();
			final Handler handler = new Handler();
			final Animation animation = AnimationUtils.loadAnimation(contexto, R.anim.animacion_imagenes);
	        final Runnable runnable = new Runnable() {
	            @Override
				public void run() {
	            	String path = Utils.getPathApp(contexto)+"/Imagenes/"+ imagenes[index%imagenes.length] +".jpg";
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if(bitmap != null){
                        imagen.setImageBitmap(BitmapFactory.decodeFile(path));
                    }else{
                        imagen.setImageResource(R.drawable.loading_picasso);
                    }
	           		index++;
	            	imagen.startAnimation(animation);
	            }
	        };
			
	        timer.scheduleAtFixedRate(new TimerTask() {
	        	@Override
				public void run() {
	        		handler.post(runnable);	
	        	}
	        }, 1, 4000);
	        
		}else{
			String path = Utils.getPathApp(contexto)+"/Imagenes/"+ imagenes[0] +".jpg";
    		imagen.setImageBitmap(BitmapFactory.decodeFile(path));
		}
        
        TextView descripcion = (TextView) v.findViewById(R.id.descripcion_negocio);
        descripcion.setText(negocio.descripcion);
                
        TextView horario = (TextView) v.findViewById(R.id.horario_negocio);
        horario.setText(negocio.horario);
        
        TextView numero_tel = (TextView) v.findViewById(R.id.numero);
        final String[] telefonos =  db.obtenerTelefonos(bundle.getLong("id_negocio"));
        numero_tel.setText(telefonos[0].toString());    
        
        favorito = (ImageButton) v.findViewById(R.id.favorito);
        favorito.setOnClickListener(this);
        if(f == 1)	favorito.setImageResource(R.drawable.ic_favorito);
        else favorito.setImageResource(R.drawable.nofavorito);
        
        TextView direccion = (TextView) v.findViewById(R.id.addres);
        direccion.setSelected(true);
        direccion.setText(negocio.direccion);
        
        if(negocio.email != null){
        	if(negocio.email.length() > 0){
    			TextView email = (TextView) v.findViewById(R.id.email);
	        	email.setVisibility(View.VISIBLE);
	        	email.setText(negocio.email);		
        	}
        }   
		return v;
	}

	@Override
	public void onClick(View v) {
		 if (fv.isFavorito(bundle.getLong("id_negocio")) == 1) {
	            fv.setFavorito(bundle.getLong("id_negocio"), 0);
	            favorito.setImageResource(R.drawable.nofavorito);
	            Toast.makeText(contexto, "Quizas despues :)", Toast.LENGTH_SHORT).show();
		 } else {
	            fv.setFavorito(bundle.getLong("id_negocio"), 1);
	            favorito.setImageResource(R.drawable.ic_favorito);
	            Toast.makeText(contexto, "Agregado a Favoritos", Toast.LENGTH_SHORT).show();
		}
	}
	
	
}