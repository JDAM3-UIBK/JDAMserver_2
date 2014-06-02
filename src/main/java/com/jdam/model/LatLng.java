package com.jdam.model;

public class LatLng {
	public double latitude;
	public double longitude;
	
	public LatLng(double Lat, double Lng){
		this.latitude = Lat;
		this.longitude = Lng;
	}

	public double getLat() {
		return latitude;
	}

	public void setLat(double lat) {
		latitude = lat;
	}

	public double getLng() {
		return longitude;
	}

	public void setLng(double lng) {
		longitude = lng;
	}
	
	public String toString(){
		return "lat: " + latitude + " lng: " + longitude;
	}
	
	
}
