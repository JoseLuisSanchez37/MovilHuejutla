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
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentNews extends Fragment {
	
	private Activity actividad;
	private Context contexto;
	private Bundle bundle;
	private static final String URL_WEB_SERVICE = "http://www.movilhuejutla.com.mx/webservice_dev.php?negocio=";
	private GridView gvAnuncios;
	private ArrayList<ObjectAnuncio> anuncios;
	
	private View view = null;
	private TextView mensaje;
	private ProgressBar bar;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		actividad = activity;
		contexto = actividad.getApplication().getApplicationContext();
		bundle = getArguments();
		anuncios = new ArrayList<ObjectAnuncio>();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.layout_default, container, false);
		mensaje = (TextView) view.findViewById(R.id.mostrar_mensaje);
		bar = (ProgressBar) view.findViewById(R.id.progreso_anuncios);
		gvAnuncios = (GridView) view.findViewById(R.id.gridview_anuncios);
		
		bar.setVisibility(View.VISIBLE);
		long negocio = bundle.getLong("id_negocio"); 
		String url = URL_WEB_SERVICE+negocio;
		
		if(Utils.redDisponible(contexto)){
			 descargarAnuncios(url);
		}else{
			bar.setVisibility(View.GONE);
			mensaje.setText("Sin conexion a Internet");
			mensaje.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_not_connection_internet,0,0);
		}
		
		return view;
	}
		
	public void descargarAnuncios(String url){
		RequestQueue volley = Volley.newRequestQueue(contexto);
		JsonObjectRequest json = new JsonObjectRequest(Method.GET, url, null, ResponseListener(), ErrorListener());
		volley.add(json);
	}
	
	private Response.Listener<JSONObject> ResponseListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            	try {
            		JSONArray ads = response.getJSONArray("anuncios");
					for(int i = 0; i < ads.length(); i++){
						anuncios.add(convertirAnuncio(ads.getJSONObject(i)));						
					}
					bar.setVisibility(View.GONE);
					mensaje.setVisibility(View.GONE);
					gvAnuncios.setVisibility(View.VISIBLE);
					gvAnuncios.setAdapter(new AdapterAnuncio(contexto,anuncios));					
				} catch (JSONException e) {
					try {
						bar.setVisibility(View.GONE);						
						JSONArray json_sms = response.getJSONArray("mensaje");
						for(int i = 0;i < json_sms.length(); i++){
							JSONObject j = json_sms.getJSONObject(i);
							mensaje.setText(j.getString("estado"));
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}//catch
            }//onResponse
        };//Response.Listener
     }
       
    private Response.ErrorListener ErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	bar.setVisibility(View.GONE);
            	mensaje.setText("Ocurrio un error al descargar los anuncios");
            }
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
}