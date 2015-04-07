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
public class OwnSharedWorkspacesListActivity extends OwnWorkspacesListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupSuper(R.layout.activity_own_shared_workspaces_list,
                R.string.own_shared_workspaces_dir,
                R.string.own_shared_workspaces_list,
                R.layout.dialog_new_shared_workspace,
                this,
                OwnSharedWorkspaceActivity.class,
                this);
        super.onCreate(savedInstanceState);

    }
    @Override
    public void newOwnWorkspace(final View view) {

        final String[] wsName = new String[1];
        final String[] wsQuota = new String[1];
        final String[] wsEmailsTemp = new String[1];

        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_new_shared_workspace, null);

        final ListView lv_emails = (ListView) customView.findViewById(R.id.lv_emails);
        final ArrayList<String> emailsList = new ArrayList<String>();
        final ArrayAdapter<String> emailsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, emailsList);
        lv_emails.setAdapter(emailsAdapter);

        Button bt_add_email = (Button) customView.findViewById(R.id.bt_add_email);
        bt_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_emails = (EditText) customView.findViewById(R.id.et_emails);
                String email = et_emails.getText().toString().trim();
                if (email.isEmpty())
                    Toast.makeText(SUBCLASS_CONTEXT, "Insert email.", Toast.LENGTH_LONG).show();
                else if (emailsList.contains(email))
                    Toast.makeText(SUBCLASS_CONTEXT, "Email already exsits.", Toast.LENGTH_LONG).show();
                else {
                    emailsList.add(et_emails.getText().toString());
                    Collections.sort(emailsList);
                    emailsAdapter.notifyDataSetChanged();
                    et_emails.setText("");
                }
            }
        });

        lv_emails.post(new Runnable() {
            @Override
            public void run() {
                lv_emails.smoothScrollToPosition(0);
            }
        });

        lv_emails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                emailsList.remove(position);
                emailsAdapter.notifyDataSetChanged();
            }
        });

        final EditText etName = (EditText) customView.findViewById(R.id.et_ws_name);
        final EditText etQuota = (EditText) customView.findViewById(R.id.et_ws_quota);
        final EditText etEmailsTemp = (EditText) customView.findViewById(R.id.et_emails);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create New Workspace")
                .setView(customView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        wsName[0] = etName.getText().toString();
                        wsQuota[0] = etQuota.getText().toString();
                        wsEmailsTemp[0] = etEmailsTemp.getText().toString();
                        wsEmailsTemp[0].replaceAll("\\s","");
                        if(wsName[0] == null || wsQuota[0] == null || wsEmailsTemp[0] == null){
                            Toast.makeText(OwnSharedWorkspacesListActivity.this, "All fields must be filled.", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        int quota;
                        // Verify if quota is an integer
                        try {
                            quota = Integer.parseInt(wsQuota[0]);
                        } catch (NumberFormatException e) {
                            Toast.makeText(OwnSharedWorkspacesListActivity.this, "Invalid Quota format. Must be a number (MB)", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        // Verify if quota doesn't exceed internal storage capacity
                        if (quota > new MemoryHelper().getAvailableInternalMemorySizeLong()) {
                            Toast.makeText(OwnSharedWorkspacesListActivity.this, "Quota higher than available memory. Available memory is of " + new MemoryHelper().getAvailableInternalMemorySize(), Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        }
                        String name = wsName[0];
                        _editor.putInt(name + "_quota", quota);
                        HashSet<String> wsEmails = new HashSet<String>(emailsList);
                        Set<String> ownSharedWs = _prefs.getStringSet(getString(R.string.own_shared_workspaces_list), new HashSet<String>());
                        Set<String> allWs = _prefs.getStringSet(getString(R.string.all_owned_workspaces_names), new HashSet<String>());
                        Set<String> foreignSharedWs = _prefs.getStringSet(getString(R.string.foreign_shared_workspaces_list), new HashSet<String>());
                        // Verify if own workspace exists with same name
                        if (allWs.contains(name)) {
                            Toast.makeText(OwnSharedWorkspacesListActivity.this, "Owned workspace with same name already exists. Choose different name", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        } else {
                            ownSharedWs.add(name);
                            allWs.add(name);
                            foreignSharedWs.add(name);

                            _editor.putStringSet(getString(R.string.own_shared_workspaces_list), ownSharedWs);
                            _editor.putStringSet(getString(R.string.all_owned_workspaces_names), allWs);
                            _editor.putStringSet(getString(R.string.foreign_shared_workspaces_list), foreignSharedWs);
                            _editor.putStringSet(name+"_emails",wsEmails);
                            _wsNamesList.add(name);
                            _wsNamesAdapter.notifyDataSetChanged();
                            // Create the actual directory in the app's private space
                            File wsDir = new File(_appDir, name);
                            if (!wsDir.exists()) {
                                Toast.makeText(OwnSharedWorkspacesListActivity.this, "Directory " + name + " created.", Toast.LENGTH_LONG).show();
                                wsDir.mkdir();
                            }
                        }
                        _editor.commit();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }
}