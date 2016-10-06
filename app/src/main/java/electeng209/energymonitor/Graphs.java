package electeng209.energymonitor;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;
import java.util.Random;

public class Graphs extends AppCompatActivity {
    int drawPower = 2;
    LineGraphSeries<DataPoint> mSeries1;
    ArrayList<MyData> powerArrayList = new ArrayList<>();
    ArrayList<MyData> voltageArrayList = new ArrayList<>();
    ArrayList<MyData> currentArrayList = new ArrayList<>();
    ArrayList<MyData> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);


/*        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default myspinner layout
        ArrayAdapter<CharSequence> adapterz = ArrayAdapter.createFromResource(this,
                R.array.graphsArray, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.myspinner,R.array.graphsArray);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the myspinner
        spinner.setAdapter(adapter);*/


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graphsArray, R.layout.myspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (arrayList.size() !=0) {
                    GraphView graph = (GraphView) findViewById(R.id.graph);
                    graph.removeAllSeries();
                    drawPower = i;
                    graphDrawer();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                //System.out.println(powerArrayList.size());
                arrayList.add(myData);
                graphDrawer();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        Button graphsButton = (Button) findViewById(R.id.realTimeButton);
        graphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRealTime();
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

    private void goToRealTime() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private DataPoint[] generateData() {
        ArrayList<MyData> myArrayList;
        String myString = "";
        if(drawPower == 0) {
            myArrayList = powerArrayList;
            myString = "W";
        } else if (drawPower == 1){
            myArrayList = currentArrayList;
            myString = "A";
        } else {
            myArrayList = voltageArrayList;
            myString = "V";
        }
            int count = arrayList.size();
            DataPoint[] values = new DataPoint[count];
            values[0] = new DataPoint(0,0);
            int counter = 0;
            float oldY = 0;
            float x = 0;
            float y = 0;
            for (int i = 1; i < count; i++) {
                if (arrayList.get(i-1).unit.equals(myString)) {
                    x = myArrayList.get(counter).number;
                    y = myArrayList.get(counter).value;
                    oldY = y;
                    counter++;
                    //System.out.println(powerArrayList.size());
                } else{
                    x++;
                    y = oldY;
                }
                //System.out.println("x " + x + " y " + y);
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }
            return values;
    }

    private void graphDrawer() {
        //System.out.println(value);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        mSeries1 = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);
        if (powerArrayList.size() > 50) {
            graph.getViewport().setScalable(true);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMaxX(powerArrayList.size());
            graph.getViewport().setMinX(powerArrayList.size() -50.5);
        }
    }

}
