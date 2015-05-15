package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
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
public class ReceiveCommTaskThread implements Runnable {

    private GlobalClass mAppContext;
    private ActionBarActivity mActivity;

    private SimWifiP2pSocket mCliSocket;
    private String mDestIp;
    private String mResponse;

    private ActionBarActivity mCurrentActivity;

    public ReceiveCommTaskThread(GlobalClass appContext, ActionBarActivity activity, SimWifiP2pSocket sock) {
        mAppContext = appContext;
        mActivity = activity;
        mCliSocket = sock;
        Log.w("RecCommTask", "Constructed");
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        Log.w("RecCommTask", "Received socket");

        BufferedReader sockIn;
        String st;
        SharedPreferences userPrefs = mAppContext.getUserPrefs();

        //  MESSAGE FORMAT: VIRTIP;COMMAND;ARGS
        try {
            sockIn = new BufferedReader(new InputStreamReader(mCliSocket.getInputStream()));
            st = sockIn.readLine();
            Log.w("RecCommTask", st);
//            sockIn.close();
//            s.getOutputStream().write(("ACK" + "\n").getBytes());
            mCliSocket.close();
            final String[] splt = st.split(";");
            String response = mAppContext.getVirtualIp() + ";";
            mDestIp = splt[0];
            Log.w("RecCommTask", "Processing: " + splt[1]);
            if (splt[1].equals("WS_SHARED_LIST")) {
                // IP;COM;EMAIL;TAGS;
                mAppContext.addVirtIpByEmail(splt[2], splt[0]);
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
                mResponse = response;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new Thread(new OutgoingCommTaskThread(mAppContext, mActivity, mDestIp, mResponse))).start();

                    }
                });
                Log.w("RecCommTask", "Response sent");

            } else if (splt[1].equals("WS_SHARED_LIST_RESPONSE")) {
                // IP;COM;WS1;Q1;WS2;Q2;
                String wsOwnList = null;
                for (int i = 2; i < splt.length; i += 2) {
                    mAppContext.addForeignWorkspace(splt[i], Long.parseLong(splt[i + 1]));
                    mAppContext.addOwnersWs(splt[0], splt[i]);
                    mAppContext.addWsOwners(splt[i], splt[0]);
                    wsOwnList += splt[i];
                }
                mCurrentActivity = mAppContext.getCurrentActivity();
                if (mCurrentActivity instanceof ForeignWorkspacesListActivity) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ForeignWorkspacesListActivity) mCurrentActivity).updateLists();

                        }
                    });
                }


            } else if (splt[1].equals("WS_FILE_LIST")) {
                //recebe -> IP;WS_FILE_LIST;WSNAME;
                response += "WS_FILE_LIST_RESPONSE;" + splt[2] + ";";
                Set<String> fileNames = userPrefs.getStringSet(splt[2] + "_files", new HashSet<String>());

                for (String file : fileNames) {
                    response += file + ";";
                }
                Log.w("RecCommTask", "Sending response: " + response);
                mResponse = response;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new Thread(new OutgoingCommTaskThread(mAppContext, mActivity, mDestIp, mResponse))).start();
                    }
                });
                Log.w("RecCommTask", "Response sent");


            } else if (splt[1].equals("WS_FILE_LIST_RESPONSE")) {
                //recebe -> IP;WS_FILE_LIST_RESPONSE;WSNAME;FILENAME1;FILENAME2....;
                for (int i = 3; i < splt.length; i++) {
                    mAppContext.addOwnersWsFiles(splt[2], splt[i]);
                }
                mCurrentActivity = mAppContext.getCurrentActivity();
                if (mCurrentActivity instanceof ForeignWorkspaceActivity) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ForeignWorkspaceActivity) mCurrentActivity).updateFilesList();

                        }
                    });
                }

            } else if (splt[1].equals("WS_FILE_READ")) {
                //TODO PROVIDE GET_WS_FILE_RESPONSE


            } else if (splt[1].equals("WS_FILE_EDIT")) {
                //TODO PROVIDE WS_FILE_EDIT_AUTHORIZATION

            } else if (splt[1].equals("WHO_AM_I")) {
                // splt -> IP;COMMAND;EMAIL
                mAppContext.addVirtIpByEmail(splt[2], splt[0]);
            } else if (splt[1].equals("EMAIL_REMOVED_FROM_WS")) {

                mCurrentActivity = mAppContext.getCurrentActivity();
                if (mCurrentActivity instanceof ForeignWorkspacesListActivity) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ForeignWorkspacesListActivity) mCurrentActivity).requestForeignWNames();
                        }
                    });
                } else if (mCurrentActivity instanceof ForeignWorkspaceActivity) {
                    final Set<String> tagSet = new HashSet<String>();
                    for (int i = 2; i < splt.length; i++) {
                        tagSet.add(splt[i]);
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ForeignWorkspaceActivity) mCurrentActivity).keepWsOpen(tagSet);
                        }
                    });
                }

            } else if (splt[1].equals("EMAIL_ADDED_TO_WS")) {

                mCurrentActivity = mAppContext.getCurrentActivity();
                if (mCurrentActivity instanceof ForeignWorkspacesListActivity) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ForeignWorkspacesListActivity) mCurrentActivity).requestForeignWNames();
                        }
                    });
                }
            } else if(splt[1].equals("REFRESH_LIST")) {
                mCurrentActivity = mAppContext.getCurrentActivity();
                if (mCurrentActivity instanceof ForeignWorkspacesListActivity) {
                    String myTags = "";
                    for (String tag : mAppContext.getTagsList()) {
                        myTags += tag + ";";
                    }
                    final String msg_tags = mAppContext.getVirtualIp() + ";WS_SHARED_LIST;" + mAppContext.getLocalEmail() + ";" + myTags;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            (new Thread(new OutgoingCommTaskThread(mAppContext, mCurrentActivity, splt[0], msg_tags))).start();
                        }
                    });
                }
            }

            if (!mCliSocket.isClosed()) {
                mCliSocket.close();
            }
        } catch (IOException e) {
            Log.w("Error reading socket:", e.getMessage());

        } catch (Exception e) {
            Log.w("RecCommTask", "Exception: " + e.getMessage());
        }

    }
}
