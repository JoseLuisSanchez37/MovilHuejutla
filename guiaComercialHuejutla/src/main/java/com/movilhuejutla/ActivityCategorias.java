package com.movilhuejutla;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityCategorias extends ActionBarActivity implements ActionBar.TabListener{
	
	public static final int FROM_PRINCIPAL = 1;
	public static final int FROM_BUSQUEDA = 2;
	public static final int FROM_FAVORITOS = 3;
	public static final int FROM_ORDENADOS = 4;
	public static final int CURRENT_API_LEVEL = android.os.Build.VERSION.SDK_INT; 
	
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle toggle;
	public GridView categorias;
	
	public TextView marquesina;
	
	private ViewPager viewPagerCategory;
	private AdapterViewCategory adapterCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_category);
        setActionBar();
        navigationDrawer();
        
        //Iniciando Fragmentos
        viewPagerCategory = (ViewPager) findViewById(R.id.viewpager_category);
        adapterCategory = new AdapterViewCategory(getSupportFragmentManager());
        viewPagerCategory.setAdapter(adapterCategory);
        
      //agregar los tabuladores a ActionBar
        getSupportActionBar().addTab(getSupportActionBar().newTab().setIcon(R.drawable.ic_action_home).setTabListener(this));
        getSupportActionBar().addTab(getSupportActionBar().newTab().setIcon(R.drawable.ic_action_spellcheck).setTabListener(this));
        
      //escuchador de cambio de pagina
        viewPagerCategory.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }
			
			@Override
			public void onPageScrollStateChanged(int arg0) { }
		});
    }
	
	public void navigationDrawer(){
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawerList.setAdapter(new AdapterDrawer(this, R.layout.layout_drawer, getResources().getStringArray(R.array.opciones)));
		drawerList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position){
					case 0:
						Intent pronostico = new Intent(ActivityCategorias.this, ActivityPronostico.class);
						startActivity(pronostico);
						break;
					case 1:
						Intent emergencia = new Intent(ActivityCategorias.this, ActivityNumerosEmergencia.class);
						startActivity(emergencia);
						break;
					case 2:
						Intent ii = new Intent(ActivityCategorias.this, ActivityMostrarAnuncios.class);
						startActivity(ii);
						break;
					case 3:
						Intent i = new Intent(ActivityCategorias.this, ActivityFavoritos.class);
						i.putExtra("navegacion", FROM_FAVORITOS);
						startActivity(i);
						break;
					case 4:
						Intent push = new Intent(ActivityCategorias.this,ActivityMisNotificaciones.class);
						startActivity(push);
						break;
					case 5:
						Intent googlePlay = new Intent(Intent.ACTION_VIEW);
						googlePlay.setData(Uri.parse("market://details?id=com.movilhuejutla"));
						startActivity(googlePlay);
						break;
					case 6:
						Intent c = new Intent(ActivityCategorias.this, ActivityContacto.class);
						startActivity(c);
						break;
				}
			}});
		
		toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.drawer, R.drawable.drawer, R.drawable.drawer){
			@Override
			public void onDrawerClosed(View view){
				supportInvalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerOpened(View drawerView){
				supportInvalidateOptionsMenu();
			}
		};
		
		drawerLayout.setDrawerListener(toggle);
	}	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.boton_search, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch(item.getItemId()){
			case R.id.button_img_search:
			Intent search = new Intent(this, ActivityBuscar.class);
			startActivity(search);
		}
		
		if(toggle.onOptionsItemSelected(item)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		toggle.onConfigurationChanged(newConfig);
	}

	public void getCategoria(View v){
		long id_cat = Integer.parseInt(v.getTag().toString());
		Intent negocio = new Intent(this, ActivityNegocio.class);
		negocio.putExtra("id_categoria", id_cat);
		negocio.putExtra("navegacion", FROM_PRINCIPAL);
		startActivity(negocio);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	public void setActionBar(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);							//icon " < "
		getSupportActionBar().setHomeButtonEnabled(true);								//habilitar boton
		getSupportActionBar().setDisplayShowHomeEnabled(true);						//mostrar icono de la app
		getSupportActionBar().setCustomView(tituloActionBar(this,"MOVILHUEJUTLA"));		//vista personalizada
		getSupportActionBar().setDisplayShowCustomEnabled(true);	//mostrar vista personalizada
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	@SuppressLint("InflateParams") 
	public static View tituloActionBar(Context contexto, String titulo){
		LayoutInflater inflate = LayoutInflater.from(contexto);
        final View v = inflate.inflate(R.layout.layout_action_bar, null); 
        final TextView titulo_bar = (TextView) v.findViewById(R.id.titulo_actionbar);
		titulo_bar.setText(titulo);
		titulo_bar.setSelected(true);
		return v;
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		viewPagerCategory.setCurrentItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}
	
}