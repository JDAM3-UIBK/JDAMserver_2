package com.jdam.server;



import java.net.URLDecoder;
import java.net.URLEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jdam.enums.InputType;
import com.jdam.logic.RouteController;
import com.jdam.logic.UrlParser;
import com.jdam.model.Route;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	

	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	@RequestMapping(value = "/calc/{input}", method = RequestMethod.GET)
	public @ResponseBody 
	Route[] returnRoutes(@PathVariable String input) {

		Route[] routes = null;
//		String decode = URLDecoder.decode(input);
		String decode = input;
		UrlParser up = new UrlParser();
		if(up.checkInput(decode)){
		decode = up.delSpace(decode);
		RouteController rc = new RouteController();
		if(up.inputType(decode) == InputType.latlng)
			routes = rc.calcRoutes(up.parserFromLatLng(decode), up.parserToLatLng(decode));
		if(up.inputType(decode) == InputType.latlngstr)
			routes = rc.calcRoutes(up.parserFromLatLng(decode), up.parserTo(decode));
		if(up.inputType(decode) == InputType.str)
			routes = rc.calcRoutes(up.parserFrom(decode), up.parserTo(decode));
		}
		
		return routes;
	}
	
	@RequestMapping(value = "/statistic/{input}", method = RequestMethod.GET)
	public @ResponseBody 
	String returnStatistic(@PathVariable String input) {

		return "statistic " + input;
	}
	
	@RequestMapping(value = "/save/{input}", method = RequestMethod.GET)
	public @ResponseBody 
	String saveRoutes(@PathVariable String input) {

		return "statistic " + input;
	}
	
}
