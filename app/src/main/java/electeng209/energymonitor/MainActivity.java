package electeng209.energymonitor;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import android.view.View;
import android.content.Intent;

import pl.pawelkleczkowski.customgauge.CustomGauge;
//ToDo Add comments to all code files of the app

public class MainActivity extends AppCompatActivity {

    ArrayList<MyData> powerArrayList = new ArrayList<>();
    ArrayList<MyData> voltageArrayList = new ArrayList<>();
    ArrayList<MyData> currentArrayList = new ArrayList<>();
    ArrayList<Long> powerTimeStampList = new ArrayList<>();
    private CustomGauge gauge1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

    }

    @Override
    protected void onStart() {
        super.onStart();

        final TextView dataDisplay = (TextView) findViewById(R.id.textView);
        final TextView currentPower = (TextView) findViewById(R.id.powerDisplayed);
        final TextView currentVoltage = (TextView) findViewById(R.id.voltageDisp);
        final TextView currentCurrent = (TextView) findViewById(R.id.currentDisp);
        final TextView totalEnergy = (TextView) findViewById(R.id.totalPowerDisplayed);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String dataString = dataSnapshot.getValue(String.class);
                String[] dataArray = dataString.split(",");
                int myNumber = Integer.parseInt(dataArray[0].substring(16));
                String myUnit = dataArray[1].substring(dataArray[0].lastIndexOf(":"),dataArray[0].lastIndexOf(":")+1);
                float myValue = Float.parseFloat(dataArray[2].substring(dataArray[2].lastIndexOf(":")+2,dataArray[2].lastIndexOf("}")));
                MyData myData = new MyData(myNumber, myValue, myUnit);
                if (myUnit.equals("W")) {
                    powerArrayList.add(myData);
                    gauge1 = (CustomGauge) findViewById(R.id.gauge1);
                    gauge1.setValue((int)(myValue*100));
                    currentPower.setText(powerArrayList.get(powerArrayList.size() -1).value + "W");
                } else if (myUnit.equals("A")){
                    currentArrayList.add(myData);
                    gauge1 = (CustomGauge) findViewById(R.id.gauge);
                    gauge1.setValue((int)(myValue*1000));
                    currentCurrent.setText(currentArrayList.get(currentArrayList.size() -1).value + "A");
                } else {
                    voltageArrayList.add(myData);
                    gauge1 = (CustomGauge) findViewById(R.id.gauge2);
                    gauge1.setValue((int)(myValue*100));
                    currentVoltage.setText(voltageArrayList.get(voltageArrayList.size() -1).value + "V");
                }
                powerTimeStampList.add(System.currentTimeMillis() / 1000);
                totalEnergy.setText("My time " + powerTimeStampList.get(powerTimeStampList.size()-1) + " " + powerTimeStampList.size());
                //System.out.println(powerArrayList.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                dataDisplay.setText("Data Cleared Remotely");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Button graphsButton = (Button) findViewById(R.id.graphButton);
        graphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGraphs();
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
    }


    private void goToGraphs() {
        Intent intent = new Intent(this, Graphs.class);
        startActivity(intent);
    }

    private void goToSettings() {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }
}
