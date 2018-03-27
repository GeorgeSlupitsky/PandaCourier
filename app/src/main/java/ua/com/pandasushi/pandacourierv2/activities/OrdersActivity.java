package ua.com.pandasushi.pandacourierv2.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.List;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;
import ua.com.pandasushi.pandacourierv2.fragments.MyOrdersFragment;
import ua.com.pandasushi.pandacourierv2.fragments.OrdersFragment;

/**
 * Created by postp on 20.03.2018.
 */

public class OrdersActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static Activity fa;

    private OrdersFragment ordersFragment;
    private boolean denyBackpress;
    private static Integer courierId;
    private SharedPreferences sharedPreferences;

    private static ArrayList<CourierOrder> orders;

    private Handler handler = new Handler();

    private Gson gson = new Gson();

    Runnable refreshOrdersList = new Runnable() {
        @Override
        public void run() {
            try {
                orders = null;

                CourierCommand courierCommand = new CourierCommand();
                courierCommand.setCourierId(courierId);
                courierCommand.setCommand(Commands.GET_ORDER_LIST);

                orders = (ArrayList) new SocketAsyncTask().execute(courierCommand).get();

                if (orders != null){
                    sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fa = this;

        setContentView(R.layout.activity_orders);

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);

        courierId = sharedPreferences.getInt("courierId", -1);

        denyBackpress = true;

        ordersFragment = new OrdersFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.frameLayout, ordersFragment);

        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finishShift:
                if (MyOrdersFragment.myOrdersNotDelivered.size() == 0){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getString(R.string.dont_delievered_orders), Toast.LENGTH_LONG);
                    toast.show();
                }
                return true;
            case R.id.chooseMap:
                Intent intent = new Intent(this, ChooseMapActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (denyBackpress){
            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.deny_backpressed), Toast.LENGTH_LONG);
            toast.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refreshOrdersList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshOrdersList);
    }

    public static ArrayList <CourierOrder> refreshOrderList(){
        try {
            orders = null;

            CourierCommand courierCommand = new CourierCommand();
            courierCommand.setCourierId(courierId);
            courierCommand.setCommand(Commands.GET_ORDER_LIST);

            orders = (ArrayList) new SocketAsyncTask().execute(courierCommand).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
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
            intent.putExtra("startShift", false);
            startActivity(intent);
        }
    }


}
