package ua.com.pandasushi.pandacourierv2.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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
    private List<CourierOrder> orders = new ArrayList<>();
    private Handler handler = new Handler();
    private Gson gson = new Gson();
    private Bundle bundle = new Bundle();

    Runnable refreshOrdersList = new Runnable() {
        @Override
        public void run() {
            try {
                CourierCommand courierCommand = new CourierCommand();
                courierCommand.setCourierId(123);
                courierCommand.setCommand(Commands.GET_ORDER_LIST);
                orders = (ArrayList) new SocketAsyncTask().execute(courierCommand).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String ordersJson = gson.toJson(orders);

            bundle.putString("orders", ordersJson);

            ordersFragment.setArguments(bundle);

            handler.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        try {
            CourierCommand courierCommand = new CourierCommand();
            courierCommand.setCourierId(123);
            courierCommand.setCommand(Commands.GET_ORDER_LIST);
            orders = (ArrayList) new SocketAsyncTask().execute(courierCommand).get();
        } catch (Exception e) {
            e.printStackTrace();
        }


        denyBackpress = true;

        ordersFragment = new OrdersFragment();

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        String ordersJson = gson.toJson(orders);

        bundle.putString("orders", ordersJson);

        ordersFragment.setArguments(bundle);

        transaction.add(R.id.frameLayout, ordersFragment);

        transaction.commitAllowingStateLoss();

        handler.post(refreshOrdersList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getString(R.string.close_shift));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                handler.removeCallbacks(refreshOrdersList);
                finish();
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
}
