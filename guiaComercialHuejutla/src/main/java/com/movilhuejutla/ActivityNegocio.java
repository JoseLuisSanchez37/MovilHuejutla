package com.movilhuejutla;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityNegocio extends ActionBarActivity implements OnItemClickListener{
	
	private ListView lista;
	private long cat;
	public static final String TAG = "com.movilhuejutla.ActivityNegocio";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_lista);
		cat = getIntent().getExtras().getLong("id_categoria");
		setActionBar();
		lista = (ListView) findViewById(R.id.listaNegocios);
		lista.setOnItemClickListener(this);
		new obtenerNegocios().execute(this);
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, categorias[(int) (cat-1)] ));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}
		
	private class obtenerNegocios extends AsyncTask<Context, Void, AdapterNegocio>{

		@Override
		protected AdapterNegocio doInBackground(Context... contexto) {
			AdapterNegocio adapter;
			Log.v(TAG, "Obteniendo datos desde AsyncTask.");
			while(true){
				if(!FlagsCopy.getFlagCopy(contexto[0])){
					Log.v(TAG, "activamos la bandera de uso de la BD");
					FlagsCopy.setFlagDatabaseInUse(contexto[0], true);
					adapter = new AdapterNegocio(contexto[0],DB_MH.getInstance(contexto[0]).obtenerNegocios(cat));
					Log.v(TAG, "bajamos la bandera de uso de la BD");
					FlagsCopy.setFlagDatabaseInUse(contexto[0], false);
					break;
				}
			}
			return adapter;
		}
		
		@Override
		protected void onPostExecute(AdapterNegocio adapter){
			if(adapter.getCount() > 0){
				lista.setAdapter(adapter);
			}else{
				setContentView(R.layout.layout_default);
				TextView mensaje = (TextView) findViewById(R.id.mostrar_mensaje);
				mensaje.setText("Aun no hay negocios en esta categoria");
				mensaje.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_launcher,0,0);
			}
		}			
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Intent intent = new Intent(ActivityNegocio.this,ActivityDescripcionNegocio.class);
		intent.putExtra("id_negocio", id);
		intent.putExtra("id_categoria", cat);
		intent.putExtra("navegacion", getIntent().getExtras().getInt("navegacion"));
		intent.putExtra("nombre_categoria", getIntent().getExtras().getString("nombre_categoria"));
		startActivity(intent);	
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			Intent intent = new Intent(this,ActivityCategorias.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	String[] categorias = {
			"COMIDAS Y BEBIDAS",
			"TURISMO Y HOTELES",
			"ENTRETENIMIENTO",
			"MODA Y ACTITUD",
			"SALUD",
			"SERVICIOS",
			"TECNOLOGIA",
			"EDUCACION",
			"PRODUCTOS ALIMENTICIOS",
			"MASCOTAS"
	};
	
}