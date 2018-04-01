package ua.com.pandasushi.pandacourierv2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.activities.LoginActivity;
import ua.com.pandasushi.pandacourierv2.activities.OrdersActivity;
import ua.com.pandasushi.pandacourierv2.adapters.OnExecuteAndMyOrdersCustomAdapter;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;
import ua.com.pandasushi.pandacourierv2.services.TrackWritingService;

/**
 * Created by User9 on 21.03.2018.
 */

public class MyOrdersFragment extends Fragment {

    private final String ATTRIBUTE_NAME_ORDER = "order";
    private final String ATTRIBUTE_NAME_ON_EXECUTE = "on execute";
    private final String ATTRIBUTE_NAME_MAPS = "maps";
    private final String ATTRIBUTE_NAME_COURIER_ID = "courier id";

    public static List<CourierOrder> myOrdersNotDelivered;
    public static String HOST;

    private List<CourierOrder> orders;
    private List<CourierOrder> myOrders;
    private ListView listView;
    private Button startMoving;
    private TextView mileage;
    private ArrayList<Map<String, Object>> data;

    private Integer courierId;

    private String [] mFrom = {ATTRIBUTE_NAME_ORDER, ATTRIBUTE_NAME_ON_EXECUTE, ATTRIBUTE_NAME_MAPS, ATTRIBUTE_NAME_COURIER_ID};

    private OnExecuteAndMyOrdersCustomAdapter onExecuteAndMyOrdersCustomAdapter;

    private Handler handler = new Handler();

    private SharedPreferences sharedPreferences;

    private Gson gson = new Gson();

    private String ordersJSON;

    private String maps;

    private boolean isConnected = true;

    public static boolean serviceStarted = false;

    Runnable refreshOrdersListAndCheckingForStartService = new Runnable() {
        @Override
        public void run() {
            createCustomAdapter();

            if (MyOrdersFragment.myOrdersNotDelivered.size() != 0){
                try {
                    CourierCommand courierCommand = new CourierCommand();
                    courierCommand.setCourierId(courierId);
                    courierCommand.setCommand(Commands.CHECK);
                    String response = (String) new SocketAsyncTask(HOST).execute(courierCommand).get();
                    if (response == null){
                        if (!serviceStarted){
                            getContext().startService(new Intent(getContext(), TrackWritingService.class));
                            serviceStarted = true;
                            startMoving.setTextColor(Color.GREEN);
                            handler.post(setTrackLength);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    if (!serviceStarted){
                        getContext().startService(new Intent(getContext(), TrackWritingService.class));
                        serviceStarted = true;
                        startMoving.setTextColor(Color.GREEN);
                        handler.post(setTrackLength);
                    }
                }
            }

            handler.postDelayed(this, 10000);
        }
    };

    Runnable refreshOrdersListIfSPMapsOrOrdersChangedOrDelivered = new Runnable() {
        @Override
        public void run() {
            String changeMaps = sharedPreferences.getString("maps", "GoogleMaps");
            if (!changeMaps.equals(maps)){
                createCustomAdapter();
            }

            String changedOrderJSON = sharedPreferences.getString("orders", "");
            if (!changedOrderJSON.equals("")){
                if (!changedOrderJSON.equals(ordersJSON)){
                    createCustomAdapter();
                }
            }

            HOST = sharedPreferences.getString("serverHost", "192.168.1.72");
            isConnected = sharedPreferences.getBoolean("connectionForMyOrders", true);
            handler.postDelayed(this, 1000);
        }
    };

    Runnable setTrackLength = new Runnable() {
        @Override
        public void run() {
            if (serviceStarted){
                if (TrackWritingService.lenghtOfTrack != null){
                    String [] length = TrackWritingService.lenghtOfTrack.split("\\.");
                    mileage.setText(length[0] + " " + getContext().getString(R.string.m));
                }
                handler.postDelayed(this, 2000);
            } else {
                startMoving.setTextColor(Color.WHITE);
                try {
                    mileage.setText("0 " + getString(R.string.m));
                } catch (Exception e){

                }
                handler.removeCallbacks(this);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);

        try{
            CourierCommand courierCommand = new CourierCommand();
            courierCommand.setCourierId(courierId);
            courierCommand.setCommand(Commands.CHECK);
            String response = (String) new SocketAsyncTask(HOST).execute(courierCommand).get();
            if (response != null){
                sharedPreferences.edit().putBoolean("connectionForMyOrders", true).apply();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        listView = rootView.findViewById(R.id.lvMyOrders);

        startMoving = rootView.findViewById(R.id.start_moving);

        mileage = rootView.findViewById(R.id.mileage);

        sharedPreferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        maps = sharedPreferences.getString("maps", "MapsME");

        courierId = sharedPreferences.getInt("courierId", -1);

        myOrders = new ArrayList<>();

        String myOrdersND = sharedPreferences.getString("myOrdersNotDelivered", "");

        if (!myOrdersND.equals("")){
            myOrdersNotDelivered = gson.fromJson(myOrdersND, new TypeToken<List<CourierOrder>>(){}.getType());
        }

        if (myOrdersNotDelivered == null){
            myOrdersNotDelivered = new ArrayList<>();
        }

        createCustomAdapter();

        handler.post(refreshOrdersListAndCheckingForStartService);
        handler.post(refreshOrdersListIfSPMapsOrOrdersChangedOrDelivered);

        startMoving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myOrdersNotDelivered.size() != 0){
                    if (!serviceStarted){
                        getContext().startService(new Intent(getContext(), TrackWritingService.class));
                        serviceStarted = true;
                        startMoving.setTextColor(Color.GREEN);
                        Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                getContext().getString(R.string.record_track), Toast.LENGTH_LONG);
                        toast.show();
                        handler.post(setTrackLength);
                    } else {
                        Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                getContext().getString(R.string.record_track_started), Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getContext().getApplicationContext(),
                            getContext().getString(R.string.cannot_start_trip), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        return rootView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshOrdersListAndCheckingForStartService);
        handler.removeCallbacks(refreshOrdersListIfSPMapsOrOrdersChangedOrDelivered);
    }

    private synchronized void createCustomAdapter(){
        ordersJSON = sharedPreferences.getString("orders", "");

        if (!ordersJSON.equals("")){
            orders = gson.fromJson(ordersJSON, new TypeToken<List<CourierOrder>>(){}.getType());
        }

        if (!isConnected){
            String myOrdersJSON = sharedPreferences.getString("myOrders", "");
            if (!myOrdersJSON.equals("")) {
                myOrders = gson.fromJson(myOrdersJSON, new TypeToken<List<CourierOrder>>() {
                }.getType());
            }
        } else {
            myOrders = new ArrayList<>();
            if (orders != null){
                for (CourierOrder order: orders){
                    if (order.getCourierId() != null){
                        if (order.getCourierId().equals(courierId)){
                            myOrders.add(order);
                        }
                    }
                }
            }
        }

        if (myOrders != null){
            Collections.sort(myOrders, (courierOrder, courierOrder2) -> {
                if (courierOrder.getDeliverTime() != null){
                    if (courierOrder2.getDeliverTime() == null){
                        return 1;
                    }
                    return courierOrder2.getDeliverTime().compareTo(courierOrder.getDeliverTime());
                } else {
                    return Integer.MIN_VALUE;
                }
            });
        }

        data = new ArrayList<>();

        for (CourierOrder order: myOrders) {
            Map<String, Object> m = new HashMap<>();

            m.put(ATTRIBUTE_NAME_ORDER, order);
            m.put(ATTRIBUTE_NAME_ON_EXECUTE, false);
            m.put(ATTRIBUTE_NAME_MAPS, maps);
            m.put(ATTRIBUTE_NAME_COURIER_ID, courierId);

            data.add(m);
        }

        onExecuteAndMyOrdersCustomAdapter = new OnExecuteAndMyOrdersCustomAdapter(getContext(), R.layout.on_execute_and_my_orders_custom_adapter, data, mFrom, myOrders);

        listView.setAdapter(onExecuteAndMyOrdersCustomAdapter);
    }
}
