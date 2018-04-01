package ua.com.pandasushi.pandacourierv2.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pandasushi.pandacourierv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User9 on 21.03.2018.
 */

public class OrdersFragment extends Fragment {

    private OnExecuteFragment onExecuteFragment;
    private MyOrdersFragment myOrdersFragment;
    private ClosedFragment closedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        onExecuteFragment = new OnExecuteFragment();
        myOrdersFragment = new MyOrdersFragment();
        closedFragment = new ClosedFragment();

        onExecuteFragment.setArguments(getArguments());
        myOrdersFragment.setArguments(getArguments());
        closedFragment.setArguments(getArguments());

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabs = (TabLayout) rootView.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(onExecuteFragment, getString(R.string.on_execute));
        adapter.addFragment(myOrdersFragment, getString(R.string.my_orders));
        adapter.addFragment(closedFragment, getString(R.string.delivered));
        viewPager.setAdapter(adapter);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
