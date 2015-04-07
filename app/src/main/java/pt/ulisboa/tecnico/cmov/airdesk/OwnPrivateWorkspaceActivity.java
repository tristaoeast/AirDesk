package pt.ulisboa.tecnico.cmov.airdesk;

import android.os.Bundle;


public class OwnPrivateWorkspaceActivity extends OwnWorkspaceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_private_workspace);
        setActivityContext(this);
        setWorkspacesList(R.string.own_private_workspaces_list);
        setWorkspaceMode("PRIVATE");
        super.onCreate(savedInstanceState);
    }
}
