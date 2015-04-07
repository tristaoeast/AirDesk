package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
        setWorkspaceMode("SHARED");
        super.onCreate(savedInstanceState);
        _prefs = getSharedPrefs();
        _editor = _prefs.edit();

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

}
