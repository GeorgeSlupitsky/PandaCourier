package ua.com.pandasushi.pandacourierv2.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pandasushi.pandacourierv2.R;

import ua.com.pandasushi.pandacourierv2.connection.HostInfo;

/**
 * Created by postp on 22.03.2018.
 */

public class ChooseMapActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton mapsMeRadio;
    private RadioButton googleMapsRadio;
    private Button save;
    private EditText serverHost;
    private SharedPreferences sharedPreferences;
    private String maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_map);

        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mapsMeRadio = (RadioButton) findViewById(R.id.mapsMeRadio);
        googleMapsRadio = (RadioButton) findViewById(R.id.googleMapsRadio);
        serverHost = (EditText) findViewById(R.id.serverHostFromMap);
        save = (Button) findViewById(R.id.saveBtn);

        maps = sharedPreferences.getString("maps", "GoogleMaps");

        serverHost.setText(sharedPreferences.getString("serverHost", HostInfo.host));

        if (maps.equals("MapsME")){
            mapsMeRadio.setChecked(true);
        } else if (maps.equals("GoogleMaps")){
            googleMapsRadio.setChecked(true);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                switch (radioButtonID){
                    case R.id.mapsMeRadio:
                        sharedPreferences.edit().putString("maps", "MapsME").apply();
                        break;
                    case R.id.googleMapsRadio:
                        sharedPreferences.edit().putString("maps", "GoogleMaps").apply();
                        break;
                }

                sharedPreferences.edit().putString("serverHost", serverHost.getText().toString()).apply();

                finish();
            }
        });
    }
}
