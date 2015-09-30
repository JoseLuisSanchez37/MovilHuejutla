package com.movilhuejutla;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityPronostico extends ActionBarActivity{

	ProgressBar progress;
	TextView mensaje;
	StickyListHeadersListView stickyList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_default);
		setActionBar();
		progress = (ProgressBar) findViewById(R.id.progreso_anuncios);
		mensaje = (TextView) findViewById(R.id.mostrar_mensaje);
		progress.setVisibility(ProgressBar.VISIBLE);
				
		if(Utils.redDisponible(this)){
			new DescargarPronostico().execute(this);
		}else{
			progress.setVisibility(View.GONE);
			mensaje.setText("Sin conexion a Internet");
			mensaje.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_not_connection_internet,0,0);
		}		
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, "EL CLIMA EN HUEJUTLA"));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}
	
	private class DescargarPronostico extends AsyncTask<Context,Void,List<InterfacePronostico>>{
			
		@Override
		protected List<InterfacePronostico> doInBackground(Context... c) {
			URL url;
			try {
				url = new URL(Utils.API_METEORED);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.connect();
				InputStream inputStream = urlConnection.getInputStream();
				XMLParserPronostico xmlParser = new XMLParserPronostico(c[0]);
				return xmlParser.procesarXML(inputStream);	
			} catch (Exception e) {
				return null;
			}
			
		}
		
		@Override
		protected void onPostExecute(List<InterfacePronostico> array){
			progress.setVisibility(ProgressBar.GONE);
			if(array != null){
				setContentView(R.layout.layout_pronostico);
				ListView list = (ListView) findViewById(R.id.listview_pronostico);
				AdapterPronostico adapter = new AdapterPronostico(getApplicationContext(), array);
				list.setAdapter(adapter);
			}else{
				
			}
		}
		
	}
		
}

