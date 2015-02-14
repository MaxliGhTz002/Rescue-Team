package lab.kultida.rescueteam;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class WifiListView extends ArrayAdapter<String>{

    private final Activity context;
    protected ArrayList<String> wifi;
    protected ArrayList<Integer> signal;
    protected ArrayList<String> red;
    protected ArrayList<String> yellow;
    protected ArrayList<String> green;
    protected ArrayList<String> total;

    public WifiListView(Activity context, ArrayList<String> wifi, ArrayList<Integer> signal, ArrayList<String> red, ArrayList<String> yellow, ArrayList<String> green, ArrayList<String> total){
        super(context, R.layout.victim_list,wifi);
        this.wifi = wifi;
        this.signal = signal;
        this.red = red;
        this.yellow = yellow;
        this.green = green;
        this.total = total;
        this.context = context;
    }

    public void addWifiInfor(JSONObject wifiInfor){
        try {
            Log.d("WifiListView-addWifi", "wifi = " + wifiInfor.toString());
            wifi.add(wifiInfor.getString("wifi"));
            signal.add(wifiInfor.getInt("signal"));
            red.add(wifiInfor.getString("numRedSignal"));
            yellow.add(wifiInfor.getString("numYellowSignal"));
            green.add(wifiInfor.getString("numGreenSignal"));
            total.add(wifiInfor.getString("numVictim"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.wifi_list, null, true);
        TextView textView_Wifi = (TextView) rowView.findViewById(R.id.textView_Wifi);
        ProgressBar progressBar_Signal = (ProgressBar) rowView.findViewById(R.id.progressBar_SignalLevel);
        TextView textView_Red = (TextView) rowView.findViewById(R.id.textView_Red);
        TextView textView_Yellow = (TextView) rowView.findViewById(R.id.textView_Yellow);
        TextView textView_Green = (TextView) rowView.findViewById(R.id.textView_Green);
        TextView textView_Total = (TextView) rowView.findViewById(R.id.textView_Total);

        Log.d("position",position + "");
        //Log.d("wifi signal red yellow green signal",wifi.size() + " " + signal.size() + " " + red.size() + "" + yellow.size() + " " + green.size() + " " + total.size());
        Log.d("signal",signal.toString());

        int signalLevel = signal.get(position) + 100;
        textView_Wifi.setText(wifi.get(position) + "   <" + signalLevel + ">");
        progressBar_Signal.setProgress(signalLevel);
        if(signalLevel == 50){
            progressBar_Signal.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }else if(signalLevel >= 50){
            progressBar_Signal.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        }else if(signalLevel >= 30){
            progressBar_Signal.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        }else{
            progressBar_Signal.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }

        textView_Red.setText(red.get(position));
        textView_Yellow.setText(yellow.get(position));
        textView_Green.setText(green.get(position));
        textView_Total.setText(total.get(position));

        return rowView;
    }
}