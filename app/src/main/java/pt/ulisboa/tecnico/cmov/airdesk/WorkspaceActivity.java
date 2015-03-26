package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class WorkspaceActivity extends ActionBarActivity {

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

    private String _localUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspaces);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        _localUsername = intent.getExtras().getString("LOCAL_USERNAME");

        // get the listview
        _expListView = (ExpandableListView) findViewById(R.id.elv_left_drawer);

        // preparing list data
        prepareListData();

        _expListAdapter = new ExpandableListAdapter(this, _listGroupTitles, _mapChildTitles);
        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setupDrawer();

        // setting list adapter
        _expListView.setAdapter(_expListAdapter);
        _expListAdapter.notifyDataSetChanged();

        _expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Create a new fragment and specify the planet to show based on position
                _itemSelected = true;
                ListWorkspacesFragment fragment = new ListWorkspacesFragment();
                Bundle args = new Bundle();
                args.putInt(ListWorkspacesFragment.GROUP_POSITION, groupPosition);
                args.putInt(ListWorkspacesFragment.CHILD_POSITION, childPosition);
                fragment.setArguments(args);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                // Highlight the selected item, update the title, and close the drawer
//                _drawerGroupList.setItemChecked(childPosition, true);
                _expListView.setItemChecked(childPosition, true);

//                setTitle(_mapChildTitles.get(_listGroupTitles.get(groupPosition)).get(childPosition));

                if (0 == groupPosition) {
                    getSupportActionBar().setTitle("Own " + _mapChildTitles.get(_listGroupTitles.get(groupPosition)).get(childPosition) + " Workspaces");
                } else if (1 == groupPosition) {
                    getSupportActionBar().setTitle("Foreign " + _mapChildTitles.get(_listGroupTitles.get(groupPosition)).get(childPosition) + " Workspaces");
                }

//                if (null == _drawerLayout) {
//                    Toast.makeText(WorkspaceActivity.this, "DRAWER LAYOUT IS NULL!!", Toast.LENGTH_LONG).show();
//                } else if (null == _expListView) {
//                    Toast.makeText(WorkspaceActivity.this, "EXTLISTVIEW IS NULL!!", Toast.LENGTH_LONG).show();
//                }

                _drawerLayout.closeDrawer(_expListView);
                return false;
            }
        });
    }

    private void setupDrawer() {
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

    }

    private void prepareListData() {
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
        _ows.add("All");

        _fws = new ArrayList<String>();
        _fws.add("Shared");
        _fws.add("Subscribed");
        _fws.add("All");

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