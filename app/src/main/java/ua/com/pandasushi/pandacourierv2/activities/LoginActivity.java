package ua.com.pandasushi.pandacourierv2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.pandasushi.pandacourierv2.R;

public class LoginActivity extends AppCompatActivity {

    Button startShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startShift = (Button) findViewById(R.id.start_shift);

        startShift.setOnClickListener(view -> {
            Intent intent = new Intent(this, OrdersActivity.class);
            startActivity(intent);
        });
    }
}
