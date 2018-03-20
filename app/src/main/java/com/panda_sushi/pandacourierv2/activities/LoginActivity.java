package com.panda_sushi.pandacourierv2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.panda_sushi.pandacourierv2.R;

public class LoginActivity extends AppCompatActivity {

    Button startShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startShift.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, OrdersActivity.class);
            startActivity(intent);
        });
    }
}
