package com.movilhuejutla;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityMostrarAnuncios extends ActionBarActivity{

	private static final String URL_WEB_SERVICE = "http://movilhuejutla.com.mx/webservice_dev.php?all";
	private GridView gvAnuncios;
	private ArrayList<ObjectAnuncio> anuncios;
	private ProgressBar bar;
	private TextView mensaje;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_default);
		setActionBar();
		gvAnuncios = (GridView) findViewById(R.id.gridview_anuncios);
		anuncios = new ArrayList<ObjectAnuncio>();
		mensaje = (TextView) findViewById(R.id.mostrar_mensaje);
		bar = (ProgressBar) findViewById(R.id.progreso_anuncios);
		bar.setVisibility(View.VISIBLE);
		
		if(Utils.redDisponible(this)){
			RequestQueue volley = Volley.newRequestQueue(this);
			JsonObjectRequest json = new JsonObjectRequest(Method.GET, URL_WEB_SERVICE, null, ResponseListener(), ErrorListener());
			volley.add(json);
		}else{
			bar.setVisibility(View.GONE);
			mensaje.setText("Sin conexion a Internet");
			mensaje.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_not_connection_internet,0,0);
		}
	}
	
	private Response.Listener<JSONObject> ResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            	try {
            		JSONArray ads = response.getJSONArray("anuncios");
            		int ads_lenght = ads.length();
					for(int i = 0; i < ads_lenght; i++){
						anuncios.add(convertirAnuncio(ads.getJSONObject(i)));						
					}
					bar.setVisibility(View.GONE);
					mensaje.setVisibility(View.GONE);
					gvAnuncios.setVisibility(View.VISIBLE);
					gvAnuncios.setAdapter(new AdapterAnuncio(getApplicationContext(),anuncios));
				} catch (JSONException e) {
					try {
						bar.setVisibility(View.GONE);						
						JSONArray json_sms = response.getJSONArray("mensaje");
						int jsonsms_lenght = json_sms.length();
						for(int i = 0;i < jsonsms_lenght; i++){
							JSONObject j = json_sms.getJSONObject(i);
							mensaje.setText(j.getString("estado"));
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
               }
            };
        }
       
    private Response.ErrorListener ErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        };
    }

	private final ObjectAnuncio convertirAnuncio(JSONObject obj) throws JSONException {
		return new ObjectAnuncio(
	    		obj.getLong("id_anuncio"),
	    		obj.getLong("id_negocio"),
	    		obj.getString("tipo"),
	    		obj.getString("titulo"),
	    		obj.getString("descripcion"),
	    		obj.getString("fecha"),
	    		obj.getString("imagen"));
	}
	
	
	public void setActionBar(){		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, "ANUNCIOS"));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int versionApiActual = android.os.Build.VERSION.SDK_INT;
		if (versionApiActual < android.os.Build.VERSION_CODES.JELLY_BEAN){
			switch (item.getItemId()){
			case android.R.id.home:
				Intent intent = new Intent(this,ActivityCategorias.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
