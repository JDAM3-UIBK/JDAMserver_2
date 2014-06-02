package com.jdam.logic;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.jdam.enums.GoogleMapModi;
import com.jdam.model.LatLng;
import com.jdam.model.Route;

import de.schildbach.pte.IvbProvider;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.NetworkProvider.Accessibility;
import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.LocationType;
import de.schildbach.pte.dto.Point;
import de.schildbach.pte.dto.Product;
import de.schildbach.pte.dto.QueryTripsResult;
import de.schildbach.pte.dto.Trip;
import de.schildbach.pte.dto.Trip.Individual;
import de.schildbach.pte.dto.Trip.Individual.Type;
import de.schildbach.pte.dto.Trip.Leg;
import de.schildbach.pte.dto.Trip.Public;

/*
 * TODO:
 * 		
 * 
 */
public class PTE implements Callable<Route>{

	private NetworkProvider provider;

	private double publicCO2 = 0.015;
	private Location from = null;
	private Location to = null;
	private Date date = null;

	public PTE(String start, String stop, Date date){
		provider = new IvbProvider();
		setStartStop(start, stop, date);
	}

	public PTE(LatLng start, LatLng stop, Date date){
		provider = new IvbProvider();
		setStartStop(start, stop, date);
	}

	public void setStartStop(String start, String stop, Date date){

		from = new Location(LocationType.STATION, 0, null, start);
		to = new Location(LocationType.STATION, 0, null, stop);
	}

	//lng / 1E6 = float lng 
	public void setStartStop(LatLng start, LatLng stop, Date date){
		//System.out.println("PTE with only latLng");
		int LatStart = (int) (start.getLat() * 1E6);
		int LngStart = (int) (start.getLng() * 1E6);

		int LatStop = (int) (stop.getLat() * 1E6);
		int LngStop = (int) (stop.getLng() * 1E6);
		//System.out.println(LatStart +"      " + LngStart +" start  PTE");


		from = new Location(LocationType.ADDRESS,LatStart, LngStart);
		to = new Location(LocationType.ADDRESS,LatStop,LngStop);

		//System.out.println("PTE from: " + from.toDebugString());
		//System.out.println("PTE to: " +to.toDebugString());
	}

	private Route calcRoute(){
		//System.out.println("start to calc the route of the PTE");
		//System.out.println("From: " + from.toDebugString() + " To: " + from.toDebugString());
		Route route = null;

		QueryTripsResult result = null;

		try {
			result = provider.queryTrips(from, null, to, new Date(), true,
					Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("Something went wrong with the PTE request");
			e.printStackTrace();
		}

		if(result != null)
		{
			//System.out.println("result != null ");
			Trip trip = result.trips.get(0);
			//System.out.println(trip.toString());
			//System.out.println("get duration");
			int duration = (int) (trip.getLastArrivalTime().getTime() - trip.getFirstDepartureTime().getTime())/1000;
			
			
			int distance = calcDistance(result);
//			int distance = -1;
			
			
			double co2 = distance/1000 * publicCO2;
			//String type = tran

			//System.out.println("get Costs");
			double costs = -1;
			if(!trip.fares.isEmpty()){
				costs = trip.fares.get(0).fare;
				//System.out.println("FARE    " + costs);
			}

			//System.out.println("convertString");
			String[] text = convertString(result);
			route = new Route(duration, distance, "PTE", text, co2 ,costs, new Date());
		}


		//		//System.out.println("PTE route: " + route.toString());		
		return route;
	}

	private String[] convertString(QueryTripsResult qt){
		List<String> result = new ArrayList<String>();
		SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat sdf_time = new SimpleDateFormat("HH.mm");
		if(qt != null){
			Trip t = (Trip) qt.trips.get(0);
			int duration = (int) (t.getLastArrivalTime().getTime() - t.getFirstDepartureTime().getTime())/1000/60;

			String datum =  sdf_date.format(t.legs.get(0).arrivalTime);

			result.add("Datum: " +datum +", Dauer: " + duration + " Minuten - Umstiege: " +t.numChanges);
			result.add("");

			for(int i = 0; i < t.legs.size(); ++i){
				try{
					Public leg = (Public) t.legs.get(i);
					result.add("Linie " + leg.line.label.substring(1));


					result.add(sdf_time.format(leg.departureTime) + " Uhr ab " + leg.departure.name);
					result.add(sdf_time.format(leg.arrivalTime) + " Uhr an " + leg.arrival.name);
				} catch ( ClassCastException e){}

				try{
					Individual leg = (Individual) t.legs.get(i);
					if(leg.type == Type.WALK)
						result.add("Fussweg ca. " + leg.min + " Minuten");
					//					leg.distance
					else
						result.add(leg.type.toString());
					result.add(sdf_time.format(leg.departureTime) + " Uhr ab " + leg.departure.name);
					result.add(sdf_time.format(leg.arrivalTime) + " Uhr an " + leg.arrival.name);
				} catch (ClassCastException e){}
				result.add("");
			}
		}
		if(result.isEmpty()){
			return null;
		}
		else{
			String[] r = new String[result.size()];
			for(int i = 0; i < result.size();++i)
				r[i] = result.get(i);
			return r;
		}
	}

	private int calcDistance(QueryTripsResult qtr){
		System.out.println("CALC DISTANCE PTE");
		int distance = -1;
		int dist_walk = 0;
		ArrayList<String> polyline = null;
		
		if(qtr != null){
			Trip t = (Trip) qtr.trips.get(0);
			polyline = new ArrayList<String>();

			for(int i = 0; i < t.legs.size(); ++i){
				
				try{
					Public l = (Public) t.legs.get(i);
					System.out.println("PUBLIC PTE: " + l.path);
					System.out.println("POLY " + l.arrival.name);
					polyline.add(l.arrival.name+ "+Innsbruck");
					
				}catch (ClassCastException e){}
				
				try{
					if(i == 0 || i == t.legs.size()-1){
					Individual ind = (Individual) t.legs.get(i);
					dist_walk += ind.distance;
					
					System.out.println("WALKING = " + dist_walk);
					
					if( i  == 0){
						System.out.println("POLY " + ind.departure.name);
						polyline.add(ind.departure.name);
					}
					
					}
					
				} catch (ClassCastException e){}
			}

		}	
		if(polyline != null && !polyline.isEmpty())
		{
			UrlParser up = new UrlParser();
			
			for(int i = 0; i < polyline.size(); ++i)
				polyline.set(i, up.delSpace(polyline.get(i)));
			
			LatLng origin = new LatLng(from.lat, from.lon);
			LatLng dest = new LatLng(to.lat, to.lon);
			GoogleMap gm = new GoogleMap(origin, dest, GoogleMapModi.walking);
			distance = gm.calcDistance(polyline);		
		}
		return distance + dist_walk;
	}


	@Override
	public Route call() throws Exception {
		// TODO Auto-generated method stub
		return calcRoute();
	}

	public static void main(String[] args) {
		String start = "Innsbruck höhenstrasse 40";
		String stop = "innsbruck sillhöfe 7";
		NetworkProvider provider = new IvbProvider();

		Location from = new Location(LocationType.ADDRESS, 0, null, start);
		Location to = new Location(LocationType.ADDRESS, 0, null, stop);
		QueryTripsResult result = null;

		try {
			result = provider.queryTrips(from, null, to, new Date(), true,
					Product.ALL, WalkSpeed.NORMAL, Accessibility.NEUTRAL, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("Something went wrong with the PTE request");
			e.printStackTrace();
		}

		/*	String[] myRoute = convertString(result);

		for(String s : myRoute)
			//System.out.println(s);
		 */
	}
}
