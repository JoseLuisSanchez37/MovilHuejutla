package com.movilhuejutla;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

public class ActivityDescripcionNegocio extends ActionBarActivity implements ActionBar.TabListener{
	
	private ObjectNegocio negocio;
	private Bundle extras;
	
	private ViewPager viewPager;
	private AdapterTabsPager mAdapter;
	
	private DB_MH db;
	private int navegacion = 0;
	private int num_tabs = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.viewpager);
		db = DB_MH.getInstance(this);
		extras = getIntent().getExtras();
		negocio = db.obtenerNegocio(extras.getLong("id_negocio"));
		        
        long id_neg = extras.getLong("id_negocio");
        long id_cat = extras.getLong("id_categoria");
        String nom_cat = extras.getString("nombre_categoria");
        navegacion = extras.getInt("navegacion");
   
        //Verificar si el negocio tiene pagina en Facebook
        //
        if(negocio.facebook != null){
        	if(negocio.facebook.length() > 0){
        		num_tabs = 3;
        	}
        }
        
        setActionBar();
        
        //inicializacion
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new AdapterTabsPager(getSupportFragmentManager(), id_neg, id_cat, nom_cat, navegacion, num_tabs);
        viewPager.setAdapter(mAdapter);
                
        //agregar los tabuladores a ActionBar
        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("DESCRIPCION").setTabListener(this));
        getSupportActionBar().addTab(getSupportActionBar().newTab().setText("NOTICIAS").setTabListener(this));
        if(num_tabs == 3){
        	getSupportActionBar().addTab(getSupportActionBar().newTab().setText("FACEBOOK").setTabListener(this));
        }
        
        //escuchador de cambio de pagina
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
        
	}
	
	private void setActionBar(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, negocio.nombre ));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	//Acciones que se realizan cuando se presiona el boton de retroceso en ActionBar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			if(navegacion == ActivityCategorias.FROM_BUSQUEDA){
				Intent intent = new Intent(this,ActivityBuscar.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			if(navegacion == ActivityCategorias.FROM_FAVORITOS){
				Intent intent = new Intent(this,ActivityFavoritos.class);
				intent.putExtra("navegacion", navegacion);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			if(navegacion == ActivityCategorias.FROM_PRINCIPAL){
				Intent intent = new Intent(this,ActivityNegocio.class);
				intent.putExtra("id_categoria", extras.getLong("id_categoria"));
				intent.putExtra("navegacion", navegacion);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			if(navegacion == ActivityCategorias.FROM_ORDENADOS){
				Intent intent = new Intent(this,ActivityCategorias.class);
				intent.putExtra("navegacion", navegacion);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Metodo que se encarga de marcar a un numero de telefono
	public void marcar(View v) {
		final String[] telefonos =  db.obtenerTelefonos(extras.getLong("id_negocio"));
		
			if(telefonos.length == 1){
				Intent llamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telefonos[0]));
				startActivity(llamar);
			}else{
				AlertDialog.Builder nums = new AlertDialog.Builder(this);
				nums.setTitle("Seleccione un Numero");
				nums.setItems(telefonos, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int telefono) {
						Intent llamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telefonos[telefono]));
						startActivity(llamar);					
					}
				});
				AlertDialog alert = nums.create();
				alert.show();
			}
			
	}
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {		
	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		viewPager.setCurrentItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {		
	}
	
	public void iniciarMapa(View v){
		Intent mapa = new Intent(this, ActivityMostrarMapa.class);
		mapa.putExtra("id_negocio",extras.getLong("id_negocio"));
		mapa.putExtra("nombre_negocio", negocio.nombre);
		mapa.putExtra("id_categoria", extras.getLong("id_categoria"));
		mapa.putExtra("nombre_categoria", extras.getString("nombre_categoria"));
		mapa.putExtra("navegacion", extras.getInt("navegacion"));
		mapa.putExtra("imagen", negocio.foto);
		startActivity(mapa);
	}
		
}