package com.movilhuejutla;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled") public class FragmentFacebook extends Fragment{

	public View view = null;
	private TextView message;
	private Context contexto;
	private Bundle bundle;
	private DB_MH db;
	private ObjectNegocio negocio;
	private WebView webview;
	private String curURL;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		contexto = activity.getApplication().getApplicationContext();
		bundle = getArguments();
		db = DB_MH.getInstance(contexto);
		negocio = db.obtenerNegocio(bundle.getLong("id_negocio"));
		curURL = negocio.facebook;
	}
	
    public void init(String url) {
        curURL = url;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.layout_fragment_facebook, container, false);
		message = (TextView) view.findViewById(R.id.message);
		webview = (WebView) view.findViewById(R.id.webviewFacebook);
				
		if(Utils.redDisponible(contexto)){
			 if (curURL != null) {
	                webview.getSettings().setJavaScriptEnabled(true);
	                webview.setWebViewClient(new webClient());
	                webview.loadUrl(curURL);
	            }

	            return view;
			
		}else{
			message.setText("Sin conexion a Internet");
			message.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_not_connection_internet,0,0);
		}
		
		return view;
	}
	
	public void updateUrl(String url) {
        curURL = url;
        WebView webview = (WebView) getView().findViewById(R.id.web);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
    }

    private class webClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;

        }

    }
	
}
