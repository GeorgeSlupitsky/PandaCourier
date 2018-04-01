package ua.com.pandasushi.pandacourierv2.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by postp on 01.04.2018.
 */

public class TrackWritingRestarterBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, TrackWritingService.class));;
    }

}
