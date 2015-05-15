package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;

public class SimWifiP2pBroadcastReceiverForeign extends SimWifiP2pBroadcastReceiver {

    private ForeignWorkspacesListActivity mActivity;
    private String mDestIp;
    private String mMsg;

    public SimWifiP2pBroadcastReceiverForeign(ForeignWorkspacesListActivity actionBarActivity, GlobalClass appContext) {
        super(actionBarActivity, appContext);
        mActivity = actionBarActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.equals(action)) {
//            mAppContext.getManager().requestGroupInfo(mAppContext.getChannel(), (SimWifiP2pManager.GroupInfoListener) SimWifiP2pBroadcastReceiverForeign.this);
        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {

        mAppContext.clearForeignWorkspaces();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.updateLists();
            }
        });
//        _peersStr.clear();
        if (mAppContext.isBound() && groupInfo.askIsConnected()) {
            if (groupInfo.askIsConnected())
                // compile list of network members
                if (mAppContext.getVirtualIp() == null) {
                    String myName = groupInfo.getDeviceName();
                    SimWifiP2pDevice myDevice = devices.getByName(myName);
                    if (myDevice != null)
                        mAppContext.setVirtualIp(myDevice.getVirtIp());
                }
            String myTags = "";
            for (String tag : mAppContext.getTagsList()) {
                myTags += tag + ";";
            }
            String msg_tags = mAppContext.getVirtualIp() + ";WS_SHARED_LIST;" + mAppContext.getLocalEmail() + ";" + myTags;
//            Log.w("ForeignList", msg_email);
//            Log.w("ForeignList", msg_tags);
            Integer s = groupInfo.getDevicesInNetwork().size();
            Log.w("BrCastFor", s.toString());
            for (String deviceName : groupInfo.getDevicesInNetwork()) {
                SimWifiP2pDevice device = devices.getByName(deviceName);
                String deviceIP = device.getVirtIp();
//                _peersStr.add(peer);
                mMsg = msg_tags;
                mDestIp = deviceIP;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new Thread(new OutgoingCommTaskThread(mAppContext, mAppContext.getCurrentActivity(), mDestIp, mMsg))).start();

                    }
                });

//                new OutgoingCommTask(mAppContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, deviceIP, msg_tags);
                Log.w("BrCastFor", "Mesg: " + msg_tags + " submitted to OutTask with destIP: " + deviceIP);

            }
        } else {
            Toast.makeText(mActBarActivity, "Not in a group or service not bound", Toast.LENGTH_LONG).show();
//            mAppContext.clearInvitedWorkspaces();
//            mAppContext.clearSubscribedWorkspaces();
//            mAppContext.clearForeignWorkspaces();
//            mActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mActivity.updateLists();
//                }
//            });

        }

    }
}