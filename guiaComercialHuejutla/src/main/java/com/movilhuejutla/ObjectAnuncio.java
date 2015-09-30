package com.movilhuejutla;

public class ObjectAnuncio {
    public long id_anuncio;
    public long id_negocio;
    public String titulo;
    public String descripcion;
    public String fecha;
    public String tipo;
    public String url;
    
    public ObjectAnuncio(long id_anuncio, long id_negocio, String tipo, String titulo, String descripcion, String fecha, String url){
    	this.id_anuncio = id_anuncio;
    	this.id_negocio = id_negocio;
    	this.titulo = titulo;
    	this.descripcion = descripcion;
    	this.fecha = fecha;
    	this.tipo = tipo;
    	this.url = url;
    }
        	
}    