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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.adapters.OnExecuteAndMyOrdersCustomAdapter;

/**
 * Created by User9 on 21.03.2018.
 */

public class OnExecuteFragment extends Fragment{

    private final String ATTRIBUTE_NAME_ORDER = "order";
    private final String ATTRIBUTE_NAME_ON_EXECUTE = "on execute";
    private final String ATTRIBUTE_NAME_MAPS = "maps";
    private final String ATTRIBUTE_NAME_COURIER_ID = "courier id";

    private List<CourierOrder> orders;

    private ListView listView;
    private ArrayList<Map<String, Object>> data;

    private Integer courierId;

    private String [] mFrom = {ATTRIBUTE_NAME_ORDER, ATTRIBUTE_NAME_ON_EXECUTE, ATTRIBUTE_NAME_MAPS, ATTRIBUTE_NAME_COURIER_ID};

    private OnExecuteAndMyOrdersCustomAdapter onExecuteAndMyOrdersCustomAdapter;

    private Handler handler = new Handler();

    private SharedPreferences sharedPreferences;

    private Gson gson = new Gson();

    private String ordersJSON;

    private String maps;

    Runnable refreshOrdersList = new Runnable() {
        @Override
        public void run() {
            createCustomAdapter();
            handler.postDelayed(this, 10000);
        }
    };

    Runnable refreshOrdersListIfSPChanged = new Runnable() {
        @Override
        public void run() {
            String changedOrderJSON = sharedPreferences.getString("orders", "");
            if (!changedOrderJSON.equals("")){
                if (!changedOrderJSON.equals(ordersJSON)){
                    createCustomAdapter();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_on_execute, container, false);

        listView = rootView.findViewById(R.id.lvOnExecute);

        sharedPreferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        maps = sharedPreferences.getString("maps", "MapsME");

        courierId = sharedPreferences.getInt("courierId", -1);

        createCustomAdapter();

        handler.post(refreshOrdersList);
        handler.postDelayed(refreshOrdersListIfSPChanged, 3000);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshOrdersList);
        handler.removeCallbacks(refreshOrdersListIfSPChanged);
    }

    private void createCustomAdapter(){
        ordersJSON = sharedPreferences.getString("orders", "");

        if (!ordersJSON.equals("")){
            orders = gson.fromJson(ordersJSON, new TypeToken<List<CourierOrder>>(){}.getType());
        }

        data = new ArrayList<>();

        if (orders != null){
            Collections.sort(orders, (courierOrder, courierOrder2) -> {
                if (courierOrder.getCourierId() != null){
                    if (courierOrder2.getCourierId() == null){
                        return 1;
                    }
                    return courierOrder.getCourierId().compareTo(courierOrder2.getCourierId());
                } else {
                    return Integer.MIN_VALUE;
                }
            });

            for (CourierOrder order: orders){
                Map<String, Object> m = new HashMap<>();

                m.put(ATTRIBUTE_NAME_ORDER, order);
                m.put(ATTRIBUTE_NAME_ON_EXECUTE, true);
                m.put(ATTRIBUTE_NAME_MAPS, maps);
                m.put(ATTRIBUTE_NAME_COURIER_ID, courierId);

                data.add(m);
            }

            onExecuteAndMyOrdersCustomAdapter = new OnExecuteAndMyOrdersCustomAdapter(getContext(), R.layout.on_execute_and_my_orders_custom_adapter, data, mFrom);

            listView.setAdapter(onExecuteAndMyOrdersCustomAdapter);
        }
    }
}
