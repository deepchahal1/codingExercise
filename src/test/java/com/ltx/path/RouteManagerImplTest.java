package com.ltx.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for RouteManagerImpl.
 * @author PradeepKrChahal
 */
public class RouteManagerImplTest {
	private RouteManager routeManager;
	@Before
    public void setup(){
		routeManager = new RouteManagerImpl();
		for(String srcDestination: routeList){
			String[] splitSrcDestination = srcDestination.split(",");
			String city1 = splitSrcDestination[0];
			String city2 = splitSrcDestination[1];
			routeManager.addConnection(city1, city2);
		}
	}
	
	@Test
	public void testExistedRouteConnectivityOnPreAddedRoutes(){
		final String city1 = "Atlanta";
		final String city2 = "Louisville";
		final List<String> expectedRoute = new ArrayList<>();
		expectedRoute.add("Atlanta");
		expectedRoute.add("Charlotte");
		expectedRoute.add("Richmond");
		expectedRoute.add("Louisville");
		boolean connected = routeManager.connected(city1, city2);
		assertTrue("Expected connected", connected);
		List<String> actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		
		//Test case: reverse source and destination
		
		//reverse expected path;
		Collections.reverse(expectedRoute);
		
		//rever
		connected = routeManager.connected(city2, city1);
		assertTrue("Expected connected", connected);
		actualRoute = routeManager.getRoute(city2, city1);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		
	}
	
	@Test
	public void testNonExistingRouteConnectivityOnPreAddedRoutes(){
		final String city1 = "Atlanta";
		final String city2 = "Omaha";
		final List<String> expectedRoute = new ArrayList<>();
		
		boolean connected = routeManager.connected(city1, city2);
		assertFalse("Expected NOT connected", connected);
		List<String> actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
			
	}
	
	@Test
	public void testRouteConnectivityOnNewlyAddedRoutes(){
		String city1 = "Atlanta";
		String city2 = "Louisville";
		final List<String> expectedRoute = new ArrayList<>();
		expectedRoute.add("Atlanta");
		expectedRoute.add("Charlotte");
		expectedRoute.add("Richmond");
		expectedRoute.add("Louisville");
		boolean connected = routeManager.connected(city1, city2);
		assertTrue("Expected connected", connected);
		List<String> actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		
		//Test case: Add additional route
		routeManager.addConnection("Louisville", "Boston");
		//add additional city in path
		expectedRoute.add("Boston");
		city2 = "Boston";
		connected = routeManager.connected(city1, city2);
		assertTrue("Expected connected", connected);
		actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
			
	}
	
	@Test
	public void testAllDirectConnectivityForBothDirection(){
		
		//test direct connectivity for all cities.
		for(String srcDestination: routeList){
			String[] splitSrcDestination = srcDestination.split(",");
			String city1 = splitSrcDestination[0];
			String city2 = splitSrcDestination[1];
			final List<String> expectedRoute = new ArrayList<>();
			expectedRoute.add(city1.trim());
			expectedRoute.add(city2.trim());
			routeManager.addConnection(city1, city2);
			boolean connected = routeManager.connected(city1, city2);
			assertTrue("Expected connected", connected);
			List<String> actualRoute = routeManager.getRoute(city1, city2);
			assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		}
		
		//test direct connectivity in reverse for all cities.
				for(String srcDestination: routeList){
					String[] splitSrcDestination = srcDestination.split(",");
					String city1 = splitSrcDestination[0];
					String city2 = splitSrcDestination[1];
					final List<String> expectedRoute = new ArrayList<>();
					expectedRoute.add(city2.trim());
					expectedRoute.add(city1.trim());
					//reverse cities
					routeManager.addConnection(city2, city1);
					boolean connected = routeManager.connected(city2, city1);
					assertTrue("Expected connected", connected);
					List<String> actualRoute = routeManager.getRoute(city2, city1);
					assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
				}
			
	}

	@Test
	public void testWithInValidSourceDesitinationRoutes(){
		
		//source destination name as null
		String city1 = null;
		String city2 = null;
		final List<String> expectedRoute = new ArrayList<>();
	
		boolean connected = routeManager.connected(city1, city2);
		assertFalse("Expected NOT connected", connected);
		List<String> actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		
		//source destination name as ""
		city1 = "";
		city2 = "";
		connected = routeManager.connected(city1, city2);
		assertFalse("Expected NOT connected", connected);
		actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		
		//valid source name, non existing  destination 
		city1 = "Atlanta";
		city2 = "Delhi";
		connected = routeManager.connected(city1, city2);
		assertFalse("Expected NOT connected", connected);
		actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		
		
		// non existing source name, valid  destination 
		city1 = "Delhi";
		city2 = "Atlanta";
		connected = routeManager.connected(city1, city2);
		assertFalse("Expected NOT connected", connected);
		actualRoute = routeManager.getRoute(city1, city2);
		assertTrue("Route did not matched",Objects.deepEquals(expectedRoute, actualRoute));
		
		
		
		
	}
	
	public final static List<String> routeList = new ArrayList<>();
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
