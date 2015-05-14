package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by mariana on 08-04-2015.
 */
public class ForeignWorkspacesListActivity extends ActionBarActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    public static final String TAG = "ForeignWSListActivity";

    private boolean keepListening;

    private boolean justCreated;
    private IntentFilter filter;
    private SimWifiP2pBroadcastReceiverForeign receiver;
    private boolean mBound;

    private ArrayList<String> _peersStr;

    private ActionBarDrawerToggle _drawerToggle;

    private ArrayList<String> _wsNamesList;
    private ArrayList<String> _wsPermissionsList;

    private ArrayAdapter<String> _wsNamesAdapter;
    private ListView _listView;

    protected SharedPreferences _userPrefs;
    protected SharedPreferences _appPrefs;

    private String _username;
    private String _email;

    protected ArrayList<String> _tagsList;
    protected ArrayAdapter<String> _tagsAdapter;
    protected ListView _tagsListView;

    protected SharedPreferences.Editor _appPrefsEditor;
    protected SharedPreferences.Editor _userPrefsEditor;


    protected ActionBarActivity SUBCLASS_LIST_ACTIVITY;

    protected Context SUBCLASS_CONTEXT;

    protected File _appDir;

    protected String LOCAL_EMAIL;
    protected String LOCAL_USERNAME;

    private String teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_workspaces_list);
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        LOCAL_USERNAME = _appPrefs.getString("username", "");
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _userPrefsEditor = _userPrefs.edit();
        SUBCLASS_LIST_ACTIVITY = this;
        SUBCLASS_CONTEXT = this;
        NavigationDrawerSetupHelper nh = new NavigationDrawerSetupHelper(SUBCLASS_LIST_ACTIVITY, SUBCLASS_CONTEXT);
        _drawerToggle = nh.setup();
        _appDir = new File(getApplicationContext().getFilesDir(), LOCAL_EMAIL);
        if (!_appDir.exists())
            _appDir.mkdir();

        setupTagsList();

        justCreated = true;

        keepListening = true;
        initSimWifiP2p();
        registerSimWifiP2pBcastReceiver();
        bindSimWifiP2pService();
        new IncommingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Toast.makeText(this,teste,Toast.LENGTH_LONG).show();

        setupWsList();
    }

    public void bindSimWifiP2pService() {
        Intent intent = new Intent(this, SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    public void initSimWifiP2p() {
        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());
    }

    public void registerSimWifiP2pBcastReceiver() {
        // register broadcast receiver
        filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        receiver = new SimWifiP2pBroadcastReceiverForeign(this);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (justCreated)
//            justCreated = false;
//        else {
//            initSimWifiP2p();
//            registerSimWifiP2pBcastReceiver();
//            bindSimWifiP2pService();
//        }

        keepListening = true;
        initSimWifiP2p();
        registerSimWifiP2pBcastReceiver();
        bindSimWifiP2pService();
        new IncommingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setupWsList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "Stopping Foreign Activity", Toast.LENGTH_LONG).show();
        unregisterReceiver(receiver);
        keepListening = false;
        try {
            mSrvSocket.close();
            mSrvSocket = null;
        } catch (IOException e) {
            Log.d("mSrvSocket close err", e.getMessage());
        }
    }


    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }

        // display list of devices in range
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {
        _peersStr.clear();
        // compile list of network members

        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = device.getVirtIp();
            _peersStr.add(devstr);
        }
    }

    private Messenger mService;
    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel;
    private ServiceConnection mConnection = new ServiceConnection() {

        /**
         * Called when a connection to the Service has been established, with
         * the {@link android.os.IBinder} of the communication channel to the
         * Service.
         *
         * @param name    The concrete component name of the service that has
         *                been connected.
         * @param service The IBinder of the Service's communication channel,
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
            teste = "Service Connected";
        }

        /**
         * Called when a connection to the Service has been lost.  This typically
         * happens when the process hosting the service has crashed or been killed.
         * This does <em>not</em> remove the ServiceConnection itself -- this
         * binding to the service will remain active, and you will receive a call
         * to {@link #onServiceConnected} when the Service is next running.
         *
         * @param name The concrete component name of the service whose
         *             connection has been lost.
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
            teste = "Service DisConnected";
        }
    };

    private SimWifiP2pSocketServer mSrvSocket = null;
    private ReceiveCommTask mComm = null;
    private SimWifiP2pSocket mCliSocket = null;

    public class IncommingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            while (true) {
                try {
                    mSrvSocket = new SimWifiP2pSocketServer(
                            Integer.parseInt(getString(R.string.port)));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            while (keepListening) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    if (mCliSocket != null && mCliSocket.isClosed()) {
                        mCliSocket = null;
                    }
                    if (mCliSocket != null) {
                        Log.d(TAG, "Closing accepted socket because mCliSocket still active.");
                        sock.close();
                    } else {
                        publishProgress(sock);
                    }
                } catch (IOException e) {
                    Log.d("Error accepting socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(SimWifiP2pSocket... values) {
            mCliSocket = values[0];
            mComm = new ReceiveCommTask();

            mComm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCliSocket);
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //mTextOutput.setText("Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SimWifiP2pSocket cliSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(getString(R.string.port)));

                try {

                    cliSocket.getOutputStream().write((params[1] + "\n").getBytes());
                    cliSocket.getInputStream().read();
                    cliSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }
    }

    public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
        SimWifiP2pSocket s;

        @Override
        protected Void doInBackground(SimWifiP2pSocket... params) {
            BufferedReader sockIn;
            String st;

            s = params[0];
            try {
                sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));

                while ((st = sockIn.readLine()) != null) {
                    publishProgress(st);
                }
            } catch (IOException e) {
                Log.d("Error reading socket:", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            /*mTextOutput.setText("");
            findViewById(R.id.idSendButton).setEnabled(true);
            findViewById(R.id.idDisconnectButton).setEnabled(true);
            findViewById(R.id.idConnectButton).setEnabled(false);
            mTextInput.setHint("");
            mTextInput.setText("");*/

        }

        @Override
        protected void onProgressUpdate(String... values) {
            //mTextOutput.append(values[0]+"\n");
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!s.isClosed()) {
                try {
                    s.close();
                } catch (Exception e) {
                    Log.d("Error closing socket:", e.getMessage());
                }
            }
            s = null;
            if (mBound) {
                //guiUpdateDisconnectedState();
            } else {
                //guiUpdateInitState();
            }
        }

    }

//    protected void setupSuper() {
//        FOREIGN_WORKSPACE_LIST_LAYOUT = R.layout.activity_foreign_workspaces_list;
//        FOREIGN_WORKSPACE_DIR = R.string.foreign_workspaces_dir;
//        FOREIGN_WORKSPACES_LIST = R.string.foreign_workspaces_list;
//        FOREIGN_WORKSPACES_PERMISSIONS_CRITERIA = R.string.foreign_workspaces_tag_crit;
//        SUBCLASS_LIST_ACTIVITY = this;
//        SUBCLASS_ACTIVITY_CLASS = ForeignWorkspacesListActivity.class;
//        SUBCLASS_CONTEXT = this;
//    }

    // A Hack done in order to ensure the correct behaviour of the back button,
    // since the main activity automatically redirects to this one
    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
        _userPrefs.edit().putBoolean(getString(R.string.event_back_button_pressed), true).commit();
        super.onBackPressed();
    }

    protected void setupTagsList() {
        _tagsList = new ArrayList<String>();
        _tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, _tagsList);
//        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
//        final View customView = inflater.inflate(R.layout.activity_own_published_workspace, null);
        _tagsListView = (ListView) findViewById(R.id.lv_tags);
        _tagsListView.setAdapter(_tagsAdapter);
        Set<String> tags = _userPrefs.getStringSet(getString(R.string.foreign_subscribed_workspaces) + "_tags", new HashSet<String>());
        for (String tag : tags) {
            _tagsList.add(tag);
        }
        Collections.sort(_tagsList);
        _tagsAdapter.notifyDataSetChanged();
    }


    protected void setupWsList() {
        _username = _appPrefs.getString("username", "invalid username");
        _email = _appPrefs.getString("email", "invalid email");
        _wsNamesList = new ArrayList<String>();
        _peersStr = new ArrayList<String>();
        _wsPermissionsList = new ArrayList<String>();
        _wsNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, _wsNamesList);
        // Get ListView object from xml
        _listView = (ListView) findViewById(R.id.lv_wsList);
        _listView.setAdapter(_wsNamesAdapter);

        updateLists();

        Collections.sort(_wsNamesList);
        _wsNamesAdapter.notifyDataSetChanged();


        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String wsName = _wsNamesList.get(position);
                Intent intent = new Intent(ForeignWorkspacesListActivity.this, ForeignWorkspaceActivity.class);
                intent.putExtra("workspace_name", wsName);
                startActivity(intent);
            }
        });
    }

    protected void updateLists() {
        //_wsNamesList.clear();
        //Set<String> wsNames = _userPrefs.getStringSet(getString(R.string.foreign_workspaces_list), new HashSet<String>());
        //TODO pedir nomes de ws aos peers em vez de ir buscar aos sharedprefs
        if (mBound) {
            mManager.requestGroupInfo(mChannel, (SimWifiP2pManager.GroupInfoListener) ForeignWorkspacesListActivity.this);

            String myTags = "";
            for (String tag : _tagsList){
                myTags += ";" + tag;
            }
            String msg_tags = "WS_SUBSCRIBED_LIST;" + myTags;

            String msg_email = "WS_SHARED_LIST;" + LOCAL_EMAIL;

            for (String peer : _peersStr) {
                new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, peer ,msg_tags);
                new OutgoingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, peer, msg_email );
            }
        } else
            Toast.makeText(this, "Service not bound", Toast.LENGTH_LONG).show();

        /*
        Set<String> privateWorkspaces = _userPrefs.getStringSet(getString(R.string.own_private_workspaces_list), new HashSet<String>());
        Set<String> publicWorkspaces = _userPrefs.getStringSet(getString(R.string.own_public_workspaces_list), new HashSet<String>());

        //For private Ws
        for (String wsName : privateWorkspaces) {

            Set<String> invitedUsersListPrivateWs = _userPrefs.getStringSet(wsName + "_invitedUsers", new HashSet<String>());
            for (String email : invitedUsersListPrivateWs) {
                if (_email.equalsIgnoreCase(email)) {
                    _wsNamesList.add(wsName);
                    //_userPrefsEditor.putStringSet(getString(R.string.foreign_workspaces_list), wsNames);
                }
            }

        }
        //For public WS
        for (String wsName : publicWorkspaces) {
            Set<String> invitedUsersListPublicWs = _userPrefs.getStringSet(wsName + "_invitedUsers", new HashSet<String>());
            for (String email : invitedUsersListPublicWs) {
                if (_email.equalsIgnoreCase(email)) {
                    _wsNamesList.add(wsName);
                    //_userPrefsEditor.putStringSet(getString(R.string.foreign_workspaces_list), wsNames);
                }
            }
        }
        if (_wsNamesList.isEmpty()) {
            Toast.makeText(this, _username + ", you have no foreign workspaces being shared with you at the moment", Toast.LENGTH_LONG);
        }
        Collections.sort(_wsNamesList);
        _wsNamesAdapter.notifyDataSetChanged();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.foreign_workspaces_list));
        updateLists();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        _drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            _appPrefs.edit().putBoolean("firstRun", true).commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        // Activate the navigation drawer toggle
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        _userPrefs.edit().commit();
    }

    public void editTags(final View view) {

        final HashSet<String> removedTagsSet = new HashSet<String>();
        final HashSet<String> addedTagsSet = new HashSet<String>();

        LayoutInflater inflater = LayoutInflater.from(ForeignWorkspacesListActivity.this);
        final View customView = inflater.inflate(R.layout.dialog_edit_tags, null);

        // Set tags list and button behaviour
        Button bt_add_tag = (Button) customView.findViewById(R.id.bt_add_tag);
        Set<String> tags = _userPrefs.getStringSet(getString(R.string.foreign_subscribed_workspaces) + "_tags", new HashSet<String>());
        final ListView lv_tags = (ListView) customView.findViewById(R.id.lv_tags);
        lv_tags.setAdapter(_tagsAdapter);
        _tagsAdapter.notifyDataSetChanged();

        bt_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_tags = (EditText) customView.findViewById(R.id.et_tags);
                String tag = et_tags.getText().toString().trim();
                if (tag.isEmpty())
                    Toast.makeText(ForeignWorkspacesListActivity.this, "Insert a tag.", Toast.LENGTH_LONG).show();
                else if (_tagsList.contains(tag))
                    Toast.makeText(ForeignWorkspacesListActivity.this, "Tag already exists.", Toast.LENGTH_LONG).show();
                else {
                    _tagsList.add(tag);
//                    tagsSet.add(tag);
                    addedTagsSet.add(tag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                    et_tags.setText("");
                }
            }
        });

        // This is used to refresh the position of the list
        lv_tags.post(new Runnable() {
            @Override
            public void run() {
                lv_tags.smoothScrollToPosition(0);
            }
        });

        // Event Listener that removes tags when clicked
        lv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removedTagsSet.add(_tagsList.get(position));
                _tagsList.remove(position);
                _tagsAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tags?");
        builder.setView(customView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                if (_tagsList.isEmpty()) {
//                    Toast.makeText(OwnPublicWorkspaceActivity.this, "At least one tag must be added.", Toast.LENGTH_LONG).show();
//                    editTags(view);
//                    return;
//                }

                HashSet<String> newTagsSet = new HashSet<String>(_tagsList);
                _userPrefs.edit().putStringSet(getString(R.string.foreign_subscribed_workspaces) + "_tags", newTagsSet).commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (String removedTag : removedTagsSet) {
                    _tagsList.add(removedTag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                }
                for (String removedTag : addedTagsSet) {
                    _tagsList.remove(removedTag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                }

//                if (_tagsList.isEmpty()) {
//                    Toast.makeText(OwnPublicWorkspaceActivity.this, "At least one tag must be added.", Toast.LENGTH_LONG).show();
//                    editTags(view);
//                    return;
//                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
