package ua.com.pandasushi.pandacourierv2.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pandasushi.pandacourierv2.R;

import java.io.ByteArrayOutputStream;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;
import ua.com.pandasushi.pandacourierv2.fragments.MyOrdersFragment;
import ua.com.pandasushi.pandacourierv2.fragments.OnExecuteFragment;
import ua.com.pandasushi.pandacourierv2.services.TrackWritingService;

/**
 * Created by postp on 25.03.2018.
 */

public class OdometerInfoActivity extends AppCompatActivity {

    private EditText odometerData;
    private EditText additionalTripData;

    private TextView tvAddTrip;

    private Button ok;

    private Integer courierId;

    private SharedPreferences sharedPreferences;

    private String responseShift;

    private boolean startShift;

    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_odometr_info);

        odometerData = (EditText) findViewById(R.id.odometerData);
        additionalTripData = (EditText) findViewById(R.id.additionalTrip);
        tvAddTrip = (TextView) findViewById(R.id.tvAddTrip);
        ok = (Button) findViewById(R.id.buttonOK);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        courierId = sharedPreferences.getInt("courierId", -1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        startShift = intent.getBooleanExtra("startShift", true);

        if (startShift){
            tvAddTrip.setVisibility(View.GONE);
            additionalTripData.setVisibility(View.GONE);
        }

        Bundle bundle = intent.getExtras();

        if (bundle != null){
            imageBitmap = (Bitmap) bundle.get("data");
        }

        byte [] b = getByteArray(imageBitmap);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double odometerDataVal;
                if (odometerData.getText() != null && !odometerData.getText().toString().equals("")){
                    odometerDataVal = Double.parseDouble(odometerData.getText().toString());

                    if (startShift){
                        try {
                            CourierCommand courierCommand = new CourierCommand();
                            courierCommand.setCourierId(courierId);
                            courierCommand.setOdometer(odometerDataVal);
                            courierCommand.setPhoto(b);
                            courierCommand.setCommand(Commands.START_CHANGE);
                            responseShift = (String) new SocketAsyncTask(LoginActivity.HOST, OdometerInfoActivity.this).execute(courierCommand).get();
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
                        Double addTripVal;
                        if (!additionalTripData.getText().toString().equals("")){
                            addTripVal = Double.parseDouble(additionalTripData.getText().toString());
                            odometerDataVal = odometerDataVal - addTripVal;
                        }

                        try {
                            sharedPreferences.edit().putString("myOrdersNotDelivered", "").apply();
                            sharedPreferences.edit().putString("orders", "").apply();
                            CourierCommand courierCommand = new CourierCommand();
                            courierCommand.setCourierId(courierId);
                            courierCommand.setOdometer(odometerDataVal);
                            courierCommand.setPhoto(b);
                            courierCommand.setCommand(Commands.END_CHANGE);
                            responseShift = (String) new SocketAsyncTask(MyOrdersFragment.HOST, OdometerInfoActivity.this).execute(courierCommand).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (responseShift != null){
                            if (responseShift.equals("OK")){
                                sharedPreferences.edit().putBoolean("correctCloseShift", true).apply();
                                sharedPreferences.edit().putBoolean("startShift", false).apply();
                                Intent intent = new Intent(OdometerInfoActivity.this, LoginActivity.class);
                                startActivity(intent);
                                OrdersActivity.fa.finish();
                                finish();
                            }
                        } else {
                            sharedPreferences.edit().putBoolean("correctCloseShift", false).apply();
                            String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                            sharedPreferences.edit().putString("photo", encoded).apply();
                            sharedPreferences.edit().putString("odometerData", String.valueOf(odometerDataVal)).apply();
                            MyOrdersFragment.serviceStarted = false;
                            sharedPreferences.edit().putBoolean("startShift", false).apply();
                            Intent intent = new Intent(OdometerInfoActivity.this, LoginActivity.class);
                            startActivity(intent);
                            OrdersActivity.fa.finish();
                            stopService(new Intent(getApplicationContext(), TrackWritingService.class));
                            finish();
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.odometer_info), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    private byte [] getByteArray (Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
