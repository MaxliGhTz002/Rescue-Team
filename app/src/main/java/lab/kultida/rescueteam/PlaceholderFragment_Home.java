package lab.kultida.rescueteam;

/**
 * Created by ekapop on 14/12/2557.
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lab.kultida.utility.TCP_Unicast_Send;

public class PlaceholderFragment_Home extends PlaceholderFragment_Prototype {
    protected TextView section_Label;
    protected ListView listView_WifiResult;
    protected TextView textView_Output;
    protected Button button;
    protected WifiListView adapter;
    protected int serverPort_CheckWifiListInfor = 9998;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        defaultOperation();
        getComponent();
        createWifiList();

        return rootView;
    }

    public void createWifiList(){
        ArrayList<String> wifi = new ArrayList<>();
        ArrayList<Integer> signal = new ArrayList<>();
        ArrayList<String> red = new ArrayList<>();
        ArrayList<String> yellow = new ArrayList<>();
        ArrayList<String> green = new ArrayList<>();
        ArrayList<String> total = new ArrayList<>();
        adapter = new WifiListView(activity,wifi,signal,red,yellow,green,total);
        listView_WifiResult.setAdapter(adapter);
        listView_WifiResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                textView_Output.append("position : " + position + ", " + adapter.wifi.get(position));
                activity.connectToWifi(adapter.wifi.get(position));
            }
        });
    }

    protected void getComponent(){
        section_Label = (TextView)rootView.findViewById(R.id.section_Label);
        button = (Button)rootView.findViewById(R.id.button_ScanWifi);
        button.setOnClickListener(this);
        textView_Output = (TextView)rootView.findViewById(R.id.textView_Output);
        textView_Output.setMovementMethod(new ScrollingMovementMethod());
        listView_WifiResult = (ListView)rootView.findViewById(R.id.listView_WifiResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ScanWifi:
                JSONObject data = new JSONObject();
                JSONObject data_frame = new JSONObject();

                WifiManager wifiManager = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> temp = wifiManager.getScanResults();
                ArrayList<String> wifiArrayList = new ArrayList<>();
                for(int z = 0; z < temp.size(); z++) {
                    if(temp.get(z).SSID.contains("My_AP_Pi")) wifiArrayList.add(temp.get(z).SSID);
                }
                try{
                    data.put("wifiList",new JSONArray(wifiArrayList));
                    data.put("signal","getWifiListInformation");

                    data_frame.put("serverIP", serverIP);
                    data_frame.put("serverPort", serverPort_CheckWifiListInfor);
                    data_frame.put("data",data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("Placeholder_Home - Click()", "TCP_Unicast_Send_CheckWifiListInfor().execute(data_frame.toString()");
                textView_Output.append("data_frame : " + data_frame.toString() + "\n");
                textView_Output.append("Checking Wifi List Information from server\n");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    new TCP_Unicast_Send_CheckWifiListInfor().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data_frame.toString());
                } else {
                    new TCP_Unicast_Send_CheckWifiListInfor().execute(data_frame.toString());
                }
//                new  TCP_Unicast_Send_CheckWifiListInfor().execute(data_frame.toString());
                break;
        }
    }

    protected class TCP_Unicast_Send_CheckWifiListInfor extends TCP_Unicast_Send {
        @Override
        protected void onPreExecute() {
            receiveData = true;
            log_Head = "TCP_Unicast_Send_CheckWifiListInfor";
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            textView_Output.append("Data Send  " + result + "\n");
            textView_Output.append("Data Receive " + data_receive + "\n");
            try {
                JSONObject data_frame = new JSONObject(data_receive);
                JSONArray wifiList = data_frame.getJSONArray("wifiList");
                for(int i = 0; i < wifiList.length();i++){
                    JSONObject wifi = wifiList.getJSONObject(i);
                    adapter.addWifiInfor(wifi);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            listView_WifiResult.setSelection(adapter.getCount() - 1);
            textView_Output.append("Check Wifi List Information from server Complete\n");
        }
    }
}
