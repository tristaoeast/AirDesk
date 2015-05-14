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

    private GlobalClass mAppContext;

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
        mAppContext = (GlobalClass) getApplicationContext();
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

        registerSimWifiP2pBcastReceiver();

        setupTagsList();
        setupWsList();
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
        updateLists();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
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
        if (mAppContext.getVirtualIp() == null) {
            String myName = groupInfo.getDeviceName();
            SimWifiP2pDevice myDevice = devices.getByName(myName);
            mAppContext.setVirtualIp(myDevice.getVirtIp());
        }


        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = device.getVirtIp();
            _peersStr.add(devstr);
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
        _wsNamesList.clear();
        //Set<String> wsNames = _userPrefs.getStringSet(getString(R.string.foreign_workspaces_list), new HashSet<String>());
        //TODO pedir nomes de ws aos peers em vez de ir buscar aos sharedprefs
        if (mAppContext.isBound()) {
            mAppContext.getManager().requestGroupInfo(mAppContext.getChannel(), (SimWifiP2pManager.GroupInfoListener) ForeignWorkspacesListActivity.this);

            String myTags = "";
            for (String tag : _tagsList) {
                myTags += ";" + tag;
            }
            String msg_tags = "WS_SUBSCRIBED_LIST;" + myTags;
            String msg_email = "WS_SHARED_LIST;" + LOCAL_EMAIL;
            for (String peer : _peersStr) {
                new OutgoingCommTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, peer, msg_tags);
                new OutgoingCommTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, peer, msg_email);
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
