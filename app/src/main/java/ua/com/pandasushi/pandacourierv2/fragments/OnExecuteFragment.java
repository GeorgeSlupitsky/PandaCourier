package ua.com.pandasushi.pandacourierv2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.activities.OrdersActivity;
import ua.com.pandasushi.pandacourierv2.adapters.CustomAdapterOnExecuteAndMyOrders;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;

/**
 * Created by User9 on 21.03.2018.
 */

public class OnExecuteFragment extends Fragment{

    private final String ATTRIBUTE_NAME_ORDER = "order";
    private final String ATTRIBUTE_NAME_ON_EXECUTE = "on execute";
    private final String ATTRIBUTE_NAME_MAPS = "maps";

    private List<CourierOrder> orders;

    private ListView listView;
    private ArrayList<Map<String, Object>> data;

    private String [] mFrom = {ATTRIBUTE_NAME_ORDER, ATTRIBUTE_NAME_ON_EXECUTE, ATTRIBUTE_NAME_MAPS};

    private CustomAdapterOnExecuteAndMyOrders customAdapterOnExecuteAndMyOrders;

    private Handler handler = new Handler();

    private SharedPreferences sharedPreferences;

    private Gson gson = new Gson();

    private String maps;

    Runnable refreshOrdersList = new Runnable() {
        @Override
        public void run() {
            createCustomAdapter();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_on_execute, container, false);

        listView = rootView.findViewById(R.id.lvOnExecute);

        sharedPreferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        maps = sharedPreferences.getString("maps", "MapsME");

        createCustomAdapter();

        handler.post(refreshOrdersList);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshOrdersList);
    }

    private void createCustomAdapter(){
        String ordersJSON = sharedPreferences.getString("orders", "");

        if (!ordersJSON.equals("")){
            orders = gson.fromJson(ordersJSON, new TypeToken<List<CourierOrder>>(){}.getType());
        }

        data = new ArrayList<>();

        if (orders != null){
            Collections.sort(orders, (courierOrder, courierOrder2) -> {
                if (courierOrder.getCourierId() == null){
                    return Integer.MIN_VALUE;
                } else {
                    return courierOrder.getCourierId().compareTo(courierOrder2.getCourierId());
                }
            });

            for (CourierOrder order: orders){
                Map<String, Object> m = new HashMap<>();

                m.put(ATTRIBUTE_NAME_ORDER, order);
                m.put(ATTRIBUTE_NAME_ON_EXECUTE, true);
                m.put(ATTRIBUTE_NAME_MAPS, maps);

                data.add(m);
            }

            customAdapterOnExecuteAndMyOrders = new CustomAdapterOnExecuteAndMyOrders(getContext(), R.layout.on_execute_and_my_orders_custom_adapter, data, mFrom);

            listView.setAdapter(customAdapterOnExecuteAndMyOrders);
        }
    }
}
