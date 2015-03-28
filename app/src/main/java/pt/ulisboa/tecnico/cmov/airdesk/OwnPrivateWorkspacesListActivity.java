package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class OwnPrivateWorkspacesListActivity extends ActionBarActivity {

    // NavDrawer related variables
    private ExpandableListAdapter _expListAdapter;
    private ExpandableListView _expListView;
    private HashMap<String, ArrayList<String>> _mapChildTitles;
    private ArrayList<String> _listGroupTitles;
    private ArrayList<String> _ows;
    private ArrayList<String> _fws;
    private ActionBarDrawerToggle _drawerToggle;
    private String _currentTitle;
    private boolean _itemSelected = false;
    private DrawerLayout _drawerLayout;
    private String _listSelected;

    private ArrayList<String> _wsNamesList;
    private ArrayList<String> _notesTextList;
    private ArrayAdapter<String> _wsNamesAdapter;
    private ListView _listView;


    private String _localUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_own_private_workspaces_list);
//        navigationDrawer = new NavigationDrawerSetup(mDrawerView, mDrawerLayout, mDrawerList, actionBar, mNavOptions, currentActivity);
//        navigationDrawer.setupDrawer();
        Intent intent = getIntent();
        _localUsername = intent.getExtras().getString("LOCAL_USERNAME");
        _listSelected = intent.getExtras().getString("LIST_SELECTED");
        if (_listSelected.equals("OPrWS")) {
            setContentView(R.layout.activity_own_private_workspaces_list);
        } else if (_listSelected.equals("OSWS")) {
            setContentView(R.layout.activity_own_shared_workspaces_list);
        } else if (_listSelected.equals("OPuWS")) {
            setContentView(R.layout.activity_own_published_workspaces_list);
        } else if (_listSelected.equals("FSuWS")) {
            setContentView(R.layout.activity_foreign_subscribed_workspaces_list);
        } else if (_listSelected.equals("FShWS")) {
            setContentView(R.layout.activity_foreign_shared_workspaces_list);
        }
        Toast.makeText(this, _listSelected, Toast.LENGTH_LONG).show();
        prepareNavigationDrawerListData();
        setupDrawer();
        setupWsList();
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
                Intent intent = new Intent(OwnPrivateWorkspacesListActivity.this, OwnPrivateWorkspacesListActivity.class);
                intent.putExtra("LOCAL_USERNAME", _localUsername);
                startActivity(intent);
            }
        });
    }

    private void setupDrawer() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        _expListView = (ExpandableListView) findViewById(R.id.elv_left_drawer);
        _expListAdapter = new ExpandableListAdapter(this, _listGroupTitles, _mapChildTitles);
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        _drawerToggle = new ActionBarDrawerToggle(this, _drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            // Called when a drawer has settled in a completely open state
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                _currentTitle = getSupportActionBar().getTitle().toString();
                getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely closed state
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!_itemSelected) {
                    getSupportActionBar().setTitle(_currentTitle);
                }
                _itemSelected = false;
//                getSupportActionBar().setTitle(_activityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        _drawerToggle.setDrawerIndicatorEnabled(true);
        _drawerLayout.setDrawerListener(_drawerToggle);

        _expListView.setAdapter(_expListAdapter);
        _expListAdapter.notifyDataSetChanged();

        _expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Create a new fragment and specify the planet to show based on position
                _itemSelected = true;
//                OwnPrivateWorkspacesListFragment wsListFragment;
//                Toast.makeText(WorkspacesActivity.this,groupPosition+" "+childPosition,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OwnPrivateWorkspacesListActivity.this, OwnPrivateWorkspacesListActivity.class);
                intent.putExtra("LOCAL_USERNAME", _localUsername);
                if (0 == groupPosition) {
                    if (0 == childPosition) {
//                        intent = new Intent(OwnPrivateWorkspacesListActivity.this, OwnPrivateWorkspacesListActivity.class);
//                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        intent.putExtra("LIST_SELECTED", "OPrWS");
                    } else if (1 == childPosition) {
//                        intent = new Intent(OwnPrivateWorkspacesListActivity.this, OwnSharedWorkspacesListActivity.class);
//                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        intent.putExtra("LIST_SELECTED", "OSWS");
                    } else if (2 == childPosition) {
//                        intent = new Intent(OwnPrivateWorkspacesListActivity.this, OwnPublishedWorkspacesListActivity.class);
//                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        intent.putExtra("LIST_SELECTED", "OPuWS");
                    }
                } else if (1 == groupPosition) {
                    if (0 == childPosition) {
//                        intent = new Intent(OwnPrivateWorkspacesListActivity.this, ForeignSharedWorkspacesListActivity.class);
//                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        intent.putExtra("LIST_SELECTED", "FShWS");
                    } else if (1 == childPosition) {
//                        intent = new Intent(OwnPrivateWorkspacesListActivity.this, ForeignSubscribedWorkspacesListActivity.class);
//                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        intent.putExtra("LIST_SELECTED", "FSuWS");
                    }
                }
                startActivity(intent);

                // Highlight the selected item, update the title, and close the drawer
                _expListView.setItemChecked(childPosition, true);

                if (0 == groupPosition) {
                    getSupportActionBar().setTitle("Own " + _mapChildTitles.get(_listGroupTitles.get(groupPosition)).get(childPosition) + " Workspaces");
                } else if (1 == groupPosition) {
                    getSupportActionBar().setTitle("Foreign " + _mapChildTitles.get(_listGroupTitles.get(groupPosition)).get(childPosition) + " Workspaces");
                }


                _drawerLayout.closeDrawer(_expListView);
                return false;
            }
        });

    }

    private void prepareNavigationDrawerListData() {
        _listGroupTitles = new ArrayList<String>();
        _mapChildTitles = new HashMap<String, ArrayList<String>>();

        // Adding group data
        _listGroupTitles.add("Owned Workspaces");
        _listGroupTitles.add("Foreign Workspaces");

        // Adding child data
        _ows = new ArrayList<String>();
        _ows.add("Private");
        _ows.add("Shared");
        _ows.add("Published");
//        _ows.add("All");

        _fws = new ArrayList<String>();
        _fws.add("Shared");
        _fws.add("Subscribed");
//        _fws.add("All");

        _mapChildTitles.put(_listGroupTitles.get(0), _ows); // Header, Child data
        _mapChildTitles.put(_listGroupTitles.get(1), _fws);
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

    public void newWorkspace(final View view) {

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
                        try {
                            Integer.parseInt(wsQuota[0]);
                        } catch (NumberFormatException e) {
                            Toast.makeText(OwnPrivateWorkspacesListActivity.this, "Invalid Quota format. Must be a number (MB)", Toast.LENGTH_LONG).show();
                            newWorkspace(view);
                            return;
                        }
                        if (Integer.parseInt(wsQuota[0]) > new MemoryHelper().getAvailableInternalMemorySizeLong()) {
                            Toast.makeText(OwnPrivateWorkspacesListActivity.this, "Quota higher than available memory. Available memory is of " + new MemoryHelper().getAvailableInternalMemorySize(), Toast.LENGTH_LONG).show();
                            newWorkspace(view);
                            return;
                        }
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }
}