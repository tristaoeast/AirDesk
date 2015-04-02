package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class OwnSharedWorkspacesListActivity extends ActionBarActivity {

    // NavDrawer related variables

    private ActionBarDrawerToggle _drawerToggle;

    private ArrayList<String> _wsNamesList;
    private ArrayAdapter<String> _wsNamesAdapter;
    private ListView _listView;

    private SharedPreferences _prefs;

    private String _localUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_shared_workspaces_list);

        setupWsList();
        NavigationDrawerSetupHelper nh = new NavigationDrawerSetupHelper(this, this);
        _drawerToggle = nh.setup();
        _prefs = getSharedPreferences(getString(R.string.activity_login_shared_preferences), MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
        _prefs.edit().putBoolean(getString(R.string.event_back_button_pressed), true).commit();
        super.onBackPressed();
    }

    protected void setupWsList() {
        _wsNamesList = new ArrayList<String>();
        _wsNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, _wsNamesList);
        // Get ListView object from xml
        _listView = (ListView) findViewById(R.id.lv_opwslist);
        _listView.setAdapter(_wsNamesAdapter);
        _wsNamesAdapter.notifyDataSetChanged();


        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String wsName = _wsNamesList.get(position);
                Intent intent = new Intent(OwnSharedWorkspacesListActivity.this, OwnSharedWorkspacesListActivity.class);
                intent.putExtra("LOCAL_USERNAME", _localUsername);
                startActivity(intent);
            }
        });
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

    public void newOwnPrivateWorkspace(final View view) {

        final String[] wsName = new String[1];
        final String[] wsQuota = new String[1];

        LayoutInflater inflater = LayoutInflater.from(this);
        final View yourCustomView = inflater.inflate(R.layout.dialog_new_private_workspace, null);

        final EditText etName = (EditText) yourCustomView.findViewById(R.id.et_ws_name);
        final EditText etQuota = (EditText) yourCustomView.findViewById(R.id.et_ws_quota);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create New Workspace")
                .setView(yourCustomView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        wsName[0] = etName.getText().toString();
                        wsQuota[0] = etQuota.getText().toString();
                        int quota = 0;
                        try {
                            quota = Integer.parseInt(wsQuota[0]);

                        } catch (NumberFormatException e) {
                            Toast.makeText(OwnSharedWorkspacesListActivity.this, "Invalid Quota format. Must be a number (MB)", Toast.LENGTH_LONG).show();
                            newOwnPrivateWorkspace(view);
                            return;
                        }
                        if (quota > new MemoryHelper().getAvailableInternalMemorySizeLong()) {
                            Toast.makeText(OwnSharedWorkspacesListActivity.this, "Quota higher than available memory. Available memory is of " + new MemoryHelper().getAvailableInternalMemorySize(), Toast.LENGTH_LONG).show();
                            newOwnPrivateWorkspace(view);
                            return;
                        }
                        _prefs.edit().putInt(wsName[0] + "_quota", quota);
                        Set<String> oprivws = _prefs.getStringSet("own_private_workspaces", null);
                        oprivws.add(wsName[0]);
                        _prefs.edit().putStringSet("own_private_workspaces", oprivws);
                        _prefs.edit().commit();


                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        _prefs.edit().commit();
    }
}