package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class OwnPublishedWorkspaceActivity extends OwnWorkspaceActivity {

    private SharedPreferences _prefs;
    private SharedPreferences.Editor _editor;

//    private ArrayList<String> _tagsList;
//    private ArrayAdapter<String> _tagsAdapter;
//    private ListView _tagsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_published_workspace);
        setActivityContext(this);
        setWorkspacesList(R.string.own_published_workspaces_list);
        setWorkspaceMode("PUBLISHED");
        _prefs = getSharedPreferences(getString(R.string.activity_login_shared_preferences), MODE_PRIVATE);
        _editor = _prefs.edit();
        super.onCreate(savedInstanceState);
        super.setupTagsList();
    }

    @Override
    public void onResume() {
        _tagsAdapter.notifyDataSetChanged();
        super.onResume();
    }


    public void unPublishWorkspace(View view) {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_unpublish_workspace, null);
        TextView tv = (TextView) customView.findViewById(R.id.tv_msg);
        tv.setText(super.getWorkspaceName() + " will be made private.");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Unpublish " + super.getWorkspaceName() + "?")
                .setView(customView)
                .setPositiveButton("Unpublish", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        _editor.remove(getWorkspaceName() + "_tags");
                        Set<String> ownPublishedWs = _prefs.getStringSet(getString(R.string.own_published_workspaces_list), new HashSet<String>());
                        ownPublishedWs.remove(getWorkspaceName());
                        _editor.putStringSet(getString(R.string.own_published_workspaces_list), ownPublishedWs);
                        Set<String> ownPrivateWs = _prefs.getStringSet(getString(R.string.own_private_workspaces_list), new HashSet<String>());
                        ownPrivateWs.add(getWorkspaceName());
                        _editor.putStringSet(getString(R.string.own_private_workspaces_list), ownPrivateWs).commit();
                        Intent intent = new Intent(OwnPublishedWorkspaceActivity.this, OwnPrivateWorkspaceActivity.class);
                        intent.putExtra("workspace_name", getWorkspaceName());
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public void editTags(final View view) {

        final HashSet<String> removedTagsSet = new HashSet<String>();
        final HashSet<String> addedTagsSet = new HashSet<String>();

        LayoutInflater inflater = LayoutInflater.from(OwnPublishedWorkspaceActivity.this);
        final View customView = inflater.inflate(R.layout.dialog_edit_tags, null);

        // Set tags list and button behaviour
        Button bt_add_tag = (Button) customView.findViewById(R.id.bt_add_tag);
        final Set<String> tagsSet = _prefs.getStringSet(getWorkspaceName() + "_tags", new HashSet<String>());
        final ListView lv_tags = (ListView) customView.findViewById(R.id.lv_tags);
        lv_tags.setAdapter(_tagsAdapter);
        _tagsAdapter.notifyDataSetChanged();

        bt_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_tags = (EditText) customView.findViewById(R.id.et_tags);
                String tag = et_tags.getText().toString().trim();
                if (tag.isEmpty())
                    Toast.makeText(OwnPublishedWorkspaceActivity.this, "Insert a tag.", Toast.LENGTH_LONG).show();
                else if (_tagsList.contains(tag))
                    Toast.makeText(OwnPublishedWorkspaceActivity.this, "Tag already exists.", Toast.LENGTH_LONG).show();
                else {
                    _tagsList.add(tag);
//                    tagsSet.add(tag);
                    addedTagsSet.add(tag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                    et_tags.setText("");
                }
            }
        });

        // This is used to refresh the position of the list
        lv_tags.post(new Runnable() {
            @Override
            public void run() {
                lv_tags.smoothScrollToPosition(0);
            }
        });

        // Event Listener that removes tags when clicked
        lv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removedTagsSet.add(_tagsList.get(position));
                _tagsList.remove(position);
                _tagsAdapter.notifyDataSetChanged();

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tags?");
        builder.setView(customView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (_tagsList.isEmpty()) {
                    Toast.makeText(OwnPublishedWorkspaceActivity.this, "At least one tag must be added.", Toast.LENGTH_LONG).show();
                    editTags(view);
                    return;
                }


                HashSet<String> newTagsSet = new HashSet<String>(_tagsList);
                _prefs.edit().putStringSet(getWorkspaceName() + "_tags", newTagsSet).commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (String removedTag : removedTagsSet) {
                    _tagsList.add(removedTag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                }
                for (String removedTag : addedTagsSet) {
                    _tagsList.remove(removedTag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                }

                if (_tagsList.isEmpty()) {
                    Toast.makeText(OwnPublishedWorkspaceActivity.this, "At least one tag must be added.", Toast.LENGTH_LONG).show();
                    editTags(view);
                    return;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
