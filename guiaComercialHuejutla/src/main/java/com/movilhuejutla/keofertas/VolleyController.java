package com.movilhuejutla.keofertas;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyController {

    private static VolleyController instancia = null;
    private RequestQueue volley = null;
    private Context context;

    /**
     *
     * @param context contexto de la actividad
     */
    private VolleyController(Context context){
        this.context = context;
    }

    /**
     *
     * @param context contexto de la actividad
     * @return devuelve un objeto VolleyController
     */
    public static synchronized VolleyController getInstancia(Context context){
        if(instancia == null){
            instancia = new VolleyController(context);
        }
        return instancia;
    }

    /**
     *
     * @return Devuelve una nueva peticion RequestQueue
     */
    private RequestQueue getRequestQueue(){
        if(volley == null){
            volley = Volley.newRequestQueue(context);
        }
        return volley;
    }

    public <T> void addToVolley(Request<T> peticion){
        getRequestQueue().add(peticion);
    }

}
