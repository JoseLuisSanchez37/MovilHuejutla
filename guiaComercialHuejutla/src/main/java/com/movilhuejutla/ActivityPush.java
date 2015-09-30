package com.movilhuejutla;

import com.parse.ParseAnalytics;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityPush extends ActionBarActivity{
	
	private DB_F dbfavoritos;
	private String titulo, descripcion, imagen, neg, type_ad, url;
		
	private ImageView image;
	private TextView title, description;
	private Button action;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_push_notificacion);
		
		//ParseAnalitycs
		ParseAnalytics.trackAppOpenedInBackground(Utils.getIntentParseAnalytics());
		
		Bundle bundle = getIntent().getExtras();
		dbfavoritos = DB_F.getInstance(this);

		//Inicializamos las vistas
		title = (TextView) findViewById(R.id.push_titulo);
		description = (TextView) findViewById(R.id.push_descripcion);
		image = (ImageView) findViewById(R.id.push_image);
		action = (Button) findViewById(R.id.push_action);
		
		//**********Notificacion General**********
		if(Integer.parseInt(bundle.getString("type")) == 1){
			//obtenemos los datos de la intencion
			titulo = bundle.getString("titulo");
			descripcion = bundle.getString("descripcion");
			imagen = bundle.getString("imagen");
			neg = bundle.getString("negocio");
			type_ad = bundle.getString("type_ad");

            //obtenemos la primera imagen del negocio, dependiente de la notificacion
            Drawable drawable;
            Resources recursos = getResources();
            String rutaImagen = Utils.getPathApp(this)+"/Imagenes/"+ neg +"_1.jpg";
            BitmapFactory.Options opciones = new BitmapFactory.Options();
            opciones.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(rutaImagen);

            //Verificamos si existe la imagen o si no, entonces ponemos una de la carpeta drawable
            if(bitmap != null) {
                drawable = new BitmapDrawable(recursos, bitmap);
            }else{
                drawable = recursos.getDrawable(R.drawable.loading_picasso);
            }

			//Mostramos la imagen
			if(imagen.equals("0")){
                image.setImageDrawable(drawable);
			}else{
				if(Utils.redDisponible(this)) {
                    Picasso.with(this)
                            .load(imagen)
                            .placeholder(drawable)
                            .error(drawable)
                            .into(image);
                }
				else {
                    image.setImageDrawable(drawable);
                }
			}
			
			//titulo y descripcion
			title.setText(titulo);
			description.setText(descripcion);
			
			//Anuncio o Promocion
			if(type_ad.equals("0")){
				action.setText("Ver al Negocio");
				action.setTag(0);
				if(neg.equals("9999")){
					action.setVisibility(View.GONE);
				}
			}else{
				action.setText("Guardar esta Promocion");
				action.setTag(1);
			}
		}

		
		//**********Notificacion Compartir con WhatsApp**********
		if(Integer.parseInt(bundle.getString("type")) == 2){
			titulo = bundle.getString("titulo");
			descripcion = bundle.getString("descripcion");
			
			image.setVisibility(View.GONE);//Ocultamos la imagen
			title.setText(titulo); //mostramos el titulo
			description.setText(descripcion); //la descripcion
			action.setText("Invitar a un Amigo");//mostramos el texto en el boton action
			action.setTag(2); //Asignamos un identificador para el tipo de action a realizar
		}
		
		//**********Notificacion Abrir URL**********
		if(Integer.parseInt(bundle.getString("type")) == 3){
				titulo = bundle.getString("titulo");
				descripcion = bundle.getString("descripcion");
				url = bundle.getString("url");
				
				image.setVisibility(View.GONE);//Ocultamos la imagen
				title.setText(titulo); //mostramos el titulo
				description.setText(descripcion); //la descripcion
				action.setText("Abrir Pagina Web");//mostramos el texto en el boton action
				action.setTag(3); //Asignamos un identificador para el tipo de action a realizar
		}
		
	}
	
	public void accionNotificacion(View v){
		
		switch(Integer.parseInt(v.getTag().toString())){
		
			case 0:
				long id_negocio = Integer.parseInt(neg);
				if(DB_MH.getInstance(this).existeNegocio(id_negocio)){
					Intent i = new Intent(this, ActivityDescripcionNegocio.class);
					i.putExtra("id_negocio", id_negocio);
					startActivity(i);
				}else{
					Toast.makeText(this, "Upss al parecer no se encontro", Toast.LENGTH_LONG);
					finish();
				}
				break;
			
			case 1:
				dbfavoritos.insertarNotificacion(titulo, descripcion, imagen, neg);
				Toast.makeText(this, "Listo!. Puedes consultarla mas tarde en el apartado NOTIFICACIONES", Toast.LENGTH_LONG).show();
				finish();
				break;
				
			case 2:
				Intent compartir = new Intent();
				compartir.setAction(Intent.ACTION_SEND);
				compartir.putExtra(Intent.EXTRA_SUBJECT, "Hola, Te invito a descargar MovilHuejutla");
				compartir.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.movilhuejutla&hl=es_419");
				compartir.setType("text/plain");
				compartir.setPackage("com.whatsapp");
				startActivity(compartir);
				break;
				
			case 3:
				Intent abrirUrl = new Intent(Intent.ACTION_VIEW);
				abrirUrl.setData(Uri.parse(url));
				startActivity(abrirUrl);
				break;
		}
	}
	
	public void cerrarNotificacion(View v){
		finish();
	}
}