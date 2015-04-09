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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mariana on 08-04-2015.
 */
public class ForeignWorkspacesListActivity extends ActionBarActivity {

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

//    protected int FOREIGN_WORKSPACE_LIST_LAYOUT;
//    protected int FOREIGN_WORKSPACE_DIR;
//    protected int FOREIGN_WORKSPACES_LIST;
//    protected int FOREIGN_WORKSPACES_PERMISSIONS_CRITERIA;
//    protected ActionBarActivity SUBCLASS_LIST_ACTIVITY;
//    protected Class SUBCLASS_ACTIVITY_CLASS;
//    protected Context SUBCLASS_CONTEXT;

    protected String LOCAL_EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_workspaces_list);
        this.setupSuper();
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _userPrefsEditor = _userPrefs.edit();
        LOCAL_EMAIL = _userPrefs.getString("email", "");
        setupWsList();
        NavigationDrawerSetupHelper nh = new NavigationDrawerSetupHelper(this, this);
        _drawerToggle = nh.setup();
    }

    protected void setupSuper() {
//        FOREIGN_WORKSPACE_LIST_LAYOUT = R.layout.activity_foreign_workspaces_list;
//        FOREIGN_WORKSPACE_DIR = R.string.foreign_workspaces_dir;
//        FOREIGN_WORKSPACES_LIST = R.string.foreign_workspaces_list;
//        FOREIGN_WORKSPACES_PERMISSIONS_CRITERIA = R.string.foreign_workspaces_tag_crit;
//        SUBCLASS_LIST_ACTIVITY = this;
//        SUBCLASS_ACTIVITY_CLASS = ForeignWorkspacesListActivity.class;
//        SUBCLASS_CONTEXT = this;
    }

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
        _wsPermissionsList = new ArrayList<String>();
        _wsNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, _wsNamesList);
        // Get ListView object from xml
        _listView = (ListView) findViewById(R.id.lv_wsList);
        _listView.setAdapter(_wsNamesAdapter);

//        updateLists();

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
        Set<String> wsNames = _userPrefs.getStringSet(getString(R.string.foreign_workspaces_list), new HashSet<String>());
        Set<String> wsPermissions;

//        if (wsNames.isEmpty()) {
//            if (getString(FOREIGN_WORKSPACES_PERMISSIONS_CRITERIA).equalsIgnoreCase("email"))
//                Toast.makeText(this, _username + ", you have no workspaces being shared with you at the moment", Toast.LENGTH_LONG).show();
//            else if (getString(FOREIGN_WORKSPACES_PERMISSIONS_CRITERIA).equalsIgnoreCase("tag"))
//                Toast.makeText(this, _username + ", you have no subscribed workspaces at the moment", Toast.LENGTH_LONG).show();
//        }

        //caso das foreign shared ws que verifica pelo email
//        if (getString(FOREIGN_WORKSPACES_PERMISSIONS_CRITERIA).equalsIgnoreCase("email")) {
//            for (String wsName : wsNames) {
//                Log.d("FWLAE - WS Name", wsName);
//                wsPermissions = _userPrefs.getStringSet(wsName + "_emails", new HashSet<String>());
//                for (String wsPermission : wsPermissions) {
//                    Log.d("FWLAE - WS Permission", wsPermission);
//                    _wsPermissionsList.add(wsPermission);
//                    verify if the logged in user is in the invitees list to choose if the ws should show up
//                    if (_email.equalsIgnoreCase(wsPermission)) {
//                        Log.d("FWLAE - email", _email);
//                        _wsNamesList.add(wsName);
//                    }
//                }
//            }
//        }
//
//        caso das foreign subscribed ws que verificam pelas tags
//        else if (getString(FOREIGN_WORKSPACES_PERMISSIONS_CRITERIA).equalsIgnoreCase("tag")) {
//            Log.d("FWLAT", "SOU UMA SUBSCRIPTION!!!!!");
//            wsNames = _userPrefs.getStringSet(getString(R.string.own_published_workspaces_list), new HashSet<String>());
//            for (String wsName : wsNames) {
//                Log.d("FWLAT - WS Name", wsName);
//                Set<String> publishedWsTags =  _userPrefs.getStringSet(wsName + "_tags", new HashSet<String>());
//                Set<String> subscribedWsTags = _userPrefs.getStringSet(getString(R.string.foreign_subscribed_workspaces)+"_tags", new HashSet<String>());
//                for(String subscribedTag : subscribedWsTags){
//                    if (publishedWsTags.contains(subscribedTag)){
//                        if(!(_wsNamesList.contains(wsName))) {
//                            _wsNamesList.add(wsName);
//                        }
//                    }
//                }
//                wsPermissions = _userPrefs.getStringSet(wsName + "_tags", new HashSet<String>());
//                for (String wsPermission : wsPermissions) {
//                    Toast.makeText(this, "wsName: "+wsName+"tags: "+wsPermission, Toast.LENGTH_LONG).show();
//                    _wsPermissionsList.add(wsPermission);
//                    //verify if the logged in user is in the invitees list to choose if the ws should show up
//                    //TODO: cycle that goes and matches the ws_tags with the users_tags
//                    /*if (_tag.equalsIgnoreCase(wsPermission)) {
//                        _wsNamesList.add(wsName);
//                    }*/
//                }
//            }
//        }
//        Collections.sort(_wsNamesList);
//        _wsNamesAdapter.notifyDataSetChanged();

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
        }
        // Activate the navigation drawer toggle
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onStop();
        _userPrefs.edit().commit();
    }
}
