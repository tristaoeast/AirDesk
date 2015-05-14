package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {

    private GlobalClass mAppContext;

    SimWifiP2pSocket s;

    public void setApplicationContext(GlobalClass appContext) {
        mAppContext = appContext;
    }

    @Override
    protected Void doInBackground(SimWifiP2pSocket... params) {
        BufferedReader sockIn;
        String st;
        s = params[0];

        SharedPreferences userPrefs = mAppContext.getUserPrefs();

        //  MESSAGE FORMAT: VIRTIP;COMMAND;ARGS
        try {
            sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                while ((st = sockIn.readLine()) != null) {
//                    publishProgress(st);
//                }
            st = sockIn.readLine();
            sockIn.close();
            s.close();

            String[] splt = st.split(";");

            String response = mAppContext.getVirtualIp() + ";";

            if (splt[1].equals("WS_SHARED_LIST")) {
                // VIRTIP;WS_SHARED_LIST;EMAIL
                //TODO PROVIDE WS_SHARED_LIST_RESPONSE
                response += "WS_SHARED_LIST_RESPONSE" + ";";
                String email = splt[2];
                Set<String> allOwnWS = userPrefs.getStringSet("All Owned Workspaces", new HashSet<String>());

                for (String ws : allOwnWS) {
                    Set<String> invitedUsers = userPrefs.getStringSet(ws + "_invitedUsers", new HashSet<String>());
                    Long quota = userPrefs.getLong(ws + "_quota", -1);
                    if (invitedUsers.contains(email)) {
                        response += email + ";" + quota.toString() + ";";
                    }
                }
                new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, splt[0], response);

            }else if(splt[1].equals("WS_SHARED_LIST_RESPONSE")){

            } else if (splt[1].equals("WS_SUBSCRIBED_LIST")) {
                //TODO PROVIDE WS_SUBSCRIBED_LIST_RESPONSE
            } else if (splt[1].equals("WS_FILE_LIST")) {
                //TODO PROVIDE WS_FILE_LIST_RESPONSE
            } else if (splt[1].equals("WS_FILE_READ")) {
                //TODO PROVIDE GET_WS_FILE_RESPONSE
            } else if (splt[1].equals("WS_FILE_EDIT")) {
                //TODO PROVIDE WS_FILE_EDIT_AUTHORIZATION
            }
//            s.close();
        } catch (
                IOException e
                )

        {
            Log.d("Error reading socket:", e.getMessage());
        }

        return null;
    }

}
