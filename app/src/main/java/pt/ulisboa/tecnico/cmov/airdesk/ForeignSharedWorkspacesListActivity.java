package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class ForeignSharedWorkspacesListActivity extends ActionBarActivity {

    // NavDrawer related variables

    private ActionBarDrawerToggle _drawerToggle;

    private ArrayList<String> _wsNamesList;

    private ArrayAdapter<String> _wsNamesAdapter;
    private ListView _listView;

    private SharedPreferences _prefs;

    private String _localUsername;

    private File _subDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_shared_workspaces_list);
        _prefs = getSharedPreferences(getString(R.string.activity_login_shared_preferences), MODE_PRIVATE);
        setupWsList();
        NavigationDrawerSetupHelper nh = new NavigationDrawerSetupHelper(this, this);
        _drawerToggle = nh.setup();
        File appDir = getApplicationContext().getFilesDir();
        _subDir = new File(appDir, getString(R.string.foreign_shared_workspaces_list));
    }

    // A Hack done in order to ensure the correct behaviour of the back button,
    // since the main activity automatically redirects to this one
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
        _listView = (ListView) findViewById(R.id.lv_wsList);
        _listView.setAdapter(_wsNamesAdapter);

        Set<String> wsNames = _prefs.getStringSet(getString(R.string.foreign_shared_workspaces_list), new HashSet<String>());

        if(wsNames.isEmpty())
            Toast.makeText(this, "Foreign_Shared_WS_LIST: " + "EMPTYYY", Toast.LENGTH_LONG).show();

        for (String wsName : wsNames) {
            Toast.makeText(this, "Foreign Shared WS Name added: " + wsName, Toast.LENGTH_LONG).show();
            _wsNamesList.add(wsName);
        }
        _wsNamesAdapter.notifyDataSetChanged();

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String wsName = _wsNamesList.get(position);
                Intent intent = new Intent(ForeignSharedWorkspacesListActivity.this, ForeignSharedWorkspaceActivity.class);
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



    @Override
    protected void onStop() {
        super.onStop();
        _prefs.edit().commit();
    }
}