package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.IOException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class IncomingCommTaskThread implements Runnable {

    private SimWifiP2pSocketServer mSrvSocket;
    private GlobalClass mAppContext;
    private ActionBarActivity mActivity;
    private SimWifiP2pSocket mCliSocket;

    public IncomingCommTaskThread(GlobalClass appContext, ActionBarActivity activity) {
        mAppContext = appContext;
        mActivity = activity;
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        Log.d("IncomingCommTask", "IncommingCommTask started (" + this.hashCode() + ").");
        try {
            mSrvSocket = new SimWifiP2pSocketServer(10001);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
//                SimWifiP2pSocket sock = mSrvSocket.accept();
                mCliSocket = mSrvSocket.accept();
                Log.w("InCommTask", "Socket accepted");

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new Thread(new ReceiveCommTaskThread(mAppContext, mActivity, mCliSocket))).start();
                    }
                });

            } catch (IOException e) {
                Log.w("InCommTask", "Error accepting socket:" + e.getMessage());
                break;
                //e.printStackTrace();
            }
        }
    }
}
