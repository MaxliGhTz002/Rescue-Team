package lab.kultida.rescueteam;

/**
* Created by ekapop on 14/12/2557.
*/

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceholderFragment_VictimLocation extends PlaceholderFragment_Prototype {
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_victim_location, container, false);
        defaultOperation();
        createMapView();
        addMarker();
        return rootView;
    }

    /**
     * Initialises the mapview
     */
    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(googleMap == null){
                SupportMapFragment supportMapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);
                googleMap = supportMapFragment.getMap();
                 /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(googleMap == null) {
                    Toast.makeText(activity,"Error creating map", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity,"create map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }

    /**
     * Adds a marker to the map
     */
    private void addMarker(){
        /** Make sure that the map has been initialised **/
        if(googleMap != null){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").draggable(true));
            Toast.makeText(activity,"Add market (0,0)",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(activity,"googleMap is null",Toast.LENGTH_SHORT).show();
        }
    }
}
