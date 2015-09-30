package com.movilhuejutla.keofertas;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utilss {

    public static final String API_SESION = "http://192.168.1.72/keofertas/IniciarSesion.php";
    public static final String API_RECOVERY_PASS = "http://192.168.1.72/keofertas/RecuperarPassword.php";
    public static final String API = "http://192.168.1.72/";

    public static final String API_PARAM_TAG = "tag";
    public static final String API_PARAM_LOGIN = "login";
    public static final String API_PARAM_REGISTER = "register";
    public static final String API_PARAM_EMAIL = "email";
    public static final String API_PARAM_PASS = "pass";

    public static boolean servidorDisponible(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                int time = 1000;
                URL url = new URL(API);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setConnectTimeout(time);
                conexion.setReadTimeout(time);
                conexion.connect();
                if (conexion.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                Log.v("ERROR:", e1.getMessage());
                return false;
            } catch (IOException e) {
                Log.v("ERROR:", e.getMessage());
                return false;
            }
        }
        return false;
    }

}
