package co.edu.javeriana.millandiego.dgpsmeasures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {


    ImageView gps_trigger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps_trigger = (ImageView) findViewById(R.id.location_trigger);
        gps_trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent measure_activity = new Intent(v.getContext(), MeasureActivity.class);
                startActivity(measure_activity);
            }
        });
    }
}
