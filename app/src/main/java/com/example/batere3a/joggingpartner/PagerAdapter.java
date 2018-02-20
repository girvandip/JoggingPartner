package com.example.batere3a.joggingpartner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Aldrich on 2/15/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;
    String data;

    public PagerAdapter(FragmentManager fm, int numOfTabs, String data) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.data = data;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        switch(position){
            case 0:
                Orders orders = new Orders();
                orders.setArguments(bundle);
                return orders;
            case 1:
                return new OpenOrder();
            case 2:
                return new History();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }


}
