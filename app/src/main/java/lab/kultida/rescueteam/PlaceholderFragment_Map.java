package lab.kultida.rescueteam;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PlaceholderFragment_Map extends PlaceholderFragment_Prototype {
    private GoogleMap googleMap;
    private ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        defaultOperation();
        getComponent();
        createMapView();
        remap();
        return rootView;
    }

    public void remap() {
        super.onResume();
        if(googleMap != null){
            googleMap.clear();
            for(int i = 0;i < markerOptionses.size();i++) {
                Log.d("marker", markerOptionses.get(i).getPosition().toString());
                googleMap.addMarker(markerOptionses.get(i));
            }
            changeFocusGoogleMap();
        }

    }

    protected void getComponent(){

    }

    protected void clear(){
        markerOptionses = new ArrayList<>();
    }

    protected void createMapView(){
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
                    Toast.makeText(activity, "Error creating map", Toast.LENGTH_SHORT).show();
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
    public void addMarker(double lat,double lng,String marker){
        /** Make sure that the map has been initialised **/
//        if(googleMap != null){
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lng)).title(marker).draggable(true);
            markerOptionses.add(markerOptions);
            //googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Marker").draggable(true));
//            Toast.makeText(activity,"Add marker (" + lat + "," +  lng + ")",Toast.LENGTH_SHORT).show();

//        }else{
            //Toast.makeText(activity,"googleMap is null",Toast.LENGTH_SHORT).show();
//        }
    }

    public void changeFocusGoogleMap(){
        if(markerOptionses.size() == 0) return;
        double lat_avg = 0;
        double lng_avg = 0;
        for(int i = 0;i < markerOptionses.size();i++){
            lat_avg = lat_avg + markerOptionses.get(i).getPosition().latitude;
            lng_avg = lng_avg + markerOptionses.get(i).getPosition().longitude;
        }
        lat_avg = lat_avg/markerOptionses.size();
        lng_avg = lng_avg/markerOptionses.size();

        Log.d("autoFocus Map","(" + lat_avg +"," + lng_avg + ")");

//        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat_avg, lng_avg));
//        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

//        googleMap.moveCamera(center);
//        googleMap.animateCamera(zoom);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat_avg, lng_avg))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//
//        }
    }
}
