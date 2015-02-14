package lab.kultida.rescueteam;

/**
 * Created by ekapop on 14/12/2557.
 */

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import lab.kultida.utility.TCP_Unicast_Send;

public class PlaceholderFragment_CheckHotspotInformation extends PlaceholderFragment_Prototype {
    protected TextView section_Label;
    protected VictimListView adapter;
    protected TextView textView_Summary;
    protected Button button_CheckHotspotInformation;
    protected TextView textView_Output;
    protected ListView listView_Victim;
    protected int serverPort_CheckHotspotInformation = 9998;
    protected String wifiName = "";
    private GoogleMap googleMap;
    ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_check_hotspot_information, container, false);

        defaultOperation();
        getComponent();
        createVictimList();
        createMapView();
        return rootView;
    }

    protected void getComponent(){
        section_Label = (TextView)rootView.findViewById(R.id.section_Label);
        button_CheckHotspotInformation = (Button)rootView.findViewById(R.id.button_CheckHotspotInformation);
        button_CheckHotspotInformation.setOnClickListener(this);
        textView_Output = (TextView)rootView.findViewById(R.id.textView_Output);
        textView_Output.setMovementMethod(new ScrollingMovementMethod());
        listView_Victim = (ListView)rootView.findViewById(R.id.listView_WifiResult);
        textView_Summary = (TextView)rootView.findViewById(R.id.textView_Summary);
    }

    public void createVictimList(){
        ArrayList<String> macAddress = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        ArrayList<String> signal = new ArrayList<>();
        ArrayList<String> annotation = new ArrayList<>();
        adapter = new VictimListView(activity,macAddress,time,signal,annotation);
        listView_Victim.setAdapter(adapter);
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
    private void addMarker(double lat,double lng,String marker){
        /** Make sure that the map has been initialised **/
        if(googleMap != null){
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat, lng)).title(marker).draggable(true);;
            markerOptionses.add(markerOptions);
            googleMap.addMarker(markerOptions);
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Marker").draggable(true));
            Toast.makeText(activity,"Add market (" + lat + "," +  lng + ")",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(activity,"googleMap is null",Toast.LENGTH_SHORT).show();
        }
    }

    private void changeFocusGoogleMap(){
        double lat_avg = 0;
        double lng_avg = 0;
        for(int i = 0;i < markerOptionses.size();i++){
            lat_avg = lat_avg + markerOptionses.get(i).getPosition().latitude;
            lng_avg = lng_avg + markerOptionses.get(i).getPosition().latitude;
        }
        lat_avg = lat_avg/markerOptionses.size();
        lng_avg = lng_avg/markerOptionses.size();

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat_avg, lng_avg));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_CheckHotspotInformation:
                JSONObject data = new JSONObject();
                JSONObject data_frame = new JSONObject();
                try {
                    data.put("signal","getHotspotInformation");
                    data.put("macaddress", activity.getMacAddress());
                    data.put("fromPi", wifiName = activity.getNetworkName());

                    data_frame.put("serverIP", serverIP);
                    data_frame.put("serverPort", serverPort_CheckHotspotInformation);
                    data_frame.put("data", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("Placeholder_ChkHp-Click", "TCP_Unicast_Send_CheckHotspotInformation()execute(data_frame.toString()");
                textView_Output.append("Checking Hotspot Information from server\n");

	            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		            new TCP_Unicast_Send_CheckHotspotInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data_frame.toString());
	            } else {
		            new TCP_Unicast_Send_CheckHotspotInformation().execute(data_frame.toString());
	            }
                //new  TCP_Unicast_Send_CheckHotspotInformation().execute(data_frame.toString());
                break;
        }
    }

    protected class TCP_Unicast_Send_CheckHotspotInformation extends TCP_Unicast_Send {
        @Override
        protected void onPreExecute() {
            receiveData = true;
            log_Head = "TCP_Unicast_Send_CheckHotspotInformation";
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            textView_Output.append("Data Send  " + result + "\n");
            textView_Output.append("Data Receive " + data_receive + "\n");
            try {
	            if(data_receive != null) {
                    createVictimList();
		            JSONObject data_frame = new JSONObject(data_receive);
		            int numVictim = data_frame.getInt("numVictim");
		            int numRedSignal = data_frame.getInt("numRedSignal");
		            int numYellowSignal = data_frame.getInt("numYellowSignal");
		            int numGreenSignal = data_frame.getInt("numGreenSignal");
		            textView_Summary.setText(
                                    "WIFI : " + wifiName +  "\n" +
				                    "Total Victim : " + numVictim + "\n" +
						            "Red Victim : " + numRedSignal + "\n" +
						            "Yellow Victim : " + numYellowSignal + "\n" +
						            "Green Victim : " + numGreenSignal + "\n"
		            );
		            JSONArray clientList = data_frame.getJSONArray("victim");
//                    clear (not testing)
//                    googleMap.clear();
		            for (int i = 0; i < clientList.length(); i++) {
			            JSONObject client = clientList.getJSONObject(i);
                        double latitude = client.getDouble("lat");
                        double longitude = client.getDouble("lon");
                        addMarker(latitude,longitude,client.getString("macaddress"));
                        adapter.addVictim(client);
		            }

                    changeFocusGoogleMap();

                    adapter.notifyDataSetChanged();
                    listView_Victim.setSelection(adapter.getCount() - 1);
                    textView_Output.append("Check Hotspot Information from server Complete\n");
	            } else {
		            textView_Summary.append("No result");
	            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
