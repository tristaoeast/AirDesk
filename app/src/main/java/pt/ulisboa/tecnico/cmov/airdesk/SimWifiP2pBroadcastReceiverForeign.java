package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;

public class SimWifiP2pBroadcastReceiverForeign extends SimWifiP2pBroadcastReceiver {

    private ForeignWorkspacesListActivity mActivity;

    public SimWifiP2pBroadcastReceiverForeign(ForeignWorkspacesListActivity actionBarActivity, GlobalClass appContext) {
        super(actionBarActivity, appContext);
        mActivity = actionBarActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // This action is triggered when the WDSim service changes state:
            // - creating the service generates the WIFI_P2P_STATE_ENABLED event
            // - destroying the service generates the WIFI_P2P_STATE_DISABLED event

            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(mActBarActivity, "WiFi Direct enabled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActBarActivity, "WiFi Direct disabled",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            Toast.makeText(mActBarActivity, "Peer list changed",
                    Toast.LENGTH_SHORT).show();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();
            Toast.makeText(mActBarActivity, "Network membership changed",
                    Toast.LENGTH_SHORT).show();

            if (mAppContext.isBound()) {
                mAppContext.getManager().requestGroupInfo(mAppContext.getChannel(), (SimWifiP2pManager.GroupInfoListener) SimWifiP2pBroadcastReceiverForeign.this);

                String myTags = "";
                for (String tag : mAppContext.getTagsList()) {
                    myTags += ";" + tag;
                }
                String msg_tags = mAppContext.getVirtualIp() + ";WS_SUBSCRIBED_LIST;" + myTags;
                String msg_email = mAppContext.getVirtualIp() + ";WS_SHARED_LIST;" + mAppContext.getLocalEmail();
                for (String peer : _peersStr) {
                    new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, peer, msg_tags);
                    new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, peer, msg_email);
                }
            } else
                Toast.makeText(mActBarActivity, "Service not bound", Toast.LENGTH_LONG).show();

            mActivity.updateLists();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();
            Toast.makeText(mActBarActivity, "Group ownership changed",
                    Toast.LENGTH_SHORT).show();
        }

    }
}