package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ist167092 on 24-03-2015.
 */
public abstract class OwnWorkspacesListActivity extends ActionBarActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(OWN_WORKSPACE_LIST_LAYOUT);
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        Log.d("WS_LIST_ACTIVITY_EMAIL", LOCAL_EMAIL);
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _userPrefsEditor = _userPrefs.edit();
        setupWsList();
        NavigationDrawerSetupHelper nh = new NavigationDrawerSetupHelper(SUBCLASS_LIST_ACTIVITY, SUBCLASS_CONTEXT);
        _drawerToggle = nh.setup();
        _appDir = getApplicationContext().getFilesDir();

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
                        int quota;
                        // Verify if quota is an integer
                        try {
                            quota = Integer.parseInt(wsQuota[0]);
                        } catch (NumberFormatException e) {
                            Toast.makeText(SUBCLASS_CONTEXT, "Invalid Quota format. Must be a number (MB)", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        // Verify if quota doesn't exceed internal storage capacity
                        if (quota > new MemoryHelper().getAvailableInternalMemorySizeLong()) {
                            Toast.makeText(SUBCLASS_CONTEXT, "Quota higher than available memory. Available memory is of " + new MemoryHelper().getAvailableInternalMemorySize(), Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        String name = wsName[0];
                        _userPrefsEditor.putInt(name + "_quota", quota);
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
                        _userPrefsEditor.commit();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }


    @Override
    protected void onPause() {
        super.onStop();
        _userPrefs.edit().commit();
    }
}