package pt.ulisboa.tecnico.cmov.airdesk;

import android.os.Bundle;


public class OwnSharedWorkspaceActivity extends OwnWorkspaceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_shared_workspace);
        setActivityContext(this);
        super.onCreate(savedInstanceState);
    }

}
