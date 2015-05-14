package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class OutgoingCommTask extends AsyncTask<String, Void, String> {

    private Context mContext;

    public OutgoingCommTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            SimWifiP2pSocket cliSocket = new SimWifiP2pSocket(params[0], 10001);
            cliSocket.getOutputStream().write((params[1] + "\n").getBytes());
            cliSocket.getInputStream().read();
            cliSocket.close();
        } catch (UnknownHostException e) {
            return "Unknown Host:" + e.getMessage();
        } catch (IOException e) {
            return "IO error:" + e.getMessage();
        }
        return null;
    }
}
