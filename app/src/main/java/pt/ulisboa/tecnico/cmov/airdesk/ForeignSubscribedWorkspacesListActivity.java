package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class ForeignSubscribedWorkspacesListActivity extends ForeignWorkspacesListActivity {



    private SharedPreferences _prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupSuper(R.layout.activity_foreign_subscribed_workspaces_list,
                R.string.foreign_subscribed_workspaces_dir,
                R.string.foreign_subscribed_workspaces_list,
                R.string.foreign_workspaces_tag_crit,
                this,
                ForeignSubscribedWorkspaceActivity.class,
                this);
        super.onCreate(savedInstanceState);
    }

    // A Hack done in order to ensure the correct behaviour of the back button,
    // since the main activity automatically redirects to this one
    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
        _prefs.edit().putBoolean(getString(R.string.event_back_button_pressed), true).commit();
        super.onBackPressed();
    }

    public void remForeignSubscribedWorkspace(final View view) {
    }

    public void addForeignSubscribedWorkspace(final View view) {
    }
}