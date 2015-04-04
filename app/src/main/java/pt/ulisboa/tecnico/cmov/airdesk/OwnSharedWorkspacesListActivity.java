package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
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
                R.string.activity_own_shared_workspaces_list,
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
        final String[] wsUsernamesTemp = new String[1];

        LayoutInflater inflater = LayoutInflater.from(this);
        final View yourCustomView = inflater.inflate(R.layout.dialog_new_shared_workspace, null);

        final EditText etName = (EditText) yourCustomView.findViewById(R.id.et_ws_name);
        final EditText etQuota = (EditText) yourCustomView.findViewById(R.id.et_ws_quota);
        final EditText etUsernamesTemp = (EditText) yourCustomView.findViewById(R.id.et_usernames);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create New Workspace")
                .setView(yourCustomView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        wsName[0] = etName.getText().toString();
                        wsQuota[0] = etQuota.getText().toString();
                        wsUsernamesTemp[0] = etUsernamesTemp.getText().toString();
                        wsUsernamesTemp[0].replaceAll("\\s","");
                        if(wsName[0] == null || wsQuota[0] == null || wsUsernamesTemp[0] == null){
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
                        MiscUtils mu = new MiscUtils();
                        HashSet<String> wsUsernames = mu.stringToSetTokenzier(wsUsernamesTemp[0],",");
                        Set<String> ownSharedWs = _prefs.getStringSet(getString(R.string.activity_own_shared_workspaces_list), new HashSet<String>());
                        Set<String> allWs = _prefs.getStringSet(getString(R.string.all_owned_workspaces_names), new HashSet<String>());
                        // Verify if own workspace exists with same name
                        if (allWs.contains(name)) {
                            Toast.makeText(OwnSharedWorkspacesListActivity.this, "Owned workspace with same name already exists. Choose different name", Toast.LENGTH_LONG).show();
                            newOwnWorkspace(view);
                            return;
                        } else {
                            ownSharedWs.add(name);
                            allWs.add(name);
                            _editor.putStringSet(getString(R.string.activity_own_shared_workspaces_list), ownSharedWs);
                            _editor.putStringSet(getString(R.string.all_owned_workspaces_names), allWs);
                            _editor.putStringSet(name+"_usernames",wsUsernames);
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