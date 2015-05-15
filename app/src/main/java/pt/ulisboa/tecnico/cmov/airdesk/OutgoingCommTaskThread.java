package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class OutgoingCommTaskThread implements Runnable {

    private GlobalClass mAppContext;
    private ActionBarActivity mActivity;
    private String mDestIp;
    private String mMsg;

    public OutgoingCommTaskThread() {
    }

    public OutgoingCommTaskThread(GlobalClass appContext, ActionBarActivity activity, String destIP, String msg) {
        mActivity = activity;
        mAppContext = appContext;
        mDestIp = destIP;
        mMsg = msg;
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        try {
            SimWifiP2pSocket cliSocket = new SimWifiP2pSocket(mDestIp, 10001);
            Log.w("OutCommTask", "Got socket to: " + mDestIp);
            cliSocket.getOutputStream().write((mMsg + "\n").getBytes());
            Log.w("OutCommTask", "Wrote message: " + mMsg);
            cliSocket.getInputStream().read();
            Log.w("OutCommTask", "Read unblocked");
            cliSocket.close();
            Log.w("OutCommTask", "Socket closed");
        } catch (UnknownHostException e) {
            Log.w("OutCommTask", "Unknown Host: " + e.getMessage());
//            return "Unknown Host:" + e.getMessage();
        } catch (IOException e) {
            Log.w("OutCommTask", "IOException: " + e.getMessage());
//            return "IO error:" + e.getMessage();
        } catch (Exception e) {
            Log.w("OutCommTask", "Exception: " + e.getMessage());
        }
    }


}
