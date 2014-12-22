package lab.kultida.utility;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCP_Unicast_Send extends AsyncTask<String, Void, String> {

    protected String log_Head = "";
    protected String data_receive = null;
    protected boolean receiveData = false;

    @Override
    protected void onPreExecute() {
        // receiveData
        // log_Head
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        Socket socket = null;
        DataOutputStream output = null;
        try{
            // Initial condition
            JSONObject data_frame = new JSONObject(arg0[0]);
            Log.d(log_Head + " - doInBackground","data_frame : " + data_frame);
            InetAddress serverIP = InetAddress.getByName(data_frame.getString("serverIP"));
            int serverPort = Integer.parseInt(data_frame.getString("serverPort_CheckHotspotInformation"));
            String data = data_frame.getJSONObject("data").toString();
            Log.d(log_Head + " - doInBackground","serverIP : " + data_frame.getString("serverIP"));
            Log.d(log_Head + " - doInBackground","serverPort : " + serverPort);
            Log.d(log_Head + " - doInBackground","send data : " + data);

            // open socket
            Log.d(log_Head + " - doInBackground","open socket");
            socket = new Socket(serverIP,serverPort);
            output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF(data);
            output.flush();

            Log.d(log_Head + " - doInBackground", "receiveData : " + receiveData);
            if(receiveData){
                DataInputStream input = new DataInputStream(socket.getInputStream());
                while(input.available() > 0){
                    data_receive = data_receive + input.readUTF();
                }
                Log.d(log_Head + " - doInBackground", "data_receive : " + data_receive);
            }

            output.close();
            socket.close();
            return "Success : " + data;
        }catch (Exception e){
            e.printStackTrace();
            try {
                if(output != null) output.close();
                if(socket != null) socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return "Fail";
        }
    }

    @Override
    protected void onPostExecute(String result) {}

}
