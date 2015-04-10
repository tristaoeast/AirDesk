package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.SharedPreferences;

/**
 * Created by ist167092 on 24-03-2015.
 */
public class ForeignSharedWorkspacesListActivity extends ForeignWorkspacesListActivity {

    private SharedPreferences _appPrefs;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
//        setupSuper(R.layout.activity_foreign_shared_workspaces_list,
//                R.string.foreign_shared_workspaces_dir,
//                R.string.foreign_shared_workspaces_list,
//                R.string.foreign_workspaces_email_crit,
//                this,
//                ForeignSharedWorkspaceActivity.class,
//                this);
        super.onCreate(savedInstanceState);
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);

    }*/

    // A Hack done in order to ensure the correct behaviour of the back button,
    // since the main activity automatically redirects to this one
    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Back button pressed", Toast.LENGTH_LONG).show();
        _appPrefs.edit().putBoolean(getString(R.string.event_back_button_pressed), true).commit();
        super.onBackPressed();
    }
}