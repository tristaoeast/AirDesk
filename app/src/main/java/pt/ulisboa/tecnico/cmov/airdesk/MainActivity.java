package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et_user = (EditText) findViewById(R.id.et_username);
        et_user.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    EditText et_username = (EditText) findViewById(R.id.et_username);
                    String username = et_username.getText().toString();
                    //Toast.makeText(ListNotesActivity.this, "Title: " + noteTitle + "\nText: " + noteText, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, WorkspaceActivity.class);
                    intent.putExtra("LOCAL_USERNAME", username);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
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
        //Toast.makeText(ListNotesActivity.this, "Title: " + noteTitle + "\nText: " + noteText, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, WorkspaceActivity.class);
        intent.putExtra("LOCAL_USERNAME", username);
        startActivity(intent);
    }
}
