package smd.ViewController;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import smd.Model.Alarm;
import smd.Model.Pill;
import smd.Model.PillBox;

import smd.Model.PillComparator;
import smd.ViewController.adapter.ExpandableListAdapter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import smd.pillapp.R;

/**
 * This activity handles the view and controller of the pillbox page, where
 * the user can view alarms by pills and edit or delete an alarm
 */
public class PillBoxActivity extends ActionBarActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    // This data structure allows us to get the ids of the alarms we want to edit
    // and store them in the tempId in the pill box model. The structure is similar
    // to the struture of listDataChild.
    List<List<List<Long>>> alarmIDData;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pill Box");
        setContentView(R.layout.activity_pill_box);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        PillBox pillBox= new PillBox();
       // pillBox.syncdown(getApplicationContext());
        try {
            prepareListData();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                String temp=listDataHeader.get(groupPosition);
                String temp2=temp.substring(0,temp.length()-("                    Container:                     \nRemaining: ").length()+2);
                Toast.makeText(getApplicationContext(),temp2
                        + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                String temp=listDataHeader.get(groupPosition);
                String temp2=temp.substring(0,temp.length()-("                    Container:                     \nRemaining: ").length()+2);
                Toast.makeText(getApplicationContext(),temp2 + " Collapsed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                PillBox pillBox = new PillBox();
                int i = alarmIDData.get(groupPosition).size();

                if(childPosition==i)
                {
                    try {
                        pillBox.setTempIds(alarmIDData.get(groupPosition).get(childPosition-1));
                        List<Long> tempIds = pillBox.getTempIds();
                        Alarm firstAlarm = pillBox.getAlarmById(getApplicationContext(), tempIds.get(0));
                        Pill pill = pillBox.getPillByName(getApplication(),firstAlarm.getPillName());
                        pillBox.updatePillRemaining(getApplicationContext(),pill.getPillId(),"7");
                        Toast.makeText(getApplicationContext(), firstAlarm.getPillName()+" is Refilled",Toast.LENGTH_LONG).show();
                        prepareListData();
                        listAdapter = new ExpandableListAdapter(getBaseContext(), listDataHeader, listDataChild);
                        expListView.setAdapter(listAdapter);
                        return false;
                    }
                    catch (URISyntaxException e) {
                        e.printStackTrace();
                    }


                }

                pillBox.setTempIds(alarmIDData.get(groupPosition).get(childPosition));
                pillBox.syncup(getApplicationContext());
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }

    @Override
    /** Inflate the menu; this adds items to the action bar if it is present */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pill_box, menu);
        return true;
    }

    @Override
    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
        startActivity(returnHome);
        finish();
        return super.onOptionsItemSelected(item);
    }




    /** Preparing the list data */
    private void prepareListData() throws URISyntaxException {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        alarmIDData = new ArrayList<List<List<Long>>>();

        PillBox pillbox = new PillBox();
        List<Pill> pills = pillbox.getPills(this);
        Collections.sort(pills, new PillComparator());

        for (Pill pill: pills){
            String name = pill.getPillName();
            String containername= pill.getContainerName();
            StringBuilder sb = new StringBuilder();
            sb.append(name).append("                    Container : ").append(containername).append("                    Remaining: ").append(pill.getRemainingPills());
            listDataHeader.add(sb.toString());
            List<String> times = new ArrayList<String>();
            List<Alarm> alarms = pillbox.getAlarmByPill(this.getBaseContext(), name);
            List<List<Long>> ids = new ArrayList<List<Long>>();

            for (Alarm alarm :alarms){
                String time = alarm.getStringTime() + daysList(alarm);
                times.add(time);
                ids.add(alarm.getIds());
            }

            times.add("Refill");
            alarmIDData.add(ids);
            listDataChild.put(sb.toString(), times);
        }
        return;
    }

    /**
     * Helper function to obtain a string of the days of the week
     * that can be used as a boolean list
     */
    private String daysList(Alarm alarm){
        String days = "#";
        for(int i=0; i<7; i++){
            if (alarm.getDayOfWeek()[i])
                days += "1";
            else
                days += "0";
        }
        return days;
    }

    @Override
    public void onBackPressed() {
        Intent returnHome = new Intent(getBaseContext(), MainActivity.class);
        startActivity(returnHome);
        finish();
    }

    protected void onPause()
    {
        super.onPause();
        PillBox pillBox= new PillBox();
        pillBox.syncup(getApplicationContext());
    }
}