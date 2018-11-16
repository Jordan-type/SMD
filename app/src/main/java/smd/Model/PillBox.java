package smd.Model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import smd.ViewController.AlertActivity;

import static android.content.Context.ALARM_SERVICE;


/**
 * Created by CharlesPK3 on 4/3/15.
 * Is used to retrieve pills and alarms by other classes.
 * Has access to and can read and write to the database.
 */
public class PillBox {
    private DbHelper db;
    private static List<Long> tempIds; // Ids of the alarms to be deleted or edited
    private static String tempName; // Ids of the alarms to be deleted or edited
    private static String tempContainer;

    public List<Long> getTempIds() { return Collections.unmodifiableList(tempIds); }

    public void setTempIds(List<Long> tempIds) { this.tempIds = tempIds; }

    public String getTempName() { return tempName; }

    public void setTempName(String tempName) { this.tempName = tempName; }

    public String getTempContainer() { return tempContainer;}

    public void setTempContainer(String tempContainer) {this.tempContainer = tempContainer;}

    public void syncup(Context c)
    {
        db= new DbHelper(c);
        List<Pill> allpills = db.getAllPills();

        StringBuilder pillquery = new StringBuilder();
        //INSERT INTO tbl_name (a,b,c) VALUES(1,2,3),(4,5,6),(7,8,9);
        pillquery.append("DELETE FROM pills;");
        pillquery.append("INSERT INTO pills ( id, pillName, remaining, container ) values  ");
        for(Pill pill:allpills)
        {   pillquery.append("(");
            pillquery.append(pill.getPillId());
            pillquery.append(",'");
            pillquery.append(pill.getPillName());
            pillquery.append("',");
            pillquery.append(pill.getRemainingPills());
            pillquery.append(",");
            pillquery.append(pill.getContainerName());
            pillquery.append(")");
            pillquery.append(" , ");
        }
        pillquery.deleteCharAt(pillquery.length()-2);

        class syncup extends AsyncTask<String,Void,String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("query",params[0]);
                String HttpURL = "https://wissamantoun.000webhostapp.com/syncup.php";

                HttpParse httpParse = new HttpParse();
                String finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        syncup su = new syncup();
        su.execute(pillquery.toString());

        List<History> hall = db.getHistory();
        StringBuilder hstring = new StringBuilder();
        hstring.append("DELETE FROM histories;");
        hstring.append("INSERT INTO histories ( pillName, date, hour, minute ) values  ");
        for(History h:hall )
        {   hstring.append("('");
            hstring.append(h.getPillName());
            hstring.append("','");
            hstring.append(h.getDateString());
            hstring.append("','");
            hstring.append(h.getHourTaken());
            hstring.append("','");
            hstring.append(h.getMinuteTaken());
            hstring.append("')");
            hstring.append(" , ");
        }

        hstring.deleteCharAt(hstring.length()-2);
        su = new syncup();
        su.execute(hstring.toString());

        List<Alarm> allalarms = db.getallAlarms();
        StringBuilder alarmsstring = new StringBuilder();
        alarmsstring.append("DELETE FROM alarms;");
        alarmsstring.append("INSERT INTO alarms ( id , hour , minute, pillName, day_of_week ) values  ");
        for(Alarm a : allalarms )
        {   alarmsstring.append("('");
            alarmsstring.append(a.getId());
            alarmsstring.append("','");
            alarmsstring.append(a.getHour());
            alarmsstring.append("','");
            alarmsstring.append(a.getMinute());
            alarmsstring.append("','");
            alarmsstring.append(a.getPillName());
            alarmsstring.append("','");
            alarmsstring.append(a.getDay());
            alarmsstring.append("')");
            alarmsstring.append(" , ");
        }

        alarmsstring.deleteCharAt(alarmsstring.length()-2);
        su = new syncup();
        su.execute(alarmsstring.toString());

        List<Alarmlinks> allalarmslinks = db.getallalarmlinks();
        StringBuilder alarmslinksstring = new StringBuilder();
        alarmslinksstring.append("DELETE FROM pill_alarm;");
        alarmslinksstring.append("INSERT INTO pill_alarm ( id , pill_id , alarm_id ) values  ");
        for(Alarmlinks al : allalarmslinks )
        {   alarmslinksstring.append("('");
            alarmslinksstring.append(al.getId());
            alarmslinksstring.append("','");
            alarmslinksstring.append(al.getPill_id());
            alarmslinksstring.append("','");
            alarmslinksstring.append(al.getAlarm_id());
            alarmslinksstring.append("')");
            alarmslinksstring.append(" , ");
        }

        alarmslinksstring.deleteCharAt(alarmslinksstring.length()-2);
        su = new syncup();
        su.execute(alarmslinksstring.toString());

        String number = db.getPhoneNumber();
        if(!number.equals("f")) {

            StringBuilder phonestring = new StringBuilder();
            phonestring.append("DELETE FROM phonenumber;");
            phonestring.append("INSERT INTO phonenumber ( phonenumber ) values  ");
            phonestring.append("('");
            phonestring.append(number);
            phonestring.append("')");

            su = new syncup();
            su.execute(phonestring.toString());

        }

        List<user> users = db.getallusers();
        StringBuilder userssstring = new StringBuilder();

        userssstring.append("DELETE FROM users;");
        userssstring.append("INSERT INTO users ( id , username , password ) values  ");
        for(user u : users )
        {   userssstring.append("('");
            userssstring.append(u.getId());
            userssstring.append("','");
            userssstring.append(u.getUsername());
            userssstring.append("','");
            userssstring.append(u.getPassword());
            userssstring.append("')");
            userssstring.append(" , ");
        }

        userssstring.deleteCharAt(userssstring.length()-2);
        su = new syncup();
        su.execute(userssstring.toString());
    }

    public void syncdown(Context c)
    {
        db = new DbHelper(c);

        class getall extends AsyncTask<String,Void,String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("query",params[0]);
                String HttpURL = "https://wissamantoun.000webhostapp.com/getall.php";

                HttpParse httpParse = new HttpParse();
                String JsonfinalResult = httpParse.postRequest(hashMap, HttpURL);

                return JsonfinalResult;
            }
        }

        getall g = new getall();
        g.execute("pills");

        String JSonResult = null;
        try {
            JSonResult = g.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<Pill> allpills = new ArrayList<>();

        if(JSonResult != null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(JSonResult);
                JSONObject jsonObject;
                Pill pill;
                allpills = new ArrayList<>();
                for(int i=0; i<jsonArray.length(); i++)
                {
                    pill= new Pill();
                    jsonObject = jsonArray.getJSONObject(i);
                    //Adding pill members.
                    pill.setPillId(Long.parseLong(jsonObject.getString("id").toString()));
                    pill.setPillName(jsonObject.get("pillName").toString());
                    pill.setRemainingPills(jsonObject.get("remaining").toString());
                    pill.setContainerName(jsonObject.getString("container").toString());
                    allpills.add(pill);
                }
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        db.insertpilltable(allpills);

        g = new getall();
        g.execute("histories");
        try {
            JSonResult = g.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<History> allhistory = new ArrayList<>();
        if(JSonResult != null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(JSonResult);
                JSONObject jsonObject;
                History hist;
                allhistory = new ArrayList<>();
                for(int i=0; i<jsonArray.length(); i++)
                {
                    hist = new History();
                    jsonObject = jsonArray.getJSONObject(i);
                    hist.setPillName(jsonObject.getString("pillName").toString());
                    hist.setDateString(jsonObject.getString("date").toString());
                    hist.setHourTaken(Integer.parseInt(jsonObject.getString("hour").toString()));
                    hist.setMinuteTaken(Integer.parseInt(jsonObject.getString("minute").toString()));
                    allhistory.add(hist);
                }
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        db.inserthisttable(allhistory);

        g = new getall();
        g.execute("alarms");
        try {
            JSonResult = g.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<Alarm> allalarms = new ArrayList<>();

        if(JSonResult != null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(JSonResult);
                JSONObject jsonObject;
                Alarm al;
                allalarms = new ArrayList<>();
                for(int i=0; i<jsonArray.length(); i++)
                {
                    al=new Alarm();
                    jsonObject = jsonArray.getJSONObject(i);
                    al.setId(Long.parseLong(jsonObject.getString("id").toString()));
                    al.setPillName(jsonObject.getString("pillName").toString());
                    al.setDay(Integer.parseInt(jsonObject.getString("day_of_week").toString()));
                    al.setHour(Integer.parseInt(jsonObject.getString("hour").toString()));
                    al.setMinute(Integer.parseInt(jsonObject.getString("minute").toString()));
                    allalarms.add(al);
                }
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        List<Alarm> exalarms = new ArrayList<>();
        exalarms=db.getallAlarms();

        for(Alarm exa : exalarms)
        {
            Intent intent = new Intent(c, AlertActivity.class);
            PendingIntent operation = PendingIntent.getActivity(c ,(int) exa.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) c.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(operation);
        }
        db.insertalarms(allalarms);
        List<Alarm> nalarms = new ArrayList<>();
        nalarms= db.getallAlarms();

        for(Alarm nal : nalarms)
        {
            Intent intent = new Intent(c, AlertActivity.class);
            intent.putExtra("pill_name", nal.getPillName());

            PendingIntent operation = PendingIntent.getActivity(c, (int) nal.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            /** Getting a reference to the System Service ALARM_SERVICE */
            AlarmManager alarmManager = (AlarmManager) c.getSystemService(ALARM_SERVICE);

            /** Creating a calendar object corresponding to the date and time set by the user */
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, nal.getHour());
            calendar.set(Calendar.MINUTE, nal.getMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.DAY_OF_WEEK, nal.getDay());

            /** Converting the date and time in to milliseconds elapsed since epoch */
            long alarm_time = calendar.getTimeInMillis();

            if (calendar.before(Calendar.getInstance()))
                alarm_time += AlarmManager.INTERVAL_DAY * 7;

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time,
                    alarmManager.INTERVAL_DAY * 7, operation);
        }


        g = new getall();
        g.execute("pill_alarm");
        try {
            JSonResult = g.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<Alarmlinks> allalarmslink = new ArrayList<>();

        if(JSonResult != null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(JSonResult);
                JSONObject jsonObject;
                Alarmlinks al;

                for(int i=0; i<jsonArray.length(); i++)
                {
                    al=new Alarmlinks();
                    jsonObject = jsonArray.getJSONObject(i);
                    al.setId(Integer.parseInt(jsonObject.getString("id").toString()));
                    al.setAlarm_id(Integer.parseInt(jsonObject.getString("alarm_id").toString()));
                    al.setPill_id(Integer.parseInt(jsonObject.getString("pill_id").toString()));
                    allalarmslink.add(al);
                }
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        db.insertalarmslink(allalarmslink);

        g = new getall();
        g.execute("phonenumber");
        try {
            JSonResult = g.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String phonenumber= new String();

        if(JSonResult != null)
        {
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(JSonResult);
                JSONObject jsonObject;
                jsonObject = jsonArray.getJSONObject(0);
                phonenumber= jsonObject.getString("phonenumber").toString();
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        db.insertphonenuber(phonenumber);
    }


    public List<Pill> getPills(Context c) {
        db = new DbHelper(c);
        List<Pill> allPills = db.getAllPills();
        db.close();
        return allPills;
    }

    public long addPill(Context c, Pill pill) {
        db = new DbHelper(c);
        long pillId = db.createPill(pill);
        pill.setPillId(pillId);
        db.close();
        return pillId;
    }

    public Pill getPillByName(Context c, String pillName){
        db = new DbHelper(c);
        Pill wantedPill = db.getPillByName(pillName);
        db.close();
        return wantedPill;
    }
    public String getphonenumber(Context c)
    {
        db = new DbHelper(c);
        String number = db.getPhoneNumber();
        db.close();
        return number;
    }

    public void addphonenumber(Context c, String phonenumber)
    {
        db = new DbHelper(c);
        db.addphonenumber(phonenumber);
        db.close();
        return;
    }

    public void updatenumber(Context c, String number)
    {
        db = new DbHelper(c);
        db.UpdateNumber(number);
        db.close();
        return;
    }
    public void addAlarm(Context c, Alarm alarm, Pill pill){
        db = new DbHelper(c);
        db.createAlarm(alarm, pill.getPillId());
        db.close();
    }


    public List<Alarm> getAlarms(Context c, int dayOfWeek) throws URISyntaxException {
        db = new DbHelper(c);
        List<Alarm> daysAlarms= db.getAlarmsByDay(dayOfWeek);
        db.close();
        Collections.sort(daysAlarms);
        return daysAlarms;
    }

    public List<Alarm> getAlarmByPill (Context c, String pillName) throws URISyntaxException {
        db = new DbHelper(c);
        List<Alarm> pillsAlarms = db.getAllAlarmsByPill(pillName);
        db.close();
        return pillsAlarms;
    }

    public boolean pillExist(Context c, String pillName) {
        db = new DbHelper(c);
        for(Pill pill: this.getPills(c)) {
            if(pill.getPillName().equals(pillName))
                return true;
        }
        return false;
    }



    public boolean containerExist(Context c, String containerName)
    {
        db=new DbHelper(c);
        for(Pill pill: this.getPills(c)) {
            if(pill.getContainerName().equals(containerName))
                return true;
        }
        return false;
    }

    public void deletePill(Context c, String pillName) throws URISyntaxException {
        db = new DbHelper(c);
        db.deletePill(pillName);
        db.close();
    }

    public void updatePillRemaining(Context c,long pill_id, String Remaining)
    {
        db= new DbHelper(c);
        db.updatePillRemaining(Long.toString(pill_id),Remaining);
        db.close();
    }

    public void updatePillContainer(Context c,long pill_id, String Container)
    {
        db= new DbHelper(c);
        db.updatePillContainer(Long.toString(pill_id),Container);
        db.close();
    }
    public void deleteAlarm(Context c, long alarmId) {
        db = new DbHelper(c);
        db.deleteAlarm(alarmId);
        db.close();
    }

    public void addToHistory(Context c, History h){
        db = new DbHelper(c);
        db.createHistory(h);
        db.close();
    }

    public List<History> getHistory (Context c){
        db = new DbHelper(c);
        List<History> history = db.getHistory();
        db.close();
        return history;
    }

    public Alarm getAlarmById(Context c, long alarm_id) throws URISyntaxException{
        db = new DbHelper(c);
        Alarm alarm = db.getAlarmById(alarm_id);
        db.close();
        return alarm;
    }

    public int getDayOfWeek(Context c, long alarm_id) throws URISyntaxException{
        db = new DbHelper(c);
        int getDayOfWeek = db.getDayOfWeek(alarm_id);
        db.close();
        return getDayOfWeek;
    }

    public void adduser(Context c ,String username, String password)
    {
        db= new DbHelper(c);
        db.adduser(username,password);
        db.close();
    }

    public void addusers(Context c, List<user> users)
    {
        db = new DbHelper(c);
        db.insertusers(users);
        db.close();
    }

    public List<user> getallusers(Context c)
    {
        db = new DbHelper(c);
        List<user> us = db.getallusers();
        db.close();
        return us;
    }

    public void updateuser(Context c, String username, String password) {
        db=new DbHelper(c);
        db.updateuser(username,password);
        db.close();
    }
}
