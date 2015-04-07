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


public class OwnSharedWorkspaceActivity extends OwnWorkspaceActivity {

    private SharedPreferences _prefs;
    private SharedPreferences.Editor _editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_shared_workspace);
        setActivityContext(this);
        setWorkspacesList(R.string.own_shared_workspaces_list);
        setWorkspaceMode("PUBLISHED");
        _prefs = getSharedPreferences(getString(R.string.activity_login_shared_preferences), MODE_PRIVATE);;
        _editor = _prefs.edit();
        super.onCreate(savedInstanceState);
        super.setupUsernamesList();

    }


    @Override
    public void onResume() {
        _usernamesAdapter.notifyDataSetChanged();
        super.onResume();
    }

    public void unShareWorkspace(View view) {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_unshare_workspace, null);
        TextView tv = (TextView) customView.findViewById(R.id.tv_msg);
        tv.setText(super.getWorkspaceName() +" will be made private.");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Unshare " + super.getWorkspaceName() + "?")
                .setView(customView)
                .setPositiveButton("Unshare", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        _editor.remove(getWorkspaceName() + "_names");
                        Set<String> ownSharedWs = _prefs.getStringSet(getString(R.string.own_shared_workspaces_list), new HashSet<String>());
                        ownSharedWs.remove(getWorkspaceName());
                        _editor.putStringSet(getString(R.string.own_shared_workspaces_list), ownSharedWs).commit();
                        Intent intent = new Intent(OwnSharedWorkspaceActivity.this, OwnPrivateWorkspaceActivity.class);
                        intent.putExtra("workspace_name", getWorkspaceName());
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public void editUsernames(final View view) {

        LayoutInflater inflater = LayoutInflater.from(OwnSharedWorkspaceActivity.this);
        final View customView = inflater.inflate(R.layout.dialog_edit_usernames, null);

        // Set usernames list and button behaviour
        Button bt_add_username = (Button) customView.findViewById(R.id.bt_add_username);
        final Set<String> usernamesSet = _prefs.getStringSet(getWorkspaceName() + "_usernames", new HashSet<String>());
        final ListView lv_usernames = (ListView) customView.findViewById(R.id.lv_usernames);
        lv_usernames.setAdapter(_usernamesAdapter);
        _usernamesAdapter.notifyDataSetChanged();

        bt_add_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_usernames = (EditText) customView.findViewById(R.id.et_usernames);
                String username = et_usernames.getText().toString().trim();
                if (username.isEmpty())
                    Toast.makeText(OwnSharedWorkspaceActivity.this, "Insert a username.", Toast.LENGTH_LONG).show();
                else if (_usernamesList.contains(username))
                    Toast.makeText(OwnSharedWorkspaceActivity.this, "Tag already exists.", Toast.LENGTH_LONG).show();
                else {
                    _usernamesList.add(username);
                    usernamesSet.add(username);
                    Collections.sort(_usernamesList);
                    _usernamesAdapter.notifyDataSetChanged();
                    et_usernames.setText("");
                }
            }
        });

        // This is used to refresh the position of the list
        lv_usernames.post(new Runnable() {
            @Override
            public void run() {
                lv_usernames.smoothScrollToPosition(0);
            }
        });

        // Event Listener that removes usernames when clicked
        lv_usernames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _usernamesList.remove(position);
                _usernamesAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Usernames?");
        builder.setView(customView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (_usernamesList.isEmpty()) {
                    Toast.makeText(OwnSharedWorkspaceActivity.this, "At least one username must be added.", Toast.LENGTH_LONG).show();
                    editUsernames(view);
                    return;
                }
                _editor.putStringSet(getWorkspaceName() + "_usernames", usernamesSet).commit();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
