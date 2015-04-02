package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TMC on 02/04/2015.
 */
public class NavigationDrawerSetupHelper {

    private final Context _currentActivityContext;


    private ActionBarActivity _currentActivity;

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


    public NavigationDrawerSetupHelper(ActionBarActivity currentActivity, Context currentActivityClass){
        _currentActivity = currentActivity;
        this._currentActivityContext = currentActivityClass;
    }

    public ActionBarDrawerToggle setup(){
        prepareNavigationDrawerListData();
        setupDrawer();
        return _drawerToggle;
    }

    private void setupDrawer() {

        _currentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        _currentActivity.getSupportActionBar().setHomeButtonEnabled(true);

        _expListView = (ExpandableListView) _currentActivity.findViewById(R.id.elv_left_drawer);
        _expListAdapter = new ExpandableListAdapter(_currentActivityContext, _listGroupTitles, _mapChildTitles);
        _drawerLayout = (DrawerLayout) _currentActivity.findViewById(R.id.drawer_layout);
        _drawerToggle = new ActionBarDrawerToggle(_currentActivity, _drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            // Called when a drawer has settled in a completely open state
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                _currentTitle = _currentActivity.getSupportActionBar().getTitle().toString();
                _currentActivity.getSupportActionBar().setTitle(R.string.app_name);
                _currentActivity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely closed state
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!_itemSelected) {
                    _currentActivity.getSupportActionBar().setTitle(_currentTitle);
                }
                _itemSelected = false;
//                getSupportActionBar().setTitle(_activityTitle);
                _currentActivity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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
                Intent intent = new Intent(_currentActivityContext, OwnPrivateWorkspacesListActivity.class);

                if (0 == groupPosition) {
                    if (0 == childPosition) {
                        intent = new Intent(_currentActivityContext, OwnPrivateWorkspacesListActivity.class);
//                        intent.putExtra("LIST_SELECTED", "OPrWS");
                    } else if (1 == childPosition) {
                        intent = new Intent(_currentActivityContext, OwnSharedWorkspacesListActivity.class);
//                        intent.putExtra("LIST_SELECTED", "OSWS");
                    } else if (2 == childPosition) {
                        intent = new Intent(_currentActivityContext, OwnPublishedWorkspacesListActivity.class);
//                        intent.putExtra("LIST_SELECTED", "OPuWS");
                    }
                } else if (1 == groupPosition) {
                    if (0 == childPosition) {
                        intent = new Intent(_currentActivityContext, ForeignSharedWorkspacesListActivity.class);
//                        intent.putExtra("LIST_SELECTED", "FShWS");
                    } else if (1 == childPosition) {
                        intent = new Intent(_currentActivityContext, ForeignSubscribedWorkspacesListActivity.class);
//                        intent.putExtra("LIST_SELECTED", "FSuWS");
                    }
                }
                _currentActivity.startActivity(intent);

                // Highlight the selected item, update the title, and close the drawer
                _expListView.setItemChecked(childPosition, true);
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

}
