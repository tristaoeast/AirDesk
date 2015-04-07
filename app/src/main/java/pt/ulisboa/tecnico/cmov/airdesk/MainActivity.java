package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    SharedPreferences _prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _prefs = getSharedPreferences(getString(R.string.activity_login_shared_preferences), MODE_PRIVATE);

        EditText et_user = (EditText) findViewById(R.id.et_username);
        et_user.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    login(v);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_prefs.getBoolean(getString(R.string.event_back_button_pressed), false)) {
            _prefs.edit().putBoolean(getString(R.string.event_back_button_pressed), false).commit();
            Toast.makeText(MainActivity.this, "Exit AirDesk", Toast.LENGTH_LONG).show();
            exitApp();
        } else {
            if (_prefs.getBoolean("firstRun", true)) {
//                Toast.makeText(MainActivity.this, "First run", Toast.LENGTH_LONG).show();
            } else {
                String username = _prefs.getString("username", "invalid_username");
                Toast.makeText(MainActivity.this, "Logged in as " + username, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, OwnPrivateWorkspacesListActivity.class);
                intent.putExtra("LOCAL_USERNAME", username);
                intent.putExtra("LIST_SELECTED", "OPrWS");
                startActivity(intent);
            }
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        EditText et_username = (EditText) findViewById(R.id.et_username);
        String username = et_username.getText().toString();
        _prefs.edit().putString("username", username).commit();
        _prefs.edit().putBoolean("firstRun", false).commit();
        //Toast.makeText(ListNotesActivity.this, "Title: " + noteTitle + "\nText: " + noteText, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, OwnPrivateWorkspacesListActivity.class);
        intent.putExtra("LOCAL_USERNAME", username);
        intent.putExtra("LIST_SELECTED", "OPrWS");
        startActivity(intent);
    }

    public void exitApp() {
        this.finish();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    /*int pid = android.os.Process.myPid();=====> use this if you want to kill your activity. But its not a good one to do.
    android.os.Process.killProcess(pid);*/
    }
}
