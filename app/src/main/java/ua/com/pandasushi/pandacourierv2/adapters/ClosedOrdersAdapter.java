package ua.com.pandasushi.pandacourierv2.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.database.common.gps.models.Track;

/**
 * Created by User9 on 28.03.2018.
 */

public class ClosedOrdersAdapter extends ArrayAdapter<Map<String, Object>> {

    private Context context;
    private ArrayList<Map<String, Object>> data;
    private int layoutResourceId;
    private String[] mFrom;

    public ClosedOrdersAdapter(Context context, int resource, ArrayList<Map<String, Object>> data, String[] mFrom) {
        super(context, resource, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = resource;
        this.mFrom = mFrom;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.closed_custom_adapter, null);
        }

        ClosedOrdersHolder holder = new ClosedOrdersHolder();

        Track track = (Track) data.get(position).get(mFrom[0]);

        holder.ordersNumber = row.findViewById(R.id.ordersNumber);
        holder.addressClosed = row.findViewById(R.id.addressClosed);
        holder.trackLength = row.findViewById(R.id.trackLength);

        String[] length = track.getTrackLenght().split("\\.");
        holder.trackLength.setText(length[0] + " " + getContext().getString(R.string.m));


        List <CourierOrder> orders = track.getOrders();

        String name = "";

        String address = "";

        Iterator itr = orders.iterator();
        while (itr.hasNext())
        {
            CourierOrder order = (CourierOrder) itr.next();
            if (itr.hasNext()){
                name += order.getCharcode() + "\n";
                address += order.getStreet() + " " + order.getHouse() + "/" + order.getApartament() + "\n";
            } else {
                name += order.getCharcode();
                address += order.getStreet() + " " + order.getHouse() + "/" + order.getApartament();
            }
        }

        holder.ordersNumber.setText(name);
        holder.addressClosed.setText(address);

        return row;
    }

    static class ClosedOrdersHolder{
        TextView ordersNumber;
        TextView addressClosed;
        TextView trackLength;
    }

}
