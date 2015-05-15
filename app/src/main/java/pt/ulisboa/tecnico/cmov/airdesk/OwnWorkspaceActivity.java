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

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;


public abstract class OwnWorkspaceActivity extends ActionBarActivity implements SimWifiP2pManager.GroupInfoListener {

    private int SUBCLASS_ACTIVITY_LAYOUT;

    protected String WORKSPACE_DIR;
    protected String WORKSPACE_NAME;
    protected String WORKSPACE_MODE;
    protected Context SUBCLASS_CONTEXT;
    protected int WORKSPACES_LIST;

    protected File _appDir;
    protected SharedPreferences _appPrefs;
    protected SharedPreferences _userPrefs;
    protected SharedPreferences.Editor _appPrefsEditor;
    protected SharedPreferences.Editor _userPrefsEditor;
    protected ArrayList<String> _fileNamesList;
    protected ArrayAdapter<String> _fileNamesAdapter;
    protected ListView _listView;
    protected ArrayList<String> _tagsList;
    protected ArrayAdapter<String> _tagsAdapter;
    protected ListView _tagsListView;
    protected ArrayList<String> _emailsList;
    protected ArrayAdapter<String> _emailsAdapter;
    protected ListView _emailsListView;

    protected String LOCAL_EMAIL;
    protected String LOCAL_USERNAME;

    protected GlobalClass mAppContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(SUBCLASS_ACTIVITY_LAYOUT);
        mAppContext = (GlobalClass) getApplicationContext();
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        _appPrefsEditor = _appPrefs.edit();
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        LOCAL_USERNAME = _appPrefs.getString("username", "");
        Log.d("WS_ACTIVITY_EMAIL", LOCAL_EMAIL);
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _userPrefsEditor = _userPrefs.edit();
        Intent intent = getIntent();
        WORKSPACE_DIR = intent.getExtras().get("workspace_name").toString();
        WORKSPACE_NAME = WORKSPACE_DIR;
        getSupportActionBar().setTitle(WORKSPACE_NAME + " (OWNED - " + WORKSPACE_MODE + ")");
        setupFilesList();
        _appDir = new File(getApplicationContext().getFilesDir(), LOCAL_EMAIL);
        if(!_appDir.exists())
            _appDir.mkdir();
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
        return _userPrefs;
    }

    protected void setupTagsList() {
        _tagsList = new ArrayList<String>();
        _tagsAdapter = new ArrayAdapter<String>(SUBCLASS_CONTEXT, android.R.layout.simple_list_item_1, android.R.id.text1, _tagsList);
//        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
//        final View customView = inflater.inflate(R.layout.activity_own_published_workspace, null);
        _tagsListView = (ListView) findViewById(R.id.lv_tags);
        _tagsListView.setAdapter(_tagsAdapter);
        Set<String> tags = _userPrefs.getStringSet(WORKSPACE_NAME + "_tags", new HashSet<String>());
        for (String tag : tags) {
            _tagsList.add(tag);
        }
        Collections.sort(_tagsList);
        _tagsAdapter.notifyDataSetChanged();
    }

    protected void setupEmailsList() {
        _emailsList = new ArrayList<String>();
        _emailsAdapter = new ArrayAdapter<String>(SUBCLASS_CONTEXT, android.R.layout.simple_list_item_1, android.R.id.text1, _emailsList);
//                LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
//        final View customView = inflater.inflate(R.layout.activity_own_shared_workspace, null);
        _emailsListView = (ListView) findViewById(R.id.lv_emails);
        _emailsListView.setAdapter(_emailsAdapter);
        Set<String> emails = _userPrefs.getStringSet(WORKSPACE_NAME + "_invitedUsers", new HashSet<String>());
//        Log.d("WORKSPACE_NAME", WORKSPACE_NAME);
        for (String email : emails) {
//            Log.d("OPuWS_TAG", email);
            _emailsList.add(email);
        }
        Collections.sort(_emailsList);
        _emailsAdapter.notifyDataSetChanged();
    }

    protected void setupFilesList() {
        _fileNamesList = new ArrayList<String>();
        _fileNamesAdapter = new ArrayAdapter<String>(SUBCLASS_CONTEXT, android.R.layout.simple_list_item_1, android.R.id.text1, _fileNamesList);
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

    private void openTextFile(int position) {
        String filename = _fileNamesList.get(position);
        Intent intent = new Intent(SUBCLASS_CONTEXT, ReadTextFileActivity.class);
        intent.putExtra("FILENAME", filename);
        intent.putExtra("WORKSPACE_DIR", WORKSPACE_DIR);
        intent.putExtra("WORKSPACE_NAME", WORKSPACE_NAME);
        startActivity(intent);
    }

//    private void editTextFileInDialog(final String filename, final File textFile, String text, final int position) {
//
//        LayoutInflater inflater = LayoutInflater.from(SUBCLASS_CONTEXT);
//        final View customView = inflater.inflate(R.layout.dialog_edit_text_file, null);
//        final EditText et_text = (EditText) customView.findViewById(R.id.et_text);
//        et_text.setText(text);
//
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("Edit " + filename)
//                .setView(customView)
//                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        String newText = et_text.getText().toString().trim();
//                        try {
//                            if (!textFile.exists()) {
//
//                                textFile.createNewFile();
//                            }
//
//                            FileWriter fileWritter = new FileWriter(textFile, false);
//                            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
//                            bufferWritter.write(newText);
//                            bufferWritter.close();
//                            updateText(position);
////TODO VERIFY IF SAVING FILES GOES ABOVE WS QUOTA
//                            return;
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Toast.makeText(SUBCLASS_CONTEXT, "Error saving file. Try again", Toast.LENGTH_LONG).show();
//                            editTextFile(filename, textFile, newText, position);
//                            return;
//                        }
//                    }
//                })
//                .setNegativeButton("Cancel", null).create();
//        dialog.show();
//    }

    @Override
    public void onResume() {
        super.onResume();
        this.setupEmailsList();
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
        }        else if(id == R.id.action_logout){
            _appPrefs.edit().putBoolean("firstRun", true).commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
                        Set<String> wsFiles = _userPrefs.getStringSet(WORKSPACE_NAME + "_files", new HashSet<String>());
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
                        _userPrefsEditor.putStringSet(WORKSPACE_NAME + "_files", wsFiles).commit();
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
                        _userPrefsEditor.remove(WORKSPACE_NAME + "_files");
                        _userPrefsEditor.remove(WORKSPACE_NAME + "_quota");
                        _userPrefsEditor.remove(WORKSPACE_NAME + "_invitedUsers");
                        Set<String> ownPrivateWsList = _userPrefs.getStringSet(getString(WORKSPACES_LIST), new HashSet<String>());
                        ownPrivateWsList.remove(WORKSPACE_NAME);
                        _userPrefsEditor.putStringSet(getString(WORKSPACES_LIST), ownPrivateWsList);
                        Set<String> allWs = _userPrefs.getStringSet(getString(R.string.own_all_workspaces_list), new HashSet<String>());
                        allWs.remove(WORKSPACE_NAME);
                        _userPrefsEditor.putStringSet(getString(R.string.own_all_workspaces_list), allWs).commit();

                        String tags = "";
                        for (String tag : mAppContext.getTagsList()) {
                            tags += tag + ";";
                        }
                        for (String email : _emailsList) {
                            Log.w("PrivateWS", "Removed email: " + email);
                            String destIp = mAppContext.getVirtIpByEmail().get(email);
                            (new Thread(new OutgoingCommTaskThread(mAppContext, OwnWorkspaceActivity.this, destIp, mAppContext.getVirtualIp() + ";EMAIL_REMOVED_FROM_WS;" + tags))).start();
                        }


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

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Publish " + WORKSPACE_NAME + "?")
                .setView(customView)
                .setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        _userPrefsEditor.putBoolean(WORKSPACE_NAME + "_private", false);
                        Set<String> ownPrivateWsList = _userPrefs.getStringSet(getString(R.string.own_private_workspaces_list), new HashSet<String>());
                        ownPrivateWsList.remove(WORKSPACE_NAME);
                        _userPrefsEditor.putStringSet(getString(R.string.own_private_workspaces_list), ownPrivateWsList);
                        Set<String> ownPublicWsList = _userPrefs.getStringSet(getString(R.string.own_public_workspaces_list), new HashSet<String>());
                        ownPublicWsList.add(WORKSPACE_NAME);
                        _userPrefsEditor.putStringSet(getString(R.string.own_public_workspaces_list), ownPublicWsList);
                        _userPrefsEditor.commit();
                        Intent intent = new Intent(SUBCLASS_CONTEXT, OwnPublicWorkspaceActivity.class);
                        intent.putExtra("workspace_name", WORKSPACE_NAME);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();


    }
    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
        if (simWifiP2pInfo.askIsConnected()) {
            for (String deviceName : simWifiP2pInfo.getDevicesInNetwork()) {
                SimWifiP2pDevice device = simWifiP2pDeviceList.getByName(deviceName);
                final String destIp = device.getVirtIp();

                OwnWorkspaceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("BCast", "sending refresh lists to" + destIp);
                        (new Thread(new OutgoingCommTaskThread(mAppContext, OwnWorkspaceActivity.this, destIp, mAppContext.getVirtualIp() + ";REFRESH_LIST;" + mAppContext.getLocalEmail() + ";"))).start();
                    }
                });
            }
        }
    }

    public void userLeft(String email) {
        _emailsList.remove(email);
        _emailsAdapter.notifyDataSetChanged();
    }

}
