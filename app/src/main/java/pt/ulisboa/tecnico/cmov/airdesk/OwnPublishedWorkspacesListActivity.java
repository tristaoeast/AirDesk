package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
public class OwnPublishedWorkspacesListActivity extends OwnWorkspacesListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupSuper(R.layout.activity_own_published_workspaces_list,
                R.string.own_published_workspaces_dir,
                R.string.own_published_workspaces_list,
                R.layout.dialog_new_published_workspace,
                this,
                OwnPublishedWorkspaceActivity.class,
                this);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void newOwnWorkspace(final View view) {

        final String[] wsName = new String[1];
        final String[] wsQuota = new String[1];
        final String[] wsTagsTemp = new String[1];

        LayoutInflater inflater = LayoutInflater.from(this);
        final View yourCustomView = inflater.inflate(R.layout.dialog_new_published_workspace, null);

        final EditText etName = (EditText) yourCustomView.findViewById(R.id.et_ws_name);
        final EditText etQuota = (EditText) yourCustomView.findViewById(R.id.et_ws_quota);
        final EditText etTagsTemp = (EditText) yourCustomView.findViewById(R.id.et_tags);

        // Set tags list and button behaviour
        final ListView lv_tags = (ListView) yourCustomView.findViewById(R.id.lv_tags);
        final ArrayList<String> tagsList = new ArrayList<String>();
        final ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tagsList);
        lv_tags.setAdapter(tagsAdapter);
        Button bt_add_tag = (Button) yourCustomView.findViewById(R.id.bt_add_tag);

        bt_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_tags = (EditText) yourCustomView.findViewById(R.id.et_tags);
                String tag = et_tags.getText().toString().trim();
                if(tag.isEmpty())
                    Toast.makeText(SUBCLASS_CONTEXT, "Insert tag.", Toast.LENGTH_LONG).show();
                else if (tagsList.contains(tag))
                    Toast.makeText(SUBCLASS_CONTEXT, "Tag already exsits.", Toast.LENGTH_LONG).show();
                else {
                    tagsList.add(et_tags.getText().toString());
                    Collections.sort(tagsList);
                    tagsAdapter.notifyDataSetChanged();
                    et_tags.setText("");
                }
            }
        });

        lv_tags.post(new Runnable() {
            @Override
            public void run() {
                lv_tags.smoothScrollToPosition(0);
            }
        });

        lv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagsList.remove(position);
                tagsAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Workspace");
        builder.setView(yourCustomView);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                wsName[0] = etName.getText().toString().trim();
                wsQuota[0] = etQuota.getText().toString().trim();
                if (wsName[0].isEmpty() || wsQuota[0].isEmpty() || tagsList.isEmpty()) {
                    Toast.makeText(OwnPublishedWorkspacesListActivity.this, "All fields must be filled.", Toast.LENGTH_LONG).show();
                    newOwnWorkspace(view);
                    return;
                }
                int quota;
                // Verify if quota is an integer
                try {
                    quota = Integer.parseInt(wsQuota[0]);
                } catch (NumberFormatException e) {
                    Toast.makeText(OwnPublishedWorkspacesListActivity.this, "Invalid Quota format. Must be a number (MB)", Toast.LENGTH_LONG).show();
                    newOwnWorkspace(view);
                    return;
                }
                // Verify if quota doesn't exceed internal storage capacity
                if (quota > new MemoryHelper().getAvailableInternalMemorySizeLong()) {
                    Toast.makeText(OwnPublishedWorkspacesListActivity.this, "Quota higher than available memory. Available memory is of " + new MemoryHelper().getAvailableInternalMemorySize(), Toast.LENGTH_LONG).show();
                    newOwnWorkspace(view);
                    return;
                }
                String name = wsName[0];
                _userPrefs.edit().putInt(name + "_quota", quota).commit();
                HashSet<String> wsTags = new HashSet<String>(tagsList);
                Set<String> ownPublishedWs = _userPrefs.getStringSet(getString(R.string.own_published_workspaces_list), new HashSet<String>());
                Set<String> allWs = _userPrefs.getStringSet(getString(R.string.all_owned_workspaces_names), new HashSet<String>());

                // Verify if own workspace exists with same name
                if (allWs.contains(name)) {
                    Toast.makeText(OwnPublishedWorkspacesListActivity.this, "Owned workspace with same name already exists. Choose different name", Toast.LENGTH_LONG).show();
                    newOwnWorkspace(view);
                    return;
                } else {
                    ownPublishedWs.add(name);
                    allWs.add(name);
                    _userPrefsEditor.putStringSet(getString(R.string.own_published_workspaces_list), ownPublishedWs);
                    _userPrefsEditor.putStringSet(getString(R.string.all_owned_workspaces_names), allWs);
                    _userPrefsEditor.putStringSet(name + "_tags", wsTags);
                    _wsNamesList.add(name);
                    _wsNamesAdapter.notifyDataSetChanged();
                    // Create the actual directory in the app's private space
                    File wsDir = new File(_appDir, name);
                    if (!wsDir.exists()) {
                        Toast.makeText(OwnPublishedWorkspacesListActivity.this, "Directory " + name + " created.", Toast.LENGTH_LONG).show();
                        wsDir.mkdir();
                    }
                }
                _userPrefsEditor.commit();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}