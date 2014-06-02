package com.jdam.logic;
import java.net.URLDecoder;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.jdam.enums.GoogleMapModi;
import com.jdam.enums.InputType;
import com.jdam.model.LatLng;
import com.jdam.model.Route;

public class RouteController {

	private int taskNum = 3;

	public RouteController(){}

	public Route[] calcRoutes(String origin, String destination){
System.out.println("str");
		Route[] routes = new Route[taskNum];

		System.out.println("-----------------------------calc first route GM Driving------------------------------------");
		GoogleMap gmCar = new GoogleMap(origin,destination, GoogleMapModi.driving);
		FutureTask<Route> taskGMcar = new FutureTask<Route>(gmCar);
		routes[0] = subCalcRoutes(taskGMcar);

		System.out.println("-----------------------------calc second route PTE ------------------------------------");
		if(gmCar.getLatlng_origin() != null && gmCar.getLatlng_dest() != null){
			System.out.println("calc PTE with latlng");
			PTE pte = new PTE(gmCar.getLatlng_origin(), gmCar.getLatlng_dest(), new Date());
			FutureTask<Route> taskPTE = new FutureTask<Route>(pte);
			routes[1] = subCalcRoutes(taskPTE);
		}
		else{
			String text[] = {"FAILED","FAILED","FAILED","FAILED","FAILED","FAILED"};
			routes[1] = new Route(123, 456, "PTE", text, 23, 234, new Date());
		}
		
		System.out.println("-----------------------------calc third route GM bike ------------------------------------");
		GoogleMap gmBike = new GoogleMap(origin,destination, GoogleMapModi.bicycling);
		FutureTask<Route> taskGMbike = new FutureTask<Route>(gmBike);	
		routes[2] = subCalcRoutes(taskGMbike);	

		return routes;
	}
	
	public Route[] calcRoutes(LatLng origin, String destination){
System.out.println("calc latlng str");
		Route[] routes = new Route[taskNum];

		System.out.println("-----------------------------calc first route GM Driving------------------------------------");
		GoogleMap gmCar = new GoogleMap(origin,destination, GoogleMapModi.driving);
		FutureTask<Route> taskGMcar = new FutureTask<Route>(gmCar);
		routes[0] = subCalcRoutes(taskGMcar);

		System.out.println("-----------------------------calc second route PTE ------------------------------------");
		if(gmCar.getLatlng_origin() != null && gmCar.getLatlng_dest() != null){
			System.out.println("calc PTE with latlng");
			PTE pte = new PTE(gmCar.getLatlng_origin(), gmCar.getLatlng_dest(), new Date());
			FutureTask<Route> taskPTE = new FutureTask<Route>(pte);
			routes[1] = subCalcRoutes(taskPTE);
		}
		else{
			String text[] = {"FAILED","FAILED","FAILED","FAILED","FAILED","FAILED"};
			routes[1] = new Route(123, 456, "PTE", text, 23, 234, new Date());
		}
		
		System.out.println("-----------------------------calc third route GM bike ------------------------------------");
		GoogleMap gmBike = new GoogleMap(origin,destination, GoogleMapModi.bicycling);
		FutureTask<Route> taskGMbike = new FutureTask<Route>(gmBike);	
		routes[2] = subCalcRoutes(taskGMbike);	

		return routes;
	}
	
	public Route[] calcRoutes(LatLng origin, LatLng destination){
System.out.println("calc latlng");
		Route[] routes = new Route[taskNum];

		System.out.println("-----------------------------calc first route GM Driving------------------------------------");
		GoogleMap gmCar = new GoogleMap(origin,destination, GoogleMapModi.driving);
		FutureTask<Route> taskGMcar = new FutureTask<Route>(gmCar);
		routes[0] = subCalcRoutes(taskGMcar);

		System.out.println("-----------------------------calc second route PTE ------------------------------------");
		if(gmCar.getLatlng_origin() != null && gmCar.getLatlng_dest() != null){
			System.out.println("calc PTE with latlng");
			PTE pte = new PTE(gmCar.getLatlng_origin(), gmCar.getLatlng_dest(), new Date());
			FutureTask<Route> taskPTE = new FutureTask<Route>(pte);
			routes[1] = subCalcRoutes(taskPTE);
		}
		else{
			String text[] = {"FAILED","FAILED","FAILED","FAILED","FAILED","FAILED"};
			routes[1] = new Route(123, 456, "PTE", text, 23, 234, new Date());
		}
		
		System.out.println("-----------------------------calc third route GM bike ------------------------------------");
		GoogleMap gmBike = new GoogleMap(origin,destination, GoogleMapModi.bicycling);
		FutureTask<Route> taskGMbike = new FutureTask<Route>(gmBike);	
		routes[2] = subCalcRoutes(taskGMbike);	

		return routes;
	}


	private Route subCalcRoutes(FutureTask<Route> task){

		final ExecutorService service;

		service = Executors.newFixedThreadPool(1);

		service.execute(task);

		Route routes = null;
		try {
			routes = task.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		service.shutdown();

		return routes;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Route[] rs = null;
		UrlParser up = new UrlParser();
		String decode = "latlngstr$from=47.2697209,11.3935881&to=sillhoefe 7";
//		String decode = "latlng$from=47.2697209,11.3935881&to=47.2568947,11.4058417";
//		String decode = "str$from=innsbruck technik&to=sillhoefe 7";
//		47.2697209,11.3935881 ibk		47.2711913,11.3918079
		decode = up.delSpace(decode);
		
		if(up.checkInput(decode)){
		
		RouteController rc = new RouteController();
		if(up.inputType(decode) == InputType.latlng)
			rs = rc.calcRoutes(up.parserFromLatLng(decode), up.parserToLatLng(decode));
		if(up.inputType(decode) == InputType.latlngstr)
			rs = rc.calcRoutes(up.parserFromLatLng(decode), up.parserTo(decode));
		if(up.inputType(decode) == InputType.str)
			rs = rc.calcRoutes(up.parserFrom(decode), up.parserTo(decode));
		}
				
		if(rs != null){
			for(int i = 0; i < rs.length; ++i){
				System.out.println(rs[i].toString());
				if(i == 1){
					for(String s : rs[1].getText())
						System.out.println(s);

				}
			}
		}
		else
			System.out.println("failed");

	}
}
