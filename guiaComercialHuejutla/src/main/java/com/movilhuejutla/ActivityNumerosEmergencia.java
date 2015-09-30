package com.movilhuejutla;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ActivityNumerosEmergencia extends ActionBarActivity implements OnItemClickListener{

	private ListView lista;
	private static ArrayList<NumEmergencia> arraynumeros;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_emergencia);
		setActionBar();
		lista = (ListView) findViewById(R.id.listaEmergencia);
		lista.setOnItemClickListener(this);
		arraynumeros = new ArrayList<NumEmergencia>();
		getNumeros();
		lista.setAdapter(new AdapterEmergencias(this, arraynumeros));
	}
	
	private void setActionBar() {
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, "NUMEROS DE EMERGENCIA"));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent llamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+arraynumeros.get(position).telefono));
		startActivity(llamar);	
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
	
	private static void getNumeros(){
		arraynumeros.add(new NumEmergencia("Presidencia","7898961515"));
		arraynumeros.add(new NumEmergencia("Seguridad Publica","7898960212"));
		arraynumeros.add(new NumEmergencia("Transito y Vialidad","7898960484"));
		arraynumeros.add(new NumEmergencia("Cruz Roja","7898961940"));
		arraynumeros.add(new NumEmergencia("Bomberos","7898964000"));
		arraynumeros.add(new NumEmergencia("Policia Ministerial","7898960483"));
		arraynumeros.add(new NumEmergencia("Policia Federal","7898960929"));
		arraynumeros.add(new NumEmergencia("84 Batallon de Infanteria","7898960213"));
		arraynumeros.add(new NumEmergencia("IMSS","7898960307"));
		arraynumeros.add(new NumEmergencia("ISSSTE","7898960526"));
		arraynumeros.add(new NumEmergencia("Hospital Regional","7898963000"));
		arraynumeros.add(new NumEmergencia("Centro de Salud","7898960380"));
		arraynumeros.add(new NumEmergencia("DIF","7898962427"));
		arraynumeros.add(new NumEmergencia("CRIT","7712108283"));
		arraynumeros.add(new NumEmergencia("SAGARPA","7898960847"));
		arraynumeros.add(new NumEmergencia("SEMEFO","7898961844"));
	}
}

class NumEmergencia{
	public String nombre;
	public String telefono;
	
	public NumEmergencia(String n, String t){
		nombre = n;
		telefono = t;
	}
}
