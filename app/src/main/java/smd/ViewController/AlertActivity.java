package smd.ViewController;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import smd.Model.History;
import smd.Model.Pill;
import smd.Model.PillBox;

/**
 * Utilized the link below as a reference guide:
 * http://wptrafficanalyzer.in/blog/setting-up-alarm-using-alarmmanager-and-waking-up-screen-and-unlocking-keypad-on-alarm-goes-off-in-android/
 *
 * This activity handles the view and controller of the alert page, which contains
 * a dialog fragment AlertAlarm that shows the dialog box to let the user respond to an alarm.
 * This is the "notification" we are using right now. But it only contains a dialog box so it is
 * not a real notification. We can change this to a real notification that has a ringtone or a
 * vibrating function in the future.
 */

public class AlertActivity extends FragmentActivity {

    private AlarmManager alarmManager;
    private PendingIntent operation;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static String address="20:15:03:23:69:76";
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");



    private MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       // Connect();
        /** Creating an Alert Dialog Window */
        AlertAlarm alert = new AlertAlarm();

        /** Opening the Alert Dialog Window. This will be opened when the alarm goes off */
        alert.show(getSupportFragmentManager(), "AlertAlarm");
        PillBox pillBox= new PillBox();
        //pillBox.syncdown(getApplicationContext());
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
    }

    // Snooze
    public void doNeutralClick(String pillName){
        final int _id = (int) System.currentTimeMillis();
        final long minute = 60000;
        long snoozeLength = 10;
        long currTime = System.currentTimeMillis();
        long min = currTime + minute * snoozeLength;

        Intent intent = new Intent(getBaseContext(), AlertActivity.class);
        intent.putExtra("pill_name", pillName);

        operation = PendingIntent.getActivity(getBaseContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /** Getting a reference to the System Service ALARM_SERVICE */
        alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, min, operation);
        Toast.makeText(getBaseContext(), "Alarm for " + pillName + " was snoozed for 10 minutes", Toast.LENGTH_SHORT).show();
        mp.stop();
        finish();

    }

    // I took it
    public void doPositiveClick(String pillName){
        Connect();
        PillBox pillBox = new PillBox();
        Pill pill = pillBox.getPillByName(this, pillName);
        History history = new History();

        Calendar takeTime = Calendar.getInstance();
        Date date = takeTime.getTime();
        String dateString = new SimpleDateFormat("MMM d, yyyy").format(date);

        int hour = takeTime.get(Calendar.HOUR_OF_DAY);
        int minute = takeTime.get(Calendar.MINUTE);
        String am_pm = (hour < 12) ? "am" : "pm";

        history.setHourTaken(hour);
        history.setMinuteTaken(minute);
        history.setDateString(dateString);
        history.setPillName(pillName);
        if(Integer.parseInt(pillBox.getPillByName(getApplicationContext(),pillName).getRemainingPills())>0)
        {
            pillBox.addToHistory(this, history);
            pillBox.updatePillRemaining(getApplicationContext(), pillBox.getPillByName(getApplicationContext(), pillName).getPillId(), String.valueOf(Integer.parseInt(pillBox.getPillByName(getApplicationContext(), pillName).getRemainingPills()) - 1));

            String stringMinute;
            if (minute < 10)
                stringMinute = "0" + minute;
            else
                stringMinute = "" + minute;

            int nonMilitaryHour = hour % 12;
            if (nonMilitaryHour == 0)
                nonMilitaryHour = 12;

            Toast.makeText(getBaseContext(), pillName + " was taken at " + nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".", Toast.LENGTH_SHORT).show();

            writeData('c' + pill.getContainerName());
            mp.stop();
            pillBox.syncup(getApplicationContext());
            Intent returnHistory = new Intent(getBaseContext(), MainActivity.class);
            startActivity(returnHistory);
            finish();
        }
        else{
            final int _id = (int) System.currentTimeMillis();
            final long minutes = 60000;
            long snoozeLength = 10;
            long currTime = System.currentTimeMillis();
            long min = currTime + minutes * snoozeLength;

            Intent intent = new Intent(getBaseContext(), AlertActivity.class);
            intent.putExtra("pill_name", pillName);

            operation = PendingIntent.getActivity(getBaseContext(), _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            /** Getting a reference to the System Service ALARM_SERVICE */
            alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, min, operation);
            Toast.makeText(getBaseContext(),"Not enough pills, PLEASE REFILL :"+pillName + "Alarm was snoozed for 10 minutes", Toast.LENGTH_LONG).show();
            mp.stop();
            finish();
        }
    }

    // I won't take it
    public void doNegativeClick(String pillName){
            try {
                Calendar takeTime = Calendar.getInstance();
                Date date = takeTime.getTime();
                String dateString = new SimpleDateFormat("MMM d, yyyy").format(date);

                int hour = takeTime.get(Calendar.HOUR_OF_DAY);
                int minute = takeTime.get(Calendar.MINUTE);
                String am_pm = (hour < 12) ? "am" : "pm";

                String stringMinute;
                if (minute < 10)
                    stringMinute = "0" + minute;
                else
                    stringMinute = "" + minute;

                int nonMilitaryHour = hour % 12;
                if (nonMilitaryHour == 0)
                    nonMilitaryHour = 12;
                SmsManager smsManager = SmsManager.getDefault();
                PillBox pillBox = new PillBox();

                smsManager.sendTextMessage(pillBox.getphonenumber(getApplicationContext()), null, pillName + " was not taken at "+ nonMilitaryHour + ":" + stringMinute + " " + am_pm + ".", null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        mp.stop();
        finish();
    }

    public void Connect() {

        if(mBluetoothAdapter.isEnabled()){

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_LONG).show();

            mBluetoothAdapter.cancelDiscovery();
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket.connect();
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                try {
                    btSocket.close();

                } catch (IOException e2) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Bluetooth is disabled", Toast.LENGTH_SHORT).show();
        }
    }

    protected  void onResume()
    {
        super.onResume();

    }

    protected void onPause()
    {
        super.onPause();
        PillBox pillBox= new PillBox();
        pillBox.syncup(getApplicationContext());
    }

    private void writeData(String data) {

        if(mBluetoothAdapter.isEnabled()){
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {

            }
            try {
                outStream.write(data.getBytes());
            } catch (IOException e) {

            }
        }
        else {
        }
    }
}