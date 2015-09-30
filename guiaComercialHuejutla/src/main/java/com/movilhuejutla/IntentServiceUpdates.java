package com.movilhuejutla;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.movilhuejutla.DB_MH.Image;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.util.Log;

public class IntentServiceUpdates extends IntentService{
	
	public IntentServiceUpdates() { 
		super(IntentServiceUpdates.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		if(Utils.isMovilHuejutlaAvaliable(this)){
		
		RequestQueue volley = Volley.newRequestQueue(this);		
		
		//Preferencias de Usuario e Instalaci�n
		SharedPreferences preferencias = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
		Boolean registrado = preferencias.getBoolean("DispositivoRegistrado", false);
		String idDispositivo = preferencias.getString("idDevice", null);
		int versionBaseDeDatosActual = preferencias.getInt("versionDatabase", 0);
		int versionApp = preferencias.getInt("versionApp", 0);
		String url = Utils.URL_MOVILHUE+"updates.php?idDevice="+
                                idDispositivo+"&versionDatabase="+
                                versionBaseDeDatosActual+"&type=updates";
		
		if(registrado){
			int versionServidor = comprobarActualizaciones(url);
			//**************************Comprobar Actualizacion de Base de Datos********************
			if(versionServidor > versionBaseDeDatosActual){
				//Descargamos la base de datos
				if(descargarBaseDeDatos(versionServidor)){
					while(true){
						//AsyncTask Finalizado procedemos a preguntar si la version de preferencias
						// es igual a la version del codigo fuente
						//este valor lo asignamos en el AsyncTask de la ActividadSplash
						// una vez que paso al metodo onPostExecute()
						if(versionApp == Utils.getVersionAppCode(this)){
							//AsyncTask Splash Finalizado. Banderas de uso de la BD y copia desde Assets activas??
							if(!FlagsCopy.getFlagDatabaseInUse(this) && !FlagsCopy.getFlagDatabaseFromAssets(this)){
                                //obtenemos la ruta de la base de datos
                                String ruta_base_datos = this.getDatabasePath("guiacomercial").getAbsolutePath();
                                //Creamos un objeto File con la ruta de la base de datos
                                File rutaBD = new File(ruta_base_datos);
                                //Levantamos bandera de copia de base de datos
								FlagsCopy.setFlagCopy(this, true);
                                //Creamos un objeto File con la ruta de de la base de datos recien descargada
								File rutaBDTemporal = new File(Utils.getPathApp(this), Utils.NAME_DB);
                                //Reemplazamos la actual por la recien descargada
								Utils.copyFile(rutaBDTemporal, rutaBD);
                                //Bajamos la bandera de copia de la base de datos
								FlagsCopy.setFlagCopy(this, false);
                                //Actualizamos la preferencia de la version de la base de datos local
								Editor editor = preferencias.edit();
								editor.putInt("versionDatabase", versionServidor);
								editor.commit();
                                //actualizamos la version de la base de datos en el registro del servidor
								actualizarVersionEnServidor(idDispositivo,versionServidor);
								break;
							}
						}
					}
				}
			}
			//**************************************************************************************
			
			//********** Eliminar Imagenes del Directorio por Medio de Fechas **********************
			
			final String urlUpdatesImages = Utils.URL_MOVILHUE+"updatesImages.php?idDevice="+idDispositivo;
			final String pathApp = Utils.getPathApp(this); 
			final String[] imagenes = {"_logo","_1","_2","_3","_4","_5","_6","_7","_8"};
			
			JsonObjectRequest updatesImages = new JsonObjectRequest(Request.Method.GET, urlUpdatesImages, null,
			    new Response.Listener<JSONObject>() {
			        @Override
			        public void onResponse(JSONObject response) {
			        	try {
							JSONArray json = response.getJSONArray("negocios");
							for(int i = 0; i <json.length(); i++){
								String negocio = json.get(i).toString();
								for(int j = 0; j < imagenes.length; j++){
									String pathImg = pathApp+"/Imagenes/"+negocio+imagenes[j]+".jpg";
						        	File f = new File(pathImg);
						        	if(f != null && f.exists()){
						        		f.delete();
							        }
								}
							}
						} catch (JSONException e) { }
			        }
			    }, 
			    new Response.ErrorListener(){
			         @Override
			         public void onErrorResponse(VolleyError error) {   }
			    }
			);
			
			volley.add(updatesImages);
			//**************************************************************************************
			
			//****************************Cambios en Imagenes***************************************
			ArrayList<Image> imgs = Utils.checkImages(this);
			String img = "";
			
			if(!imgs.isEmpty()){
				int j = imgs.size();
				for(int i = 0; i < j; i++){
					img = imgs.get(i).imagen;
					String urlImg = Utils.URL_IMAGES + imgs.get(i).negocio+"/"+ img +".jpg";
					final String pathSave = pathApp+"/Imagenes/"+ img +".jpg";
					ImageRequest request = new ImageRequest(urlImg,
						    new Response.Listener<Bitmap>() {
								@Override
								public void onResponse(Bitmap bitmap) {
									saveImage(bitmap, pathSave);
								}
						    }, 0, 0, null,
						    new Response.ErrorListener() {
						        @Override
								public void onErrorResponse(VolleyError error) {   }
						    });
					volley.add(request);
				}
			}
			//**************************************************************************************
			
			//*********************************Negocios Gratuitos***********************************
			int lastRow = DB_F.getInstance(this).getLastRow();
			String w = Utils.URL_MOVILHUE+"negs.php?lastId="+lastRow;
			JsonObjectRequest json = new JsonObjectRequest
			        (Request.Method.GET, w, null, new Response.Listener<JSONObject>() {

			    @Override
			    public void onResponse(JSONObject json) {
			    		DownloadNegocios negs = new DownloadNegocios(json, getApplicationContext());
			    		negs.parserJSON();
			    }
			}, new Response.ErrorListener() {

			    @Override
			    public void onErrorResponse(VolleyError error) {  }
			});
			
			volley.add(json);
			//**************************************************************************************

		}else{
			String idParse = Application.getIdParse();
			if(idParse != null && !idParse.isEmpty()){
				if(registrarNuevaInstalacion(idParse, versionBaseDeDatosActual)){
					Editor editor = preferencias.edit();
					editor.putBoolean("DispositivoRegistrado", true);
					editor.putString("idDevice", idParse);
					editor.commit();
				}
			}
		}
		
		}
        //Fin Metodo HandleIntent()
    }

    /**
	 * Metodo que descarga la base de datos desde el servidor
	 * @param versionResult int el valor del resultado de la respuesta del servidor
	 */
	private boolean descargarBaseDeDatos(int versionServidor){
		boolean download = false;
		String urlServer = Utils.URL_DOWNLOAD_DATABASE;
		File fileOut = new File(Utils.getPathApp(this), Utils.NAME_DB);
		FileOutputStream outputFile = null;
		InputStream inputStream = null;
		URL url;
		HttpURLConnection connection = null;
		int timeConnect = 1000; //Tiempo de Espera de Conexion con el Servidor
		int timeRead = 10000; //10 s. Tiempo de Espera de Lectura de Datos
		
		try {
			url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(timeConnect);
			connection.setReadTimeout(timeRead);
			connection.connect();
			
			if(connection.getResponseCode() == 200){
				
				if (fileOut.exists()) fileOut.delete();
				
				byte[] buffer = new byte[1024];
				int downloadedSize = 0;
				int bufferLength;
				int totalSize = connection.getContentLength();
				inputStream = connection.getInputStream();
				outputFile = new FileOutputStream(fileOut.getAbsolutePath());
				
				try{
					while ((bufferLength = inputStream.read(buffer)) > 0) {
						outputFile.write(buffer, 0, bufferLength);
					    downloadedSize += bufferLength;
					}

                    if(totalSize == downloadedSize){
                        download = true;
                    }
                    if(inputStream != null && outputFile != null){
                        inputStream.close();
                        outputFile.flush();
                        outputFile.close();
                    }

				}catch (IOException e){
                    if(inputStream != null){
                        inputStream.close();
                    }
                    if(outputFile != null){
                        outputFile.close();
                    }
                }
			}
		} catch (IOException e) { }
		finally{
			if(connection != null){
				connection.disconnect();
			}
			if(inputStream != null){
				try { inputStream.close(); } catch (IOException e) { }
			}
			if(outputFile != null){
				try { outputFile.close(); } catch (IOException e) { }
			}
		}
		return download;
	}
	
	/**
	 * 
	 * @param url La direccion URL para revisar si hay actualizaciones
	 * @return return int el numero de version, en caso de error devuelve 0
	 */
	private int comprobarActualizaciones(String url){
		int respuesta = 0;
		URL Url;
		HttpURLConnection conexion = null;
		InputStream inputStream = null;
		String resultado = "";	
		int time = 1000; //Tiempo de Conexion con Servidor y Espera de Respuesta
		
		try {
			Url = new URL(url);
			conexion = (HttpURLConnection) Url.openConnection();
			conexion.setConnectTimeout(time);
			conexion.setReadTimeout(time);
        	conexion.connect();
        	
        	if(conexion.getResponseCode() == 200){
		    	inputStream = conexion.getInputStream();
		        resultado = Utils.convertInputStreamToString(inputStream);
                if(resultado != "" && resultado.length() < 4){
                    int res = Integer.parseInt(resultado);
                    respuesta = res;
                }
            }
		}catch (IOException e) { }
		finally{
			if(conexion != null){
				conexion.disconnect();
			}
			if(inputStream != null){
				try { inputStream.close(); } catch (IOException e) { }
			}
		}
		return respuesta;
	}
	
	/**
	 * 
	 * @param idDevice ID de instalacion del Dispositivo
	 * @param versionDatabase Version de la base de datos actual
	 * @return Devuelve true si se registro exitosamente, false si no se registro
	 * @throws IOException 
	 */
	private boolean registrarNuevaInstalacion(String idDevice, int versionDatabase){
		boolean respuesta = false;
		String resultado = "";
		InputStream inputStream = null;
		URL Url;
		HttpURLConnection conexion = null;
		String url = Utils.URL_MOVILHUE+"updates.php?idDevice="+
                            idDevice+"&versionDatabase="+
                            versionDatabase+"&type=register";
		int time = 1000; //Tiempo de espera de conexion con el servidor
		
		try {
			Url = new URL(url);
			conexion = (HttpURLConnection) Url.openConnection();
			conexion.setConnectTimeout(time);
			conexion.setReadTimeout(time);
        	conexion.connect();
        	
        	if(conexion.getResponseCode() == 200){
		    	inputStream = conexion.getInputStream();
		        resultado = Utils.convertInputStreamToString(inputStream);
		        if(resultado.equals("ok")){
		        	respuesta = true;
		        }
        	}
		}catch (IOException e) { }
		finally{
			if(conexion != null){
				conexion.disconnect();
			}
			if(inputStream != null){
				try { inputStream.close(); } catch (IOException e1) { }
			}
		}
        return respuesta;
	}
	
	/**
	 * 
	 * @param idDevice el ID del dispositivo
	 * @param updateVersion la version actual de las preferencias
	 * @return 
	 */
	private boolean actualizarVersionEnServidor(String idDevice, int updateVersion){
		String url = Utils.URL_MOVILHUE+"updates.php?idDevice="+
                            idDevice+"&versionDatabase="+
                            updateVersion+"&type=updateChanges";
		String resultado = "";
		boolean respuesta = false;
		InputStream inputStream = null;
		URL Url = null;
		HttpURLConnection conexion = null;
		int time = 1000; //Tiempo de esper de conexion con el servidor
		
		try {
			Url = new URL(url);
			conexion = (HttpURLConnection) Url.openConnection();
			conexion.setConnectTimeout(time);
			conexion.setReadTimeout(time);
        	conexion.connect();
        	
        	if(conexion.getResponseCode() == 200){
	        	inputStream = conexion.getInputStream();
	            resultado = Utils.convertInputStreamToString(inputStream);
	            if(!resultado.equals("0")){
	            	respuesta = true;
	            }
        	}
		}catch (IOException e) { }
		finally{
			if(conexion != null){
				conexion.disconnect();
			}
			if(inputStream != null){
				try { inputStream.close(); } catch (IOException e1) { }
			}
		}
        return respuesta;			
	}
	
	/**
	 * 
	 * @param urlImage
	 * @param pathSaveImage
	 * @return
	 */
	private boolean saveImage(Bitmap bitmap, String pathSaveImage){
		if(bitmap != null){
		    File file = new File(pathSaveImage);
		    FileOutputStream fileOutputStream = null;
		    try{
		        fileOutputStream = new FileOutputStream(file);
		        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		        fileOutputStream.flush();
		        fileOutputStream.close();
		        return true;
		    }catch (IOException ex){
		    	if(file.exists() && file != null) file.delete();
		        return false;
		    }
		}else{
			return false;
		}
	}

}