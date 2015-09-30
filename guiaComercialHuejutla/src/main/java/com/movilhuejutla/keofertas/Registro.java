package com.movilhuejutla.keofertas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Registro extends ActionBarActivity {

    private EditText email, pass1, pass2;
    private Button registrar;
    private CheckBox terminos;
    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_layout_registro);

        email = (EditText) findViewById(R.id.editText_email);
        pass1 = (EditText) findViewById(R.id.editText_password);
        pass2 = (EditText) findViewById(R.id.editText_again_password);
        registrar = (Button) findViewById(R.id.button_registrar);
        terminos = (CheckBox) findViewById(R.id.checkbox_terminos);
    }

    public void registrarCuenta(View view){
        String email = this.email.getText().toString();
        String pass1 = this.pass1.getText().toString();
        String pass2 = this.pass2.getText().toString();

        if(validarCampos(email, pass1, pass2)){
            crearCuenta(email, pass1);
        }
    }

    private boolean validarCampos(String email, String pass1, String pass2){

        int p1 = pass1.length();
        int p2 = pass2.length();

        if(email.length() != 0) {
            if(email.contains("@")) {
                if (p1 >= 5 && p2 >= 5) {
                    if (pass1.contentEquals(pass2)) {
                        if(terminos.isChecked()){
                            return true;
                        }else{
                            Toast.makeText(this, "Debes aceptar los terminos de uso",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    } else {
                        Toast.makeText(this, "Las contraseñas no coinciden",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(this, "La contraseña debe tener 5 caracteres como minimo",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }else{
                Toast.makeText(this, "Su correo electronico no es válido",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, "Ingrese su correo electronico",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void crearCuenta(final String mail, String pass){

        //AlertDialog
        dialog = new SweetAlertDialog(Registro.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Registrando...");
        dialog.setCancelable(false);
        dialog.show();

        //Parametros a enviar
        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put(Utilss.API_PARAM_TAG, Utilss.API_PARAM_REGISTER);
        parametros.put(Utilss.API_PARAM_EMAIL, mail);
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
                            "Error al conectar con el servidor. Intentalo nuevamente."+
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        );

        //Verificar si hay conexion a internet
        if(Utils.redDisponible(this)){
            VolleyController.getInstancia(this).addToVolley(peticion);
        }else{
            Toast.makeText(getApplicationContext(), "No hay conexion a Internet",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Procesamos la respuesta del Servidor
    private void respuestaServidor(JSONObject response){
        try {
            boolean error = response.getBoolean("error");
            if(error == false) {
                //Extraemos los datos del Servidor
                JSONObject usuario = response.getJSONObject("usuario");
                String email = usuario.getString("nombre");

                //actualizamos propiedades del AlertDialog
                dialog.setTitleText(email);
                dialog.setContentText("Registrado Correctamente");

                //TimerTask para ejecutar el Inicio a KeOfertas
                TimerTask tarea = new TimerTask() {
                    @Override
                    public void run() {
                        //Mostramos la siguiente actividad
                        dialog.dismiss();
                        Intent start = new Intent(Registro.this, Inicio.class);
                        startActivity(start);
                        finish();
                    }
                };
                Timer tiempo = new Timer();
                tiempo.schedule(tarea, 2000);
            }else{
                //Mensaje de Error del Servidor
                dialog.dismissWithAnimation();
                String msg_error = response.getString("error_msg");
                Toast.makeText(getApplicationContext(), msg_error, Toast.LENGTH_LONG).show();
            }
        }catch (JSONException json){
            //Error al parsear los datos del servidor
            dialog.dismissWithAnimation();
            Toast.makeText(getApplicationContext(), "Error en Servidor: "+json.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
