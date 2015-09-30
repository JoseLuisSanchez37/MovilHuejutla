package com.movilhuejutla;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.movilhuejutla.DB_MH.Image;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Utils {

	//Urls para el registro y descarga de base de datos
	public static final String URL_DOWNLOAD_DATABASE = "http://movilhuejutla.com.mx/database/guiacomercial";
	public static final String NAME_DB = "guiacomercial";
	public static final String URL_IMAGES = "http://www.movilhuejutla.com.mx/database/";
	public static final String API_METEORED = "http://api.meteored.mx/index.php?api_lang=mx&localidad=70372&affiliate_id=ldo9e648usky&v=2&h=1";
    public static final String URL_MOVILHUE = "http://movilhuejutla.com.mx/";
	public static Intent parseIntent;

	/**
	 * 
	 * @param contexto
	 * @return Devuelve true si hay una conexion a Internet
	 */
	public static boolean redDisponible(Context contexto) {
		 ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo netInfo = cm.getActiveNetworkInfo();
		 if(netInfo != null && netInfo.isConnected()){
			 return true;
		 }else{
			 return false;
		 }
	}
	
	/**
	 * 
	 * @param inputStream
	 * @return String Devuelve la cadena convertida
	 * @throws IOException
	 */
	public static String convertInputStreamToString(InputStream inputStream){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "0";
        try {
            result= "";
			while((line = bufferedReader.readLine()) != null)
			    result += line;
		} catch (IOException e) { result = "0"; }
 
        try {
			inputStream.close();
		} catch (IOException e) { }
        return result;
    }
	
	/**
	 * 
	 * @param sourceLocation File ->Archivo de origen del archivo
	 * @param targetLocation File-> Archivo de destino donde se copiara el archivo
	 * @return true si a copia fue exitosa, false si no lo fue
	 */
	public static boolean copyFile(File sourceLocation, File targetLocation) {
		InputStream in = null;
		OutputStream out = null;
		
		try{
		    in = new FileInputStream(sourceLocation);
		    out = new FileOutputStream(targetLocation);
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		      out.write(buf, 0, len);
		    }
		    out.flush();
		    out.close();
		    in.close();
		    return true;
		}catch(Exception e){
			return false; 
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) { }
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) { }
			}
		}
		    
	}
	
	/**
	 * 
	 * @return String Devuelve la ruta obsoluta de la carpeta de la aplicacion
	 */
	public static String getPathApp(Context contexto){
		PackageManager packManager = contexto.getPackageManager (); 
		String folder = contexto.getPackageName(); 
		try  { 
			PackageInfo info = packManager.getPackageInfo(folder, 0); 
			folder = info.applicationInfo.dataDir;
		}catch(NameNotFoundException e){ 
			File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			folder = file.getAbsolutePath();
		}
		return folder;
	}
	
	/**
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyImageFromAsset(InputStream in, OutputStream out){
	    byte[] buffer = new byte[1024];
	    int read;
	    try {
			while((read = in.read(buffer)) != -1){
			  out.write(buffer, 0, read);
			}
		} catch (IOException e) { }
	}
	
	/**
	 * Metodo que nos permite verificar si faltan imagenes de extraer desde el servidor
	 * @return
	 */
	public static ArrayList<Image> checkImages(Context contexto){
		ArrayList<Image> faltantes = new ArrayList<Image>();
		ArrayList<Image> images = DB_MH.getInstance(contexto).getAllImages();
		
		for(int i = 0; i < images.size(); i++){
			String path = Utils.getPathApp(contexto)+"/Imagenes/"+ images.get(i).imagen +".jpg";
			if(!new File(path).exists()){
				faltantes.add(images.get(i));
			}
		}
		return faltantes;
	}
	
	public static int getVersionAppCode(Context contexto){
		int versionApp = 0;
		try {
			versionApp = contexto.getPackageManager().getPackageInfo(contexto.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) { }
		return versionApp;
	}
	
	public static Intent getIntentParseAnalytics(){
		return parseIntent;
	}
	
	public static Intent setIntentParseAnalytics(Intent intent){
		parseIntent = intent;
		return parseIntent;
	}
	
	public static boolean isMovilHuejutlaAvaliable(Context context) {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        try {
	        	int time = 1000;
	            URL url = new URL("http://movilhuejutla.com.mx/");  
	            HttpURLConnection ping = (HttpURLConnection) url.openConnection();
	            ping.setConnectTimeout(time);
	            ping.setReadTimeout(time);
	            ping.connect();
	            
	            if(ping.getResponseCode() == 200) {
	                return true;
	            } else {
	                return false;
	            }
	        } catch (MalformedURLException e1) {
	            return false;
	        } catch (IOException e) {
	            return false;
	        }
	    }
	    return false;
	}
	
}
