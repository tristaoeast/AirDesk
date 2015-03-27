package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class OwnPublishedWorkspacesListActivity extends ActionBarActivity {

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

    private ArrayList<String> _wsNamesList;
    private ArrayList<String> _notesTextList;
    private ArrayAdapter<String> _wsNamesAdapter;
    private ListView _listView;


    private String _localUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_private_workspaces_list);

//        navigationDrawer = new NavigationDrawerSetup(mDrawerView, mDrawerLayout, mDrawerList, actionBar, mNavOptions, currentActivity);
//        navigationDrawer.setupDrawer();

        Intent intent = getIntent();
        _localUsername = intent.getExtras().getString("LOCAL_USERNAME");
        Toast.makeText(this, _localUsername, Toast.LENGTH_LONG).show();
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
        _wsNamesList.add("OPrivWS 1");
        _wsNamesList.add("OPrivWS 2");
        _wsNamesAdapter.notifyDataSetChanged();


        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String wsName = _wsNamesList.get(position);
                //Toast.makeText(ListNotesActivity.this, "Title: " + noteTitle + "\nText: " + noteText, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OwnPublishedWorkspacesListActivity.this, OwnPublishedWorkspacesListActivity.class);
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
                Intent intent;
                if (0 == groupPosition) {
                    if (0 == childPosition) {
                        intent = new Intent(OwnPublishedWorkspacesListActivity.this, OwnPrivateWorkspacesListActivity.class);
                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        startActivity(intent);
                    } else if (1 == childPosition) {
                        intent = new Intent(OwnPublishedWorkspacesListActivity.this, OwnSharedWorkspacesListActivity.class);
                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        startActivity(intent);
                    } else if (2 == childPosition) {
                        intent = new Intent(OwnPublishedWorkspacesListActivity.this, OwnPublishedWorkspacesListActivity.class);
                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        startActivity(intent);
                    }
                } else if (1 == groupPosition) {
                    if (0 == childPosition) {
                    } else if (0 == childPosition) {
                        intent = new Intent(OwnPublishedWorkspacesListActivity.this, ForeignSharedWorkspacesListActivity.class);
                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        startActivity(intent);
                    } else if (1 == childPosition) {
                        intent = new Intent(OwnPublishedWorkspacesListActivity.this, ForeignSubscribedWorkspacesListActivity.class);
                        intent.putExtra("LOCAL_USERNAME", _localUsername);
                        startActivity(intent);
                    }
                }

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
}