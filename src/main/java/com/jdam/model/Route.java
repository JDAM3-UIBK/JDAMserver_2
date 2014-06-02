package com.jdam.model;



import java.util.ArrayList;
import java.util.Date;


public class Route {

	private int duration;
	private int length;
	private String type;
	private ArrayList<ArrayList<LatLng>> polylinePoints; //google maps route -> noch konvertierung in polyline (PTE?)
	private String text[]; //PTE routen text -> wie IVB app
	private double CO2;
	private double costs;
	private Date date;
	
	public Route(int duration, int length, String type, ArrayList<ArrayList<LatLng>> polylinePoints, String text[], double CO2, double costs, Date date){
		this.duration = duration;
		this.length = length;
		this.type = type;
		this.polylinePoints = polylinePoints;
		this.text = text;
		this.CO2 = CO2;
		this.costs = costs;
		this.date = date;
	}
	
	public Route(int duration, int length, String type, String text[], double CO2, double costs, Date date){
		this.duration = duration;
		this.length = length;
		this.type = type;
		this.text = text;
		this.CO2 = CO2;
		this.costs = costs;		
		this.polylinePoints = null;
		this.date = date;
	}
	
	public Route(int duration, int length, String type, ArrayList<ArrayList<LatLng>> polylinePoints, double CO2, double costs, Date date){
		this.duration = duration;
		this.length = length;
		this.type = type;
		this.polylinePoints = polylinePoints;
		this.CO2 = CO2;
		this.costs = costs;		
		this.text = null;
		this.date = date;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<ArrayList<LatLng>> getPolylinePoints() {
		return polylinePoints;
	}

	public void setPolylinePoints(ArrayList<ArrayList<LatLng>> polylinePoints) {
		this.polylinePoints = polylinePoints;
	}

	public String[] getText() {
		return text;
	}

	public void setText(String[] text) {
		this.text = text;
	}

	public double getCO2() {
		return CO2;
	}

	public void setCO2(double cO2) {
		CO2 = cO2;
	}

	public double getCosts() {
		return costs;
	}

	public void setCosts(double costs) {
		this.costs = costs;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "duration="+duration+" distance="+length+" type="+type+" polyline=xxx text=xxx Co2="+CO2+" costs="+costs;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
