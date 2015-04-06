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
    private Context SUBCLASS_CONTEXT;
    private int WORKSPACES_LIST;

    private File _appDir;
    private SharedPreferences _prefs;
    private SharedPreferences.Editor _editor;
    private ArrayList<String> _fileNamesList;
    private ArrayAdapter<String> _fileNamesAdapter;
    private ListView _listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(SUBCLASS_ACTIVITY_LAYOUT);
        _prefs = getSharedPreferences(getString(R.string.activity_login_shared_preferences), MODE_PRIVATE);
        _editor = _prefs.edit();
        Intent intent = getIntent();
        WORKSPACE_DIR = intent.getExtras().get("workspace_name").toString();
        WORKSPACE_NAME = WORKSPACE_DIR;
        getSupportActionBar().setTitle(WORKSPACE_NAME);

        setupFilesList();

        _appDir = getApplicationContext().getFilesDir();


    }

    protected void setupFilesList() {
        _fileNamesList = new ArrayList<String>();
        _fileNamesAdapter = new ArrayAdapter<String>(SUBCLASS_CONTEXT, android.R.layout.simple_list_item_1, android.R.id.text1, _fileNamesList);
        // Get ListView object from xml
        _listView = (ListView) findViewById(R.id.lv_filesList);
        _listView.setAdapter(_fileNamesAdapter);

        Set<String> fileNames = _prefs.getStringSet(WORKSPACE_NAME + "_files", new HashSet<String>());
        String debug = "";
        for (String fileName : fileNames) {
            _fileNamesList.add(fileName);
            debug += fileName + ",";
        }
        Log.d("Filenames list onCreate", debug);
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
//        Set<String> fileNames = _prefs.getStringSet(WORKSPACE_NAME + "_files", new HashSet<String>());
//        String debug = "";
//        for (String fileName : fileNames) {
//            debug += fileName + ",";
//        }
//        Log.d("Filenames list onResume", debug);
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

    public void newFile(final View view) {
        final File wsDir = new File(_appDir, WORKSPACE_DIR);
        final String[] fName = new String[1];
        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
        final View customView = inflater.inflate(R.layout.dialog_new_file, null);
        final EditText etName = (EditText) customView.findViewById(R.id.et_file_name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Empty Text File")
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

}
