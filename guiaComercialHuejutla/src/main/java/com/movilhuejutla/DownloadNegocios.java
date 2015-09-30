package com.movilhuejutla;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;

public class DownloadNegocios {
	
		public static final String Tag = "com.movilhuejutla.DownloadNegocios";

		private JSONObject json;
		private Context context;
		
		public DownloadNegocios(JSONObject json, Context context){
			this.json = json;
			this.context = context;
		}
		
		public void parserJSON(){
			try {
				JSONArray ads = json.getJSONArray("Negs");
				int ads_lenght = ads.length();
				for(int i = 0; i < ads_lenght; i++){
					JSONObject neg = ads.getJSONObject(i);
					ContentValues cv = new ContentValues();
					cv.put("Id", neg.getLong("Id"));
					cv.put("Name", neg.getString("Name"));
					cv.put("Dress", neg.getString("Dress"));
					cv.put("Phone", neg.getString("Phone"));
					DB_F.getInstance(context).getWritableDatabase().insert("negs", null, cv);
				}
			} catch (JSONException e) { }
		}
	
}
