package ua.com.pandasushi.pandacourierv2.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pandasushi.pandacourierv2.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.Commands;
import ua.com.pandasushi.database.common.Courier;
import ua.com.pandasushi.database.common.CourierCommand;
import ua.com.pandasushi.database.common.CourierOrder;
import ua.com.pandasushi.database.common.gps.models.Points;
import ua.com.pandasushi.pandacourierv2.DBHelper;
import ua.com.pandasushi.pandacourierv2.activities.LoginActivity;
import ua.com.pandasushi.pandacourierv2.activities.OrdersActivity;
import ua.com.pandasushi.pandacourierv2.connection.SocketAsyncTask;
import ua.com.pandasushi.database.common.gps.models.Track;
import ua.com.pandasushi.pandacourierv2.fragments.MyOrdersFragment;
import ua.com.pandasushi.pandacourierv2.mapsmeapi.MWMPoint;
import ua.com.pandasushi.pandacourierv2.mapsmeapi.MapsWithMeApi;
import ua.com.pandasushi.pandacourierv2.parser.JSONParser;
import ua.com.pandasushi.pandacourierv2.services.TrackWritingService;

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
    private Gson gson = new Gson();
    private List<CourierOrder> myOrders;
    private Handler handler = new Handler();
    private Integer courierId;
    private DBHelper dbHelper;
    private boolean connection = true;

    private Runnable requestForSavingTrackAndMyDeliveredOrders = new Runnable() {
        @Override
        public void run() {
            try {
                CourierCommand courierCommand = new CourierCommand();
                courierCommand.setCourierId(courierId);
                courierCommand.setCommand(Commands.CHECK);
                String response = (String) new SocketAsyncTask(MyOrdersFragment.HOST).execute(courierCommand).get();
                if (response.equals("OK")){
                    Track track = TrackWritingService.track;
                    context.stopService(new Intent(context, TrackWritingService.class));
                    MyOrdersFragment.serviceStarted = false;
                    sharedPreferences.edit().putBoolean("connectionForMyOrders", true).apply();

                    String response2;
                    if (track != null){
                        CourierCommand courierCommand2 = new CourierCommand();
                        courierCommand2.setCourierId(courierId);
                        courierCommand2.setTrack(track);
                        courierCommand2.setCommand(Commands.SAVE_TRACK);
                        response2 = (String) new SocketAsyncTask(MyOrdersFragment.HOST).execute(courierCommand2).get();
                        if (response2 != null) {
                            if (response2.equals("OK")){
                                CourierCommand courierCommand3 = new CourierCommand();
                                courierCommand3.setCourierId(courierId);
                                courierCommand3.setCommand(Commands.GET_ORDER_LIST);
                                ArrayList<CourierOrder> orders = OrdersActivity.refreshOrderList();

                                if (orders != null){
                                    sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                                }

                                handler.removeCallbacks(this);
                            }
                        }
                    } else {
                        Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                getContext().getString(R.string.no_saved_tracks), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

            } catch (Exception e){
                handler.postDelayed(this, 1000);
            }
        }
    };

    public OnExecuteAndMyOrdersCustomAdapter(Context context, int resource, ArrayList<Map<String, Object>> data, String[] mFrom) {
        super(context, resource, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = resource;
        this.mFrom = mFrom;
    }

    public OnExecuteAndMyOrdersCustomAdapter(Context context, int resource, ArrayList<Map<String, Object>> data, String[] mFrom, List<CourierOrder> myOrders) {
        super(context, resource, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = resource;
        this.mFrom = mFrom;
        this.myOrders = myOrders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        dbHelper = new DBHelper(context);

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
        holder.orderNumber = row.findViewById(R.id.order_number);

        CourierOrder order = (CourierOrder) data.get(position).get(mFrom[0]);

        String dishes = order.getDishes();

        courierId = (Integer) data.get(position).get(mFrom[3]);

        char[] chars = dishes.toCharArray();

        Resources resources = context.getResources();

        String pizza = null;
        String sushi = null;
        String drink = null;
        String soup = null;

        for (char aChar : chars) {
            switch (aChar) {
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

        long diff = (order.getPromiseTime().getTime() - new Date().getTime())/60000;

        holder.time.setText(diff+"");

        if (order.getOnTime() != null){
            if (!order.getOnTime()){
                if (diff > 30){
                    holder.time.setTextColor(Color.GREEN);
                } else if (diff <= 30 && diff > 20){
                    holder.time.setTextColor(Color.YELLOW);
                } else if (diff <= 20 && diff > 5){
                    holder.time.setTextColor(Color.parseColor("#ffa500"));
                } else if (diff <= 5){
                    holder.time.setTextColor(Color.RED);
                }
            } else {
                holder.time.setTextColor(Color.parseColor("#551a8b"));
                holder.time.setTypeface(null, Typeface.BOLD);
            }
        }

        holder.promiseTime.setText(sdf.format(order.getPromiseTime()));
        holder.recommendedTime.setText(sdf.format(order.getPreferedTime()));
        holder.street.setText(order.getStreet());
        holder.address.setText(order.getHouse() + " / " + order.getApartament());

        Boolean onExecute = (Boolean) data.get(position).get(mFrom[1]);

        if (onExecute){
            holder.orderNumber.setText(order.getCharcode());
            if (order.getCourierId() == null){
                row.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            } else {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.grey));
            }
        } else {
            holder.orderNumber.setVisibility(View.GONE);
            if (order.getDeliverTime() == null){
                row.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            } else {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.grey));
            }
        }

        sharedPreferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        maps = sharedPreferences.getString("maps", "GoogleMaps");

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
                                                order.setCourierId(courierId);
                                                order.setSendTime(new Date());
                                                courierCommand.setOrder(order);
                                                courierCommand.setCommand(Commands.UPDATE_ORDER);
                                                String response = (String) new SocketAsyncTask(MyOrdersFragment.HOST).execute(courierCommand).get();
                                                if (response.equals("OK")){
                                                    ArrayList<CourierOrder> orders = OrdersActivity.refreshOrderList();

                                                    Gson gson = new Gson();

                                                    if (orders != null){
                                                        sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                                                    }

                                                    if (MyOrdersFragment.myOrdersNotDelivered.size() == 0){
                                                        TrackWritingService.courierId = courierId;
                                                    }

                                                    MyOrdersFragment.myOrdersNotDelivered.add(order);

                                                    String myOrdersND = gson.toJson(MyOrdersFragment.myOrdersNotDelivered);

                                                    sharedPreferences.edit().putString("myOrdersNotDelivered", myOrdersND).apply();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                                        getContext().getString(R.string.connection_error_cannot_take), Toast.LENGTH_LONG);
                                                toast.show();
                                                connection = false;
                                            }
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                } else {
                    if (connection){
                        String courierJson = sharedPreferences.getString("couriers", "");

                        if (!courierJson.equals("")){
                            List <Courier> couriers = gson.fromJson(courierJson, new TypeToken<List<Courier>>(){}.getType());

                            String courierName = null;

                            for (Courier courier: couriers){
                                if (order.getCourierId().equals(courier.getId())){
                                    courierName = courier.getName();
                                }
                            }

                            if (courierName != null){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setItems(new CharSequence[]
                                                {courierName},
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                }
                                            }
                                        });
                                builder.create().show();
                            }


                        }
                    }
                }
            } else {
                if (order.getDeliverTime() == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    String comment = order.getComment();
                    if (comment == null) {
                        comment = "";
                    }
                    builder.setTitle(getContext().getString(R.string.client_name) + ": " + order.getName() + "\n" +
                                    getContext().getString(R.string.comment) + ": " + comment);
                    builder.setItems(new CharSequence[]
                                    {getContext().getString(R.string.cancel), getContext().getString(R.string.call),
                                            getContext().getString(R.string.map), getContext().getString(R.string.delivered), getContext().getString(R.string.customer_refused)},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            try {
                                                CourierCommand courierCommand = new CourierCommand();
                                                courierCommand.setCourierId(null);
                                                courierCommand.setOrder(order);
                                                courierCommand.setCommand(Commands.UPDATE_ORDER);
                                                String response = (String) new SocketAsyncTask(MyOrdersFragment.HOST).execute(courierCommand).get();
                                                if (response.equals("OK")){
                                                    CourierCommand courierCommand2 = new CourierCommand();
                                                    courierCommand2.setCourierId(courierId);
                                                    courierCommand2.setCommand(Commands.GET_ORDER_LIST);
                                                    ArrayList<CourierOrder> orders = OrdersActivity.refreshOrderList();

                                                    if (orders != null){
                                                        sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                                                    }

                                                    Iterator itr = MyOrdersFragment.myOrdersNotDelivered.iterator();
                                                    while (itr.hasNext())
                                                    {
                                                        CourierOrder courierOrderToRemove = (CourierOrder) itr.next();
                                                        if (courierOrderToRemove.getOrderID().equals(order.getOrderID())){
                                                            itr.remove();
                                                        }
                                                    }

                                                    Iterator itr2 = TrackWritingService.orders.iterator();
                                                    while (itr2.hasNext())
                                                    {
                                                        CourierOrder orderToRemoveFromService = (CourierOrder) itr2.next();
                                                        if (orderToRemoveFromService.getOrderID().equals(order.getOrderID())){
                                                            itr2.remove();
                                                        }
                                                    }

                                                    if (MyOrdersFragment.myOrdersNotDelivered.size() == 0){
                                                        context.stopService(new Intent(context, TrackWritingService.class));
                                                        MyOrdersFragment.serviceStarted = false;
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
                                            for (CourierOrder courierOrder: myOrders){
                                                if (courierOrder.getOrderID().equals(order.getOrderID())){
                                                    courierOrder.setDeliverTime(new Date());
                                                    courierOrder.setPoint(new Points(String.valueOf(TrackWritingService.currentLat), String.valueOf(TrackWritingService.currentLon)));

                                                    try {
                                                        CourierCommand courierCommand = new CourierCommand();
                                                        courierCommand.setCourierId(courierId);
                                                        courierCommand.setCommand(Commands.CHECK);
                                                        String response = (String) new SocketAsyncTask(MyOrdersFragment.HOST).execute(courierCommand).get();
                                                        if (response.equals("OK")){
                                                            sharedPreferences.edit().putBoolean("connectionForMyOrders", true).apply();
                                                            CourierCommand courierCommand2 = new CourierCommand();
                                                            courierCommand2.setCourierId(courierId);
                                                            courierCommand2.setCommand(Commands.GET_ORDER_LIST);
                                                            ArrayList<CourierOrder> orders = OrdersActivity.refreshOrderList();

                                                            if (orders != null){
                                                                sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                                                            }

                                                            String myOrdersND = gson.toJson(MyOrdersFragment.myOrdersNotDelivered);

                                                            sharedPreferences.edit().putString("myOrdersNotDelivered", myOrdersND).apply();

                                                            Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                                                    getContext().getString(R.string.didnt_go), Toast.LENGTH_LONG);
                                                            toast.show();
                                                        }


                                                    } catch (Exception e){
                                                        sharedPreferences.edit().putBoolean("connectionForMyOrders", false).apply();
                                                        sharedPreferences.edit().putString("myOrders", gson.toJson(myOrders)).apply();

                                                        Iterator itr = MyOrdersFragment.myOrdersNotDelivered.iterator();
                                                        while (itr.hasNext())
                                                        {
                                                            CourierOrder courierOrderToRemove = (CourierOrder) itr.next();
                                                            if (courierOrderToRemove.getOrderID().equals(order.getOrderID())){
                                                                itr.remove();
                                                                TrackWritingService.orders.add(courierOrder);
                                                                String myOrdersND = gson.toJson(MyOrdersFragment.myOrdersNotDelivered);
                                                                sharedPreferences.edit().putString("myOrdersNotDelivered", myOrdersND).apply();
                                                            }
                                                        }

                                                        if (MyOrdersFragment.myOrdersNotDelivered.size() == 0){
                                                            handler.post(requestForSavingTrackAndMyDeliveredOrders);
                                                        }
                                                    }
                                                }
                                            }
                                            break;

                                        case 4:
                                            for (CourierOrder courierOrder: myOrders){
                                                if (courierOrder.getOrderID().equals(order.getOrderID())){
                                                    courierOrder.setDeliverTime(new Date());
                                                    courierOrder.setPoint(new Points(String.valueOf(TrackWritingService.currentLat), String.valueOf(TrackWritingService.currentLon)));

                                                    try {
                                                        CourierCommand courierCommand = new CourierCommand();
                                                        courierCommand.setCourierId(courierId);
                                                        courierCommand.setCommand(Commands.CHECK);
                                                        String response = (String) new SocketAsyncTask(MyOrdersFragment.HOST).execute(courierCommand).get();
                                                        if (response.equals("OK")){
                                                            sharedPreferences.edit().putBoolean("connectionForMyOrders", true).apply();
                                                            CourierCommand courierCommand2 = new CourierCommand();
                                                            courierCommand2.setCourierId(courierId);
                                                            courierCommand2.setCommand(Commands.GET_ORDER_LIST);
                                                            ArrayList<CourierOrder> orders = OrdersActivity.refreshOrderList();

                                                            if (orders != null){
                                                                sharedPreferences.edit().putString("orders", gson.toJson(orders)).apply();
                                                            }

                                                            String myOrdersND = gson.toJson(MyOrdersFragment.myOrdersNotDelivered);

                                                            sharedPreferences.edit().putString("myOrdersNotDelivered", myOrdersND).apply();

                                                            Toast toast = Toast.makeText(getContext().getApplicationContext(),
                                                                    getContext().getString(R.string.didnt_go), Toast.LENGTH_LONG);
                                                            toast.show();
                                                        }


                                                    } catch (Exception e){
                                                        sharedPreferences.edit().putBoolean("connectionForMyOrders", false).apply();
                                                        sharedPreferences.edit().putString("myOrders", gson.toJson(myOrders)).apply();

                                                        Iterator itr = MyOrdersFragment.myOrdersNotDelivered.iterator();
                                                        while (itr.hasNext())
                                                        {
                                                            CourierOrder courierOrderToRemove = (CourierOrder) itr.next();
                                                            if (courierOrderToRemove.getOrderID().equals(order.getOrderID())){
                                                                itr.remove();
                                                                TrackWritingService.orders.add(courierOrder);
                                                                String myOrdersND = gson.toJson(MyOrdersFragment.myOrdersNotDelivered);

                                                                sharedPreferences.edit().putString("myOrdersNotDelivered", myOrdersND).apply();
                                                            }
                                                        }

                                                        if (MyOrdersFragment.myOrdersNotDelivered.size() == 0){
                                                            handler.post(requestForSavingTrackAndMyDeliveredOrders);
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                }
            }
        });

        return row;
    }

    static class OnExecuteAndMyOrdersHolder{
        TextView orderNumber;
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
