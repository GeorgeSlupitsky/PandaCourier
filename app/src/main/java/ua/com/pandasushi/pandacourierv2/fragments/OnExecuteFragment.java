package ua.com.pandasushi.pandacourierv2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pandasushi.pandacourierv2.R;

/**
 * Created by User9 on 21.03.2018.
 */

public class OnExecuteFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_on_execute, container, false);

        return rootView;
    }
}
