package com.movilhuejutla;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class ReceiverPush extends BroadcastReceiver{	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Utils.setIntentParseAnalytics(intent);
		NotificationCompat.Builder notificacion = null;
		
		try{
			//****************************************************************************
			JSONObject ad = new JSONObject(intent.getExtras().getString("com.parse.Data"));
						
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        if(ad.has("delete")){
        		try{
        			DB_F.getInstance(context).getWritableDatabase().delete("negs", "Id="+ad.getString("delete"), null);
        		}catch(Exception e){ }
	        }
	        
	        /*1 = notificacion en general   
	         2 = notificacion para compartir la app con whatssapp o facebook
	         3 = notificacion para visualizar una direccion web URL*/
			//*******************************Creamos la Intencion***********************************************
	        Intent push = new Intent(context, ActivityPush.class);
	        
	        //***************************************type = 1***************************************************
	        if(Integer.parseInt(ad.getString("type")) == 1 ){
					push.putExtra("titulo", ad.getString("titulo"));
					push.putExtra("descripcion", ad.getString("descripcion"));
					push.putExtra("imagen", ad.getString("imagen"));
					push.putExtra("negocio", ad.getString("negocio"));
					push.putExtra("type_ad", ad.getString("type_ad"));
					push.putExtra("type", ad.getString("type"));
					
					PendingIntent intencionEnEspera = PendingIntent.getActivity(context, 0, push, PendingIntent.FLAG_UPDATE_CURRENT);
					String pathImg = Utils.getPathApp(context)+"/Imagenes/"+ad.getString("negocio")+"_logo.jpg";
					Bitmap bm = BitmapFactory.decodeFile(pathImg);
			        					
					notificacion = new NotificationCompat.Builder(context)
							.setContentTitle(ad.getString("titulo")).setContentText(ad.getString("descripcion"))
							.setContentIntent(intencionEnEspera).setAutoCancel(true).setSound(uri);
					if(bm != null){
						notificacion.setLargeIcon(bm);
						notificacion.setSmallIcon(R.drawable.ic_launcher);
					}
					else
						notificacion.setSmallIcon(R.drawable.ic_launcher);
	        }
	       //***************************************type = 2****************************************************
	       if(Integer.parseInt(ad.getString("type")) == 2){
	    	   push.putExtra("titulo", ad.getString("titulo"));
	    	   push.putExtra("descripcion", ad.getString("descripcion"));
	    	   push.putExtra("type", ad.getString("type"));
	    	   
	    	   PendingIntent intencionEnEspera = PendingIntent.getActivity(context, 0, push, PendingIntent.FLAG_UPDATE_CURRENT);
	    	   notificacion = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
	    			   .setContentTitle(ad.getString("titulo")).setContentText(ad.getString("descripcion"))
	    			   .setContentIntent(intencionEnEspera).setAutoCancel(true).setSound(uri);
	       }
	       //***************************************type = 3****************************************************
	       if(Integer.parseInt(ad.getString("type")) == 3){
	    	   push.putExtra("titulo", ad.getString("titulo"));
	    	   push.putExtra("descripcion", ad.getString("descripcion"));
	    	   push.putExtra("type", ad.getString("type"));
	    	   push.putExtra("url", ad.getString("url"));
	    	   
	    	   PendingIntent intencionEnEspera = PendingIntent.getActivity(context, 0, push, PendingIntent.FLAG_UPDATE_CURRENT);
	    	   notificacion = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
	    			   .setContentTitle(ad.getString("titulo")).setContentText(ad.getString("descripcion"))
	    			   .setContentIntent(intencionEnEspera).setAutoCancel(true).setSound(uri);
	       }
	        
	       //***************************************Mostramos la Notificacion*********************************************
	       NotificationManager pushManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        					pushManager.notify(1, notificacion.build());
	        
	       //*****************************Iniciamos el IntentService Actualizaciones*********************************************
    		//Intent actualizaciones = new Intent(context, IntentServiceUpdates.class);
    		//context.startService(actualizaciones);
        
	    }catch (JSONException e) {  }
	}
}