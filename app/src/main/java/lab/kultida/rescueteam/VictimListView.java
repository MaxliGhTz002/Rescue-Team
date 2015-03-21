package lab.kultida.rescueteam;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class VictimListView extends ArrayAdapter<String>{

    private final Activity context;
    protected ArrayList<String> macAddress;
    protected ArrayList<String> time;
    protected ArrayList<String> signal;
    protected ArrayList<String> annotation;

    public VictimListView(Activity context, ArrayList<String> macAddress, ArrayList<String> time, ArrayList<String> signal, ArrayList<String> annotation){
        super(context, R.layout.victim_list,macAddress);
        this.macAddress = macAddress;
        this.time = time;
        this.signal = signal;
        this.annotation = annotation;
        this.context = context;
    }

    public void addVictim(JSONObject client){
        try {
            Log.d("VictimList-addVictim","client = " + client.toString());
            macAddress.add(client.getString("macaddress"));
            time.add(client.getString("time"));
            annotation.add(client.getString("annotation"));
            signal.add(client.getString("signals"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.victim_list, null, true);
        TextView textView_Signal = (TextView) rowView.findViewById(R.id.textView_Signal);
        TextView textView_MacAddress = (TextView) rowView.findViewById(R.id.textView_MacAddress);
        TextView textView_Time = (TextView) rowView.findViewById(R.id.textView_Time);
        TextView textView_Annotation = (TextView) rowView.findViewById(R.id.textView_Annotation);

        switch (signal.get(position).toUpperCase()){
            case "RED" :
                textView_Signal.setBackgroundResource(R.color.RED);
                break;
            case "YELLOW":
                textView_Signal.setBackgroundResource(R.color.YELLOW);
                break;
            case "GREEN":
                textView_Signal.setBackgroundResource(R.color.GREEN);
                break;
        }
        textView_MacAddress.setText(macAddress.get(position));
        textView_Time.setText(time.get(position));
        textView_Annotation.setText(annotation.get(position));

        return rowView;
    }
}