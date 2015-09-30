package com.movilhuejutla;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_F extends SQLiteOpenHelper{

	private static DB_F instancia = null;
	private static final String TABLA = "favoritos";
	private static final String COLUMNA_ID = "id_negocio";
	private static final String COLUMNA_FAVORITO = "favorito";
	private static final String NOMBRE_BD = "favoritos.db";
	private static final int VERSION_BD = 20;
		
	private static final String SQL_GET = "SELECT "+COLUMNA_FAVORITO+" FROM "+TABLA+" WHERE id_negocio =";
	private static final String SQL_GET_ALL_FAVORITOS = "SELECT "+COLUMNA_ID+" FROM "+TABLA+" WHERE "+COLUMNA_FAVORITO+"=1";
	
	private static final String CREAR_TABLA_FAVORITOS = "create table "+
			TABLA+"("+COLUMNA_ID+" integer primary key, "+
			COLUMNA_FAVORITO+" integer not null )";
	
	private static final String CREAR_TABLA_NOTIFICACIONES = "create table if not exists notificaciones(" +
															"id_notificacion INTEGER PRIMARY KEY AUTOINCREMENT," +
															"titulo TEXT NOT NULL," +
															"descripcion TEXT NOT NULL," +
															"fecha TEXT NOT NULL," +
															"imagen TEXT," +
															"negocio integer)";
	
	private static final String CREAR_TABLA_GRATUITOS = "create table if not exists negs(" +
														"Id INTEGER NOT NULL," +
														"Name TEXT NOT NULL," +
														"Dress TEXT NOT NULL," +
														"Phone TEXT," +
														"pagado INTEGER DEFAULT 0)";
	
	public static DB_F getInstance(Context contexto){
		if(instancia == null){
    		instancia = new DB_F(contexto.getApplicationContext());
    	}
    	return instancia;
	}
	
	
	private DB_F(Context contexto) {
		super(contexto, NOMBRE_BD, null, VERSION_BD);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREAR_TABLA_FAVORITOS);
		db.execSQL(CREAR_TABLA_NOTIFICACIONES);
		db.execSQL(CREAR_TABLA_GRATUITOS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(CREAR_TABLA_NOTIFICACIONES);
		db.execSQL(CREAR_TABLA_GRATUITOS);
	}
	 
	@Override
	public void close() { 
    	    super.close();
	}
	 
	/**
	 * 
	 * @param id del negocio
	 * @return devuelve el valor del campo favorito, por default devuelve 0 si no se encuentra ningun registro con ese id 
	 */
	public int isFavorito(long id){
		 Cursor c = this.getReadableDatabase().rawQuery(SQL_GET+id, null);
		 if(c.moveToFirst()){
			 int favorito = c.getInt(0);
			 c.close();
			 this.close();
			 return favorito;
		 }
		 else{
			 c.close();
			 this.close();
			 return 0;
		 }
		
	}
	
	/**
	 * 
	 * @param id del negocio
	 * @param favorito 1 favorito , 0 no favorito
	 */
	public void setFavorito(long id, int favorito){
		Cursor c = this.getReadableDatabase().rawQuery(SQL_GET+id, null);		
		 if(c.moveToFirst()){
			 ContentValues cv = new ContentValues();
			 cv.put(COLUMNA_FAVORITO, favorito);
			 this.getWritableDatabase().update(TABLA, cv, COLUMNA_ID+"="+id, null);
			 this.close();
		 }
		 else{
			 ContentValues cv = new ContentValues();
			 cv.put(COLUMNA_ID, id);
			 cv.put(COLUMNA_FAVORITO, favorito);
			 this.getWritableDatabase().insert(TABLA, null, cv);
			 this.close();
		 }
		 c.close();
		 this.close();
	}
	
	public ArrayList<ObjectNegocio> getAllFavoritos(){
		Cursor c = this.getReadableDatabase().rawQuery(SQL_GET_ALL_FAVORITOS, null);
		ArrayList<ObjectNegocio> favoritos = new ArrayList<ObjectNegocio>(c.getCount());
		ObjectNegocio negocio;
		
		while(c.moveToNext()){
			negocio = new ObjectNegocio();
			negocio.id = c.getLong(0);
			favoritos.add(negocio);
		}
		c.close();
		this.close();
		return favoritos;
	}
	
	/************************************************NOTIFICACIONES***************************************/
	
	@SuppressLint("SimpleDateFormat")
	public void insertarNotificacion(String titulo, String descripcion, String imagen, String negocio){

			Calendar c3 = Calendar.getInstance();
			SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MMM-yy H:m:s");
			String strdate3 = sdf3.format(c3.getTime());
			
			ContentValues values = new ContentValues();
			values.put("titulo", titulo);
			values.put("descripcion", descripcion);
			values.put("fecha",strdate3);
			values.put("imagen",imagen);
			values.put("negocio",negocio);
			
			this.getWritableDatabase().insert("notificaciones", null, values);
			this.close();
	}
	
	public ArrayList<Notificacion> obtenerNotificaciones(){
		String consulta = "SELECT * FROM notificaciones ORDER BY id_notificacion DESC";
		Cursor c = this.getReadableDatabase().rawQuery(consulta, null);
		ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>(c.getCount());
		Notificacion push;
		
		while(c.moveToNext()){
			push = new Notificacion();
			push.id = c.getLong(0);
			push.titulo = c.getString(1);
			push.descripcion = c.getString(2);
			push.fecha = c.getString(3);
			push.imagen = c.getString(4);
			push.negocio = c.getLong(5);
			notificaciones.add(push);
		}
		c.close();
		this.close();
		return notificaciones;
	}
	
	/**
	 * Metodo para eliminar las notificaciones
	 */
	public void EliminarNotificaciones(){
		this.getReadableDatabase().execSQL("delete from notificaciones");
		this.close();
	}
	
	/**
	 * Metodo que devuelve el numero mayor de los registros
	 * @return el numero mas grande de la tabla Id
	 */
	public int getLastRow(){
		String sql = "SELECT max(Id) FROM negs";
		int i = 0;
		Cursor c = this.getReadableDatabase().rawQuery(sql, null);
		if(c.moveToFirst()){
			i = c.getInt(i)+1;
		}
		c.close();
		this.close();
		return i;
	}
	
	/**
	 * Metodo que devuelve todos los registros de la tabla 'negs'
	 * @return ArrayList<Negs> con todos los registros encontrados
	 */
	public ArrayList<Negs> getAllNegs(){
		String sql = "SELECT Id, Name, Dress, pagado FROM negs";
		Cursor c = this.getReadableDatabase().rawQuery(sql, null);
		ArrayList<Negs> array = new ArrayList<Negs>(c.getCount());
		Negs neg;
		
		while(c.moveToNext()){
			neg = new Negs();
			neg.id = c.getLong(0);
			neg.foto = "logo";
			neg.nombre = c.getString(1);
			neg.direccion = c.getString(2);
			neg.pagado = c.getInt(3);
			array.add(neg);
		}
		
		c.close();
		this.close();
		return array;
	}
	
	public String getPhone(long id){
		String sql = "select Phone from negs where Id="+id;
		Cursor c = this.getReadableDatabase().rawQuery(sql, null);
		String r = "";
		if(c.moveToFirst()){
			r = c.getString(0);
		}
		c.close();
		this.close();
		return r;
	}
		
}
	/**
	 * 
	 * @author Class Negs
	 *
	 */
	class Negs {
		public long id;
		public String foto;
		public String nombre;
		public String direccion;
		public int pagado;
	}
	/**
	 * 
	 * @author Clase Notificacion
	 *
	 */
	class Notificacion{
		public long id;
		public String titulo;
		public String descripcion;
		public String fecha;
		public String imagen;
		public long negocio;
	}

	