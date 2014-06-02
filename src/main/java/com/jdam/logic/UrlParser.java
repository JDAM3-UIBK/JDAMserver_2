package com.jdam.logic;

import com.jdam.enums.InputType;
import com.jdam.model.LatLng;

public class UrlParser {

	private char delimiterFromTo = '=';
	private char delimiterDestOrigin = '&';
	private char delimiterLatLng = ',';
	private char delimiterType = '$';

	public UrlParser(){}

	//url/Server/from=---&to=---
	// --- lat,long oder string

	//parsing text /Server/latlng?from=innsbruck&to=wien
	//	latlngstr
	//	str

	public boolean checkInput(String str){
		boolean result = false;
		if(inputType(str) != null && inputType(str).toString().equals(InputType.str.toString()))
		{
			if(!parserFrom(str).equals(""))
			{
				if(!parserTo(str).equals("")){				
					result = true;
				}
			}
		}
		if(inputType(str) != null && inputType(str).toString().equals(InputType.latlngstr.toString()))
		{
			if(parserFromLatLng(str) != null)
			{
				if(!parserTo(str).equals("")){				
					result = true;
				}
			}
		}	
		if(inputType(str) != null && inputType(str).toString().equals(InputType.latlng.toString()))
		{
			
			if(parserFromLatLng(str) != null && parserToLatLng(str) != null)
			{
					result = true;
			}
		}	
		return result;
	}

	public Enum inputType(String str){
		int start = 0;
		int stop = str.indexOf(delimiterType, start);
		if(stop == -1)
			return null;

		String text = str.substring(start, stop);

		if(text.equals(InputType.latlng.toString()))
			return InputType.latlng;
		else if(text.equals(InputType.latlngstr.toString()))
			return InputType.latlngstr;
		else if(text.equals(InputType.str.toString()))
			return InputType.str;
		else
			return null;
	}

	public String parserFrom(String str){
		int start = 0;
		start = str.indexOf(delimiterFromTo, start)+1;
		int stop = str.indexOf(delimiterDestOrigin, start);

		if(start == -1 || stop == -1)
			return "";

		return str.substring(start, stop);
	}
	
	public String parserTo(String str){
		int start = 0;
		start = str.indexOf(delimiterDestOrigin, start);
		start = str.indexOf(delimiterFromTo, start)+1;
		int stop = str.length();

		if(start == -1 || stop == -1)
			return "";

		return str.substring(start, stop);
	}

	public LatLng parserFromLatLng(String str){	
		int start = 0;
		start = str.indexOf(delimiterFromTo, start)+1;
		int stop = str.indexOf(delimiterLatLng, start);

		if(start == -1 || stop == -1)
			return null;		
		Double lat = 0.0;
		try{
			lat = Double.valueOf(str.substring(start, stop));
		}catch(NumberFormatException e){
			return null;
		}

		start = stop+1;
		stop = str.indexOf(delimiterDestOrigin, start);
		if(start == -1 || stop == -1)
			return null;	

		Double lng = 0.0;
		try{
//			System.out.println("SUBSTRING: " +str.substring(start,stop));
			lng = Double.valueOf(str.substring(start, stop));
		} catch (NumberFormatException e){
			return null;
		}
		
//		System.out.println("FROM: " + lat + " / " +lng);
		return new LatLng(lat, lng);
	}

	public LatLng parserToLatLng(String str){
		int start = 0;
		start = str.indexOf(delimiterDestOrigin, start);
		start = str.indexOf(delimiterFromTo, start)+1;
		int stop = str.indexOf(delimiterLatLng, start);
		if(start == -1 || stop == -1)
			return null;		
		Double lat = 0.0;
		try{
			lat = Double.valueOf(str.substring(start, stop));
		}catch(NumberFormatException e){
			return null;
		}
		start = stop+1;
		stop = str.length();
		if(start == -1 || stop == -1)
			return null;	

		Double lng = 0.0;
		try{
			lng = Double.valueOf(str.substring(start, stop));
		} catch (NumberFormatException e){
			return null;
		}
//		System.out.println("TO: " + lat + " / " +lng);
		return new LatLng(lat, lng);
	}

	public String delSpace(String url){	
		return url.replaceAll(" ", "+");
	}


	public static void main(String[] args) {
		UrlParser up = new UrlParser();

		System.out.println("false: " + up.checkInput("?from=innsbruck&to=Wien"));
		System.out.println("false: " + up.checkInput("str?from=&to=Wien"));
		System.out.println("false: " + up.checkInput("latlng?from=dfj&to=sdf"));
		System.out.println();
		System.out.println("true: " + up.checkInput("latlng?from=342.34,234.453&to=2323.2354356345435,2.0000000"));
		System.out.println("true: " + up.checkInput("str?from=Innsbruck&to=Wien"));
		System.out.println("true: " + up.checkInput("latlngstr?from=22.3,323.3&to=Wien"));
		System.out.println();
//		System.out.println(up.parserFrom("from=&to=Wien"));
		System.out.println(up.parserFrom("http://127.0.0.1:8080/Server18/str?from=innsbruck&to=wien"));
		System.out.println(up.parserTo("str?from=Innsbruck&to=Wien"));
		System.out.println(up.parserFromLatLng("from=342.34,234.453&to=2323.2354356345435,2.0000000"));
		System.out.println(up.parserToLatLng("from=342.34,234.453&to=2323.2354356345435,2.00000001"));
		System.out.println(up.delSpace("from= innsbruck innstrasse"));
	}
}
