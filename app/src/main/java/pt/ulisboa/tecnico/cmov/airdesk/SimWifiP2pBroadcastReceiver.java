package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import java.util.ArrayList;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver implements SimWifiP2pManager.GroupInfoListener{

    protected ActionBarActivity mActBarActivity;
    protected GlobalClass mAppContext;
    protected ArrayList<String> _peersStr;

    public SimWifiP2pBroadcastReceiver(ActionBarActivity actBarActivity, GlobalClass appContext) {
        super();
        this.mActBarActivity = actBarActivity;
        mAppContext = appContext;
        _peersStr = new ArrayList<String>();
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

            //TODO: if GO - check uptodate groupmember list and compare with groupinfo and verify if
            //there are new members or lost members

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();
            Toast.makeText(mActBarActivity, "Network membership changed",
                    Toast.LENGTH_SHORT).show();

            if (mAppContext.isBound()) {
                mAppContext.getManager().requestGroupInfo(mAppContext.getChannel(), (SimWifiP2pManager.GroupInfoListener) SimWifiP2pBroadcastReceiver.this);

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

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
            ginfo.print();
            Toast.makeText(mActBarActivity, "Group ownership changed",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
        _peersStr.clear();
        // compile list of network members
        if (mAppContext.getVirtualIp() == null) {
            String myName = simWifiP2pInfo.getDeviceName();
            SimWifiP2pDevice myDevice = simWifiP2pDeviceList.getByName(myName);
            if(myDevice != null)
                mAppContext.setVirtualIp(myDevice.getVirtIp());
        }


        for (String deviceName : simWifiP2pInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = simWifiP2pDeviceList.getByName(deviceName);
            String devstr = device.getVirtIp();
            _peersStr.add(devstr);
        }
    }
}
