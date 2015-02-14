package lab.kultida.rescueteam;

/**
 * Created by ekapop on 14/12/2557.
 */

import android.support.v4.app.Fragment;
import android.view.View;

import lab.kultida.utility.DataBase;

public class PlaceholderFragment_Prototype extends Fragment implements View.OnClickListener{
    protected MainActivity activity;
    protected boolean debugging_mode = true;
    protected View rootView;
    protected String serverIP = "1.1.1.99";
    protected DataBase database;

    protected void defaultOperation(){
        setRetainInstance(true);
        activity = (MainActivity)getActivity();
        database = activity.database;
    }
    
    @Override
    public void onClick(View v) {

    }
}


