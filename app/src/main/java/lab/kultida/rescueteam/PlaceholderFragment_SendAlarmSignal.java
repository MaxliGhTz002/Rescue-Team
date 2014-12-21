package lab.kultida.rescueteam;

/**
 * Created by ekapop on 14/12/2557.
 */

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import lab.kultida.utility.UDP_Broadcast_Send;

public class PlaceholderFragment_SendAlarmSignal extends PlaceholderFragment_Prototype {
    protected TextView section_Label;
    protected Button button_SendAlarmSignal;
    protected TextView textView_Output;
    protected int serverPort = 21234;
    protected String alarm = "alarm signal";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_send_alarm_signal, container, false);
        
        defaultOperation();
        getComponent();

        return rootView;
    }

    protected void getComponent(){
        section_Label = (TextView)rootView.findViewById(R.id.section_Label);
        button_SendAlarmSignal = (Button)rootView.findViewById(R.id.button_CheckHotspotInformation);
        button_SendAlarmSignal.setOnClickListener(this);
        textView_Output = (TextView)rootView.findViewById(R.id.textView_Output);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_CheckHotspotInformation:
                JSONObject data = new JSONObject();
                JSONObject data_frame = new JSONObject();
                try {
                    data.put("alarm",alarm);

                    data_frame.put("data", data);
                    data_frame.put("serverPort_CheckHotspotInformation", serverPort);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("sending broadcast", data_frame.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    new UDP_Broadcast_Send_AlarmSignal().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data_frame.toString());
                else
                    new UDP_Broadcast_Send_AlarmSignal().execute(data_frame.toString());
                break;
        }
    }

    //  <<--------------------------  ASYNCTASK OPERATION  ------------------------->>
    protected class UDP_Broadcast_Send_AlarmSignal extends UDP_Broadcast_Send {
        @Override
        protected void onPreExecute() {
            log_Head = "UDP_Broadcast_Send_AlarmSignal";
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("sending broadcast", "finished");
            textView_Output.append("Send Alarm Signal " + result + "\n");
        }
    }
}
