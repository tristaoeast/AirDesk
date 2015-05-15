package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;

public class SimWifiP2pBroadcastReceiverForeignActivity extends SimWifiP2pBroadcastReceiver {

    private ForeignWorkspaceActivity mActivity;

    public SimWifiP2pBroadcastReceiverForeignActivity(ForeignWorkspaceActivity actionBarActivity, GlobalClass appContext) {
        super(actionBarActivity, appContext);
        mActivity = actionBarActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       super.onReceive(context, intent);
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
            //check if the owner of the ws I'm in has left! If so, get back to the foreign activity list,
            //if not let it be

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            mActivity.isOwnerGone(ginfo.getDevicesInNetwork());
        }
    }
}