package com.movilhuejutla;

import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class Application extends android.app.Application {

	//public static final String APPLICATION_ID_PRODUCCION = "XByCH134Ou22uCI25y10IMpPtCKZfHnCiH9vhLkN";
	//public static final String CLIENT_KEY_PRODUCCION = "MzpNcQJYzTlwfaZ1T0z5q7REq5BhPDettop0mKMB";
	
	//public static final String APPLICATION_ID_DESARROLLO = "i2ffCqykeuwcowjy0Tmz6W7L4ozrFrHB5q5Vz6mi";
	//public static final String CLIENT_KEY_DESARROLLO = "EkYSjGc9gk94FkO6gLyZsQRd7y9aZpoZvcPOq8rh";
	
	@Override
	public void onCreate() {
		super.onCreate();
		//Parse.initialize(this, APPLICATION_ID_PRODUCCION, CLIENT_KEY_PRODUCCION);
        //ParseInstallation.getCurrentInstallation().saveInBackground();
        
        //Intent actualizaciones = new Intent(this, IntentServiceUpdates.class);
    	//startService(actualizaciones);

    }
	
	public static String getIdParse(){
        return null;
    	//return ParseInstallation.getCurrentInstallation().getInstallationId();
    }
	
}
