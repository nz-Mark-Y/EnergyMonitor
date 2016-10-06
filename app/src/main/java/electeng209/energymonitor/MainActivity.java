package electeng209.energymonitor;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    ArrayList<MyData> powerArrayList = new ArrayList<>();
    ArrayList<MyData> voltageArrayList = new ArrayList<>();
    ArrayList<MyData> currentArrayList = new ArrayList<>();
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
                } else if (myUnit.equals("A")){
                    currentArrayList.add(myData);
                } else {
                    voltageArrayList.add(myData);
                }

                if (powerArrayList.size() != 0) {
                    currentPower.setText(powerArrayList.get(powerArrayList.size() -1).value + "W");
                    dataDisplay.setText("Number is: " + powerArrayList.get(powerArrayList.size() - 1).number + " Value is: " + powerArrayList.get(powerArrayList.size() - 1).value + powerArrayList.get(powerArrayList.size() - 1).unit);
                }
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
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
