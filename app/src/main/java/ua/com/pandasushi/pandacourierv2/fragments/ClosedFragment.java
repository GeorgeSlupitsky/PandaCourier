package ua.com.pandasushi.pandacourierv2.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.pandasushi.database.common.gps.models.Track;
import ua.com.pandasushi.pandacourierv2.DBHelper;
import ua.com.pandasushi.pandacourierv2.adapters.ClosedOrdersAdapter;

/**
 * Created by User9 on 21.03.2018.
 */

public class ClosedFragment extends Fragment{

    private final String ATTRIBUTE_NAME_TRACK = "track";

    private DBHelper dbHelper;
    private List<Track> trackList;
    private Handler handler = new Handler();
    private ClosedOrdersAdapter closedOrdersAdapter;
    private ListView listView;
    private ArrayList<Map<String, Object>> data;
    private String [] mFrom = {ATTRIBUTE_NAME_TRACK};

    Runnable refresh = new Runnable() {
        @Override
        public void run() {
            trackList = new ArrayList<>();

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query("trackdata", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do{ byte[] blob = cursor.getBlob(cursor.getColumnIndex("track"));

                    String json = new String(blob);
                    Gson gson = new Gson();
                    Track track = gson.fromJson(json, new TypeToken<Track>() {
                    }.getType());

                    trackList.add(track);
                }while (cursor.moveToNext());

            }

            cursor.close();
            db.close();

            createCustomAdapter();

            handler.postDelayed(refresh, 30000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_closed, container, false);

        dbHelper = new DBHelper(getContext());

        trackList = new ArrayList<>();

        listView = rootView.findViewById(R.id.lvClosed);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("trackdata", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do{ byte[] blob = cursor.getBlob(cursor.getColumnIndex("track"));

                String json = new String(blob);
                Gson gson = new Gson();
                Track track = gson.fromJson(json, new TypeToken<Track>() {
                }.getType());

                trackList.add(track);
            }while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        createCustomAdapter();

        handler.post(refresh);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refresh);
    }

    private void createCustomAdapter(){

        data = new ArrayList<>();

        for (Track track: trackList) {
            Map<String, Object> m = new HashMap<>();

            m.put(ATTRIBUTE_NAME_TRACK, track);

            data.add(m);
        }

        closedOrdersAdapter = new ClosedOrdersAdapter(getContext(), R.layout.closed_custom_adapter, data, mFrom);

        listView.setAdapter(closedOrdersAdapter);
    }

}
