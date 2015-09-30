package com.movilhuejutla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser {
	
	/** Receives a JSONObject and returns a list of lists containing latitude and longitude */
	public List<List<HashMap<String,String>>> parse(JSONObject jObject){
		
		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;	
		JSONObject jDistance = null;
		JSONObject jDuration = null;
		
		try {
			
			jRoutes = jObject.getJSONArray("routes");
			
			/** Traversing all routes */
			int jRoutes_length = jRoutes.length();//for more performance
			for(int i=0;i<jRoutes_length;i++){		
				jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
				ArrayList<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
				//List path = new ArrayList<HashMap<String, String>>();
				
				/** Traversing all legs */
				
				int jLegs_length = jLegs.length();//for more performance
				for(int j=0;j<jLegs_length;j++){
					
					/** Getting distance from the json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put("distance", jDistance.getString("text"));
 
                    /** Getting duration from the json data */
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", jDuration.getString("text"));
 
                    /** Adding distance object to the path */
                    path.add(hmDistance);
 
                    /** Adding duration object to the path */
                    path.add(hmDuration);
					
					
					jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
					
					/** Traversing all steps */
					int jSteps_length = jSteps.length();//for more performance
					for(int k=0;k<jSteps_length;k++){
						String polyline = "";
						polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);
						
						/** Traversing all points */
						int list_size = list.size();//for more performance
						for(int l=0;l<list_size;l++){
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat", Double.toString(list.get(l).latitude) );
							hm.put("lng", Double.toString(list.get(l).longitude) );
							path.add(hm);						
						}								
					}
					routes.add(path);
				}
			}
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}catch (Exception e){			
		}
		
		
		return routes;
	}	
	
	
	/**
	 * Method to decode polyline points 
	 * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java 
	 * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((lat / 1E5)),
                    ((lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}