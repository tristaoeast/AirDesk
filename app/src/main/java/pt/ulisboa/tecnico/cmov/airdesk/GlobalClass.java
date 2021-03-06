package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.Hashtable;

import pt.inesc.termite.wifidirect.SimWifiP2pManager;

/**
 * Created by ist167092 on 14-05-2015.
 */
public class GlobalClass extends Application {

    private boolean mInAGroup = false;

    private ActionBarActivity mCurrentActivity;

    private String mVirtualIp = null;

    private SharedPreferences mAppPrefs;
    private SharedPreferences mUserPrefs;

    private ArrayList<String> mForeignTagsList = new ArrayList<String>();

    private String mLocalUsername;
    private String mLocalEmail;
    private boolean mBound = false;

    private Messenger mService;
    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel;

    private Hashtable<String, Long> mSubscribedWorkspaces = new Hashtable<String, Long>();
    private Hashtable<String, Long> mInvitedWorkspaces = new Hashtable<String, Long>();
    private Hashtable<String, Long> mForeignWorkspaces = new Hashtable<String, Long>();

    private Hashtable<String, String> mWsOwners = new Hashtable<String, String>();
    private Hashtable<String, ArrayList<String>> mOwnersWs = new Hashtable<String, ArrayList<String>>();

    private Hashtable<String, ArrayList<String>> mOwnersWsFiles = new Hashtable<String, ArrayList<String>>();

    private Hashtable<String,String> mVirtIpByEmail = new Hashtable<String,String>();

    public Hashtable<String, String> getVirtIpByEmail() {
        return mVirtIpByEmail;
    }

    public void setVirtIpByEmail(Hashtable<String, String> mVirtIpByEmail) {
        this.mVirtIpByEmail = mVirtIpByEmail;
    }

    public String addVirtIpByEmail(String email, String ip){
        return mVirtIpByEmail.put(email,ip);
    }

    public Hashtable<String, Long> getForeignWorkspaces() {
        return mForeignWorkspaces;
    }

    public void setForeignWorkspaces(Hashtable<String, Long> mForeignWorkspaces) {
        this.mForeignWorkspaces = mForeignWorkspaces;
    }

    public void clearSubscribedWorkspaces() {
        mSubscribedWorkspaces = new Hashtable<String, Long>();
    }

    public void clearInvitedWorkspaces() {
        mInvitedWorkspaces = new Hashtable<String, Long>();
    }
    public void clearForeignWorkspaces() {
        mForeignWorkspaces = new Hashtable<String, Long>();
    }

    public ArrayList<String> getWsNameFiles(String wsName) {
        return mOwnersWsFiles.get(wsName);
    }

    public void addOwnersWsFiles(String wsName, String fileName) {
        if (!mOwnersWsFiles.containsKey(wsName)) {
            ArrayList<String> values = new ArrayList<String>();
            values.add(fileName);
            android.util.Log.w("GLOBAL CLASS", "ADD to WS: " + wsName + "filename: " + fileName);
            this.mOwnersWsFiles.put(wsName, values);
        } else {
            ArrayList<String> moreValues = mOwnersWsFiles.get(wsName);
            if (!moreValues.contains(fileName)) {
                moreValues.add(fileName);
                android.util.Log.w("GLOBAL CLASS", "ADD to WS: " + wsName + "filename: " + fileName);
                this.mOwnersWsFiles.put(wsName, moreValues);
            }
        }
    }

    public boolean isInAGroup() {
        return mInAGroup;
    }

    public void setInAGroup(boolean mInAGroup) {
        this.mInAGroup = mInAGroup;
    }

    public ArrayList<String> getTagsList() {
        return mForeignTagsList;
    }

    public void setTagsList(ArrayList<String> _tagsList) {
        this.mForeignTagsList = _tagsList;
    }

    public void addTag(String tag) {
        mForeignTagsList.add(tag);
    }

    public void removeTag(String tag) {
        mForeignTagsList.remove(tag);
    }

    public void removeTagPosition(int pos) {
        mForeignTagsList.remove(pos);
    }

    public String getVirtualIp() {
        return mVirtualIp;
    }

    public void setVirtualIp(String mVirtualIp) {
        this.mVirtualIp = mVirtualIp;
    }

    public ActionBarActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(ActionBarActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public Hashtable<String, Long> getInvitedWorkspaces() {
        return mInvitedWorkspaces;
    }

    public void addInvitedWorkspace(String invitedWorkspace, long quota) {
        this.mInvitedWorkspaces.put(invitedWorkspace, quota);
    }

    public Hashtable<String, Long> getSubscribedWorkspaces() {
        return mSubscribedWorkspaces;
    }

    public void addSubscribedWorkspace(String subscribedWorkspace, long quota) {
        this.mSubscribedWorkspaces.put(subscribedWorkspace, quota);
    }

    public void addForeignWorkspace(String foreignWorkspace, long quota) {
        this.mForeignWorkspaces.put(foreignWorkspace, quota);
    }

    public SharedPreferences getAppPrefs() {
        return mAppPrefs;
    }

    public void setAppPrefs(SharedPreferences mAppPrefs) {
        this.mAppPrefs = mAppPrefs;
    }

    public SharedPreferences getUserPrefs() {
        return mUserPrefs;
    }

    public void setUserPrefs(SharedPreferences mUserPrefs) {
        this.mUserPrefs = mUserPrefs;
    }

    public String getLocalUsername() {
        return mLocalUsername;
    }

    public void setLocalUsername(String mLocalUsername) {
        this.mLocalUsername = mLocalUsername;
    }

    public String getLocalEmail() {
        return mLocalEmail;
    }

    public void setLocalEmail(String mLocalEmail) {
        this.mLocalEmail = mLocalEmail;
    }

    public Hashtable<String, String> getWsOwners() {
        return mWsOwners;
    }

    public void addWsOwners(String mWs, String mOwnerIp) {
        this.mWsOwners.put(mWs, mOwnerIp);
    }

    public void removeWsOwners(Long mOwnerIp) {
        this.mWsOwners.remove(mOwnerIp);
    }

    public Hashtable<String, ArrayList<String>> getOwnersWs() {
        return mOwnersWs;
    }

    public void addOwnersWs(String mOwnerIp, String mWs) {
        if (!mWsOwners.containsKey(mOwnerIp)) {
            ArrayList<String> values = new ArrayList<String>();
            values.add(mWs);
            this.mOwnersWs.put(mOwnerIp, values);
        } else {
            ArrayList<String> moreValues = mOwnersWs.get(mOwnerIp);
            if (!moreValues.contains(mWs)) {
                moreValues.add(mWs);
                this.mOwnersWs.put(mOwnerIp, moreValues);
            }
        }
    }

    public void removeOwnersWs(String mWs) {
        this.mOwnersWs.remove(mWs);
    }

    public boolean isBound() {
        return mBound;
    }

    public void setBound(boolean mBound) {
        this.mBound = mBound;
    }

    public Messenger getService() {
        return mService;
    }

    public void setService(Messenger mService) {
        this.mService = mService;
    }

    public SimWifiP2pManager getManager() {
        return mManager;
    }

    public void setManager(SimWifiP2pManager mManager) {
        this.mManager = mManager;
    }

    public SimWifiP2pManager.Channel getChannel() {
        return mChannel;
    }

    public void setChannel(SimWifiP2pManager.Channel mChannel) {
        this.mChannel = mChannel;
    }
}
