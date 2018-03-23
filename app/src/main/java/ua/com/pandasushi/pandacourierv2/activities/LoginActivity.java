package ua.com.pandasushi.pandacourierv2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.Courier;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;

public class LoginActivity extends AppCompatActivity {

    private Button startShift;
    private EditText passwordET;
    private Spinner spinner;
    private String responseStartShift;
    private String checkPassword;
    private ArrayList<Courier> couriers = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private Integer courierId;
    private boolean isStartShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startShift = (Button) findViewById(R.id.start_shift);
        passwordET = (EditText) findViewById(R.id.password);
        spinner = (Spinner) findViewById(R.id.spinnerCourier);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        isStartShift = sharedPreferences.getBoolean("startShift", false);

        if (isStartShift){
            Intent intent = new Intent(this, OrdersActivity.class);
            startActivity(intent);
        }

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
                            courierCommand.setCommand(Commands.START_CHANGE);
                            responseStartShift = (String) new SocketAsyncTask(this).execute(courierCommand).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (responseStartShift != null){
                            if (responseStartShift.equals("OK")){
                                sharedPreferences.edit().putBoolean("startShift", true).apply();
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
                    courierCommand.setCommand(Commands.START_CHANGE);
                    responseStartShift = (String) new SocketAsyncTask(this).execute(courierCommand).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (responseStartShift != null){
                    if (responseStartShift.equals("OK")){
                        sharedPreferences.edit().putBoolean("startShift", true).apply();
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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        responseStartShift = null;
        if (courierId != -1){
            spinner.setVisibility(View.GONE);
            passwordET.setVisibility(View.GONE);
        }
    }
}
