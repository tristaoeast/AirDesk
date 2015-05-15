package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Hashtable;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;

public class ReadTextFileActivityForeign extends ReadTextFileActivity {

    private IntentFilter filter;
    private SimWifiP2pBroadcastReceiverReadTextFileForeign receiver;

    public void isOwnerGone(Set<String> devicesInNetwork) {
        String wsName = WORKSPACE_NAME;
        String owner;
        Hashtable<String, String> owners = mAppContext.getWsOwners();
        Log.w("ForeignActivity", "isOwnerGone; wsName: " + wsName);
        if (owners.containsKey(wsName)) {
            owner = owners.get(wsName);
            Log.w("ForeignActivity", "Owner: " + owner + " exists");

            //check peers and see if owner is still there
            if (!devicesInNetwork.contains(owner.toString())) {
                //go back to foreignactivitylist
                Intent intent = new Intent(getApplicationContext(), ForeignWorkspacesListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }
    }

    @Override
    public void registerSimWifiP2pBcastReceiver() {
        // register broadcast receiver
        filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        receiver = new SimWifiP2pBroadcastReceiverReadTextFileForeign(this, mAppContext);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSimWifiP2pBcastReceiver();
        mAppContext.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
