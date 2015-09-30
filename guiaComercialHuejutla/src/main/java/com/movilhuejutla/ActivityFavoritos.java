package com.movilhuejutla;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityFavoritos extends ActionBarActivity implements OnItemClickListener{
	
	private ListView lista;
	private TextView mensaje;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_lista);
		setActionBar();
		lista = (ListView) findViewById(R.id.listaNegocios);
		lista.setOnItemClickListener(this);
		
		DB_F f = DB_F.getInstance(this);
		DB_MH db = DB_MH.getInstance(this);
		
		int num_favoritos = f.getAllFavoritos().size();
		
		if(num_favoritos != 0){
			ArrayList<ObjectNegocio> favoritos = new ArrayList<ObjectNegocio>(num_favoritos);
			favoritos = f.getAllFavoritos();
			ArrayList<ObjectNegocio> negocios = new ArrayList<ObjectNegocio>(num_favoritos);
			
			for(int i = 0; i < num_favoritos; i++){
				negocios.add(db.obtenerFavorito(favoritos.get(i).id));
			}
			lista.setAdapter(new AdapterFavoritos(this, negocios));
		}else{
			setContentView(R.layout.layout_default);
			mensaje = (TextView) findViewById(R.id.mostrar_mensaje);
			mensaje.setText("Aun no tienes negocios marcados como favoritos");
			mensaje.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_favorito,0,0);
		}
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, "FAVORITOS"));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Intent i =  new Intent(this, ActivityDescripcionNegocio.class);
		i.putExtra("id_negocio", id);
		i.putExtra("navegacion", getIntent().getExtras().getInt("navegacion"));
		startActivity(i);
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
	
}
