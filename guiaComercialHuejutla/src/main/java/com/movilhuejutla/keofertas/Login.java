package com.movilhuejutla.keofertas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.movilhuejutla.R;
import com.movilhuejutla.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends ActionBarActivity {

    private EditText email, password;
    private SweetAlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.k_layout_login);

        email = (EditText) findViewById(R.id.editText_email_login);
        password = (EditText) findViewById(R.id.editText_password_login);
	}

    public void crearCuenta(View view){
        Intent cuenta =  new Intent(this, Registro.class);
        startActivity(cuenta);
    }

    public void recuperarCredenciales(View v){
        Intent recuperar = new Intent(this, RecuperarCredenciales.class);
        startActivity(recuperar);
    }

    public void login(View view){
        String correo = email.getText().toString();
        String pass = password.getText().toString();

        //desarrollo
        Intent start = new Intent(Login.this, Inicio.class);
        startActivity(start);


        /*if(correo.contains("@") && correo.length()> 5){
            if(pass.length() > 4){
                iniciarSesion(correo, pass);
            }else{
                Toast.makeText(this,"La contraseña debe tener 5 caracteres como minimo",
                        Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"La direccion de correo electronico no es valido",
                    Toast.LENGTH_LONG).show();
        }*/
    }

    private void iniciarSesion(String email, String pass){

        //AlertDialog
        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Iniciando Sesion...");
        dialog.setCancelable(false);
        dialog.show();

        //Parametros a enviar
        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put(Utilss.API_PARAM_TAG, Utilss.API_PARAM_LOGIN);
        parametros.put(Utilss.API_PARAM_EMAIL, email);
        parametros.put(Utilss.API_PARAM_PASS, pass);

        //Enviando solicitud al Servidor
        KeofertasAPI peticion = new KeofertasAPI(
            Request.Method.POST, Utilss.API_SESION, parametros,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    respuestaServidor(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismissWithAnimation();
                    Toast.makeText(getApplicationContext(),
                            "Error al conectar con el servidor. Intentalo nuevamente",
                            Toast.LENGTH_LONG).show();
                }
            }
        );

        if(Utils.redDisponible(this)){
            VolleyController.getInstancia(this).addToVolley(peticion);
        }else{
            Toast.makeText(getApplicationContext(), "No hay conexion a Internet",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Procesamos la respuesta del Servidor
    private void respuestaServidor(JSONObject response){
        dialog.dismissWithAnimation();
        try {
            boolean error = response.getBoolean("error");
            if(error == false) {
                Intent start = new Intent(Login.this, Inicio.class);
                startActivity(start);
                finish();
            }else{
                String msg_error = response.getString("error_msg");
                Toast.makeText(getApplicationContext(), msg_error,
                        Toast.LENGTH_LONG).show();
            }
        }catch(JSONException json){
            Toast.makeText(getApplicationContext(), "Error en Servidor: "+
                    json.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
