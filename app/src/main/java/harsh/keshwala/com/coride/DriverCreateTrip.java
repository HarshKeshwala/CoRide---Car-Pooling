package harsh.keshwala.com.coride;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DriverCreateTrip extends AppCompatActivity {

    private EditText tripSource;
    private EditText tripDestination;
    private EditText tripDate;
    private EditText tripTime;
    private EditText tripExpense;

    private Button createTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_create_trip);

        tripSource = (EditText) findViewById(R.id.tripSource);
        tripDestination = (EditText) findViewById(R.id.tripDestination);
        tripDate = (EditText) findViewById(R.id.tripDate);
        tripTime = (EditText) findViewById(R.id.tripTime);
        tripExpense = (EditText) findViewById(R.id.tripExpense);

        createTrip = (Button) findViewById(R.id.tripCreateButton);

        createTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
