package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public abstract class OwnWorkspaceActivity extends ActionBarActivity {

    private int SUBCLASS_ACTIVITY_LAYOUT;

    private String WORKSPACE_DIR;
    private String WORKSPACE_NAME;
    private String WORKSPACE_MODE;
    private Context SUBCLASS_CONTEXT;
    private int WORKSPACES_LIST;

    private File _appDir;
    private SharedPreferences _prefs;
    private SharedPreferences.Editor _editor;
    private ArrayList<String> _fileNamesList;
    private ArrayAdapter<String> _fileNamesAdapter;
    private ListView _listView;
    protected ArrayList<String> _tagsList;
    protected ArrayAdapter<String> _tagsAdapter;
    protected ListView _tagsListView;
    protected ArrayList<String> _usernamesList;
    protected ArrayAdapter<String> _usernamesAdapter;
    protected ListView _usernamesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(SUBCLASS_ACTIVITY_LAYOUT);
        _prefs = getSharedPreferences(getString(R.string.activity_login_shared_preferences), MODE_PRIVATE);
        _editor = _prefs.edit();
        Intent intent = getIntent();
        WORKSPACE_DIR = intent.getExtras().get("workspace_name").toString();
        WORKSPACE_NAME = WORKSPACE_DIR;
        getSupportActionBar().setTitle(WORKSPACE_NAME + " (OWNED - " + WORKSPACE_MODE + ")");
        setupFilesList();
        _appDir = getApplicationContext().getFilesDir();
    }

    public String getWorkspaceName() {
        return WORKSPACE_NAME;
    }


    public String getWorkspaceDir() {
        return WORKSPACE_DIR;
    }


    public File getAppDir() {
        return _appDir;
    }


    public SharedPreferences getSharedPrefs() {
        return _prefs;
    }

    protected void setupTagsList() {
        _tagsList = new ArrayList<String>();
        _tagsAdapter = new ArrayAdapter<String>(SUBCLASS_CONTEXT, android.R.layout.simple_list_item_1, android.R.id.text1, _tagsList);
//        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
//        final View customView = inflater.inflate(R.layout.activity_own_published_workspace, null);
        _tagsListView = (ListView) findViewById(R.id.lv_tags);
        _tagsListView.setAdapter(_tagsAdapter);
        Set<String> tags = _prefs.getStringSet(WORKSPACE_NAME + "_tags", new HashSet<String>());
        for (String tag : tags) {
            _tagsList.add(tag);
        }
        Collections.sort(_tagsList);
        _tagsAdapter.notifyDataSetChanged();
    }

    protected void setupUsernamesList() {
        _usernamesList = new ArrayList<String>();
        _usernamesAdapter = new ArrayAdapter<String>(SUBCLASS_CONTEXT, android.R.layout.simple_list_item_1, android.R.id.text1, _usernamesList);
//        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
//        final View customView = inflater.inflate(R.layout.activity_own_published_workspace, null);
        _usernamesListView = (ListView) findViewById(R.id.lv_usernames);
        _usernamesListView.setAdapter(_usernamesAdapter);
        Set<String> usernames = _prefs.getStringSet(WORKSPACE_NAME + "_usernames", new HashSet<String>());
        Log.d("WORKSPACE_NAME",WORKSPACE_NAME);
        for (String username : usernames) {
            Log.d("OPuWS_TAG",username);
            _usernamesList.add(username);
        }
        Collections.sort(_usernamesList);
        _usernamesAdapter.notifyDataSetChanged();
    }

    protected void setupFilesList() {
        _fileNamesList = new ArrayList<String>();
        _fileNamesAdapter = new ArrayAdapter<String>(SUBCLASS_CONTEXT, android.R.layout.simple_list_item_1, android.R.id.text1, _fileNamesList);
        _listView = (ListView) findViewById(R.id.lv_filesList);
        _listView.setAdapter(_fileNamesAdapter);
        Set<String> fileNames = _prefs.getStringSet(WORKSPACE_NAME + "_files", new HashSet<String>());
        for (String fileName : fileNames) {
            _fileNamesList.add(fileName);
        }
        Collections.sort(_fileNamesList);
        _fileNamesAdapter.notifyDataSetChanged();
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO create new activity which presents text or image
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        _fileNamesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_own_private_workspace, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActivityLayout(int activityLayout) {
        SUBCLASS_ACTIVITY_LAYOUT = activityLayout;
    }

    public void setActivityContext(Context subclassContext) {
        SUBCLASS_CONTEXT = subclassContext;
    }

    public void setWorkspacesList(int workspacesList) {
        WORKSPACES_LIST = workspacesList;
    }

    public void setWorkspaceMode(String workspaceMode) {
        WORKSPACE_MODE = workspaceMode;
    }

    public void newFile(final View view) {
        final File wsDir = new File(_appDir, WORKSPACE_DIR);
        final String[] fName = new String[1];
        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
        final View customView = inflater.inflate(R.layout.dialog_new_file, null);
        final EditText etName = (EditText) customView.findViewById(R.id.et_file_name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create Empty Text File?")
                .setView(customView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        fName[0] = etName.getText().toString();
                        String filename = fName[0] + ".txt";
                        Set<String> wsFiles = _prefs.getStringSet(WORKSPACE_NAME + "_files", new HashSet<String>());
                        if (wsFiles.contains(filename)) {
                            Toast.makeText(SUBCLASS_CONTEXT, "File with that name already exists.", Toast.LENGTH_LONG).show();
                            newFile(view);
                            return;
                        }
                        if (fName[0].isEmpty()) {
                            Toast.makeText(SUBCLASS_CONTEXT, "Name field must be filled.", Toast.LENGTH_LONG).show();
                            newFile(view);
                            return;
                        }
                        File file = new File(wsDir, filename);
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            Log.d("New file IOException", e.toString());
                            Toast.makeText(SUBCLASS_CONTEXT, "Error creating new file: IOException", Toast.LENGTH_LONG).show();
                            newFile(view);
                            return;
                        }
                        _fileNamesList.add(filename);
                        Collections.sort(_fileNamesList);
                        _fileNamesAdapter.notifyDataSetChanged();
                        wsFiles.add(filename);
                        _editor.putStringSet(WORKSPACE_NAME + "_files", wsFiles).commit();
                        Toast.makeText(SUBCLASS_CONTEXT, "Empty File " + filename + " created.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public void deleteWorkspace(View view) {

        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
        final View customView = inflater.inflate(R.layout.dialog_delete_workspace, null);
        final EditText etName = (EditText) customView.findViewById(R.id.et_file_name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete " + WORKSPACE_NAME + "?")
                .setView(customView)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        File dir = new File(_appDir, WORKSPACE_DIR);
                        deleteRecursive(dir);
                        _editor.remove(WORKSPACE_NAME + "_files");
                        _editor.remove(WORKSPACE_NAME + "_quota");
                        Set<String> oWs = _prefs.getStringSet(getString(WORKSPACES_LIST), new HashSet<String>());
                        oWs.remove(WORKSPACE_NAME);
                        _editor.putStringSet(getString(WORKSPACES_LIST), oWs);
                        Set<String> allWs = _prefs.getStringSet(getString(R.string.all_owned_workspaces_names), new HashSet<String>());
                        allWs.remove(WORKSPACE_NAME);
                        _editor.putStringSet(getString(R.string.all_owned_workspaces_names), allWs).commit();
                        Intent intent = new Intent(SUBCLASS_CONTEXT, OwnPrivateWorkspacesListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public void deleteRecursive(File fileOrDirectory) {
        Log.d("File or dir path", fileOrDirectory.getPath());
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    public void publishWorkspace(final View view) {

        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
        final View customView = inflater.inflate(R.layout.dialog_publish_workspace, null);
        final EditText etName = (EditText) customView.findViewById(R.id.et_file_name);
        final String[] wsTagsTemp = new String[1];

        final EditText etTagsTemp = (EditText) customView.findViewById(R.id.et_tags);

        // Set tags list and button behaviour
        final ListView lv_tags = (ListView) customView.findViewById(R.id.lv_tags);
        final ArrayList<String> tagsList = new ArrayList<String>();
        final ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, tagsList);

        lv_tags.setAdapter(tagsAdapter);
        Button bt_add_tag = (Button) customView.findViewById(R.id.bt_add_tag);

        bt_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_tags = (EditText) customView.findViewById(R.id.et_tags);
                String tag = et_tags.getText().toString().trim();
                if (tag.isEmpty())
                    Toast.makeText(SUBCLASS_CONTEXT, "Insert a tag.", Toast.LENGTH_LONG).show();
                else if (tagsList.contains(tag))
                    Toast.makeText(SUBCLASS_CONTEXT, "Tag already exists.", Toast.LENGTH_LONG).show();
                else {
                    tagsList.add(et_tags.getText().toString());
                    Collections.sort(tagsList);
                    tagsAdapter.notifyDataSetChanged();
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
                tagsList.remove(position);
                tagsAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Publish Workspace?");
        builder.setView(customView);
        builder.setPositiveButton("Publish", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (tagsList.isEmpty()) {
                    Toast.makeText(SUBCLASS_CONTEXT, "At least one tag must be added.", Toast.LENGTH_LONG).show();
                    publishWorkspace(view);
                    return;
                }
                HashSet<String> wsTags = new HashSet<String>(tagsList);
                Set<String> ownPublishedWsList = _prefs.getStringSet(getString(R.string.own_published_workspaces_list), new HashSet<String>());
                Set<String> currentWsList = _prefs.getStringSet(getString(WORKSPACES_LIST), new HashSet<String>());
                currentWsList.remove(WORKSPACE_NAME);
                ownPublishedWsList.add(WORKSPACE_NAME);
                _editor.putStringSet(getString(R.string.own_published_workspaces_list), ownPublishedWsList);
                _editor.putStringSet(WORKSPACE_NAME + "_tags", wsTags);
                _editor.commit();

                Intent intent = new Intent(SUBCLASS_CONTEXT, OwnPublishedWorkspaceActivity.class);
                intent.putExtra("workspace_name", WORKSPACE_NAME);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void shareWorkspace(final View view) {
        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
        final View customView = inflater.inflate(R.layout.dialog_share_workspace, null);
        final EditText etName = (EditText) customView.findViewById(R.id.et_file_name);
        final String[] wsUsernamesTemp = new String[1];

        final EditText etUsernamesTemp = (EditText) customView.findViewById(R.id.et_emails);

        // Set usernames list and button behaviour
        final ListView lv_usernames = (ListView) customView.findViewById(R.id.lv_usernames);
        final ArrayList<String> usernamesList = new ArrayList<String>();
        final ArrayAdapter<String> usernamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, usernamesList);

        lv_usernames.setAdapter(usernamesAdapter);
        Button bt_add_username = (Button) customView.findViewById(R.id.bt_add_email);

        bt_add_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_usernames = (EditText) customView.findViewById(R.id.et_emails);
                String username = et_usernames.getText().toString().trim();
                if (username.isEmpty())
                    Toast.makeText(SUBCLASS_CONTEXT, "Insert a username.", Toast.LENGTH_LONG).show();
                else if (usernamesList.contains(username))
                    Toast.makeText(SUBCLASS_CONTEXT, "Username already exists.", Toast.LENGTH_LONG).show();
                else {
                    usernamesList.add(et_usernames.getText().toString());
                    Collections.sort(usernamesList);
                    usernamesAdapter.notifyDataSetChanged();
                    et_usernames.setText("");
                }
            }
        });

        // This is used to refresh the position of the list
        lv_usernames.post(new Runnable() {
            @Override
            public void run() {
                lv_usernames.smoothScrollToPosition(0);
            }
        });

        // Event Listener that removes usernames when clicked
        lv_usernames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usernamesList.remove(position);
                usernamesAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share Workspace?");
        builder.setView(customView);
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (usernamesList.isEmpty()) {
                    Toast.makeText(SUBCLASS_CONTEXT, "At least one username must be added.", Toast.LENGTH_LONG).show();
                    shareWorkspace(view);
                    return;
                }
                HashSet<String> wsUsernames = new HashSet<String>(usernamesList);
                Set<String> ownSharedWsList = _prefs.getStringSet(getString(R.string.own_shared_workspaces_list), new HashSet<String>());
                Set<String> currentWsList = _prefs.getStringSet(getString(WORKSPACES_LIST), new HashSet<String>());
                currentWsList.remove(WORKSPACE_NAME);
                ownSharedWsList.add(WORKSPACE_NAME);
                _editor.putStringSet(getString(R.string.own_shared_workspaces_list), ownSharedWsList);
                _editor.putStringSet(WORKSPACE_NAME + "_usernames", wsUsernames);
                _editor.commit();
                Intent intent = new Intent(SUBCLASS_CONTEXT, OwnSharedWorkspaceActivity.class);
                intent.putExtra("workspace_name", WORKSPACE_NAME);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
