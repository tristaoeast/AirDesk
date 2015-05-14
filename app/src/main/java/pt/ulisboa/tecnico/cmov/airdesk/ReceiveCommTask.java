package pt.ulisboa.tecnico.cmov.airdesk;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {

    private GlobalClass mAppContext;

    SimWifiP2pSocket s;

    public void setApplicationContext(GlobalClass appContext){
        mAppContext = appContext;
    }

    @Override
    protected Void doInBackground(SimWifiP2pSocket... params) {
        BufferedReader sockIn;
        String st;
        s = params[0];
        try {
            sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                while ((st = sockIn.readLine()) != null) {
//                    publishProgress(st);
//                }
            st = sockIn.readLine();

            String[] splt = st.split(";");

            if (splt[0].equals("WS_SHARED_LIST")) {
                //TODO PROVIDE WS_SHARED_LIST_RESPONSE

//                String email = splt[1];
//                Set<String> allOwnWS = _userPrefs.getStringSet(getString(R.string.own_all_workspaces_list), null);
//                if (null != allOwnWS) {
//                    for (String ws : allOwnWS) {
//
//                    }
//                }

            } else if (splt[0].equals("WS_SUBSCRIBED_LIST")) {
                //TODO PROVIDE WS_SUBSCRIBED_LIST_RESPONSE
            } else if (splt[0].equals("WS_FILE_LIST")) {
                //TODO PROVIDE WS_FILE_LIST_RESPONSE
            } else if (splt[0].equals("WS_FILE_READ")) {
                //TODO PROVIDE GET_WS_FILE_RESPONSE
            } else if (splt[0].equals("WS_FILE_EDIT")) {
                //TODO PROVIDE WS_FILE_EDIT_AUTHORIZATION
            }
            s.close();
        } catch (IOException e) {
            Log.d("Error reading socket:", e.getMessage());
        }
        return null;
    }

}
