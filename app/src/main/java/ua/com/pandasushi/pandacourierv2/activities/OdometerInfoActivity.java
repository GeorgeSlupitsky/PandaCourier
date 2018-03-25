package ua.com.pandasushi.pandacourierv2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pandasushi.pandacourierv2.R;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;

/**
 * Created by postp on 25.03.2018.
 */

public class OdometerInfoActivity extends AppCompatActivity {

    private EditText odometerData;

    private Button ok;

    private Integer courierId;

    private SharedPreferences sharedPreferences;

    private String responseShift;

    private boolean startShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_odometr_info);

        odometerData = (EditText) findViewById(R.id.odometerData);
        ok = (Button) findViewById(R.id.buttonOK);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        courierId = sharedPreferences.getInt("courierId", -1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        startShift = intent.getBooleanExtra("startShift", true);

        Bundle bundle = intent.getExtras();

        if (bundle != null){
            Bitmap imageBitmap = (Bitmap) bundle.get("data");
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double odometrDataVal = Double.parseDouble(odometerData.getText().toString());

                if (startShift){
                    try {
                        CourierCommand courierCommand = new CourierCommand();
                        courierCommand.setCourierId(courierId);
                        courierCommand.setCommand(Commands.START_CHANGE);
                        responseShift = (String) new SocketAsyncTask(OdometerInfoActivity.this).execute(courierCommand).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (responseShift != null){
                        if (responseShift.equals("OK")){
                            sharedPreferences.edit().putBoolean("startShift", true).apply();
                            Intent intent = new Intent(OdometerInfoActivity.this, OrdersActivity.class);
                            startActivity(intent);
                            LoginActivity.fa.finish();
                            finish();
                        }
                    }
                } else {
                    try {
                        CourierCommand courierCommand = new CourierCommand();
                        courierCommand.setCourierId(courierId);
                        courierCommand.setCommand(Commands.END_CHANGE);
                        responseShift = (String) new SocketAsyncTask(OdometerInfoActivity.this).execute(courierCommand).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (responseShift != null){
                        if (responseShift.equals("OK")){
                            sharedPreferences.edit().putBoolean("startShift", false).apply();
                            Intent intent = new Intent(OdometerInfoActivity.this, LoginActivity.class);
                            startActivity(intent);
                            OrdersActivity.fa.finish();
                            finish();
                        }
                    }
                }

            }
        });
    }


}
