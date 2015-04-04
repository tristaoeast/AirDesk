package pt.ulisboa.tecnico.cmov.airdesk;

import android.os.Bundle;


public class OwnPrivateWorkspaceActivity extends OwnWorkspaceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_private_workspace);
        super.onCreate(savedInstanceState);
    }
}
