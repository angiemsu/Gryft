/* Time spent on a7:  hh hours and mm minutes.

 * Name:
 * Netid: 
 * What I thought about this assignment:
 *
 *
 */

package student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import heap.Heap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.*;
import com.google.android.gms.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.TravelMode;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {


    /**
     * Return a HashMap whose key is all the Addresses whose distance is in the acceptable
     * interval the user assign and the value is the time required for this customer to 
     * go from this Address to the final destination. Make use of Google Maps Distance
     * Matrix API.
     * @param timeThreshold: The longest time that the customer accept for walking in minutes
     * @param destination: the final destination, which is an array with only one element
     */
    public HashMap<String,Long> generateHashMapForTime(int timeThreshold, String[] destination){
        HashMap<String, Long> mapForTime = new HashMap<String, Long>();
        long time;
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyDqgPPIEtCFkoRvI5JP6xC50EmNxVazHLo").setQueryRateLimit(10);
        for (String[] n : getAllNearNodes(timeThreshold, destination)){
            try{
                DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
                DistanceMatrix trix = req.origins(n).destinations(destination).mode(TravelMode.WALKING).await();
                time = trix.rows[0].elements[0].duration.inSeconds;
                mapForTime.put(n[0], time);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return mapForTime;
    }

    /**
     * Return a List of List of Addresses: for each List of addresses it contains one address and
     * all the Addresses whose distance is in the acceptable interval the user assign are contained.
     * We assume the speed of human to be 1.4m/s, which is 42 m/min. Make Use of Google Maps
     * Geocoding API and Places API.
     * @param timeThreshold: the longest time that the customer accept for walking in minutes
     * @param destination: the final destination
     */


    public List<String[]> getAllNearNodes(int timeThreshold, String[] destination){
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyDqgPPIEtCFkoRvI5JP6xC50EmNxVazHLo").setQueryRateLimit(10);
        com.google.maps.model.LatLng destLoc = null;
        ArrayList<String[]> nearAddress = new ArrayList<String[]>();
        try{
            GeocodingApiRequest req = GeocodingApi.geocode(context, destination[0]);
            GeocodingResult[] code = req.await();
            destLoc = code[0].geometry.location;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        try{
            NearbySearchRequest Nreq = PlacesApi.nearbySearchQuery(context,destLoc);
            PlacesSearchResponse response = Nreq.radius(42 * timeThreshold).await();
            for(int i = 0; i < response.results.length; i++){
                com.google.maps.model.LatLng possibleLoc = response.results[i].geometry.location;
                try{
                    GeocodingApiRequest Greq = GeocodingApi.reverseGeocode(context, possibleLoc);
                    GeocodingResult[] revCode = Greq.await();
                    String addr = revCode[0].formattedAddress;
                    String[] address = new String[10];
                    address[0] = addr;
                    nearAddress.add(address);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return nearAddress;
    }

    /**
     * Return a HashMap whose key is all the Nodes whose distance is in the acceptable 
     * interval the user assign and the value is the price it takes from the starting Node
     * to that Node
     * @param timeThreshold: the longest time that the customer accept for walking
     * @param origin: the place where the customer departs
     * @param destination: the final destination
     */
    public HashMap<String, Double> generateHashMapForPrice(int timeThreshold, String[] origin, String[] destination){
        HashMap<String, Double> mapForPrice = new HashMap<String, Double>();
        for (String[] n : getAllNearNodes(timeThreshold, destination)){
            mapForPrice.put(n[0], getPrice(origin[0], n[0]));
        }
        return mapForPrice;
    }

    //TODO
    public double getPrice(String origin, String destination){
        return 0.0;
    }


    /**
     * Return the Node which maps the smallest value in mapForPrice.
     * @param mapForPrice: a HashMap whose key is all the Nodes whose distance is in the 
     * acceptable interval the user assign and the value is the price it takes from the 
     * starting Node
     * @param timeThreshold: the longest time that the customer accept for walking
     * @param destination: the final destination
     * @return
     */
    public String minNode(HashMap<String, Double> mapForPrice, int timeThreshold, String[] destination){
        Heap<String> minHeap = new Heap<String>();
        for (String[] n : getAllNearNodes(timeThreshold, destination)){
            minHeap.add(n[0], mapForPrice.get(n[0]));
        }
        return minHeap.poll();
    }


    public String bestChoice (String[] origin, String[] destination, int timeThreshold){
        HashMap <String, Long> mapForTime = generateHashMapForTime(timeThreshold, destination);
        HashMap <String, Double> mapForPrice = generateHashMapForPrice(timeThreshold, origin, destination);
        double priceThreshold = - mapForPrice.get(minNode(mapForPrice, timeThreshold, destination)) + getPrice(origin[0], destination[0]);
        double rate = timeThreshold / priceThreshold;
        String min = origin[0];
        for(String[] n : getAllNearNodes(timeThreshold, destination)){
            if (mapForTime.get(n) <= mapForTime.get(min) && mapForPrice.get(n) <= mapForPrice.get(min))
                min = n[0];
            else if (mapForTime.get(n) <= mapForTime.get(min) && mapForPrice.get(n) > mapForPrice.get(min)){
                if (mapForTime.get(min) - mapForTime.get(n) <= rate * (mapForPrice.get(n) - mapForPrice.get(min)))
                    min = n[0];
            }
            else if(mapForTime.get(n) > mapForTime.get(min) && mapForPrice.get(n) <= mapForPrice.get(min)){
                if (mapForTime.get(n) - mapForTime.get(min) > rate * (mapForPrice.get(min) - mapForPrice.get(n)))
                    min = n[0];
            }
        }
        return min;
    }


}