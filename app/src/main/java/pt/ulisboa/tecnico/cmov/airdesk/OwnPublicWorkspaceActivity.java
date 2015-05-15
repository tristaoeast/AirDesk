package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class OwnPublicWorkspaceActivity extends OwnWorkspaceActivity {

    private SharedPreferences _appPrefs;
    private SharedPreferences _userPrefs;
    private SharedPreferences.Editor _appPrefsEditor;
    private SharedPreferences.Editor _userPrefsEditor;

//    private ArrayList<String> _tagsList;
//    private ArrayAdapter<String> _tagsAdapter;
//    private ListView _tagsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_public_workspace);
        setActivityContext(this);
        setWorkspacesList(R.string.own_public_workspaces_list);
        setWorkspaceMode("PUBLIC");
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _userPrefsEditor = _userPrefs.edit();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.setupTagsList();
        _tagsAdapter.notifyDataSetChanged();
        super.onResume();
        mAppContext.setCurrentActivity(this);
    }


    public void unPublishWorkspace(View view) {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_unpublish_workspace, null);
        TextView tv = (TextView) customView.findViewById(R.id.tv_msg);
        tv.setText(super.getWorkspaceName() + " will be made private.");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Unpublish " + super.getWorkspaceName() + "?")
                .setView(customView)
                .setPositiveButton("Unpublish", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        _userPrefsEditor.remove(getWorkspaceName() + "_tags");
                        Set<String> ownPublishedWs = _userPrefs.getStringSet(getString(R.string.own_public_workspaces_list), new HashSet<String>());
                        ownPublishedWs.remove(getWorkspaceName());
                        _userPrefsEditor.putStringSet(getString(R.string.own_public_workspaces_list), ownPublishedWs);
                        Set<String> ownPrivateWs = _userPrefs.getStringSet(getString(R.string.own_private_workspaces_list), new HashSet<String>());
                        ownPrivateWs.add(getWorkspaceName());
                        _userPrefsEditor.putStringSet(getString(R.string.own_private_workspaces_list), ownPrivateWs).commit();
                        Intent intent = new Intent(OwnPublicWorkspaceActivity.this, OwnPrivateWorkspaceActivity.class);
                        intent.putExtra("workspace_name", getWorkspaceName());
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public void editTags(final View view) {

        final HashSet<String> removedTagsSet = new HashSet<String>();
        final HashSet<String> addedTagsSet = new HashSet<String>();

        LayoutInflater inflater = LayoutInflater.from(OwnPublicWorkspaceActivity.this);
        final View customView = inflater.inflate(R.layout.dialog_edit_tags, null);

        // Set tags list and button behaviour
        Button bt_add_tag = (Button) customView.findViewById(R.id.bt_add_tag);
        final Set<String> tagsSet = _userPrefs.getStringSet(getWorkspaceName() + "_tags", new HashSet<String>());
        final ListView lv_tags = (ListView) customView.findViewById(R.id.lv_tags);
        lv_tags.setAdapter(_tagsAdapter);
        _tagsAdapter.notifyDataSetChanged();

        bt_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_tags = (EditText) customView.findViewById(R.id.et_tags);
                String tag = et_tags.getText().toString().trim();
                if (tag.isEmpty())
                    Toast.makeText(OwnPublicWorkspaceActivity.this, "Insert a tag.", Toast.LENGTH_LONG).show();
                else if (_tagsList.contains(tag))
                    Toast.makeText(OwnPublicWorkspaceActivity.this, "Tag already exists.", Toast.LENGTH_LONG).show();
                else {
                    _tagsList.add(tag);
//                    tagsSet.add(tag);
                    addedTagsSet.add(tag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                    et_tags.setText("");
                }
            }
        });

        // This is used to refresh the position of the list
        lv_tags.post(new Runnable() {
            @Override
            public void run() {
                lv_tags.smoothScrollToPosition(0);
            }
        });

        // Event Listener that removes tags when clicked
        lv_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removedTagsSet.add(_tagsList.get(position));
                _tagsList.remove(position);
                _tagsAdapter.notifyDataSetChanged();

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Tags?");
        builder.setView(customView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                if (_tagsList.isEmpty()) {
//                    Toast.makeText(OwnPublicWorkspaceActivity.this, "At least one tag must be added.", Toast.LENGTH_LONG).show();
//                    editTags(view);
//                    return;
//                }

                HashSet<String> newTagsSet = new HashSet<String>(_tagsList);
                _userPrefs.edit().putStringSet(getWorkspaceName() + "_tags", newTagsSet).commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (String removedTag : removedTagsSet) {
                    _tagsList.add(removedTag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                }
                for (String removedTag : addedTagsSet) {
                    _tagsList.remove(removedTag);
                    Collections.sort(_tagsList);
                    _tagsAdapter.notifyDataSetChanged();
                }

//                if (_tagsList.isEmpty()) {
//                    Toast.makeText(OwnPublicWorkspaceActivity.this, "At least one tag must be added.", Toast.LENGTH_LONG).show();
//                    editTags(view);
//                    return;
//                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void editEmails(final View view) {
        final HashSet<String> removedEmailsSet = new HashSet<String>();
        final HashSet<String> addedEmailsSet = new HashSet<String>();

        LayoutInflater inflater = LayoutInflater.from(OwnPublicWorkspaceActivity.this);
        final View customView = inflater.inflate(R.layout.dialog_edit_emails, null);

        // Set emails list and button behaviour
        Button bt_add_email = (Button) customView.findViewById(R.id.bt_add_email);
//        final Set<String> invitedEmailsSet = _userPrefs.getStringSet(getWorkspaceName() + "_invitedUsers", new HashSet<String>());
        final ListView lv_emails = (ListView) customView.findViewById(R.id.lv_emails);
        lv_emails.setAdapter(_emailsAdapter);
        _emailsAdapter.notifyDataSetChanged();

        bt_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_email = (EditText) customView.findViewById(R.id.et_email);
                String email = et_email.getText().toString().trim();
                if (email.isEmpty())
                    Toast.makeText(OwnPublicWorkspaceActivity.this, "Insert an email.", Toast.LENGTH_LONG).show();
                else if (_emailsList.contains(email))
                    Toast.makeText(OwnPublicWorkspaceActivity.this, "Email already exists.", Toast.LENGTH_LONG).show();
                else {
                    _emailsList.add(email);
//                    emailsSet.add(email);
                    addedEmailsSet.add(email);
                    Collections.sort(_emailsList);
                    _emailsAdapter.notifyDataSetChanged();
                    et_email.setText("");
                }
            }
        });

        // This is used to refresh the position of the list
        lv_emails.post(new Runnable() {
            @Override
            public void run() {
                lv_emails.smoothScrollToPosition(0);
            }
        });

        // Event Listener that removes emails when clicked
        lv_emails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removedEmailsSet.add(_emailsList.get(position));
                _emailsList.remove(position);
                _emailsAdapter.notifyDataSetChanged();

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Emails?");
        builder.setView(customView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                if (_emailsList.isEmpty()) {
//                    Toast.makeText(OwnPrivateWorkspaceActivity.this, "At least one email must be added.", Toast.LENGTH_LONG).show();
//                    editEmails(view);
//                    return;
//                }
                HashSet<String> newEmailsSet = new HashSet<String>(_emailsList);
                _userPrefsEditor.putStringSet(getWorkspaceName() + "_invitedUsers", newEmailsSet).commit();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (String removedEmail : removedEmailsSet) {
                    _emailsList.add(removedEmail);
                    Collections.sort(_emailsList);
                    _emailsAdapter.notifyDataSetChanged();
                }

                for (String addedEmail : addedEmailsSet) {
                    _emailsList.remove(addedEmail);
                    Collections.sort(_emailsList);
                    _emailsAdapter.notifyDataSetChanged();
                }

//                if (_emailsList.isEmpty()) {
//                    Toast.makeText(OwnPrivateWorkspaceActivity.this, "At least one email must be added.", Toast.LENGTH_LONG).show();
//                    editEmails(view);
//                    return;
//                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
