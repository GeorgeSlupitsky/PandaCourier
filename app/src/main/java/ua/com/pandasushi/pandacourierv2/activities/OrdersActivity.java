package ua.com.pandasushi.pandacourierv2.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

        transaction.add(R.id.frameLayout, ordersFragment);

        transaction.commitAllowingStateLoss();
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
