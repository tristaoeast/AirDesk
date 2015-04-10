package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ForeignWorkspaceActivity extends ActionBarActivity {

    private String WORKSPACE_DIR;
    private String WORKSPACE_NAME;
    private String WORKSPACE_MODE;
    private int WORKSPACES_LIST;

    private File _appDir;
    private SharedPreferences _appPrefs;
    private SharedPreferences _userPrefs;
    private SharedPreferences.Editor _appPrefsEditor;
    private SharedPreferences.Editor _userPrefsEditor;
    private ArrayList<String> _fileNamesList;
    private ArrayAdapter<String> _fileNamesAdapter;
    private ListView _listView;
    protected ArrayList<String> _tagsList;
    protected ArrayAdapter<String> _tagsAdapter;
    protected ListView _tagsListView;
    protected ArrayList<String> _usernamesList;
    protected ArrayAdapter<String> _usernamesAdapter;
    protected ListView _usernamesListView;

    protected String LOCAL_EMAIL;
    protected String LOCAL_USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_workspace);
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        LOCAL_USERNAME = _appPrefs.getString("username", "");
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _userPrefsEditor = _userPrefs.edit();
//        LOCAL_EMAIL = _userPrefs.getString("email", "");
        Intent intent = getIntent();
        WORKSPACE_DIR = intent.getExtras().get("workspace_name").toString();
        WORKSPACE_NAME = WORKSPACE_DIR;
        getSupportActionBar().setTitle(WORKSPACE_NAME + " (FOREIGN)");
        setupFilesList();
        _appDir = new File(getApplicationContext().getFilesDir(), LOCAL_EMAIL);
        if(!_appDir.exists())
            _appDir.mkdir();
    }

    protected void setupFilesList() {
        _fileNamesList = new ArrayList<String>();
        _fileNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, _fileNamesList);
        _listView = (ListView) findViewById(R.id.lv_filesList);
        _listView.setAdapter(_fileNamesAdapter);
        Set<String> fileNames = _userPrefs.getStringSet(WORKSPACE_NAME + "_files", new HashSet<String>());
        for (String fileName : fileNames) {
            _fileNamesList.add(fileName);
        }
        Collections.sort(_fileNamesList);
        _fileNamesAdapter.notifyDataSetChanged();
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openTextFile(position);
            }
        });
    }

    private void openTextFile(int position){
        String filename = _fileNamesList.get(position);
        Intent intent = new Intent(ForeignWorkspaceActivity.this, ReadTextFileActivity.class);
        intent.putExtra("FILENAME",filename);
        intent.putExtra("WORKSPACE_DIR",WORKSPACE_DIR);
        intent.putExtra("WORKSPACE_NAME", WORKSPACE_NAME);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        _fileNamesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }  else if(id == R.id.action_logout){
            _appPrefs.edit().putBoolean("firstRun", true).commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    public void newFile(final View view) {
        final File wsDir = new File(_appDir, WORKSPACE_DIR);
        final String[] fName = new String[1];
        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_new_file, null);
        final EditText etName = (EditText) customView.findViewById(R.id.et_file_name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create Empty Text File?")
                .setView(customView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        fName[0] = etName.getText().toString();
                        String filename = fName[0] + ".txt";
                        Set<String> wsFiles = _userPrefs.getStringSet(WORKSPACE_NAME + "_files", new HashSet<String>());
                        if (wsFiles.contains(filename)) {
                            Toast.makeText(ForeignWorkspaceActivity.this, "File with that name already exists.", Toast.LENGTH_LONG).show();
                            newFile(view);
                            return;
                        }
                        if (fName[0].isEmpty()) {
                            Toast.makeText(ForeignWorkspaceActivity.this, "Name field must be filled.", Toast.LENGTH_LONG).show();
                            newFile(view);
                            return;
                        }
                        File file = new File(wsDir, filename);
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            Log.d("New file IOException", e.toString());
                            Toast.makeText(ForeignWorkspaceActivity.this, "Error creating new file: IOException", Toast.LENGTH_LONG).show();
                            newFile(view);
                            return;
                        }
                        _fileNamesList.add(filename);
                        Collections.sort(_fileNamesList);
                        _fileNamesAdapter.notifyDataSetChanged();
                        wsFiles.add(filename);
                        _userPrefsEditor.putStringSet(WORKSPACE_NAME + "_files", wsFiles).commit();
                        Toast.makeText(ForeignWorkspaceActivity.this, "Empty File " + filename + " created.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public void leaveWorkspace(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_leave_workspace, null);
        final EditText etName = (EditText) customView.findViewById(R.id.et_file_name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Leave " + WORKSPACE_NAME + "?")
                .setView(customView)
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Set<String> invitedUsersList = _userPrefs.getStringSet( WORKSPACE_NAME + "_invitedUsers", new HashSet<String>());
                        invitedUsersList.remove(LOCAL_EMAIL);
                        _userPrefsEditor.putStringSet(WORKSPACE_NAME + "_invitedUsers", invitedUsersList);
                        _userPrefsEditor.commit();

                        Intent intent = new Intent(ForeignWorkspaceActivity.this, OwnPrivateWorkspacesListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }
}
