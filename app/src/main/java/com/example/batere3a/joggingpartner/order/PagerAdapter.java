package com.example.batere3a.joggingpartner.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.batere3a.joggingpartner.History;

/**
 * Created by Aldrich on 2/15/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private String data;
    private String username;

    public PagerAdapter(FragmentManager fm, int numOfTabs, String data, String username) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.data = data;
        this.username = username;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        bundle.putString("username", username);
        switch(position){
            case 0:
                Orders orders = new Orders();
                orders.setArguments(bundle);
                return orders;
            case 1:
                OpenOrder openOrder = new OpenOrder();
                openOrder.setArguments(bundle);
                return openOrder;
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
