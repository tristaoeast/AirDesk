package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

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

    private ArrayList<String> _workspaceTopItems;
    private DrawerLayout _drawerLayout;
    private ListView _drawerGroupList;
    private ArrayAdapter<String> _workspacesAdapter;
    private String _localUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspaces);

        Intent intent = getIntent();
        _localUsername = intent.getExtras().getString("LOCAL_USERNAME");

        // get the listview
        _expListView = (ExpandableListView) findViewById(R.id.elv_left_drawer);

        // preparing list data
        prepareListData();

        _expListAdapter = new ExpandableListAdapter(this, _listGroupTitles, _mapChildTitles);

        // setting list adapter
        _expListView.setAdapter(_expListAdapter);
        _expListAdapter.notifyDataSetChanged();

        _expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Create a new fragment and specify the planet to show based on position
                WorkspaceFragment fragment = new WorkspaceFragment();
                Bundle args = new Bundle();
                args.putInt(WorkspaceFragment.GROUP_POSITION, groupPosition);
                args.putInt(WorkspaceFragment.CHILD_POSITION, childPosition);
                fragment.setArguments(args);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                // Highlight the selected item, update the title, and close the drawer
//                _drawerGroupList.setItemChecked(childPosition, true);
                _expListView.setItemChecked(childPosition,true);

                setTitle(_mapChildTitles.get(groupPosition).get(childPosition));
                _drawerLayout.closeDrawer(_drawerGroupList);

                return false;
            }
        });
    }

    private void prepareListData() {
        _listGroupTitles = new ArrayList<String>();
        _mapChildTitles = new HashMap<String, ArrayList<String>>();

        // Adding group data
        _listGroupTitles.add("Owned Workspaces");
        _listGroupTitles.add("Foreign Workspaces");

        // Adding child data
        ArrayList<String> ows = new ArrayList<String>();
        ows.add("Private");
        ows.add("Shared");
        ows.add("Published");
        ows.add("All");

        ArrayList<String> nowShowing = new ArrayList<String>();
        nowShowing.add("Shared");
        nowShowing.add("Subscribed");
        nowShowing.add("All");

        _mapChildTitles.put(_listGroupTitles.get(0), ows); // Header, Child data
        _mapChildTitles.put(_listGroupTitles.get(1), nowShowing);
    }
}