package ua.com.pandasushi.pandacourierv2.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.Courier;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static Activity fa;

    private Button startShift;
    private EditText passwordET;
    private Spinner spinner;
    private String responseCheck;
    private String checkPassword;
    private ArrayList<Courier> couriers = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private Integer courierId;
    private boolean isStartShift;
    private boolean isCorrectCloseShift;

    private Handler handler = new Handler();

    private Gson gson = new Gson();

    Runnable refresh = new Runnable() {
        @Override
        public void run() {
            try {
                CourierCommand courierCommand = new CourierCommand();
                courierCommand.setCourierId(courierId);
                courierCommand.setCommand(Commands.GET_COURIER_LIST);
                couriers = (ArrayList<Courier>) new SocketAsyncTask(LoginActivity.this).execute(courierCommand).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (couriers != null && !couriers.isEmpty()){
                String couriersJson = gson.toJson(couriers);
                sharedPreferences.edit().putString("couriers", couriersJson).apply();

                String[] items = new String[couriers.size()];

                for (Courier courier: couriers){
                    items[couriers.indexOf(courier)] = courier.getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, items);

                spinner.setAdapter(adapter);

                spinner.setEnabled(true);
                passwordET.setEnabled(true);
            } else {
                spinner.setEnabled(false);
                passwordET.setEnabled(false);
            }

            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    },
                    1);

        }

        fa = this;

        startShift = (Button) findViewById(R.id.start_shift);
        passwordET = (EditText) findViewById(R.id.password);
        spinner = (Spinner) findViewById(R.id.spinnerCourier);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        isStartShift = sharedPreferences.getBoolean("startShift", false);

        if (isStartShift){
            Intent intent = new Intent(this, OrdersActivity.class);
            startActivity(intent);
        }

        isCorrectCloseShift = sharedPreferences.getBoolean("correctCloseShift", true);

        courierId = sharedPreferences.getInt("courierId", -1);

        if (courierId == -1){
            try {
                CourierCommand courierCommand = new CourierCommand();
                courierCommand.setCourierId(courierId);
                courierCommand.setCommand(Commands.GET_COURIER_LIST);
                couriers = (ArrayList<Courier>) new SocketAsyncTask(this).execute(courierCommand).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (couriers != null && !couriers.isEmpty()){
                String couriersJson = gson.toJson(couriers);
                sharedPreferences.edit().putString("couriers", couriersJson).apply();

                String[] items = new String[couriers.size()];

                for (Courier courier: couriers){
                    items[couriers.indexOf(courier)] = courier.getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

                spinner.setAdapter(adapter);
            } else {
                spinner.setEnabled(false);
                passwordET.setEnabled(false);
            }


        } else {
            spinner.setVisibility(View.GONE);
            passwordET.setVisibility(View.GONE);

            try {
                CourierCommand courierCommand = new CourierCommand();
                courierCommand.setCourierId(courierId);
                courierCommand.setCommand(Commands.GET_COURIER_LIST);
                couriers = (ArrayList<Courier>) new SocketAsyncTask(this).execute(courierCommand).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (couriers != null && !couriers.isEmpty()){
                String couriersJson = gson.toJson(couriers);
                sharedPreferences.edit().putString("couriers", couriersJson).apply();
            }
        }

        startShift.setOnClickListener(view -> {

            if (courierId == -1){
                String password = passwordET.getText().toString();

                try {
                    CourierCommand courierCommand = new CourierCommand();
                    courierCommand.setCommand(Commands.CHECK_COURIER_PASSWORD);
                    checkPassword = (String) new SocketAsyncTask(this).execute(courierCommand).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (checkPassword != null){
                    if (checkPassword.equals("ОК")){
                        courierId = couriers.get(spinner.getSelectedItemPosition()).getId();
                        sharedPreferences.edit().putInt("courierId", courierId).apply();

                        try {
                            CourierCommand courierCommand = new CourierCommand();
                            courierCommand.setCourierId(courierId);
                            courierCommand.setCommand(Commands.CHECK);
                            responseCheck = (String) new SocketAsyncTask(this).execute(courierCommand).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (responseCheck != null){
                            if (responseCheck.equals("OK")){
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
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
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.password_incorrect), Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.connection_error), Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                try {
                    CourierCommand courierCommand = new CourierCommand();
                    courierCommand.setCourierId(courierId);
                    courierCommand.setCommand(Commands.CHECK);
                    responseCheck = (String) new SocketAsyncTask(this).execute(courierCommand).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (responseCheck != null){
                    if (responseCheck.equals("OK")){
                        if(!isCorrectCloseShift){
                            String responseShift = null;
                            try {
                                //TODO get odometer info, photo and last track info
                                CourierCommand courierCommand = new CourierCommand();
                                courierCommand.setCourierId(courierId);
                                courierCommand.setCommand(Commands.END_CHANGE);
                                responseShift = (String) new SocketAsyncTask(LoginActivity.this).execute(courierCommand).get();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (responseShift != null){
                                if (responseShift.equals("OK")){
                                    sharedPreferences.edit().putBoolean("correctCloseShift", true).apply();
                                    sharedPreferences.edit().putBoolean("startShift", false).apply();
                                }
                            }
                        }

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
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
            }
        });

        if (courierId == -1){
            handler.post(refresh);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Intent intent = new Intent(this, OdometerInfoActivity.class);
            if (extras != null){
                intent.putExtras(extras);
            }
            intent.putExtra("startShift", true);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (courierId != -1){
            spinner.setVisibility(View.GONE);
            passwordET.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(refresh);
    }
}
