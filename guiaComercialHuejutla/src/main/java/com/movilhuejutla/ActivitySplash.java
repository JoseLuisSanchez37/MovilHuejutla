package com.movilhuejutla;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivitySplash extends ActionBarActivity{
	
	private static final long RETARDO_ACTIVIDAD_PRINCIPAL = 2209;
    private SharedPreferences preferencias;
    private boolean imagenesCopiados, bienvenido;
        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.layout_splashapp);
	    
	    preferencias = getSharedPreferences("Preferencias",Context.MODE_PRIVATE);
	    imagenesCopiados = preferencias.getBoolean("ImagenesCopiados", false);
	    bienvenido = preferencias.getBoolean("bienvenido", false);

        //Ejecutamos el servicio en segundo plano
		//Intent actualizaciones = new Intent(this, IntentServiceUpdates.class);
		//startService(actualizaciones);

        //Tarea Asincrona para copia de datos e imagenes
	    new copiarDatos().execute(this);
	}
	

	private class copiarDatos extends AsyncTask<Activity, Void, Activity>{
				
		@Override
		protected Activity doInBackground(Activity... activity){
			copiandoInformacion(activity[0].getApplicationContext());
			return activity[0];
		}
		
		@Override
		protected void onPostExecute(Activity activity){
			//preferencia que nos indicara que antes de realizar la copia de la base de datos del servidor,
		    //no interfiera en la copia o actualizacion de las bases de datos y en la copia de las imagenes
		    //en el directorio de la app cuando se abre por primera vez o despues de actualizar la app
		    //El valor por defecto sera 0, Posteriormente se actualizara a la version del codigo fuente de la app.
		    //Y asi sucesivamente para cambios futuros
			Editor editor = preferencias.edit();
			editor.putInt("versionApp", Utils.getVersionAppCode(getApplicationContext()));
			editor.commit();
			
			//Mostramos las novedades de la version
			if(!bienvenido){
				editor = preferencias.edit();
				editor.putBoolean("bienvenido", true);
				editor.commit();

                //Mensaje de bienvenida
                SweetAlertDialog alertDialog = new SweetAlertDialog(activity, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
                alertDialog.setTitleText("Bienvenido");
                alertDialog.setContentText(activity.getResources().getString(R.string.mensaje_bienvenida));
                alertDialog.setCustomImage(R.drawable.ic_launcher);
                alertDialog.setConfirmText("OK. Entendido");
                alertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        TimerTask tarea = new TimerTask() {
                            @Override
                            public void run() {
                                Intent start = new Intent(ActivitySplash.this, ActivityCategorias.class);
                                startActivity(start);
                                finish();
                            }
                        };
                        Timer tiempo = new Timer();
                        tiempo.schedule(tarea,RETARDO_ACTIVIDAD_PRINCIPAL);
                    }
                });
                alertDialog.show();

			}else{
				 //Ejecutamos la nueva tarea
    			TimerTask tarea = new TimerTask() {
    		          @Override
    		          public void run() {
    		              Intent start = new Intent(ActivitySplash.this, ActivityCategorias.class);
    		              startActivity(start);
    		              finish();
    		          }
    		      };
    		    Timer tiempo = new Timer();
    		    tiempo.schedule(tarea,RETARDO_ACTIVIDAD_PRINCIPAL);
			}
		}
	  }
	
	private void copiandoInformacion(Context contexto){
		FlagsCopy.setFlagDatabaseFromAssets(contexto, true);
		DB_MH dbmovilhuejutla = DB_MH.getInstance(contexto);
	    DB_F dbfavoritos = DB_F.getInstance(contexto);
		
		try{
			 dbmovilhuejutla.crearBaseDeDatos();
		     dbfavoritos.getWritableDatabase();
		    
		    if(!imagenesCopiados){
		    	if(copyImagesFromAsset()){
		    		Editor editor = preferencias.edit();
		    		editor.putBoolean("ImagenesCopiados", true);
		    		editor.commit();
		    	}
		    }
		    FlagsCopy.setFlagDatabaseFromAssets(contexto, false);
		 
		}catch (Exception e) {
			FlagsCopy.setFlagDatabaseFromAssets(contexto, false);
		}
	}
		
	private boolean copyImagesFromAsset(){
		AssetManager assetManager = getAssets();
		String[] files = null;
		
		try{
			files = assetManager.list("Imagenes");
			File folderImages = new File(Utils.getPathApp(this)+"/Imagenes"); 
			if(!folderImages.exists()) folderImages.mkdirs(); //Si no existe la carpeta, se crea
		
			//Hacemos un ciclo for para copiar un archivo a la vez
			for(String filename : files){
				InputStream inputStream = null;
				OutputStream outputStream = null;
				//obtenemos el flujo de datos desde el archivo de Asset
				inputStream = assetManager.open("Imagenes/"+filename);
				//creamos un objeto File con la ruta de imagenes + el nombre del archivo
				File outFile = new File( folderImages.getAbsolutePath(), filename);
				//creamos un archivo de salida de datos
				outputStream = new FileOutputStream(outFile);
				//copiamos el archivo
				Utils.copyImageFromAsset(inputStream, outputStream);
				//cerramos los flujos de entrada y salida de datos
				inputStream.close();
				outputStream.close();
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
}