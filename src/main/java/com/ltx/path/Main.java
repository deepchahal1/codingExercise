package com.ltx.path;

import com.ltx.path.RouteManagerImpl.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class create RouteManagerImpl instance,initialize it with sample route data, option to load sample data from
 * file or a List.
 * to load data from file set VM argument -DcityRouteFile=<>, default value is "testDataFile.txt", saved in same samke package.
 * Source city VM argument -Dcity1=<>  default value is "Atlanta"
 * Destination city VM argument -Dcity2=<>  default value is "Louisville"
 *
 * @author PradeepKrChahal
 * 
 */
public class Main {

	public static void main(String[] args) {
        //property cityRouteFile is used if loading routes info from file
		@SuppressWarnings("unused")
		String cityRouteFile = System.getProperty("cityRouteFile", "testDataFile.txt");//either change default here itself or pass through VM arguments
		String city1 = System.getProperty("city1", "Atlanta");//either change default here itself or pass through VM arguments
		String city2 = System.getProperty("city2", "Louisville");//either change default here itself or pass through VM arguments


				
		// creating routeManager instance from routes stored in list
		RouteManager routeManager = loadRoutesFromList(routeList);
		
		// creating routeManager instance from routes stored in file
		//please uncomment below line if want to load routes from file
		//routeManager = loadRoutesFromFile(cityRouteFile);
		
		
		//Test
		boolean exist = routeManager.connected(city1, city2);
		List<String> route = routeManager.getRoute(city1, city2);
		if (exist) {
			System.out.println(city1 + " and " + city2 + " is Connected");
			System.out.println("Route: " + route.toString());
		} else {
			System.out.println(city1 + " and " + city2 + " is NOT Connected");
		}

		exist = routeManager.connected(city2, city1);
		route = routeManager.getRoute(city2, city1);
		if (exist) {
			System.out.println(city2 + " and " + city1 + " is Connected");
			System.out.println("Route: " + route.toString());
		} else {
			System.out.println(city2 + " and " + city1 + " is NOT Connected");
		}
	
    	System.exit(0);

	}

	/**
	 * This function RouteManagerImpl initialize with routes stored in a file.
	 * 
	 * @param cityRouteFile  sample route file
	 */
	@SuppressWarnings("unused")
	private static RouteManager loadRoutesFromFile(final String cityRouteFile) {

		try {
			URL url = Main.class.getResource(cityRouteFile);
			URI uri = url.toURI();
			File file = new File(uri);
			if (file.exists()) {
				// creating RouteManagerImpl instance
				return new RouteManagerImpl(new FileRoutesLoader(file, ","));
			} else {
				throw new RuntimeException("file could not be found: " + cityRouteFile);
			}
		} catch (URISyntaxException | FileNotFoundException e1) {
			throw new RuntimeException(e1);
		}

	}
	
	/**
	 * This function return RouteManagrImpl initialize with rout.eList
	 * 
	 * @param  routeList  (delimiter separated routes list)
	 */
	private static RouteManager loadRoutesFromList(final List<String> routeList){
		final RouteManager routeManager = new RouteManagerImpl();
		for(String delimiterSepratedRoute : routeList){
			String[] splitSrcDestination = delimiterSepratedRoute.split(",");
			if(splitSrcDestination.length < 2) throw new RuntimeException("route is not correct: "+ delimiterSepratedRoute);
			String city1 = splitSrcDestination[0];
			String city2 = splitSrcDestination[1];
			routeManager.addConnection(city1, city2);
		}
		return routeManager;
	}
	
	//sample routes stored in list.
	private final static List<String> routeList = new ArrayList<>();
	  static{
		  routeList.add("Atlanta,New Orleans");
		  routeList.add("New Orleans, Oklahoma City");
		  routeList.add("Atlanta,Miami");
		  routeList.add("Atlanta,Charlotte");
		  routeList.add("Charlotte,Richmond");
		  routeList.add("Richmond,Louisville");
		  routeList.add("Chicago,St. Louis");
		  routeList.add("Chicago,Indianapolis");
		  routeList.add("St. Louis,Kansas City");
		  routeList.add("Kansas City,Omaha");
		  routeList.add("Omaha,Denver");
		  routeList.add("Richmond,Washington");
		  routeList.add("Washington,Baltimore");
		  routeList.add("Baltimore,Philadelphia");
		  routeList.add("Philadelphia,Pittsburgh");
		  routeList.add("Philadelphia,Newark");
		  routeList.add("Newark,New York");
		  routeList.add("New York,Boston");
		  routeList.add("Boston,Montreal");
		  routeList.add("Pittsburgh,Cleveland");
		  routeList.add("Pittsburgh,New York");
		  routeList.add("Pittsburgh,Charlotte");
		  routeList.add("Cleveland,Detroit");
		  routeList.add("Dallas,El Paso");
		  routeList.add("El Paso, Phoenix");
		  routeList.add("El Paso, Albuquerque");
		  routeList.add("Phoenix,San Diego");
		  routeList.add("San Diego, Los Angeles");
		  routeList.add("Los Angeles,San Francisco");
	  }

}
