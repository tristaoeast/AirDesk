package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;

public class SimWifiP2pBroadcastReceiverReadTextFileForeign extends SimWifiP2pBroadcastReceiver {

    private ReadTextFileActivityForeign mActivity;

    public SimWifiP2pBroadcastReceiverReadTextFileForeign(ReadTextFileActivityForeign actionBarActivity, GlobalClass appContext) {
        super(actionBarActivity, appContext);
        mActivity = actionBarActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       super.onReceive(context, intent);
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action))
            mActivity.isOwnerGone();
    }
}