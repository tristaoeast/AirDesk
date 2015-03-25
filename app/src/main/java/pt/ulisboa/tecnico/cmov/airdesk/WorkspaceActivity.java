package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class WorkspaceActivity extends ActionBarActivity {

    private ExpandableListAdapter mExpListAdapter;
    private ExpandableListView mExpListView;
    private HashMap<String, ArrayList<String>> mListDataChild;

    private ArrayList<String> mWorkspaceTopItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerTopList;
    private ArrayAdapter<String> mWorkspacesAdapter;
    private String localUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspaces);

        Intent intent = getIntent();
        localUsername = intent.getExtras().getString("LOCAL_USERNAME");



//        mWorkspaceTopItems = new ArrayList<>();
//        mWorkspaceTopItems.add("Owned Workspaces");
//        mWorkspaceTopItems.add("Foreign Workspaces");
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerTopList = (ListView) findViewById(R.id.left_drawer);
//
//        mWorkspacesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mWorkspaceTopItems);
//
//        // Set the adapter for the list view
//        mDrawerTopList.setAdapter(mWorkspacesAdapter);
//        mWorkspacesAdapter.notifyDataSetChanged();
//
//        // Set the list's click listener
//        mDrawerTopList.setOnItemClickListener(new DrawerTopItemClickListener());

    }

    private class DrawerTopItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            expandTopItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void expandTopItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        WorkspaceFragment fragment = new WorkspaceFragment();
        Bundle args = new Bundle();
        args.putInt(WorkspaceFragment.ARG_WORKSPACE_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerTopList.setItemChecked(position, true);
        setTitle(mWorkspaceTopItems.get(position));
        mDrawerLayout.closeDrawer(mDrawerTopList);
    }
}
