package com.movilhuejutla;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMisNotificaciones extends ActionBarActivity {

	private ArrayList<Notificacion> noticias;
	private TextView mensaje;
	private ListView lista_noticias;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.layout_default);
	    setActionBar();
	    DB_F db = DB_F.getInstance(this);
	    
		noticias = new ArrayList<Notificacion>();
		noticias = db.obtenerNotificaciones();
		mensaje = (TextView) findViewById(R.id.mostrar_mensaje);
		
		if(noticias.size() != 0){
			setContentView(R.layout.layout_noticias);
			lista_noticias = (ListView) findViewById(R.id.listaNoticia);
			lista_noticias.setAdapter(new AdapterPush(this, noticias));
			
		}else{
			mensaje.setText("Aun no hay nuevas noticias");
			mensaje.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_launcher,0,0);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		Intent i = new Intent(this, ActivityCategorias.class);
	    		startActivity(i);
	    		return true;
	    
	        case R.id.borrar_notificaciones:
	        	
	        	AlertDialog.Builder deletepushes =  new AlertDialog.Builder(this);
	    		deletepushes.setMessage("Desea Eliminar las Notificaciones?")
	    				   .setTitle("Eliminar Notificaciones")
	    				   .setCancelable(false)
	    				   .setPositiveButton("Si", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DB_F.getInstance(getApplicationContext()).EliminarNotificaciones();
						Toast.makeText(getApplicationContext(), "Se han eliminado las notificaciones", Toast.LENGTH_SHORT).show();
						setContentView(R.layout.layout_default);
						mensaje.setText("Aun no hay nuevas noticias");
						mensaje.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_launcher,0,0);
					}
				})
	    		.setNegativeButton("No", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				
				.setIcon(android.R.drawable.ic_dialog_info)
				.show();
	    	
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.borrar_notificaciones, menu);
	    return true;
	}
	
	public void setActionBar(){		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, "NOTICIAS"));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}
	
	public void verNegocio(View v){
		long id_negocio = (Long) v.getTag();
		
		if(DB_MH.getInstance(this).existeNegocio(id_negocio)){
			Intent i = new Intent(ActivityMisNotificaciones.this,ActivityDescripcionNegocio.class);
			i.putExtra("id_negocio", id_negocio);
			startActivity(i);
		}else{
			AlertDialog.Builder update =  new AlertDialog.Builder(this);
    		update.setMessage("Actualiza a la ultima version  de tu app MOVILHUEJUTLA. Encuentra a este y mas negocios nuevos!!")
    				   .setTitle("Actualizar MovilHuejutla")
    				   .setCancelable(false)
    				   .setPositiveButton("Actualizar Ahora!", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=com.movilhuejutla"));
					startActivity(intent);
				}
			})
    		.setNegativeButton("Quizas despues", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			
			.setIcon(android.R.drawable.ic_dialog_info)
			.show();
		}
		
		
	}
}
