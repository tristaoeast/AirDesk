package pt.ulisboa.tecnico.cmov.airdesk;

import android.os.Bundle;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class OwnPrivateWorkspacesListActivity extends OwnWorkspacesListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupSuper(R.layout.activity_own_private_workspaces_list,
                R.string.own_private_workspaces_dir,
                R.string.own_private_workspaces_list,
                R.layout.dialog_new_private_workspace,
                this,
                OwnPrivateWorkspaceActivity.class,
                this);
        super.onCreate(savedInstanceState);
    }

}