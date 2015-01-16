package lab.kultida.rescueteam;

/**
 * Created by ekapop on 14/12/2557.
 */

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import lab.kultida.utility.TCP_Unicast_Send;
import lab.kultida.utility.UDP_Unicast_Send;

public class PlaceholderFragment_PacketSimulator extends PlaceholderFragment_Prototype {
    EditText editText_IpAddress;
    EditText editText_Port;
    RadioGroup radioGroup_Protocol;
    RadioButton radioButton_UDP;
    RadioButton radioButton_TCP;
    EditText editText_Count;
    Spinner spinner_Count;
    Button button_Start;
    Button button_Stop;
    Switch switch_Running;
    TextView textView_Summary;
    TextView textView_Status;

    int count_send = 0;
    int count_send_complete = 0;
    int max_send = 0;
    boolean running = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_packet_simulator, container, false);

        defaultOperation();
        getComponent();

        return rootView;
    }

    public void getComponent(){
        editText_IpAddress = (EditText)rootView.findViewById(R.id.editText_IpAddress);
        editText_Port = (EditText)rootView.findViewById(R.id.editText_Port);
        radioGroup_Protocol = (RadioGroup)rootView.findViewById(R.id.radioGroup_Protocol);
        radioButton_UDP = (RadioButton)rootView.findViewById(R.id.radioButton_UDP);
        radioButton_TCP = (RadioButton)rootView.findViewById(R.id.radioButton_TCP);
        editText_Count = (EditText)rootView.findViewById(R.id.editText_Count);
        spinner_Count = (Spinner)rootView.findViewById(R.id.spinner_Count);
        button_Start = (Button)rootView.findViewById(R.id.button_Start);
        button_Stop = (Button)rootView.findViewById(R.id.button_Stop);
        switch_Running = (Switch)rootView.findViewById(R.id.switch_Running);
        textView_Summary = (TextView)rootView.findViewById(R.id.textView_Summary);
        textView_Status = (TextView)rootView.findViewById(R.id.textView_Status);


//        radioGroup_Protocol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.radioButton_UDP) {
////                    Toast.makeText(activity,"UDP",Toast.LENGTH_SHORT).show();
//                }else if(checkedId == R.id.radioButton_TCP){
////                    Toast.makeText(activity,"TCP",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        final String[] protocol = {"limit","unlimit"};
        ArrayAdapter<String> arrAd = new ArrayAdapter<String>(this.activity,android.R.layout.simple_spinner_item,protocol);
        arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Count.setAdapter(arrAd);
        switch_Running.setVisibility(View.INVISIBLE);
        spinner_Count.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(activity, protocol[position] + " : " + position, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    switch_Running.setVisibility(View.INVISIBLE);
                    button_Start.setVisibility(View.VISIBLE);
                    button_Stop.setVisibility(View.VISIBLE);
                    editText_Count.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    switch_Running.setVisibility(View.VISIBLE);
                    button_Start.setVisibility(View.INVISIBLE);
                    button_Stop.setVisibility(View.INVISIBLE);
                    editText_Count.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(activity, "none", Toast.LENGTH_SHORT).show();
            }
        });

        button_Start.setOnClickListener(this);
        button_Stop.setOnClickListener(this);
        switch_Running.setOnClickListener(this);

        //test
        editText_IpAddress.setText("192.168.99.100");
        editText_Port.setText("100");
        editText_Count.setText("100");

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Start:
                textView_Summary.setText("");
                textView_Status.setText("");
                disableUI();
                running = true;
                count_send = 0;
                max_send = Integer.parseInt(editText_Count.getText().toString());
                sendPacket(count_send + 1 + "");
                break;
            case R.id.button_Stop:
                enableUI();
                running = false;
                break;
            case R.id.switch_Running:
                if(switch_Running.isChecked() == true){
                    textView_Summary.setText("");
                    textView_Status.setText("");
                    switch_Running.setText("RUNNING");
                    disableUI();
                    running = true;
                    count_send = 0;
                    sendPacket(count_send + 1 + "");
                }else{
                    switch_Running.setText("STOPPED");
                    enableUI();
                    running = false;
                }
                break;
        }
    }

    public void disableUI(){
        editText_IpAddress.setFocusableInTouchMode(false);
        editText_IpAddress.setFocusable(false);
        editText_IpAddress.setTextColor(getResources().getColor(R.color.GRAY));
        editText_Port.setFocusableInTouchMode(false);
        editText_Port.setFocusable(false);
        editText_Port.setTextColor(getResources().getColor(R.color.GRAY));
        editText_Count.setFocusableInTouchMode(false);
        editText_Count.setFocusable(false);
        editText_Count.setTextColor(getResources().getColor(R.color.GRAY));
        spinner_Count.setClickable(false);
        spinner_Count.setEnabled(false);
        radioButton_UDP.setClickable(false);
        radioButton_UDP.setEnabled(false);
        radioButton_TCP.setClickable(false);
        radioButton_TCP.setEnabled(false);
        button_Start.setEnabled(false);
    }

    public void enableUI(){
        editText_IpAddress.setFocusableInTouchMode(true);
        editText_IpAddress.setFocusable(true);
        editText_IpAddress.setTextColor(getResources().getColor(R.color.BLACK));
        editText_Port.setFocusableInTouchMode(true);
        editText_Port.setFocusable(true);
        editText_Port.setTextColor(getResources().getColor(R.color.BLACK));
        editText_Count.setFocusableInTouchMode(true);
        editText_Count.setFocusable(true);
        editText_Count.setTextColor(getResources().getColor(R.color.BLACK));
        spinner_Count.setClickable(true);
        spinner_Count.setEnabled(true);
        radioButton_UDP.setClickable(true);
        radioButton_UDP.setEnabled(true);
        radioButton_TCP.setClickable(true);
        radioButton_TCP.setEnabled(true);
        button_Start.setEnabled(true);
    }

    public void sendPacket(String message){
        //Prepare parameter
        JSONObject data = new JSONObject();
        JSONObject data_frame = new JSONObject();
        try {
            data.put("message",message);
            data_frame.put("serverIP", editText_IpAddress.getText().toString());
            data_frame.put("serverPort", editText_Port.getText().toString());
            data_frame.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int selectedId = radioGroup_Protocol.getCheckedRadioButtonId();

        if(selectedId == radioButton_UDP.getId()){
            Toast.makeText(activity,"UDP",Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new UDP_Unicast_Send_PacketSimulator().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data_frame.toString());
            } else {
                new UDP_Unicast_Send_PacketSimulator().execute(data_frame.toString());
            }
        }else if(selectedId == radioButton_TCP.getId()){
            Toast.makeText(activity,"TCP",Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new TCP_Unicast_Send_PacketSimulator().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data_frame.toString());
            } else {
                new TCP_Unicast_Send_PacketSimulator().execute(data_frame.toString());
            }
        }
    }

    protected class UDP_Unicast_Send_PacketSimulator extends UDP_Unicast_Send {
        @Override
        protected void onPreExecute() {
            count_send++;
            log_Head = "TCP_Unicast_Send_PacketSimulator";
            super.onPreExecute();
        }

        protected void specificFunction(){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            textView_Status.append("Packet : " + message + "    " + result + "\n");
            if(running && count_send < max_send){
                sendPacket(count_send + 1 + "");
            }else{
                enableUI();
                running = false;
                textView_Summary.setText("Total Send : " + count_send + "\n");
            }
        }
    }



    protected class TCP_Unicast_Send_PacketSimulator extends TCP_Unicast_Send {
        @Override
        protected void onPreExecute() {
            count_send++;
            receiveData = true;
            log_Head = "TCP_Unicast_Send_PacketSimulator";
            super.onPreExecute();
        }

        protected void specificFunction(){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            textView_Status.append("Packet : " + message + "\n");
            textView_Status.append("Data Send  " + result + "\n");
            textView_Status.append("Data Receive " + data_receive + "\n");
            try {
                if(data_receive != null) {
                    textView_Status.append("send Complete ");
                    count_send_complete++;
                } else {
                    textView_Status.append("No result" + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(running && count_send < max_send){
                sendPacket(count_send + 1 + "");
            }else{
                enableUI();
                running = false;
                textView_Summary.setText("Total Send : " + count_send + "\n" +
                                         "Total Send Complete : " + count_send_complete + " , " + ((count_send_complete/count_send)*100) + "%" + "\n");
            }

        }
    }
}
