package com.movilhuejutla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMostrarMapa extends ActionBarActivity implements OnMarkerClickListener{
	 
	private LocationManager locManager;
	private GoogleMap mapa;
	private Bundle p;
	public ProgressBar progress;
	public TextView distance;
	public TextView time;
	public LinearLayout distances;
	public ArrayList<LatLng> markerPoints;	
	
	private DB_MH db;
	
	public Location GPS_LOCATION = null;
	public LatLng MARKER_SELECTED = null;
	public boolean FIRST_LOCATION = true;
	public boolean MARKER_OK = false;
	public String CURRENT_MOD  = " "; 
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mapa);
        setActionBar();
        db = DB_MH.getInstance(this);
        progress = (ProgressBar) findViewById(R.id.progreso_inde);
        
        distance = (TextView) findViewById(R.id.distancia);
        time = (TextView) findViewById(R.id.tiempo);
        
        distances = (LinearLayout) findViewById(R.id.dis_time);
        p = getIntent().getExtras();
        markerPoints = new ArrayList<LatLng>();
        iniciarMapa();
    }
       
    @Override
    public boolean onMarkerClick(final Marker marker) {

    	CameraPosition camara = new CameraPosition(marker.getPosition(),17.0f,mapa.getCameraPosition().tilt,mapa.getCameraPosition().bearing);
		mapa.animateCamera(CameraUpdateFactory.newCameraPosition(camara), 30, null);
    	
		//Add info marker
		marker.showInfoWindow();
			
		//Make the marker bounce
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 800;
        
        Projection proj = mapa.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        MARKER_SELECTED = markerLatLng; //Asignamos las coordenadas
        
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
        
        if(MARKER_OK){
        	crearRuta(CURRENT_MOD);
        }
        
        return true;
	}
      
    /** metodo para crear la Ruta entre dos puntos**/
    private void crearRuta(String modo_viaje){
    	
    	if(isGPSAvaliable()){	
    		if (GPS_LOCATION != null){	
		    	if(Utils.redDisponible(this)){
		    		
		    		/************************************************************************/
			    	if(markerPoints.size() == 2){
						String url = getDirectionsUrl(markerPoints.get(1), markerPoints.get(0), modo_viaje);
						
						try{
							DownloadTask downloadTask = new DownloadTask();
							downloadTask.execute(url);
							LatLngBounds.Builder builder = new LatLngBounds.Builder (); 
							builder.include(markerPoints.get(0)); 
							builder.include(markerPoints.get(1)); 
							LatLngBounds bounds = builder.build(); 
							mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
							
						}catch(Exception e){
							Toast.makeText(this, "Ocurrio un error al crear la ruta", Toast.LENGTH_LONG).show();
						}
			    	}else{
			    		if(MARKER_SELECTED != null){
			    			String url = getDirectionsUrl(markerPoints.get(markerPoints.size()-1), MARKER_SELECTED, modo_viaje);
			    			
							try{
								DownloadTask downloadTask = new DownloadTask();
								downloadTask.execute(url);
								Log.v("MARCADORES MULTIPLE", "ejecutando download task");
								LatLngBounds.Builder builder = new LatLngBounds.Builder(); 
								builder.include(markerPoints.get(markerPoints.size()-1));
								builder.include(MARKER_SELECTED); 
								LatLngBounds bounds = builder.build(); 
								mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30));
								
							}catch(Exception e){
								Toast.makeText(this, "Ocurrio un error al crear la ruta", Toast.LENGTH_LONG).show();
							}
			    		}else{
			    			Toast.makeText(this, "Debes seleccionar un marcador", Toast.LENGTH_LONG).show();
			    			MARKER_OK = true;
			    			CURRENT_MOD = modo_viaje;
			    		}
			    	}	
					
			    	/***********************************************************************/
		    	}else{
		    		Toast.makeText(this, "Lo sentimos no hay una conexion a Internet", Toast.LENGTH_LONG).show();
		    	}
	    	}else{
	    		Toast.makeText(this, "Buscando Ubicacion GPS...", Toast.LENGTH_LONG).show();
	    	}
    	}else{
    		buscarUbicacion();
    	}
    }
    
    /** metodo que se carga al inicio de la actividad que muestra el mapa **/
	private void iniciarMapa() {
        if (mapa == null) {
            mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mapa != null) {
            	mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            	mapa.setBuildingsEnabled(true);
            	mapa.setOnMarkerClickListener(this);
            	marcadorNegocio();
           }
        }
    }

	/** metodo para dibujar el marcador del negocio en el mapa **/
	private void marcadorNegocio(){
	
		ArrayList<LatLng> coordenadas = db.obtenerCoordenadas(p.getLong("id_negocio"));
		String[] direcciones = db.getDirecciones(p.getLong("id_negocio"));
		final LatLngBounds.Builder builder = new LatLngBounds.Builder();//builder
		String path = Utils.getPathApp(this)+"/Imagenes/"+ p.getString("imagen") +".jpg";
		Bitmap logo;
		
		if(BitmapFactory.decodeFile(path) != null){
			logo = BitmapFactory.decodeFile(path);
		}else{
			logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		}
		
        int size = coordenadas.size();
		for (int i = 0; i < size; i++) {
			MarkerOptions marker = new MarkerOptions();
			marker.position(coordenadas.get(i));
			marker.icon(BitmapDescriptorFactory.fromBitmap(dibujarImagenMarker(logo)));
			marker.title(p.getString("nombre_negocio"));
			marker.snippet(direcciones[i]);
			mapa.addMarker(marker);
			markerPoints.add(coordenadas.get(i));
			builder.include(marker.getPosition()); //builder
		}
				
		CameraPosition camara = new CameraPosition(coordenadas.get(0),17.0f,mapa.getCameraPosition().tilt,mapa.getCameraPosition().bearing);
		mapa.animateCamera(CameraUpdateFactory.newCameraPosition(camara), 30, null);
		mapa.setOnCameraChangeListener(new OnCameraChangeListener() {

		    @Override
		    public void onCameraChange(CameraPosition arg0) {
		    	if(markerPoints.size() > 1){
		    		mapa.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 30));
		    		mapa.setOnCameraChangeListener(null);
		    	}else{
		    		mapa.setOnCameraChangeListener(null);
		    	}
		    }
		});			
	}
	
	public Bitmap dibujarImagenMarker(Bitmap logo){
        Bitmap bitmap = logo; 
        Paint paint = new Paint();
           paint.setFilterBitmap(true);
           int targetWidth  = 100;
           int targetHeight = 100;
           Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,Bitmap.Config.ARGB_8888);
           RectF rectf = new RectF(0, 0, 100, 100);
           Canvas canvas = new Canvas(targetBitmap);
           Path path = new Path();
           path.addRoundRect(rectf, targetWidth, targetHeight, Path.Direction.CW);
           canvas.clipPath(path);
           canvas.drawBitmap( bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                           new Rect(0, 0, targetWidth, targetHeight), paint);

           Matrix matrix = new Matrix();
           matrix.postScale(1f, 1f);
           Bitmap resizedBitmap = Bitmap.createBitmap(targetBitmap, 0, 0, 100, 100, matrix, true);
           return resizedBitmap;           
    }
	
	/** metodo que dibuja mi ubicacion actual en el mapa **/
	private void posicionActual(Location loc){
		
		this.GPS_LOCATION = loc;
		LatLng miPosicion = new LatLng(loc.getLatitude(),loc.getLongitude());
		
		if(FIRST_LOCATION){
			progress.setVisibility(View.GONE);					//ocultamos la barra de progreso de busqueda
			markerPoints.add(markerPoints.size(), miPosicion); //agregamos la ubicacion GPS en el array de marcadores
			FIRST_LOCATION = false;								//bandera de primera posicion encontrada
			mapa.setMyLocationEnabled(true);					// activamos la ubicacion en pantalla
		}else{
			progress.setVisibility(View.GONE);
			markerPoints.set(markerPoints.size()-1, miPosicion);	//actualizamos la ubicacion en el arreglo
			mapa.setMyLocationEnabled(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.action_bar_mapa, menu);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(isGPSAvaliable()){
			if(GPS_LOCATION == null){
				progress.setVisibility(View.VISIBLE);
				buscarUbicacion();
			}else{
				progress.setVisibility(View.GONE);
			}
		}	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				Intent intent = new Intent(this,ActivityDescripcionNegocio.class);
				intent.putExtra("id_negocio",p.getLong("id_negocio"));
				intent.putExtra("id_categoria",p.getLong("id_categoria"));
				intent.putExtra("nombre_categoria", p.getString("nombre_categoria"));
				intent.putExtra("navegacion", p.getInt("navegacion"));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
		
			case R.id.a_pie:
				crearRuta("walking");
				return true;
				
			case R.id.en_coche:
				crearRuta("driving");
				return true;
				
			case R.id.hibrido:
				mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);				
				return true;
				
			case R.id.normal:
				mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				return true;
			
			case R.id.satelite:
				mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				return true;
				
			case R.id.terreno:
				mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				return true;
			
		}
		return super.onOptionsItemSelected(item);
		
	}

	//*************************************************OBTENER LOCALIZACION***************************************************************//
	
	/** metodo que permite verificar si el dispositivo GPS esta activado **/
    private boolean isGPSAvaliable(){
    	locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	return locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);    	
    }
    
	public void setActionBar(){
		getSupportActionBar().setDisplayShowTitleEnabled(false);
    	getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, getIntent().getExtras().getString("nombre_negocio")));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
    }
    
    /** metodo que se ejecuta al iniciar la actividad,
     * en caso de tener activado el GPS se suscribira al proveedor de localizacion para obtener la ubicacion actualizada
     */
    public void buscarUbicacion(){
    	if(isGPSAvaliable()){
	    	locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 2, new EscuchadorDeLocalizacion());
	    }
    	else{
    		AlertDialog.Builder encenderGPS =  new AlertDialog.Builder(this);
    		encenderGPS.setMessage("Para mostrar la ruta es necesario activar el servicio de localizacion GPS")
    				   .setTitle("Activar GPS")
    				   .setCancelable(false)
    				   .setPositiveButton("Ir a Activar", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		    	    startActivity(intent);
				}
			})
    		.setNegativeButton("Cancelar", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			
			.setIcon(android.R.drawable.ic_dialog_info)
			.show();
    	}
    }
    
    /******************************************************CLASE ESCUCHADORA DEL GPS***********************************************************/
    
    private class EscuchadorDeLocalizacion implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			posicionActual(location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
    	
    }    
    /*************************************************METODOS Y CLASES PARA MOSTRAR LA RUTA****************************************/
    
    
	private String getDirectionsUrl(LatLng origin,LatLng dest,String modo){
					
		ArrayList<LatLng> wayPoints = new ArrayList<LatLng>();
		wayPoints.add(dest);
		wayPoints.add(origin);
		
		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		
		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
					
		// Sensor enabled
		String sensor = "sensor=false";			
				
		// Waypoints
		String waypoints = "";
		for(int i=2;i < wayPoints.size();i++){
			LatLng point  = wayPoints.get(i);
			if(i==2)
				waypoints = "waypoints=";
			waypoints += point.latitude + "," + point.longitude + "|";
		}
		
		//Travel Modes
		String mode = "mode="+modo;
		
					
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints+"&"+mode;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "http://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		return url;
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		@Override
		protected void onPreExecute(){
			progress.setVisibility(View.VISIBLE);
		}
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
			// For storing data from web service
			String data = "";
					
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		@Override
		protected void onProgressUpdate(Void... parms){
			progress.setVisibility(View.VISIBLE);
		}
		
		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			progress.setVisibility(View.GONE);
			
			ParserTask parserTask = new ParserTask();
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			String distancia = "";
			String tiempo = "";
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);					
					
					if(j==0){    // Get distance from the list
                        distancia = point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        tiempo = point.get("duration");
                        continue;
                    }
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(10);
				lineOptions.color(Color.BLUE);				
			}
			
			progress.setVisibility(View.GONE);
			distances.setVisibility(View.VISIBLE);
			distance.setText(distancia);
			time.setText(tiempo);
			
			// Drawing polyline in the Google Map for the i-th route
			mapa.addPolyline(lineOptions);			
		}	
				
    }   
    
}