package ua.com.pandasushi.pandacourierv2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.List;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.activities.OrdersActivity;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;

/**
 * Created by User9 on 21.03.2018.
 */

public class ClosedFragment extends Fragment{

    private List<CourierOrder> orders;

    private Handler handler = new Handler();

    private SharedPreferences sharedPreferences;

    private Gson gson = new Gson();


    Runnable refreshOrdersList = new Runnable() {
        @Override
        public void run() {
            String ordersJSON = sharedPreferences.getString("orders", "");

            if (!ordersJSON.equals("")){
                orders = gson.fromJson(ordersJSON, new TypeToken<List<CourierOrder>>(){}.getType());
            }

            handler.postDelayed(this, 30000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_closed, container, false);

        sharedPreferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        String ordersJSON = sharedPreferences.getString("orders", "");

        if (!ordersJSON.equals("")){
            orders = gson.fromJson(ordersJSON, new TypeToken<List<CourierOrder>>(){}.getType());
        }

        handler.post(refreshOrdersList);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshOrdersList);
    }
}
