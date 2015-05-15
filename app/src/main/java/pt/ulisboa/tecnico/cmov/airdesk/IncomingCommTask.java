package pt.ulisboa.tecnico.cmov.airdesk;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class IncomingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {

    private SimWifiP2pSocketServer mSrvSocket;
    private GlobalClass mAppContext;

    public void setApplicationContext(GlobalClass appContext){
        mAppContext = appContext;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d("IncomingCommTask", "IncommingCommTask started (" + this.hashCode() + ").");
        try {
            mSrvSocket = new SimWifiP2pSocketServer(10001);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SimWifiP2pSocket sock = mSrvSocket.accept();
                Log.w("InCommTask", "Socket accepted");
                publishProgress(sock);

            } catch (IOException e) {
                Log.w("InCommTask","Error accepting socket:"+ e.getMessage());
                break;
                //e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(SimWifiP2pSocket... values) {
        ReceiveCommTask recCommTask = new ReceiveCommTask(mAppContext);
        recCommTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, values[0]);
        Log.w("InCommTask","Started new RecCommTask");
    }
}
