package ua.com.pandasushi.pandacourierv2.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pandasushi.pandacourierv2.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by postp on 21.03.2018.
 */

public class CustomAdapterOnExecuteAndMyOrders extends ArrayAdapter<Map<String, Object>> {

    private Context context;
    private ArrayList<Map<String, Object>> data;
    private int layoutResourceId;
    private String[] mFrom;

    public CustomAdapterOnExecuteAndMyOrders(Context context, int resource, ArrayList<Map<String, Object>> data, String[] mFrom) {
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
            row = inflater.inflate(R.layout.on_execute_and_my_orders_custom_adapter, null);
        }

        OnExecuteAndMyOrdersHolder holder = new OnExecuteAndMyOrdersHolder();

        holder.imageView1 = row.findViewById(R.id.iv1);
        holder.imageView2 = row.findViewById(R.id.iv2);
        holder.imageView3 = row.findViewById(R.id.iv3);
        holder.imageView4 = row.findViewById(R.id.iv4);
        holder.recommendedTime = row.findViewById(R.id.recommendedTime);
        holder.time = row.findViewById(R.id.time);
        holder.promiseTime = row.findViewById(R.id.promiseTime);
        holder.district = row.findViewById(R.id.district);
        holder.street = row.findViewById(R.id.street);
        holder.address = row.findViewById(R.id.address);

        String dishes = (String) data.get(position).get(mFrom[9]);

        char[] chars = dishes.toCharArray();

        Resources resources = context.getResources();

        for (int i = 0; i < chars.length - 1; i++){
            switch (chars[i]){
                case 'П':
                    final int statusResourceId1 = resources.getIdentifier("pizza", "drawable",
                            context.getPackageName());
                    holder.imageView1.setImageResource(statusResourceId1);
                    break;
                case 'Р':
                    final int statusResourceId2 = resources.getIdentifier("sushi", "drawable",
                            context.getPackageName());
                    holder.imageView2.setImageResource(statusResourceId2);
                    break;
                case 'Н':
                    final int statusResourceId3 = resources.getIdentifier("drinks", "drawable",
                            context.getPackageName());
                    holder.imageView3.setImageResource(statusResourceId3);
                    break;
                case 'С':
                    final int statusResourceId4 = resources.getIdentifier("soup", "drawable",
                            context.getPackageName());
                    holder.imageView4.setImageResource(statusResourceId4);
                    break;
            }
        }

        holder.district.setText((String) data.get(position).get(mFrom[2]));
        holder.district.setBackgroundColor((Integer) data.get(position).get(mFrom[6]));

        DateFormat df = new SimpleDateFormat("HH:mm");

        holder.promiseTime.setText(df.format((Date) data.get(position).get(mFrom[8])));
        holder.recommendedTime.setText(df.format((Date) data.get(position).get(mFrom[7])));
        holder.street.setText((String) data.get(position).get(mFrom[3]));
        holder.address.setText(data.get(position).get(mFrom[4]) + " " + data.get(position).get(mFrom[5]));

        Boolean onExecute = (Boolean) data.get(position).get(mFrom[11]);

        row.setOnClickListener(view -> {
            if (onExecute){
                if (data.get(position).get(mFrom[10]) == null){

                } else {

                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Ім'я клієнта");
                builder.setItems(new CharSequence[]
                                {getContext().getString(R.string.cancel), getContext().getString(R.string.call),
                                        getContext().getString(R.string.map), getContext().getString(R.string.delivered)},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        break;
                                    case 1:
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + data.get(position).get(mFrom[1])));
                                        getContext().startActivity(intent);
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                }
                            }
                        });
                builder.create().show();
            }
        });

        return row;
    }

    static class OnExecuteAndMyOrdersHolder{
        TextView recommendedTime;
        TextView time;
        TextView promiseTime;
        TextView district;
        TextView street;
        TextView address;
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        ImageView imageView4;
    }

}
