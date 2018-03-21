package ua.com.pandasushi.pandacourierv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.adapters.CustomAdapterOnExecuteAndMyOrders;

/**
 * Created by User9 on 21.03.2018.
 */

public class OnExecuteFragment extends Fragment{

    final String ATTRIBUTE_NAME_ID = "id";
    final String ATTRIBUTE_NAME_PHONE = "phone";
    final String ATTRIBUTE_NAME_DISTRICT = "district";
    final String ATTRIBUTE_NAME_STREET = "street";
    final String ATTRIBUTE_NAME_HOUSE = "house";
    final String ATTRIBUTE_NAME_APARTMENT = "apartment";
    final String ATTRIBUTE_NAME_DISTRICT_BACKGROUND = "district background";
    final String ATTRIBUTE_NAME_PREFERED_TIME = "prefered time";
    final String ATTRIBUTE_NAME_PROMISE_TIME = "promise time";
    final String ATTRIBUTE_NAME_DISHES = "dishes";
    final String ATTRIBUTE_NAME_COURIER_ID = "courier id";
    final String ATTRIBUTE_NAME_ON_EXECUTE = "on execute";

    private Gson gson = new Gson();
    private List<CourierOrder> orders = new ArrayList<>();

    private ListView listView;
    private ArrayList<Map<String, Object>> data = new ArrayList<>();

    private String [] mFrom = {ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_PHONE, ATTRIBUTE_NAME_DISTRICT, ATTRIBUTE_NAME_STREET,
        ATTRIBUTE_NAME_HOUSE, ATTRIBUTE_NAME_APARTMENT, ATTRIBUTE_NAME_DISTRICT_BACKGROUND, ATTRIBUTE_NAME_PREFERED_TIME,
        ATTRIBUTE_NAME_PROMISE_TIME, ATTRIBUTE_NAME_DISHES, ATTRIBUTE_NAME_COURIER_ID, ATTRIBUTE_NAME_ON_EXECUTE};

    private CustomAdapterOnExecuteAndMyOrders customAdapterOnExecuteAndMyOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_on_execute, container, false);

        listView = rootView.findViewById(R.id.lvOnExecute);

        Bundle bundle = getArguments();

        String ordersJson = bundle.getString("orders");

        orders = gson.fromJson(ordersJson, new TypeToken<List<CourierOrder>>(){}.getType());

        if (orders != null){
            for (CourierOrder order: orders){
                Map<String, Object> m = new HashMap<>();

                m.put(ATTRIBUTE_NAME_ID, order.getOrderID());
                m.put(ATTRIBUTE_NAME_PHONE, order.getPhone());
                m.put(ATTRIBUTE_NAME_DISTRICT, order.getRegionCharcode());
                m.put(ATTRIBUTE_NAME_STREET, order.getStreet());
                m.put(ATTRIBUTE_NAME_HOUSE, order.getHouse());
                m.put(ATTRIBUTE_NAME_APARTMENT, order.getApartament());
                m.put(ATTRIBUTE_NAME_DISTRICT_BACKGROUND, order.getRegionBackground());
                m.put(ATTRIBUTE_NAME_PREFERED_TIME, order.getPreferedTime());
                m.put(ATTRIBUTE_NAME_PROMISE_TIME, order.getPromiseTime());
                m.put(ATTRIBUTE_NAME_DISHES, order.getDishes());
                m.put(ATTRIBUTE_NAME_COURIER_ID, order.getCourierId());
                m.put(ATTRIBUTE_NAME_ON_EXECUTE, true);

                data.add(m);
            }

            customAdapterOnExecuteAndMyOrders = new CustomAdapterOnExecuteAndMyOrders(getContext(), R.layout.on_execute_and_my_orders_custom_adapter, data, mFrom);

            listView.setAdapter(customAdapterOnExecuteAndMyOrders);
        }

        return rootView;
    }
}
