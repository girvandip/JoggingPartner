package com.example.batere3a.joggingpartner.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Aldrich on 2/15/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private String data;
    private String username;
    private String id;

    public PagerAdapter(FragmentManager fm, int numOfTabs, String data, String username, String id) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.data = data;
        this.username = username;
        this.id = id;
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
        bundle.putString("id", id);
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
                History history = new History();
                history.setArguments(bundle);
                return history;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }


}
