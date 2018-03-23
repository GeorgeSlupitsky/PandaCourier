package ua.com.pandasushi.pandacourierv2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import ua.com.pandasushi.pandacourierv2.fragments.OrdersFragment;

/**
 * Created by postp on 20.03.2018.
 */

public class OrdersActivity extends AppCompatActivity {

    private OrdersFragment ordersFragment;
    private boolean denyBackpress;
    private static Integer courierId;
    private SharedPreferences sharedPreferences;

    private static ArrayList<CourierOrder> orders;

    private Handler handler = new Handler();

    private Gson gson = new Gson();

    private String responseFinishShift;

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
                try {
                    CourierCommand courierCommand = new CourierCommand();
                    courierCommand.setCourierId(courierId);
                    courierCommand.setCommand(Commands.END_CHANGE);
                    responseFinishShift = (String) new SocketAsyncTask(OrdersActivity.this).execute(courierCommand).get();
                    if (responseFinishShift.equals("OK")){
                        sharedPreferences.edit().putBoolean("startShift", false).apply();
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
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
}
