package com.movilhuejutla;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class ActivityBuscar extends ActionBarActivity implements OnQueryTextListener,OnFocusChangeListener,OnCloseListener, OnItemClickListener{

	private SearchView searchView;
	private SearchManager searchManager;
	private MenuItem searchItem; 
	
	private ListView list;
	private InputMethodManager teclado;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);		
		setContentView(R.layout.layout_default);
		
		searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		teclado = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.main, menu);
		searchItem = menu.findItem(R.id.action_search);
	    searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    		
		if(searchManager != null){
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setFocusable(true);
			searchView.setIconified(false);
			searchView.setIconifiedByDefault(true);
			searchView.requestFocusFromTouch();
			searchView.setOnQueryTextFocusChangeListener(this);
			searchView.setOnQueryTextListener(this);
			searchView.setOnCloseListener(this);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		if(teclado != null){
			teclado.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
		}
		return true;
	}

	@Override
	public boolean onQueryTextChange(String texto) {
		if(texto.length() == 0){
			setContentView(R.layout.layout_default);
		}else{
			setContentView(R.layout.layout_lista);
			list = (ListView) findViewById(R.id.listaNegocios);
			list.setOnItemClickListener(this);
	        new ObtenerBusqueda().execute(texto);
		}
		return true;
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			if(teclado != null){
				teclado.showSoftInput(v.findFocus(), 0);
			}	
		}
	}

	@Override
	public boolean onClose() {
		if(searchView.isFocusable()){	
			Intent i = new Intent(this,ActivityCategorias.class);
			startActivity(i);
		}
			return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, ActivityDescripcionNegocio.class);
		intent.putExtra("navegacion", ActivityCategorias.FROM_BUSQUEDA);
		intent.putExtra("id_negocio", id);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			Intent intent = new Intent(this,ActivityCategorias.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class ObtenerBusqueda extends AsyncTask<String, Void, ArrayList<ObjectNegocio> >{

		@Override
		protected ArrayList<ObjectNegocio> doInBackground(String... params) {
			ArrayList<ObjectNegocio> array = null;
			
			while(true){
				if(!FlagsCopy.getFlagCopy(getApplicationContext())){
					Log.v("ActivityBuscar", "La bandera de Copy NO esta activa. Procedemos a hacer la consulta");
					FlagsCopy.setFlagDatabaseInUse(getApplicationContext(), true);//base de datos en uso = si						
					array = DB_MH.getInstance(getApplicationContext()).obtenerNegocios(params[0]);
					FlagsCopy.setFlagDatabaseInUse(getApplicationContext(), false);//base de datos en uso = no
					Log.v("ActivityBuscar", "Listo terminamos. La base de datos no se esta usando. Devolvemos el array");
					break;
				}
			}
			return array;
			
		}
		
		@Override
		protected void onPostExecute(ArrayList<ObjectNegocio> array){
			AdapterNegocio adapter = new AdapterNegocio(getApplicationContext(),array);
	        list.setAdapter(adapter);
		}
		
	}
		
}