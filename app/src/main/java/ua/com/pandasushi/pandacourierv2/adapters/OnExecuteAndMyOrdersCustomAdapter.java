package ua.com.pandasushi.pandacourierv2.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pandasushi.pandacourierv2.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.pandacourierv2.activities.OrdersActivity;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;
import ua.com.pandasushi.pandacourierv2.mapsmeapi.MWMPoint;
import ua.com.pandasushi.pandacourierv2.mapsmeapi.MapsWithMeApi;
import ua.com.pandasushi.pandacourierv2.parser.JSONParser;

/**
 * Created by postp on 21.03.2018.
 */

public class OnExecuteAndMyOrdersCustomAdapter extends ArrayAdapter<Map<String, Object>> {

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private Context context;
    private ArrayList<Map<String, Object>> data;
    private int layoutResourceId;
    private String[] mFrom;
    private String maps;
    private SharedPreferences sharedPreferences;

    public OnExecuteAndMyOrdersCustomAdapter(Context context, int resource, ArrayList<Map<String, Object>> data, String[] mFrom) {
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

        CourierOrder order = (CourierOrder) data.get(position).get(mFrom[0]);

        String dishes = order.getDishes();

        Integer courierId = (Integer) data.get(position).get(mFrom[3]);

        char[] chars = dishes.toCharArray();

        Resources resources = context.getResources();

        String pizza = null;
        String sushi = null;
        String drink = null;
        String soup = null;

        for (int i = 0; i < chars.length; i++){
            switch (chars[i]){
                case 'П':
                    pizza = "pizza";
                    break;
                case 'Р':
                    sushi = "sushi";
                    break;
                case 'Н':
                    drink = "drink";
                    break;
                case 'С':
                    soup = "soup";
                    break;
            }
        }

        if (pizza != null){
            final int statusResourceId1 = resources.getIdentifier(pizza, "drawable",
                    context.getPackageName());
            holder.imageView1.setImageResource(statusResourceId1);
        }

        if (sushi != null){
            final int statusResourceId2 = resources.getIdentifier(sushi, "drawable",
                    context.getPackageName());
            holder.imageView2.setImageResource(statusResourceId2);
        }

        if (drink != null){
            final int statusResourceId3 = resources.getIdentifier(drink, "drawable",
                    context.getPackageName());
            holder.imageView3.setImageResource(statusResourceId3);
        }

        if (soup != null){
            final int statusResourceId4 = resources.getIdentifier(soup, "drawable",
                    context.getPackageName());
            holder.imageView4.setImageResource(statusResourceId4);
        }

        holder.district.setText(order.getRegionCharcode());
        holder.district.setBackgroundColor(order.getRegionBackground());

        holder.promiseTime.setText(sdf.format(order.getPromiseTime()));
        holder.recommendedTime.setText(sdf.format(order.getPreferedTime()));
        holder.street.setText(order.getStreet());
        holder.address.setText(order.getHouse() + " / " + order.getApartament());

        Boolean onExecute = (Boolean) data.get(position).get(mFrom[1]);

        if (order.getCourierId() == null){
            row.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        } else {
            row.setBackgroundColor(getContext().getResources().getColor(R.color.grey));
        }

        sharedPreferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        maps = sharedPreferences.getString("maps", "GoogleMaps");

        System.out.println("");

        row.setOnClickListener(view -> {
            if (onExecute){
                if (order.getCourierId() == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new CharSequence[]
                                    {getContext().getString(R.string.take)},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            try {
                                                CourierCommand courierCommand = new CourierCommand();
                                                courierCommand.setCourierId(courierId);
                                                courierCommand.setOrderId(order.getOrderID());
                                                courierCommand.setCommand(Commands.UPDATE_ORDER);
                                                String response = (String) new SocketAsyncTask(getContext()).execute(courierCommand).get();
                                                if (response.equals("OK")){
                                                    ArrayList<CourierOrder> orders = OrdersActivity.refreshOrderList();

                                                    Gson gson = new Gson();

                                                    if (orders != null){
                                                        sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                                        getContext().getString(R.string.connection_error_cannot_take), Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                } else {

                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getContext().getString(R.string.client_name) + ": " + order.getName());
                builder.setItems(new CharSequence[]
                                {getContext().getString(R.string.cancel), getContext().getString(R.string.call),
                                        getContext().getString(R.string.map), getContext().getString(R.string.delivered)},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        try {
                                            CourierCommand courierCommand = new CourierCommand();
                                            courierCommand.setCourierId(null);
                                            courierCommand.setOrderId(order.getOrderID());
                                            courierCommand.setCommand(Commands.UPDATE_ORDER);
                                            String response = (String) new SocketAsyncTask(getContext()).execute(courierCommand).get();
                                            if (response.equals("OK")){
                                                CourierCommand courierCommand2 = new CourierCommand();
                                                courierCommand2.setCourierId(courierId);
                                                courierCommand2.setCommand(Commands.GET_ORDER_LIST);
                                                ArrayList<CourierOrder> orders = OrdersActivity.refreshOrderList();

                                                Gson gson = new Gson();

                                                if (orders != null){
                                                    sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                                    getContext().getString(R.string.connection_error_cannot_cancel), Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                        break;
                                    case 1:
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + order.getPhone()));
                                        getContext().startActivity(intent);
                                        break;
                                    case 2:
                                        if (maps.equals("MapsME")){
                                            new OpenMapsMe(order).execute();
                                        } else if (maps.equals("GoogleMaps")){
                                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + order.getStreet() + "+" + order.getHouse() + ",+Lviv+Ukraine");
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                                context.startActivity(mapIntent);
                                            }
                                        }
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

    class OpenMapsMe extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;
        CourierOrder order;

        OpenMapsMe(CourierOrder order){
            this.order = order;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage(context.getString(R.string.loading_maps_me));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            List<MWMPoint> pointsToShow = new ArrayList<>();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("format", "jsonv2"));
            param.add(new BasicNameValuePair("accept-language","UA"));
            param.add(new BasicNameValuePair("country", "Україна"));
            param.add(new BasicNameValuePair("city", "Львів"));
            param.add(new BasicNameValuePair("street", order.getHouse() + " " + order.getStreet()));
            JSONObject result = new JSONParser().makeHttpRequest("http://nominatim.openstreetmap.org/search","GET",param);
            try {
                JSONArray jArr = result.getJSONArray("array");
                JSONObject jObj = jArr.getJSONObject(0);
                System.out.println(jObj.get("lat"));
                System.out.println(jObj.get("lon"));
                MWMPoint pnt = new MWMPoint(jObj.getDouble("lat"), jObj.getDouble("lon"), sdf.format(order.getPromiseTime()) + "\n" + order.getStreet() + ", " + order.getHouse() + "/" + order.getApartament(), order.getOrderID()+"", MWMPoint.Style.PlacemarkOrange);
                pointsToShow.add(pnt);
            } catch (JSONException e) {
                MWMPoint pnt = null;
                try {
                    pnt = new MWMPoint(result.getDouble("lat"), result.getDouble("lon"), sdf.format(order.getPromiseTime()) + "\n" + order.getStreet() + ", " + order.getHouse() + "/" + order.getApartament(), order.getOrderID()+"", MWMPoint.Style.PlacemarkOrange);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if(pnt != null)
                    pointsToShow.add(pnt);
                e.printStackTrace();
            }
            Activity main = (Activity) context;

            MapsWithMeApi.showPointsOnMap(main, "", 9.5, main.createPendingResult(1, new Intent(), 0), 1, pointsToShow.toArray(new MWMPoint[pointsToShow.size()]));

            return result.toString();
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
    }

}
