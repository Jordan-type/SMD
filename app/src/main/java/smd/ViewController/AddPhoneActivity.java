package smd.ViewController;

import smd.Model.user;
import smd.pillapp.R;

import smd.Model.PillBox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

/**
 * Utilized the link below as a reference guide:
 * http://wptrafficanalyzer.in/blog/setting-up-alarm-using-alarmmanager-and-waking-up-screen-and-unlocking-keypad-on-alarm-goes-off-in-android/
 *
 * This activity handles the view and controller of the add page, where the user can add an alarm
 */
public class AddPhoneActivity extends ActionBarActivity{

    PillBox pillBox = new PillBox();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addphone);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        EditText editText = (EditText) findViewById(R.id.PhoneNumber);
        final EditText usernametext = (EditText) findViewById(R.id.Username);
        final EditText passwordtext = (EditText) findViewById(R.id.Passwords);
        //pillBox.syncdown(getApplicationContext());
        String phonenumber= pillBox.getphonenumber(getApplicationContext());

            editText.setText(phonenumber);


        OnClickListener setClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.PhoneNumber);
                String phonenumber = editText.getText().toString().trim();

                if(phonenumber.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Empty Number isn't allowed", Toast.LENGTH_SHORT).show();
                    return;
                }
                String phonenumberold= pillBox.getphonenumber(getApplicationContext());
                if(!phonenumberold.equals("f")) {
                    pillBox.updatenumber(getApplicationContext(),phonenumber);
                    Toast.makeText(getBaseContext(), "Phone Number is set successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pillBox.addphonenumber(getApplicationContext(),phonenumber);
                    Toast.makeText(getBaseContext(), "Phone Number is set successfully", Toast.LENGTH_SHORT).show();
                }
                String username = usernametext.getText().toString().trim();
                String password = passwordtext.getText().toString().trim();
                if(!username.isEmpty()&&!password.isEmpty())
                {
                    List<user> users  = pillBox.getallusers(getApplicationContext());
                    boolean flag= false;
                    for(user u : users)
                    {
                        if (u.getUsername().equals(username))
                        {
                            if(u.getPassword().equals(password))
                            {
                                Toast.makeText(getBaseContext(),"Duplicate user entry",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                pillBox.updateuser(getApplicationContext(),username,password);
                                Toast.makeText(getBaseContext(),"user credentials updated",Toast.LENGTH_SHORT).show();
                            }
                            flag=true;
                            break;
                        }
                    }

                    if(!flag)
                    {
                        pillBox.adduser(getApplicationContext(),username,password);
                        Toast.makeText(getBaseContext(),"New user added",Toast.LENGTH_SHORT).show();
                    }

                }
                pillBox.syncup(getApplicationContext());
                Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
                startActivity(returnHome);
                finish();
            }
        };

        OnClickListener cancelClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
                startActivity(returnHome);
                finish();
            }
        };
        Button btnSetPhone= (Button) findViewById(R.id.btn_set_phone);
        btnSetPhone.setOnClickListener(setClickListener);

        Button btnQuitphone = (Button) findViewById(R.id.btn_cancel_phone);
        btnQuitphone.setOnClickListener(cancelClickListener);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
        startActivity(returnHome);
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
        startActivity(returnHome);
        finish();
    }

    protected  void onResume()
    {
        super.onResume();


    }

    protected void onPause()
    {
        super.onPause();
    }
}
