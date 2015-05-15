package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;

public class ReadTextFileActivityForeign extends ActionBarActivity implements SimWifiP2pManager.GroupInfoListener {

    private String WORKSPACE_DIR_STRING;
    private SharedPreferences _userPrefs;
    private SharedPreferences _appPrefs;
    private String LOCAL_EMAIL;
    private String FILENAME;
    private File WORKSPACE_DIR_FILE;
    protected String WORKSPACE_NAME;
    private long WORKSPACE_QUOTA;
    private File _appDir;

    protected GlobalClass mAppContext;
    private IntentFilter filter;
    private SimWifiP2pBroadcastReceiverReadTextFileForeign receiver;
    private String mMsg;
    private String mDestIp;
    private ArrayList<String> mDevicesInNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text_file);
        mAppContext = (GlobalClass) getApplicationContext();
        Intent intent = getIntent();
        FILENAME = intent.getExtras().getString("FILENAME");
        getSupportActionBar().setTitle(FILENAME);
        WORKSPACE_DIR_STRING = intent.getExtras().getString("WORKSPACE_DIR");
        WORKSPACE_NAME = intent.getExtras().getString("WORKSPACE_NAME");
        _appPrefs = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        LOCAL_EMAIL = _appPrefs.getString("email", "");
        _userPrefs = getSharedPreferences(getString(R.string.app_preferences) + "_" + LOCAL_EMAIL, MODE_PRIVATE);
        _appDir = new File(getApplicationContext().getFilesDir(), LOCAL_EMAIL);
        WORKSPACE_DIR_FILE = new File(_appDir, WORKSPACE_DIR_STRING);
        WORKSPACE_QUOTA = _userPrefs.getLong(WORKSPACE_NAME + "_quota", 0);
        mDevicesInNetwork = new ArrayList<String>();
    }

    public void isOwnerGone(Set<String> devicesInNetwork) {
        String wsName = WORKSPACE_NAME;
        String owner;
        Hashtable<String, String> owners = mAppContext.getWsOwners();
        Log.w("ForeignActivity", "isOwnerGone; wsName: " + wsName);
        if (owners.containsKey(wsName)) {
            owner = owners.get(wsName);
            Log.w("ForeignActivity", "Owner: " + owner + " exists");

            //check peers and see if owner is still there
            if (!devicesInNetwork.contains(owner.toString())) {
                //go back to foreignactivitylist
                Intent intent = new Intent(getApplicationContext(), ForeignWorkspacesListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {

        mDevicesInNetwork.clear();
        if (mAppContext.isBound() && groupInfo.askIsConnected()) {
            // compile list of network members
            if (mAppContext.getVirtualIp() == null) {
                String myName = groupInfo.getDeviceName();
                SimWifiP2pDevice myDevice = devices.getByName(myName);
                if (myDevice != null)
                    mAppContext.setVirtualIp(myDevice.getVirtIp());
            }
            //TODO
            //String msg_readFile = mAppContext.getVirtualIp() + ";WS_FILE_READ;" + _fileNamesList.get(fileName_index) + ";

            for (String deviceName : groupInfo.getDevicesInNetwork()) {
                SimWifiP2pDevice device = devices.getByName(deviceName);
                String peer = device.getVirtIp();
                mDevicesInNetwork.add(peer);
                mDestIp = peer;
                //TODO
                //mMsg = msg_readFile;
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new Thread(new OutgoingCommTaskThread(mAppContext, ReadTextFileActivityForeign.this, mDestIp, mMsg))).start();

                    }
                });
                Log.w("ForeignList", "Mesg: " + mMsg + " submitted to OutTask with destIP: " + mDestIp);
//                new OutgoingCommTask(mAppContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, peer, msg_files);
            }

        } else
            Toast.makeText(this, "Service not bound", Toast.LENGTH_LONG).show();
    }

    public void registerSimWifiP2pBcastReceiver() {
        // register broadcast receiver
        filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        receiver = new SimWifiP2pBroadcastReceiverReadTextFileForeign(this, mAppContext);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSimWifiP2pBcastReceiver();
        mAppContext.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void updateText(final String filename) {


        final File textFile = new File(this.WORKSPACE_DIR_FILE, filename);
//        Toast.makeText(SUBCLASS_CONTEXT,dir.getName()+" size: "+Double.toString(MemoryHelper.fileSizeInKB(textFile)),Toast.LENGTH_LONG).show();
        //Read text from file
        final StringBuilder builtText = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(textFile));
            String line;
            while ((line = br.readLine()) != null) {
                builtText.append(line);
                builtText.append('\n');
            }
            br.close();
        } catch (IOException e) {
//            Log.d("IOException", e.);
            Toast.makeText(this, "Error opening " + filename + ". Please try again.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        TextView tv_text = (TextView) findViewById(R.id.tv_text);
        tv_text.setMovementMethod(new ScrollingMovementMethod());
        tv_text.setText(builtText);

        final String text = builtText.toString();
        Button bt_edit_file = (Button) findViewById(R.id.bt_edit_file);
        bt_edit_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextFileInDialog(filename, textFile, text);
            }
        });

    }

    private void editTextFileInDialog(final String filename, final File textFile, final String oldText) {

        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.dialog_edit_text_file, null);
        final EditText et_text = (EditText) customView.findViewById(R.id.et_text);
        et_text.setText(oldText);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit " + filename)
                .setView(customView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newText = et_text.getText().toString().trim();
                        try {
                            if (!textFile.exists()) {
                                textFile.createNewFile();
                            }

                            FileWriter fileWritter = new FileWriter(textFile, false);
                            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                            bufferWritter.write(newText);
                            bufferWritter.close();
                            if (quotaExceeded()) {
                                FileWriter fileWriterOld = new FileWriter(textFile, false);
                                BufferedWriter bufferWriterOld = new BufferedWriter(fileWriterOld);
                                bufferWriterOld.write(oldText);
                                bufferWriterOld.close();
                                Toast.makeText(ReadTextFileActivityForeign.this, "Workspace quota exceeded. Please write a shorter text or delete some files.", Toast.LENGTH_LONG).show();
                                editTextFileInDialog(filename, textFile, newText);
                            }
                            updateText(filename);
                            //TODO VERIFY IF SAVING FILES GOES ABOVE WS QUOTA
                            return;

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ReadTextFileActivityForeign.this, "Error saving file. Try again", Toast.LENGTH_LONG).show();
                            editTextFileInDialog(filename, textFile, newText);
                            return;
                        }
                    }
                })
                .setNegativeButton("Cancel", null).create();
        dialog.show();
    }

    public boolean quotaExceeded() {

        long wsSize = folderSize(WORKSPACE_DIR_FILE);

        if(wsSize > WORKSPACE_QUOTA)
            return true;

        return false;
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_read_text_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
