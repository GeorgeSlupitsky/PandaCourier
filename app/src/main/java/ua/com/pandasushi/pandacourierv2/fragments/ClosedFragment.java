package ua.com.pandasushi.pandacourierv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.List;

import ua.com.pandasushi.database.common.CourierOrder;

/**
 * Created by User9 on 21.03.2018.
 */

public class ClosedFragment extends Fragment{

    private Gson gson = new Gson();
    private List<CourierOrder> orders = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_closed, container, false);

        Bundle bundle = getArguments();

        String ordersJson = bundle.getString("orders");

        orders = gson.fromJson(ordersJson, new TypeToken<List<CourierOrder>>(){}.getType());

        return rootView;
    }

}
