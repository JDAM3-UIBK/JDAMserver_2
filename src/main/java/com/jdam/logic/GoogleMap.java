package com.jdam.logic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jdam.enums.GoogleMapModi;
import com.jdam.model.LatLng;
import com.jdam.model.Route;


/*
 * TODO:
 * 		
 * 		- Berechnung des CO2 Wertes und der Kosten
 * 		
 * 
 */
public class GoogleMap implements Callable<Route>{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Start");
		//calcRoute("Innsbruck", "Wien", GoogleMapModi.walking);
	}

	private String str_origin = null;
	private String str_dest = null;
	private LatLng latlng_origin = null;
	private LatLng latlng_dest = null;
	
	public LatLng getLatlng_origin() {
		return latlng_origin;
	}

	public void setLatlng_origin(LatLng latlng_origin) {
		this.latlng_origin = latlng_origin;
	}

	public LatLng getLatlng_dest() {
		return latlng_dest;
	}

	public void setLatlng_dest(LatLng latlng_dest) {
		this.latlng_dest = latlng_dest;
	}

	private static Enum mode = null;
	
	public GoogleMap(String origin, String dest, Enum mode){
		this.mode = mode;
		this.str_origin = origin;
		this.str_dest = dest;
	}
	
	public GoogleMap(LatLng origin, LatLng dest, Enum mode){
		this.mode = mode;
		this.latlng_origin = origin;
		this.latlng_dest = dest;
	}
	
	public GoogleMap(LatLng origin, String dest, Enum mode){
		this.mode = mode;
		this.latlng_origin = origin;
		this.str_dest = dest;
	}

	private static JSONObject calcRoute(String origin, String dest) {
		System.out.println("calc gm str");
		// Origin of route
		String str_origin = "origin=" + origin;

		// Destination of route
		String str_dest = "destination=" + dest;

		String url = getDirectionsUrlSub(str_origin, str_dest) + "&mode=" +mode.toString();
		System.out.println("URL: " + url);
		
		
		JSONObject jObject = null;
		try {
			jObject = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jObject;
	}

	private static JSONObject calcRoute(LatLng origin, LatLng dest) {
		System.out.println("calc gm latlng");
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		String url = getDirectionsUrlSub(str_origin, str_dest)+"&mode=" +mode.toString();
System.out.println(url);
		// Start downloading json data from Google Directions API
		JSONObject jObject = null;
		try {
			jObject = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jObject;
	}
	
	private static JSONObject calcRoute(LatLng origin, String dest){
		System.out.println("calc GM latlng str");
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest;

		String url = getDirectionsUrlSub(str_origin, str_dest)+"&mode=" +mode.toString();
		
		System.out.println(url);
		// Start downloading json data from Google Directions API
		JSONObject jObject = null;
		try {
			jObject = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jObject;
	}

	private static String getDirectionsUrlSub(String origin, String dest) {

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = origin + "&" + dest + "&" + sensor;
		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
		
		return url;
	}
	
	public int calcDistance(ArrayList<String> polyline){
		
		
		String str_origin = "origin=" + polyline.get(0);
		String str_dest = "destination=" + polyline.get(polyline.size()-1);
		String waypoints = "";
		
		StringBuilder sb = new StringBuilder();
		
		if(polyline.size() > 2){
		for(int i = 1; i < polyline.size()-1; ++i){
			if(i+2 == polyline.size()){
				sb.append(polyline.get(i));	
			}else{
				sb.append(polyline.get(i)+"|");	
			}
		}
		
		waypoints = "&waypoints=" + sb.toString();
		
		}
		
		String url = "https://maps.googleapis.com/maps/api/directions/json?" +str_origin + "&" + str_dest + waypoints  +"&sensor=false" + "&mode="+GoogleMapModi.walking.toString();
		url = url.replace(",", "");
		url = url.replace("ö", "oe");
		url = url.replace("ä", "ae");
		url = url.replace("ü", "ue");
		url = url.replace("ß", "ss");
		
		System.out.println(url);
		
		JSONObject jObject = null;
		try {
			jObject = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int distance = -1;
		
		if(jObject != null)
			distance = getDistance(jObject);
		
		return distance;
	}
	
	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException{
		InputStream is = new URL(url).openStream();
		try{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json= new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException{
		StringBuilder sb = new StringBuilder();
		int cp;
		while((cp = rd.read()) != -1){
			sb.append((char) cp);
		}
		return sb.toString();
	} 

	private static ArrayList<ArrayList<LatLng>> getPolyline(JSONObject jObject){
		DirectionsJSONParser parser = new DirectionsJSONParser();
		List<List<HashMap<String, String>>> routes = parser.parse(jObject);
		ArrayList<LatLng> points = null;
		ArrayList<ArrayList<LatLng>> polyline = new ArrayList<ArrayList<LatLng>>();

		for(int i = 0; i < routes.size(); ++i){
			points = new ArrayList<LatLng>();
			List<HashMap<String, String>> path = routes.get(i);
			for(int j = 0; j < path.size(); ++j){
				HashMap<String, String> point = path.get(j);
				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);
				points.add(position);
			}
			polyline.add(points);
		}

//		return polyline;
		return null;
	}

	
	private static int getDuration(JSONObject jObject) throws JSONException{
		int duration = -1;
		if(jObject != null)
		{
	
			JSONArray jar = jObject.getJSONArray("routes");
			JSONArray jal = ( (JSONObject)jar.get(0)).getJSONArray("legs");
			duration = (Integer) ((JSONObject) ((JSONObject)jal.get(0)).get("duration")).get("value");

		}
		
		return duration;
	}
	
	private static int getDistance(JSONObject jObject) throws JSONException{
		int distance = -1;
		if(jObject != null)
		{
			System.out.println(jObject.toString());
			JSONArray jar = jObject.getJSONArray("routes");
			JSONArray jal = ( (JSONObject)jar.get(0)).getJSONArray("legs");
			distance = (Integer) ((JSONObject) ((JSONObject)jal.get(0)).get("distance")).get("value");
		}
		
		return distance;
	}
	
	private void setStart(JSONObject jObject) throws JSONException{
		if(jObject != null)
		{
			JSONArray jar = jObject.getJSONArray("routes");
			JSONArray jal = ( (JSONObject)jar.get(0)).getJSONArray("legs");
			double lat = (Double) ((JSONObject) ((JSONObject)jal.get(0)).get("start_location")).get("lat");
			double lng = (Double) ((JSONObject) ((JSONObject)jal.get(0)).get("start_location")).get("lng");
			
//			System.out.println("lat: " + lat + " lng " + lng);
			setLatlng_origin(new LatLng(lat, lng));
		}
	}
	
	private void setStop(JSONObject jObject) throws JSONException{
		
//		System.out.println("" + jObject.toString());
		if(jObject != null)
		{
//			JSONArray jar = jObject.getJSONArray("routes");
			JSONArray jar = jObject.getJSONArray("routes");
			JSONArray jal = ( (JSONObject)jar.get(0)).getJSONArray("legs");
		
//			System.out.println("******legs found******");
			double lat = (Double) ((JSONObject) ((JSONObject)jal.get(0)).get("end_location")).get("lat");
			double lng = (Double) ((JSONObject) ((JSONObject)jal.get(0)).get("end_location")).get("lng");
			
//			System.out.println("lat: " + lat + " lng " + lng);
			setLatlng_dest(new LatLng(lat, lng));
		}
	}
	
	@Override
	public Route call() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("GOOGLE MAP CALL");
		Route route = null;
		JSONObject jObject = null;
		
		if(str_origin != null && str_dest != null)
		{
			jObject = calcRoute(str_origin, str_dest);
		}
		else if(latlng_dest != null && latlng_origin != null)
		{
			jObject = calcRoute(latlng_origin, latlng_dest);
		}
		else if(latlng_origin != null && str_dest != null)
		{
			jObject = calcRoute(latlng_origin, str_dest);
		}
		
		int duration = getDuration(jObject);
		int distance = getDistance(jObject);
		setStart(jObject);
		setStop(jObject);
		
//		int duration = 0;
//		int distance = 0;
		
		ArrayList<ArrayList<LatLng>> polyline = getPolyline(jObject);
		double CO2 = 0.0;
		double costs = 0.0;
		
		
		route = new Route(duration, distance, mode.toString(), polyline, CO2, costs, new Date());
		
		return route;
	}
}
