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
    String wifiName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_check_hotspot_information, container, false);

        defaultOperation();
        getComponent();
        createVictimList();
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

                Log.d("Placeholder_CheckHotspotInformation - Click()", "TCP_Unicast_Send_CheckHotspotInformation()execute(data_frame.toString()");
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
		            for (int i = 0; i < clientList.length(); i++) {
			            JSONObject client = clientList.getJSONObject(i);
			            adapter.addVictim(client);
		            }
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
