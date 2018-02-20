package com.example.batere3a.joggingpartner;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Aldrich on 2/15/2018.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private final LinkedList<String> mWordList;
    private JSONArray orderDataArray = null;

    public OrderListAdapter(LinkedList<String> orderList, String data) {
        try {
            JSONObject orderData = new JSONObject(data);
            JSONArray names = orderData.names();
            orderDataArray = orderData.toJSONArray(names);
            for (int i = 0; i < orderDataArray.length(); i++) {
                if (((JSONObject) orderDataArray.get(i))
                        .getString("status")
                        .equals("completed")) {
                    orderDataArray.remove(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.mWordList = orderList;
    }

    @Override
    public OrderListAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderlist_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),
                        "you click on an order", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), OrderDetails.class);
                // TODO: intent put extra data
                view.getContext().startActivity(intent);
            }
        });
        return new OrderViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.OrderViewHolder holder, int position) {
        try {
            JSONObject temp = (JSONObject) orderDataArray.get(position);

            holder.partnerName.setText(temp.getString("runner"));

            String dateTime = temp.getString("date")
                    + " " + temp.getString("time");
            holder.orderTime.setText(dateTime);

            holder.joggingPlace.setText(temp.getString("address"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderDataArray.length();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        public final TextView partnerName;
        public final TextView orderTime;
        public final TextView joggingPlace;
        final OrderListAdapter mAdapter;

        public OrderViewHolder(View itemView, OrderListAdapter adapter) {
            super(itemView);
            partnerName = (TextView) itemView.findViewById(R.id.partner_name);
            orderTime = (TextView) itemView.findViewById(R.id.order_time);
            joggingPlace = (TextView) itemView.findViewById(R.id.jogging_place);
            this.mAdapter = adapter;
        }
    }
}
