package com.movilhuejutla.keofertas;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.movilhuejutla.R;
import com.movilhuejutla.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecuperarCredenciales extends ActionBarActivity {

    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.k_layout_recuperar_credenciales);
        email = (EditText) findViewById(R.id.editText_recuperar_email);
    }

    public void recuperarPassword(View view){
        String email = this.email.getText().toString();
        if(email.contains("@") && email.length() > 4){
            String url = Utilss.API_RECOVERY_PASS+"?email="+email;
            //alertdialog
            final SweetAlertDialog dialog = new SweetAlertDialog(RecuperarCredenciales.this, SweetAlertDialog.PROGRESS_TYPE);
            dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            dialog.setTitleText("Enviando...");
            dialog.setCancelable(false);
            dialog.show();

            JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, url,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean error = response.getBoolean("error");
                                if(error == false) {
                                    //Extraemos el Mensaje
                                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    dialog.setTitleText(response.getString("mensaje"));
                                    //TimerTask para ejecutar el Inicio a KeOfertas
                                    TimerTask tarea = new TimerTask() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            Intent start = new Intent(RecuperarCredenciales.this, Inicio.class);
                                            startActivity(start);
                                            finish();
                                        }
                                    };
                                    Timer tiempo = new Timer();
                                    tiempo.schedule(tarea, 2000);
                                }else{
                                    dialog.dismissWithAnimation();
                                    Toast.makeText(getApplicationContext(), response.getString("error_msg"), Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException json){
                                dialog.dismissWithAnimation();
                                Toast.makeText(getApplicationContext(), "Mensaje del Servidor: "+json.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismissWithAnimation();
                            Toast.makeText(getApplicationContext(), "Ocurrio un error al conectar con el servidor. Intentelo nuevamente."+error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            if(Utils.redDisponible(this)){
                VolleyController.getInstancia(this).addToVolley(peticion);
            }else{
                Toast.makeText(getApplicationContext(), "No hay conexion a Internet", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(this, "La direccion de correo no es valida", Toast.LENGTH_SHORT).show();
        }
    }

}
