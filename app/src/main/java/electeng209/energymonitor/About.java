package electeng209.energymonitor;

import android.content.Intent; //General imports for app functionality
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);//Custom display bar
        getSupportActionBar().setCustomView(R.layout.actionbar);


        Button graphsButton = (Button) findViewById(R.id.graphButton);//Changing between tabs functionality
        graphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGraphs();
            }
        });

        Button settingsButton = (Button) findViewById(R.id.realTimeButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRealTime();
            }
        });
    }

    private void goToRealTime() {
        Intent intent = new Intent(this, MainActivity.class);//Switch to main activity
        startActivity(intent);
    }
    private void goToGraphs() {
        Intent intent = new Intent(this, Graphs.class);//Switch to graphs
        startActivity(intent);
    }

}
