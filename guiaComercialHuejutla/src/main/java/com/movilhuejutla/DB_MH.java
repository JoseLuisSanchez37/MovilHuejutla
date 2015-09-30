package com.movilhuejutla;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB_MH extends SQLiteOpenHelper{
	
	private static DB_MH instancia = null;
    private static final String NOMBREBD = "guiacomercial"; 
    private static String ruta;
    private final Context contexto;
    private static final int version = 20;
    private Cursor c;
 
    /**
     * Constructor privado
     * Se utiliza el patron Singleton para crear una unica instancia de base de datos
     * @param context
     */
    private DB_MH(Context contexto) {
    	super(contexto, NOMBREBD, null, version);
        this.contexto = contexto;
        ruta = contexto.getDatabasePath(NOMBREBD).getAbsolutePath();
    }
    
    public static DB_MH getInstance(Context contexto){
    	if(instancia == null){
    		instancia = new DB_MH(contexto);
    	}
    	return instancia;
    }
 
  /**
     * Creamos una base de datos vacia para posteriormente sobreescribirlo con la nuestra
     * */
    public void crearBaseDeDatos(){
    	if(!new File(ruta).exists()){
    		this.getReadableDatabase(); // getReadableDatabase() crea la base de datos "guia comercial" vacia
    		this.close();    		//despues la cerramos
    		contexto.deleteDatabase(NOMBREBD); //procedemos a eliminarla de nuevo
			copiarBaseDeDatos(); //copiamos la base de datos
    	}
    }

    /**Copiamos nuestra base de datos de la carpeta de assets justamente despues de crear la base de datos
     * vacia en el sistema de archivos, de donde podremos tener acceso y poder manejarlo.
     * Se copiara mediante InputStream
     * */
    private boolean copiarBaseDeDatos(){
    	try{
	    	InputStream entrada = contexto.getAssets().open(NOMBREBD);
	    	OutputStream salida = new FileOutputStream(ruta);
	    	byte[] buffer = new byte[1024];
	    	int longitud;
	    	while ((longitud = entrada.read(buffer))>0){
	    		salida.write(buffer, 0, longitud);
	    	}
	    	salida.flush();
	    	salida.close();
	    	entrada.close();
	    	return true;
    	}catch(IOException e){
    		return false;
    	}
    }
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		  
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		contexto.deleteDatabase(NOMBREBD); //procedemos a eliminarla de nuevo
		copiarBaseDeDatos(); //copiamos la base de datos
	}
	
	/**
	 * 
	 * @return devuelve un arraylist con objetos de tipo categoria
	 */
	public ArrayList<ObjectCategoria> obtenerCategorias() {
		
		String sql = "SELECT * FROM categoria ORDER BY _id DESC";
		c = this.getReadableDatabase().rawQuery(sql, null);
		ArrayList<ObjectCategoria> categorias =  new ArrayList<ObjectCategoria>(c.getCount());
		ObjectCategoria categoria;
		
		while(c.moveToNext()){
			categoria = new ObjectCategoria();
			categoria.id = c.getLong(1);
			categoria.imagen = c.getString(0);
			categoria.nombre = c.getString(2);
			categorias.add(categoria);
		}
		
		c.close();
	    this.close();
	    return categorias;
	}

	/**
	 * 
	 * @param categoria el id
	 * @return un arraylist con los objetos que pertenescan a X categoria
	 */
	public ArrayList<ObjectNegocio> obtenerNegocios(long categoria){
		
		String cat = String.valueOf(categoria);
		String sql = "select logotipo, nombre, direccion, id_negocio from negocio where categoria="+cat;
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		ArrayList<ObjectNegocio> negocios  = new ArrayList<ObjectNegocio>(cursor.getCount());
		ObjectNegocio negocio;
		
		while(cursor.moveToNext()){
			negocio = new ObjectNegocio();
			negocio.foto = cursor.getString(0);
			negocio.nombre = cursor.getString(1);
			negocio.direccion = cursor.getString(2);
			negocio.id = cursor.getInt(3);
			negocios.add(negocio);
		}
		
		cursor.close();
		this.close();
		return negocios;
	}
	
	/**
	 * 
	 * @param categoria
	 * @return devuelve el el negocio que esta considerado como favorito
	 */
	public ObjectNegocio obtenerFavorito(long id){
			
		String sql = "select logotipo, nombre, direccion, id_negocio from negocio where id_negocio="+id;
		c = this.getReadableDatabase().rawQuery(sql, null);
		ObjectNegocio negocio = new ObjectNegocio(); 
		
		if(c.moveToFirst()){
			negocio.foto = c.getString(0);
			negocio.nombre = c.getString(1);
			negocio.direccion = c.getString(2);
			negocio.id = c.getLong(3);
		}
		c.close();
		this.close();
		return negocio;
	}
	
	/**
	 * 	
	 * @param id del negocio a buscar
	 * @return devuelve un objeto de tipo Negocio
	 */
	public ObjectNegocio obtenerNegocio(long id){
		
		String sql = "select nombre, descripcion, direccion, horario, facebook, email, logotipo from negocio where id_negocio="+id;
		c = this.getReadableDatabase().rawQuery(sql, null);
		ObjectNegocio neg = new ObjectNegocio();
		
		if(c.moveToFirst()){
			neg.nombre = c.getString(0);
			neg.descripcion = c.getString(1);
			neg.direccion = c.getString(2);
			neg.horario = c.getString(3);
			neg.facebook = c.getString(4);
			neg.email = c.getString(5);
			neg.foto = c.getString(6);
		}
		
		c.close();
		this.close();
		return neg;
	}
	
	/**
	 * 
	 * @param buscar 
	 * @return devuelve un lista de arreglo con objetos de tipo Negocio
	 *  que cumplan con las especificaciones de busqueda
	 */
	public ArrayList<ObjectNegocio> obtenerNegocios(String buscar){
		
		String var = "%" + buscar + "%";
		String sql = "SELECT logotipo, nombre, direccion, id_negocio FROM negocio WHERE nombre LIKE '"+
                    var+"' OR descripcion LIKE '"+var+"' ORDER BY id_negocio DESC";
		
		c = this.getReadableDatabase().rawQuery(sql, null);
		ArrayList<ObjectNegocio> negocios  = new ArrayList<ObjectNegocio>(c.getCount());
		ObjectNegocio negocio;
		
		while(c.moveToNext()){
			negocio = new ObjectNegocio();
			negocio.foto = c.getString(0);
			negocio.nombre = c.getString(1);
			negocio.direccion = c.getString(2);
			negocio.id = c.getInt(3);
			negocios.add(negocio);
		}
		
		c.close();
		this.close();
		return negocios;
	}
	
	public String[] obtenerTelefonos(long id){
		String sql = "SELECT telefono FROM telefonos where id_negocio = "+id;
		c = this.getReadableDatabase().rawQuery(sql, null);
		String[] telefonos = new String[c.getCount()];
		int i = 0;
		
		while(c.moveToNext()){
			telefonos[i] = c.getString(0); 
			i++;
		}
		c.close();
		this.close();
		return telefonos;
	}
	
	public String[] obtenerImagenes(long id){
		String sql = "SELECT imagen FROM imagenes where id_negocio = "+id+" ORDER BY imagen";
		
		c = this.getReadableDatabase().rawQuery(sql, null);
		String[] imagenes = new String[c.getCount()];
		int i = 0;
		
		while(c.moveToNext()){
			imagenes[i] = c.getString(0);
			i++;
		}
		c.close();
		this.close();
		return imagenes;
	}
	
	public ArrayList<LatLng> obtenerCoordenadas(long id){
		String sql = "SELECT latitud, longitud FROM coordenadas where id_negocio ="+id;
		c = this.getReadableDatabase().rawQuery(sql, null);
		ArrayList<LatLng> coordenadas = new ArrayList<LatLng>(c.getCount());
		LatLng coordenada;
		
		while(c.moveToNext()){
			coordenada = new LatLng(Double.parseDouble(c.getString(0)), Double.parseDouble(c.getString(1)));
			coordenadas.add(coordenada);
		}
		
		c.close();
		this.close();
		return coordenadas;
	}
	
	public String[] getDirecciones(long id){
		String sql = "SELECT direccion FROM coordenadas where id_negocio="+id;
		c = this.getReadableDatabase().rawQuery(sql, null);
		String[] direcciones = new String[c.getCount()];
		int i = 0;
		
		while(c.moveToNext()){
			direcciones[i] = c.getString(0);
			i++;
		}
		c.close();
		this.close();
		return direcciones;
	}
	
	public boolean existeNegocio(long id){
		
		String sql = "select id_negocio from negocio where id_negocio="+id;
		c = this.getReadableDatabase().rawQuery(sql, null);
		
		if(c.moveToFirst()){
			c.close();
			this.close();
			return true;
		}else{
			c.close();
			this.close();
			return false;
		}
	
	}
	
	//Devolvemos todas la Imagenes
	public ArrayList<Image> getAllImages(){
		String sql1 = "select imagen, id_negocio from imagenes";
		String sql2 = "select logotipo, id_negocio from negocio";
		ArrayList<Image> imagenes = new ArrayList<Image>();
		Image image;
		
		//*****sql1*****
		c = this.getReadableDatabase().rawQuery(sql1, null);
		while(c.moveToNext()){
			image = new Image(c.getString(0), c.getLong(1));
			imagenes.add(image);
		}
		c.close();
		this.close();
		
		//*****sql2*****
		c = this.getReadableDatabase().rawQuery(sql2, null);
		while(c.moveToNext()){
			image = new Image(c.getString(0), c.getLong(1));
			imagenes.add(image);
		}
		
		c.close();
		this.close();
		return imagenes;
	}
	
	/**
	 * Metodo que devuelve todos los registros de la tabla 'negocios'
	 * @return ArrayList<Negs> con todos los registros encontrados
	 */
	public ArrayList<Negs> getAllNegs(){
		String sql = "SELECT id_negocio, logotipo, nombre, direccion, pagado FROM negocio";
		c = this.getReadableDatabase().rawQuery(sql, null);
		ArrayList<Negs> array = new ArrayList<Negs>(c.getCount());
		Negs neg;
		
		while(c.moveToNext()){
			neg = new Negs();
			neg.id = c.getLong(0);
			neg.foto = c.getString(1);
			neg.nombre = c.getString(2);
			neg.direccion = c.getString(3);
			neg.pagado = c.getInt(4);
			array.add(neg);
		}
		
		c.close();
		this.close();
		return array;
	}
	
	
	public class Image{
		public String imagen;
		public long negocio;
		
		public Image(String img, long neg){
			imagen = img;
			negocio = neg;
		}
		
	}
}