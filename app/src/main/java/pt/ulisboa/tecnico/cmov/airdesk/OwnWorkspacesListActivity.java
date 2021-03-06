package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;

/**
 * Created by ist167092 on 24-03-2015.
 */
public abstract class OwnWorkspacesListActivity extends ActionBarActivity implements SimWifiP2pManager.GroupInfoListener {

    public static final String TAG = "OwnWSListActivity";

    protected ActionBarDrawerToggle _drawerToggle;
    protected ArrayList<String> _wsNamesList;
    protected ArrayAdapter<String> _wsNamesAdapter;
    protected ListView _listView;
    protected SharedPreferences _appPrefs;
    protected SharedPreferences _userPrefs;
    protected File _appDir;
    protected SharedPreferences.Editor _appPrefsEditor;
    protected SharedPreferences.Editor _userPrefsEditor;

    protected int OWN_WORKSPACE_LIST_LAYOUT;
    protected int OWN_WORKSPACE_DIR;
    protected int OWN_WORKSPACES_LIST;
    protected int NEW_WORKSPACE_DIALOG_LAYOUT;
    protected ActionBarActivity SUBCLASS_LIST_ACTIVITY;
    protected Class SUBCLASS_ACTIVITY_CLASS;
    protected Context SUBCLASS_CONTEXT;

    protected String LOCAL_EMAIL;
    protected String LOCAL_USERNAME;

    private IntentFilter filter;
    private SimWifiP2pBroadcastReceiver receiver;

    protected GlobalClass mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(OWN_WORKSPACE_LIST_LAYOUT);
        mAppContext = (GlobalClass) getApplicationContext();
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        LOCAL_USERNAME = _appPrefs.getString("username", "");
        Log.d("WS_LIST_ACTIVITY_EMAIL", LOCAL_EMAIL);
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        mAppContext.setUserPrefs(_userPrefs);
        mAppContext.setLocalEmail(LOCAL_EMAIL);
        mAppContext.setLocalUsername(LOCAL_USERNAME);

        _userPrefsEditor = _userPrefs.edit();
        setupWsList();
        NavigationDrawerSetupHelper nh = new NavigationDrawerSetupHelper(SUBCLASS_LIST_ACTIVITY, SUBCLASS_CONTEXT);
        _drawerToggle = nh.setup();
        _appDir = new File(getApplicationContext().getFilesDir(), LOCAL_EMAIL);
        if (!_appDir.exists())
            _appDir.mkdir();


    }


    public void registerSimWifiP2pBcastReceiver() {
        // register broadcast receiver
        filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        receiver = new SimWifiP2pBroadcastReceiver(this, mAppContext);
        registerReceiver(receiver, filter);
    }

    protected void setupSuper(int wsListLayout, int wsDir, int wsList, int wsDL, ActionBarActivity subClassListActivity, Class subClassActivity, Context subClassContext) {
        OWN_WORKSPACE_LIST_LAYOUT = wsListLayout;
        OWN_WORKSPACE_DIR = wsDir;
        OWN_WORKSPACES_LIST = wsList;
        NEW_WORKSPACE_DIALOG_LAYOUT = wsDL;
        SUBCLASS_LIST_ACTIVITY = subClassListActivity;
        SUBCLASS_ACTIVITY_CLASS = subClassActivity;
        SUBCLASS_CONTEXT = subClassContext;

    }

    // A Hack done in order to ensure the correct behaviour of the back button,
    // since the main activity automatically redirects to this one
    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
        _appPrefs.edit().putBoolean(getString(R.string.event_back_button_pressed), true).commit();
        super.onBackPressed();
    }

    protected void setupWsList() {
        _wsNamesList = new ArrayList<String>();
        _wsNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, _wsNamesList);
        // Get ListView object from xml
        _listView = (ListView) findViewById(R.id.lv_wsList);
        _listView.setAdapter(_wsNamesAdapter);
//        Toast.makeText(SUBCLASS_CONTEXT, "OWN_WORKSPACES_LIST: " + getString(OWN_WORKSPACES_LIST), Toast.LENGTH_LONG).show();

        Set<String> wsNames = _userPrefs.getStringSet(getString(OWN_WORKSPACES_LIST), new HashSet<String>());
        for (String wsName : wsNames) {
//            Toast.makeText(SUBCLASS_CONTEXT, "ws Name added: " + wsName, Toast.LENGTH_LONG).show();
            _wsNamesList.add(wsName);
        }
        Collections.sort(_wsNamesList);
        _wsNamesAdapter.notifyDataSetChanged();

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String wsName = _wsNamesList.get(position);
                Intent intent = new Intent(SUBCLASS_CONTEXT, SUBCLASS_ACTIVITY_CLASS);
                intent.putExtra("workspace_name", wsName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getString(OWN_WORKSPACES_LIST));
        Set<String> wsNames = _userPrefs.getStringSet(getString(OWN_WORKSPACES_LIST), new HashSet<String>());
        _wsNamesList.clear();
        for (String wsName : wsNames) {
//            Toast.makeText(SUBCLASS_CONTEXT, "ws Name added: " + wsName, Toast.LENGTH_LONG).show();
            _wsNamesList.add(wsName);
        }
        Collections.sort(_wsNamesList);
        _wsNamesAdapter.notifyDataSetChanged();
        registerSimWifiP2pBcastReceiver();
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

    public void newOwnWorkspace(final View view) {

        final String[] wsName = new String[1];
        final String[] wsQuota = new String[1];

        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
        final View customView = inflater.inflate(NEW_WORKSPACE_DIALOG_LAYOUT, null);

        final ListView lv_emails = (ListView) customView.findViewById(R.id.lv_emails);
        final ArrayList<String> emailsList = new ArrayList<String>();
        final ArrayAdapter<String> emailsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, emailsList);
        lv_emails.setAdapter(emailsAdapter);

        Button bt_add_email = (Button) customView.findViewById(R.id.bt_add_email);
        bt_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_emails = (EditText) customView.findViewById(R.id.et_emails);
                String email = et_emails.getText().toString().trim();
                if (email.isEmpty())
                    Toast.makeText(SUBCLASS_CONTEXT, "Insert email.", Toast.LENGTH_LONG).show();
                else if (emailsList.contains(email))
                    Toast.makeText(SUBCLASS_CONTEXT, "Email already exists.", Toast.LENGTH_LONG).show();
                else {
                    emailsList.add(et_emails.getText().toString());
                    Collections.sort(emailsList);
                    emailsAdapter.notifyDataSetChanged();
                    et_emails.setText("");
                }
            }
        });

        lv_emails.post(new Runnable() {
            @Override
            public void run() {
                lv_emails.smoothScrollToPosition(0);
            }
        });

        lv_emails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                emailsList.remove(position);
                emailsAdapter.notifyDataSetChanged();
            }
        });

        final ListView lv_tags = (ListView) customView.findViewById(R.id.lv_tags);
        final ArrayList<String> tagsList = new ArrayList<String>();
        final ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tagsList);
        lv_tags.setAdapter(tagsAdapter);

        Button bt_add_tag = (Button) customView.findViewById(R.id.bt_add_tag);
        bt_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_tags = (EditText) customView.findViewById(R.id.et_tags);
                String tag = et_tags.getText().toString().trim();
                if (tag.isEmpty())
                    Toast.makeText(SUBCLASS_CONTEXT, "Insert tag.", Toast.LENGTH_LONG).show();
                else if (emailsList.contains(tag))
                    Toast.makeText(SUBCLASS_CONTEXT, "Tag already exists.", Toast.LENGTH_LONG).show();
                else {
                    tagsList.add(et_tags.getText().toString());
                    Collections.sort(tagsList);
                    tagsAdapter.notifyDataSetChanged();
                    et_tags.setText("");
                }
            }
        });

        lv_tags.post(new Runnable() {
            @Override
            public void run() {
                lv_tags.smoothScrollToPosition(0);
            }
        });

        lv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagsList.remove(position);
                tagsAdapter.notifyDataSetChanged();
            }
        });


        final EditText etName = (EditText) customView.findViewById(R.id.et_ws_name);
        final EditText etQuota = (EditText) customView.findViewById(R.id.et_ws_quota);
        AlertDialog dialog = new AlertDialog.Builder(SUBCLASS_CONTEXT)
                .setTitle("Create New Workspace")
                .setView(customView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        wsName[0] = etName.getText().toString();
                        wsQuota[0] = etQuota.getText().toString();
                        if (wsName[0] == null || wsQuota[0] == null) {
                            Toast.makeText(SUBCLASS_CONTEXT, "All fields must be filled.", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        long quota;
                        // Verify if quota is an integer
                        try {
                            quota = Long.parseLong(wsQuota[0]);
                        } catch (NumberFormatException e) {
                            Toast.makeText(SUBCLASS_CONTEXT, "Invalid Quota format. Must be a number (bytes)", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        // Verify if quota doesn't exceed internal storage capacity
                        if (quota > new MemoryHelper().getAvailableInternalMemorySizeLongInBytes()) {
                            Toast.makeText(SUBCLASS_CONTEXT, "Quota higher than available memory. Available memory is of " + new MemoryHelper().getAvailableInternalMemorySize(), Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        String name = wsName[0];
                        _userPrefsEditor.putLong(name + "_quota", quota);
                        Set<String> ownWs = _userPrefs.getStringSet(getString(OWN_WORKSPACES_LIST), new HashSet<String>());
                        Set<String> allWs = _userPrefs.getStringSet(getString(R.string.own_all_workspaces_list), new HashSet<String>());
                        // Verify if own workspace exists with same name
                        if (allWs.contains(name)) {
                            Toast.makeText(SUBCLASS_CONTEXT, "Owned workspace with same name already exists. Choose different name", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        } else {
                            ownWs.add(name);
                            allWs.add(name);
                            _userPrefsEditor.putStringSet(getString(OWN_WORKSPACES_LIST), ownWs);
                            _userPrefsEditor.putStringSet(getString(R.string.own_all_workspaces_list), allWs);
                            _wsNamesList.add(name);
                            Collections.sort(_wsNamesList);
                            _wsNamesAdapter.notifyDataSetChanged();
                            // Create the actual directory in the app's private space
                            File wsDir = new File(_appDir, name);
                            if (!wsDir.exists()) {
                                Toast.makeText(SUBCLASS_CONTEXT, "Directory " + name + " created.", Toast.LENGTH_LONG).show();
                                wsDir.mkdir();
                            }
                        }
                        HashSet<String> newTagsSet = new HashSet<String>(tagsList);
                        HashSet<String> wsEmails = new HashSet<String>(emailsList);

                        _userPrefsEditor.putStringSet(name + "_invitedUsers", wsEmails);
                        _userPrefsEditor.putStringSet(name + "_tags", newTagsSet).commit();
                        _userPrefsEditor.commit();

                        mAppContext.getManager().requestGroupInfo(mAppContext.getChannel(), (SimWifiP2pManager.GroupInfoListener) OwnWorkspacesListActivity.this);

                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        _userPrefs.edit().commit();
        unregisterReceiver(receiver);
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
        if (simWifiP2pInfo.askIsConnected()) {
            for (String deviceName : simWifiP2pInfo.getDevicesInNetwork()) {
                SimWifiP2pDevice device = simWifiP2pDeviceList.getByName(deviceName);
                final String destIp = device.getVirtIp();

                OwnWorkspacesListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("BCast", "sending refresh lists to" + destIp);
                        (new Thread(new OutgoingCommTaskThread(mAppContext, OwnWorkspacesListActivity.this, destIp, mAppContext.getVirtualIp() + ";REFRESH_LIST;" + mAppContext.getLocalEmail() + ";"))).start();
                    }
                });
            }
        }
    }
}