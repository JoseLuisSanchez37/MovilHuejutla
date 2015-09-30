package com.movilhuejutla;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

public class ActivityContacto extends ActionBarActivity{
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_contacto);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
    	getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setCustomView(ActivityCategorias.tituloActionBar(this, "ANUNCIESE ES GRATIS!!"));
		getSupportActionBar().setDisplayShowCustomEnabled(true);
	}
	
	public void llamar(View v){
		Intent llamar = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+v.getTag().toString()));
		startActivity(llamar);						
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			Intent intent = new Intent(this, ActivityCategorias.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

    public void enviarEmail(View v){
        String correo = v.getTag().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{correo});
        startActivity(intent);
    }

    public void gotoFacebook(View v){
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent app = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/774356639290724"));
            startActivity(app);
        } catch (Exception e) {
            Intent webpage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/MovilHuejutlaApp"));
            startActivity(webpage);
        }
    }

    public void gotoMovilHuejutla(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://movilhuejutla.com.mx/"));
        startActivity(intent);
    }
	
}