package com.movilhuejutla;

import android.content.Context;
import android.content.SharedPreferences;

public class FlagsCopy {
	
	private static final String COPY_DATABASE = "CopiaBD";
	private static final String FLAG_COPY_DATABASE = "copiando";
	private static final String FLAG_COPY_DATABASE_FROM_ASSETS = "copiandoAssets";
	private static final String FLAG_DATABASE_IN_USE = "consultando";
	
	private FlagsCopy(){}
	
	private static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(COPY_DATABASE, Context.MODE_PRIVATE);
	}
	
	//Getter y Setter para la Bandera de Copia de Base de Datos desde el Servidor
	public static boolean getFlagCopy(Context context){
		return getSharedPreferences(context).getBoolean(FLAG_COPY_DATABASE, false);
	}
	
	public static void setFlagCopy(Context context, boolean copiando){
		final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(FLAG_COPY_DATABASE, copiando);
		editor.commit();
	}
	
	//Getter y Setter para la Bandera de Consulta a la Base de Datos
	public static boolean getFlagDatabaseInUse(Context context){
		return getSharedPreferences(context).getBoolean(FLAG_DATABASE_IN_USE, false);
	}
		
	public static void setFlagDatabaseInUse(Context context, boolean consultando){
		final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(FLAG_DATABASE_IN_USE, consultando);
		editor.commit();
	}
	
	//Getter y Setter para la Bandera de Primera Copia de Base de Datos desde Asssets
	public static boolean getFlagDatabaseFromAssets(Context context){
		return getSharedPreferences(context).getBoolean(FLAG_COPY_DATABASE_FROM_ASSETS, false);
	}
		
	public static void setFlagDatabaseFromAssets(Context context, boolean consultando){
		final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
		editor.putBoolean(FLAG_COPY_DATABASE_FROM_ASSETS, consultando);
		editor.commit();
	}
			
}
