package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
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
public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, String> {

    private GlobalClass mAppContext;

    SimWifiP2pSocket s;

    public ReceiveCommTask(GlobalClass appContext) {
        mAppContext = appContext;
        Log.w("RecCommTask", "Constructed");
    }

    @Override
    protected String doInBackground(SimWifiP2pSocket... params) {
        Log.w("RecCommTask", "Received socket");

        BufferedReader sockIn;
        String st;
        s = params[0];

        SharedPreferences userPrefs = mAppContext.getUserPrefs();

        //  MESSAGE FORMAT: VIRTIP;COMMAND;ARGS
        try {
            sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            st = sockIn.readLine();
            Log.w("RecCommTask", st);
//            sockIn.close();
//            s.getOutputStream().write(("ACK" + "\n").getBytes());
            s.close();
            String[] splt = st.split(";");
            String response = mAppContext.getVirtualIp() + ";";

            Log.w("RecCommTask", "Processing: " + splt[1]);
            if (splt[1].equals("WS_SHARED_LIST")) {
                // IP;COM;EMAIL;TAGS;
                response += "WS_SHARED_LIST_RESPONSE" + ";";
                String email = splt[2];
                Set<String> allOwnWS = userPrefs.getStringSet("All Owned Workspaces", new HashSet<String>());

                for (String ws : allOwnWS) {
                    Set<String> invitedUsers = userPrefs.getStringSet(ws + "_invitedUsers", new HashSet<String>());
                    Long quota = userPrefs.getLong(ws + "_quota", -1);
                    if (invitedUsers.contains(email)) {
                        response += ws + ";" + quota.toString() + ";";
                    }
                }
                Set<String> ownPublishedWs = userPrefs.getStringSet("Own Public Workspaces", new HashSet<String>());
                for (int i = 3; i < splt.length; i++) {
                    for (String ws : ownPublishedWs) {
                        Set<String> tags = userPrefs.getStringSet(ws + "_tags", new HashSet<String>());
                        String l = ws + ";";
                        for (String tag : tags) {
                            l += tag + ";";
                        }
                        Log.w("RecCommTask", l);
                        if (tags.contains(splt[i])) {
                            Long quota = userPrefs.getLong(ws + "_quota", -1);
                            response += ws + ";" + quota.toString() + ";";
                        }
                    }
                }
                Log.w("RecCommTask", "Sending response: " + response);
                publishProgress(splt[0], response);

                Log.w("RecCommTask", "Response sent");
                return "DO NOTHING";
            } else if (splt[1].equals("WS_SHARED_LIST_RESPONSE")) {
                // IP;COM;WS1;Q1;WS2;Q2;
                String wsOwnList = null;
                for (int i = 2; i < splt.length; i += 2) {
                    mAppContext.addForeignWorkspace(splt[i], Long.parseLong(splt[i + 1]));
                    mAppContext.addOwnersWs(splt[0], splt[i]);
                    wsOwnList += splt[i];
                }
                ActionBarActivity act = mAppContext.getCurrentActivity();
                if (act instanceof ForeignWorkspacesListActivity) {
//                    ((ForeignWorkspacesListActivity) act).updateLists();
                    return "UPDATE_FOREIGN_WS_LIST";
                }
                return "DO NOTHING";

//            } else if (splt[1].equals("WS_SUBSCRIBED_LIST")) {
//
//                //IP;COM;TAGS...
//                response += "WS_SUBSCRIBED_LIST_RESPONSE" + ";";
//                Set<String> ownPublishedWs = userPrefs.getStringSet("Own Public Workspaces", new HashSet<String>());
//                Log.w("RecCommTask", "ownPublishedWS isEmpty: " + ownPublishedWs.isEmpty());
//
//                for (int i = 2; i < splt.length; i++) {
//                    for (String ws : ownPublishedWs) {
//                        Set<String> tags = userPrefs.getStringSet(ws + "_tags", new HashSet<String>());
//                        String l = ws + ";";
//                        for (String tag : tags) {
//                            l += tag + ";";
//                        }
//                        Log.w("RecCommTask", l);
//                        if (tags.contains(splt[i])) {
//                            Long quota = userPrefs.getLong(ws + "_quota", -1);
//                            response += ws + ";" + quota.toString() + ";";
//                        }
//                    }
//                }
//                Log.w("RecCommTask", "Sending response: " + response);
//                publishProgress(splt[0], response);
////                new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, splt[0], response);
//                Log.w("RecCommTask", "Response sent");
//                return "DO NOTHING";
//            } else if (splt[1].equals("WS_SUBSCRIBED_LIST_RESPONSE")) {
//                String wsOwnList = null;
//                // IP;COM;WS1;Q1;WS2;Q2;
//                for (int i = 2; i < splt.length; i += 2) {
//                    mAppContext.addSubscribedWorkspace(splt[i], Long.parseLong(splt[i + 1]));
//                    mAppContext.addOwnersWs(splt[0], splt[i]);
//                    wsOwnList += splt[i];
//                }
//                mAppContext.addWsOwners(wsOwnList, splt[0]);
//                if (mAppContext.getCurrentActivity() instanceof ForeignWorkspacesListActivity) {
//                    Log.w("RecCommTask", "Current activity is a ForeignWSList");
////                    ((ForeignWorkspacesListActivity) act).updateLists();
//                    return "UPDATE_FOREIGN_WS_LIST";
//                }
//                return "DO NOTHING";
            } else if (splt[1].equals("WS_FILE_LIST")) {
                //recebe -> IP;WS_FILE_LIST;WSNAME;
                response += "WS_FILE_LIST_RESPONSE;" + splt[2] + ";";
                Set<String> fileNames = userPrefs.getStringSet(splt[2] + "_files", new HashSet<String>());

                for (String file : fileNames) {
                    response += file + ";";
                }
                publishProgress(splt[0], response);
                return "DO NOTHING";

            } else if (splt[1].equals("WS_FILE_LIST_RESPONSE")) {
                //recebe -> IP;WS_FILE_LIST_RESPONSE;WSNAME;FILENAME1;FILENAME2....;
                for (int i = 3; i < splt.length; i++) {
                    mAppContext.addOwnersWsFiles(splt[2], splt[i]);
                }
                if (mAppContext.getCurrentActivity() instanceof ForeignWorkspacesListActivity) {
                    Log.w("RecCommTask", "Current activity is a ForeignWSList");
//                    ((ForeignWorkspacesListActivity) act).updateLists();
                    return "UPDATE_FOREIGN_WS_LIST";
                }
                return "DO NOTHING";

            } else if (splt[1].equals("WS_FILE_READ")) {
                //TODO PROVIDE GET_WS_FILE_RESPONSE

                return "DO NOTHING";

            } else if (splt[1].equals("WS_FILE_EDIT")) {
                //TODO PROVIDE WS_FILE_EDIT_AUTHORIZATION

                return "DO NOTHING";
            }
            if (!s.isClosed()) {
                s.close();
            }
            return "DO NOTHING";
        } catch (IOException e) {
            Log.w("Error reading socket:", e.getMessage());
            return "DO NOTHING";

        } catch (Exception e) {
            Log.w("RecCommTask", "Exception: " + e.getMessage());
            return "DO NOTHING";
        }

    }

    @Override
    protected void onProgressUpdate(String... params) {
        new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params[0], params[1]);
        Log.w("RecCommTask", "Started new OutCommTask to: " + params[0] + " with message: " + params[1]);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.w("RecCommTask", "Finished 1");
        if (s.equals("UPDATE_FOREIGN_WS_LIST")) {
            ActionBarActivity act = mAppContext.getCurrentActivity();
            ((ForeignWorkspacesListActivity) act).updateLists();
        }
        Log.w("RecCommTask", "Finished 2");
    }
}
