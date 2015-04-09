package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
public class ForeignSubscribedWorkspacesListActivity extends ForeignWorkspacesListActivity {



    private SharedPreferences _prefs;
    private SharedPreferences.Editor _editor;
    private String _email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupSuper(R.layout.activity_foreign_subscribed_workspaces_list,
                R.string.foreign_subscribed_workspaces_dir,
                R.string.foreign_subscribed_workspaces_list,
                R.string.foreign_workspaces_tag_crit,
                this,
                ForeignSubscribedWorkspaceActivity.class,
                this);
        super.onCreate(savedInstanceState);
    }

    // A Hack done in order to ensure the correct behaviour of the back button,
    // since the main activity automatically redirects to this one
    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
        _prefs.edit().putBoolean(getString(R.string.event_back_button_pressed), true).commit();
        super.onBackPressed();
    }

    public void editForeignSubscribedWorkspace(final View view) {
        _email = _prefs.getString("email", "invalid email");

        LayoutInflater inflater = LayoutInflater.from(this);
        final View yourCustomView = inflater.inflate(R.layout.dialog_edit_subscription_tags, null);

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
                    Toast.makeText(SUBCLASS_CONTEXT, "Tag already exists.", Toast.LENGTH_LONG).show();
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
        builder.setTitle("Edit subscription tags");
        builder.setView(yourCustomView);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (tagsList.isEmpty()) {
                    Toast.makeText(ForeignSubscribedWorkspacesListActivity.this, "Tag field must be filled.", Toast.LENGTH_LONG).show();
                    return;
                }

                HashSet<String> wsTags = new HashSet<String>(tagsList);
                _editor.putStringSet(_email + "_tags", wsTags);
                _editor.commit();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}