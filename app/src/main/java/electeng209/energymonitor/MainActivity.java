package electeng209.energymonitor;


import android.support.v7.app.ActionBar; //Imports for generic app functionality
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import java.util.ArrayList;
import android.view.View;
import android.content.Intent;

import com.google.firebase.database.ChildEventListener; //Imports for firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.pawelkleczkowski.customgauge.CustomGauge; //Import for custom gauges


public class MainActivity extends AppCompatActivity {

    ArrayList<MyData> powerArrayList = new ArrayList<>();//ArrayLists to store all the data
    ArrayList<MyData> voltageArrayList = new ArrayList<>();
    ArrayList<MyData> currentArrayList = new ArrayList<>();
    ArrayList<Long> powerTimeStampList = new ArrayList<>();
    float totalEnergyUsed = (float)0.0; //Initially total energy used is 0
    private CustomGauge gauge1;//Custom gauge initialization



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Restored previously saved instance
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Override deafult actionbar at top
        getSupportActionBar().setCustomView(R.layout.actionbar);

    }

    @Override
    protected void onStart() {
        super.onStart();

        final TextView dataDisplay = (TextView) findViewById(R.id.textView); //Variables of used text fields in layout
        final TextView currentPower = (TextView) findViewById(R.id.powerDisplayed);
        final TextView currentVoltage = (TextView) findViewById(R.id.voltageDisp);
        final TextView currentCurrent = (TextView) findViewById(R.id.currentDisp);
        final TextView totalEnergy = (TextView) findViewById(R.id.totalPowerDisplayed);

        FirebaseDatabase database = FirebaseDatabase.getInstance();//Database initialization
        DatabaseReference myRef = database.getReference("data");//data is stored under data field on database
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String dataString = dataSnapshot.getValue(String.class);//Read from database each time new data is added
                String[] dataArray = dataString.split(",");//Data extraction
                int myNumber = Integer.parseInt(dataArray[0].substring(16));
                String myUnit = dataArray[1].substring(dataArray[0].lastIndexOf(":"),dataArray[0].lastIndexOf(":")+1);
                float myValue = Float.parseFloat(dataArray[2].substring(dataArray[2].lastIndexOf(":")+2,dataArray[2].lastIndexOf("}")));

                if (myUnit.equals("W")) {//Filtering out impossibly large values for each based on unit
                    if (myValue > 9){
                        myValue = 9;
                    }
                } else if (myUnit.equals("A")){
                    if (myValue > 1){
                        myValue = 1;
                    }
                } else if (myUnit.equals("V")){
                    if (myValue > 14.5){
                        myValue = (float)14.5;
                    }
                }

                MyData myData = new MyData(myNumber, myValue, myUnit);//Store the data in custom class
                if (myUnit.equals("W")) {//Storage of read data
                    powerArrayList.add(myData);
                    gauge1 = (CustomGauge) findViewById(R.id.gauge1);
                    gauge1.setValue((int)(myValue*100));//Set the gauge value
                    currentPower.setText(powerArrayList.get(powerArrayList.size() -1).value + "W");//Change text

                    powerTimeStampList.add(System.currentTimeMillis());//Time stamping when the value was received
                    if(powerTimeStampList.size() > 1) {
                        float timeDifference = ((powerTimeStampList.get(powerTimeStampList.size() - 1)) - (powerTimeStampList.get(powerTimeStampList.size() - 2))); //Time difference in milliseconds
                        float energySince = (powerArrayList.get(powerArrayList.size()-1).value + powerArrayList.get(powerArrayList.size()-2).value) / 2 * timeDifference; //power difference times time
                        totalEnergyUsed += energySince / 3600000; //conversion to Wh from Wms
                        totalEnergy.setText(String.format( "%.3f", totalEnergyUsed )+ "Wh");//Display it to 3 dp
                    }
                } else if (myUnit.equals("A")){//Storage of current
                    currentArrayList.add(myData);
                    gauge1 = (CustomGauge) findViewById(R.id.gauge);
                    gauge1.setValue((int)(myValue*1000));//Set the gauge value
                    currentCurrent.setText(currentArrayList.get(currentArrayList.size() -1).value + "A");//Change text
                } else {
                    voltageArrayList.add(myData);
                    gauge1 = (CustomGauge) findViewById(R.id.gauge2);
                    gauge1.setValue((int)(myValue*100));//Set the gauge value
                    currentVoltage.setText(voltageArrayList.get(voltageArrayList.size() -1).value + "V");//Change the text
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                dataDisplay.setText("Data Cleared Remotely");//When data is cleared remotely
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Button graphsButton = (Button) findViewById(R.id.graphButton);//Button functionality change tabs
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
        Intent intent = new Intent(this, Graphs.class);//Switch to graphs tab
        startActivity(intent);
    }

    private void goToSettings() {
        Intent intent = new Intent(this, About.class);//Switch to about tab
        startActivity(intent);
    }
}
