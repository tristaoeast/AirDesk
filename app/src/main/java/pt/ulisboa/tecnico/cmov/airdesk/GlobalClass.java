package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

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

    private ArrayList<String> _tagsList;

    private String mLocalUsername;
    private String mLocalEmail;
    private boolean mBound;

    private Messenger mService;
    private SimWifiP2pManager mManager;
    private SimWifiP2pManager.Channel mChannel;

    private Hashtable<String, Long> mSubscribedWorkspaces = new Hashtable<String, Long>();
    private Hashtable<String, Long> mInvitedWorkspaces = new Hashtable<String, Long>();

    public boolean isInAGroup() {
        return mInAGroup;
    }

    public void setInAGroup(boolean mInAGroup) {
        this.mInAGroup = mInAGroup;
    }

    public ArrayList<String> getTagsList() {
        return _tagsList;
    }

    public void setTagsList(ArrayList<String> _tagsList) {
        this._tagsList = _tagsList;
    }

    public void addTag(String tag) {
        _tagsList.add(tag);
    }

    public void removeTag(String tag) {
        _tagsList.remove(tag);
    }

    public void removeTagPosition(int pos) {
        _tagsList.remove(pos);
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
        this.mSubscribedWorkspaces.put(subscribedWorkspace,quota);
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
