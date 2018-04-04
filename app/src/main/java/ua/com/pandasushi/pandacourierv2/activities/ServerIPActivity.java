package ua.com.pandasushi.pandacourierv2.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pandasushi.pandacourierv2.R;

/**
 * Created by User9 on 04.04.2018.
 */

public class ServerIPActivity extends AppCompatActivity {

    private Button save;
    private EditText serverHost;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_server_ip);

        serverHost = (EditText) findViewById(R.id.serverHost);
        save = (Button) findViewById(R.id.saveBtn);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        serverHost.setText(sharedPreferences.getString("serverHost", "192.168.88.94"));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences.edit().putString("serverHost", serverHost.getText().toString()).apply();

                finish();
            }
        });
    }
}
