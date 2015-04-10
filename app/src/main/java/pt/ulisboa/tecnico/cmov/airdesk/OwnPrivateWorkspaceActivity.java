package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;


public class OwnPrivateWorkspaceActivity extends OwnWorkspaceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActivityLayout(R.layout.activity_own_private_workspace);
        setActivityContext(this);
        setWorkspacesList(R.string.own_private_workspaces_list);
        setWorkspaceMode("PRIVATE");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void setupEmailsList() {
//        _emailsList = new ArrayList<String>();
//        _emailsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, _emailsList);
////        LayoutInflater inflater = LayoutInflater.from(this);
////        final View customView = inflater.inflate(R.layout.activity_own_private_workspace, null);
//        _emailsListView = (ListView) findViewById(R.id.lv_emails);
//        _emailsListView.setAdapter(_emailsAdapter);
//        Set<String> invitedUsersEmails = _userPrefs.getStringSet(WORKSPACE_NAME + "_invitedUsers", new HashSet<String>());
////        Set<String> subscribedUsersEmails = _userPrefs.getStringSet(WORKSPACE_NAME + "_subscribedUsers", new HashSet<String>());
////        Set<String> allUsersEmails = new HashSet<String>();
////        allUsersEmails.addAll(invitedUsersEmails);
////        allUsersEmails.addAll(subscribedUsersEmails);
//        for (String email : invitedUsersEmails) {
//            Log.d("INVITED EMAIL", email);
//            _emailsList.add(email);
//        }
//        Collections.sort(_emailsList);
//        _emailsAdapter.notifyDataSetChanged();
        super.setupEmailsList();
    }

    public void editEmails(final View view) {
        final HashSet<String> removedEmailsSet = new HashSet<String>();
        final HashSet<String> addedEmailsSet = new HashSet<String>();

        LayoutInflater inflater = LayoutInflater.from(OwnPrivateWorkspaceActivity.this);
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
                    Toast.makeText(OwnPrivateWorkspaceActivity.this, "Insert an email.", Toast.LENGTH_LONG).show();
                else if (_emailsList.contains(email))
                    Toast.makeText(OwnPrivateWorkspaceActivity.this, "Email already exists.", Toast.LENGTH_LONG).show();
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
