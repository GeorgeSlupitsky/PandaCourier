package ua.com.pandasushi.pandacourierv2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.pandasushi.pandacourierv2.R;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;

public class LoginActivity extends AppCompatActivity {

    private Button startShift;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startShift = (Button) findViewById(R.id.start_shift);

        startShift.setOnClickListener(view -> {

            try {
                CourierCommand courierCommand = new CourierCommand();
                courierCommand.setCourierId(123);
                courierCommand.setCommand(Commands.START_CHANGE);
                response = (String) new SocketAsyncTask().execute(courierCommand).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (response != null){
                if (response.equals("OK")){
                    Intent intent = new Intent(this, OrdersActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.connection_error), Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.connection_error), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        response = null;
    }
}
