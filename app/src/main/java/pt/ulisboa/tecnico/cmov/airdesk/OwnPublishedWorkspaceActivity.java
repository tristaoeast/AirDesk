package pt.ulisboa.tecnico.cmov.airdesk;

import android.os.Bundle;


public class OwnPublishedWorkspaceActivity extends OwnWorkspaceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_published_workspace);
        setActivityContext(this);
        setWorkspacesList(R.string.own_published_workspaces_list);
        super.onCreate(savedInstanceState);
    }

}
