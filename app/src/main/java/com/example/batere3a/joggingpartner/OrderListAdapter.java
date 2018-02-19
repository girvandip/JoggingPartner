package com.example.batere3a.joggingpartner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by Aldrich on 2/15/2018.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private final LinkedList<String> mWordList;

    public OrderListAdapter(LinkedList<String> orderList) {
        this.mWordList = orderList;
    }

    @Override
    public OrderListAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderlist_item, parent, false);
        return new OrderViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.OrderViewHolder holder, int position) {
        // TODO: check the bind variables to output order data
        String current = mWordList.get(position);
        holder.orderItemView.setText(current);
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        public final TextView orderItemView;
        final OrderListAdapter mAdapter;

        public OrderViewHolder(View itemView, OrderListAdapter adapter) {
            super(itemView);
            orderItemView = (TextView) itemView.findViewById(R.id.partner_name);
            this.mAdapter = adapter;
        }
    }
}
