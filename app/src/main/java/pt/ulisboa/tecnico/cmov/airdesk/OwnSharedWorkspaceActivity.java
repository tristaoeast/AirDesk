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

    private SharedPreferences _appPrefs;
    private SharedPreferences _userPrefs;
    private SharedPreferences.Editor _appPrefsEditor;
    private SharedPreferences.Editor _userPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_public_workspace);
        setActivityContext(this);
        setWorkspacesList(R.string.own_shared_workspaces_list);
        setWorkspaceMode("SHARED");
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _userPrefsEditor = _userPrefs.edit();
        super.onCreate(savedInstanceState);
        super.setupEmailsList();

    }


    @Override
    public void onResume() {
        _emailsAdapter.notifyDataSetChanged();
        super.onResume();
    }

    public void unShareWorkspace(View view) {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_unshare_workspace, null);
        TextView tv = (TextView) customView.findViewById(R.id.tv_msg);
        tv.setText(super.getWorkspaceName() + " will be made private.");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Unshare " + super.getWorkspaceName() + "?")
                .setView(customView)
                .setPositiveButton("Unshare", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        _userPrefsEditor.remove(getWorkspaceName() + "_emails");
                        Set<String> ownSharedWs = _userPrefs.getStringSet(getString(R.string.own_shared_workspaces_list), new HashSet<String>());
                        ownSharedWs.remove(getWorkspaceName());
                        _userPrefsEditor.putStringSet(getString(R.string.own_shared_workspaces_list), ownSharedWs);
                        Set<String> ownPrivateWs = _userPrefs.getStringSet(getString(R.string.own_private_workspaces_list), new HashSet<String>());
                        ownPrivateWs.add(getWorkspaceName());
                        _userPrefsEditor.putStringSet(getString(R.string.own_private_workspaces_list), ownPrivateWs);
                        Set<String> foreignSharedWs = _userPrefs.getStringSet(getString(R.string.foreign_shared_workspaces_list), new HashSet<String>());
                        foreignSharedWs.remove(getWorkspaceName());
                        _userPrefsEditor.putStringSet(getString(R.string.foreign_shared_workspaces_list), foreignSharedWs);
                        _userPrefsEditor.commit();
                        Intent intent = new Intent(OwnSharedWorkspaceActivity.this, OwnPrivateWorkspaceActivity.class);
                        intent.putExtra("workspace_name", getWorkspaceName());
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public void editEmails(final View view) {

        final HashSet<String> removedEmailsSet = new HashSet<String>();
        final HashSet<String> addedEmailsSet = new HashSet<String>();

        LayoutInflater inflater = LayoutInflater.from(OwnSharedWorkspaceActivity.this);
        final View customView = inflater.inflate(R.layout.dialog_edit_emails, null);

        // Set emails list and button behaviour
        Button bt_add_email = (Button) customView.findViewById(R.id.bt_add_email);
        final Set<String> emailsSet = _userPrefs.getStringSet(getWorkspaceName() + "_emails", new HashSet<String>());
        final ListView lv_emails = (ListView) customView.findViewById(R.id.lv_emails);
        lv_emails.setAdapter(_emailsAdapter);
        _emailsAdapter.notifyDataSetChanged();

        bt_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_email = (EditText) customView.findViewById(R.id.et_email);
                String email = et_email.getText().toString().trim();
                if (email.isEmpty())
                    Toast.makeText(OwnSharedWorkspaceActivity.this, "Insert an email.", Toast.LENGTH_LONG).show();
                else if (_emailsList.contains(email))
                    Toast.makeText(OwnSharedWorkspaceActivity.this, "Email already exists.", Toast.LENGTH_LONG).show();
                else {
                    _emailsList.add(email);
//                    emailsSet.add(email);
                    addedEmailsSet.add(email);
                    Collections.sort(_emailsList);
                    _emailsAdapter.notifyDataSetChanged();
                    et_email.setText("");
                }
            }
        });

        // This is used to refresh the position of the list
        lv_emails.post(new Runnable() {
            @Override
            public void run() {
                lv_emails.smoothScrollToPosition(0);
            }
        });

        // Event Listener that removes emails when clicked
        lv_emails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removedEmailsSet.add(_emailsList.get(position));
                _emailsList.remove(position);
                _emailsAdapter.notifyDataSetChanged();

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Emails?");
        builder.setView(customView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (_emailsList.isEmpty()) {
                    Toast.makeText(OwnSharedWorkspaceActivity.this, "At least one email must be added.", Toast.LENGTH_LONG).show();
                    editEmails(view);
                    return;
                }

                HashSet<String> newEmailsSet = new HashSet<String>(_emailsList);
                _userPrefs.edit().putStringSet(getWorkspaceName() + "_emails", newEmailsSet).commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (String removedEmail : removedEmailsSet) {
                    _emailsList.add(removedEmail);
                    Collections.sort(_emailsList);
                    _emailsAdapter.notifyDataSetChanged();
                }

                for (String addedEmail : addedEmailsSet) {
                    _emailsList.remove(addedEmail);
                    Collections.sort(_emailsList);
                    _emailsAdapter.notifyDataSetChanged();
                }

                if (_emailsList.isEmpty()) {
                    Toast.makeText(OwnSharedWorkspaceActivity.this, "At least one email must be added.", Toast.LENGTH_LONG).show();
                    editEmails(view);
                    return;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
