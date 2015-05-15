package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class OutgoingCommTask extends AsyncTask<String, Void, Void> {

    private Context mContext;

    public OutgoingCommTask() {
    }

    public OutgoingCommTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            SimWifiP2pSocket cliSocket = new SimWifiP2pSocket(params[0], 10001);
            Log.w("OutCommTask", "Got socket to: " + params[0]);
            cliSocket.getOutputStream().write((params[1] + "\n").getBytes());
            Log.w("OutCommTask", "Wrote message: " + params[1]);
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
        } finally {
            return null;
        }
    }
}
