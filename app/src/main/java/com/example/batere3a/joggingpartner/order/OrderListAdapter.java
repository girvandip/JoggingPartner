package com.example.batere3a.joggingpartner.order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batere3a.joggingpartner.MainActivity;
import com.example.batere3a.joggingpartner.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Aldrich on 2/15/2018.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private final String username;
    private JSONArray orderDataArray = null;
    private JSONArray orderIdArray = null;

    public OrderListAdapter(String username, String data) {
        try {
            JSONObject orderData = new JSONObject(data);
            orderIdArray = orderData.names();
            orderDataArray = orderData.toJSONArray(orderIdArray);
            int i = 0;
            while(i < orderDataArray.length()){
                if (((JSONObject) orderDataArray.get(i)).getString("status")
                        .equals("Completed")) {
                    orderDataArray.remove(i);
                    orderIdArray.remove(i);
                } else {
                    i++;
                }
            }
            i = 0;
            while(i < orderDataArray.length()){
                if (((JSONObject) orderDataArray.get(i)).getString("status")
                        .equals("Open")) {
                    orderDataArray.remove(i);
                    orderIdArray.remove(i);
                } else {
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.username = username;
    }

    @Override
    public OrderListAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderlist_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OrderDetails.class);

                String data = ((TextView)view.findViewById(R.id.json_container))
                                .getText().toString();
                intent.putExtra("data", data);

                data = ((TextView)view.findViewById(R.id.order_id))
                        .getText().toString();
                intent.putExtra("order_id", data);
                view.getContext().startActivity(intent);

            }
        });
        return new OrderViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.OrderViewHolder holder, int position) {
        try {
            JSONObject temp = (JSONObject) orderDataArray.get(position);

            if(username.equals(temp.getString("runner"))){
                holder.partnerName.setText(temp.getString("partner"));
            } else {
                holder.partnerName.setText(temp.getString("runner"));
            }


            String dateTime = temp.getString("date")
                    + " " + temp.getString("time");
            holder.orderTime.setText(dateTime);

            holder.joggingPlace.setText(temp.getString("address"));

            holder.orderData.setText(temp.toString());

            holder.orderId.setText(orderIdArray.get(position).toString());
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
        public final TextView orderData;
        public final TextView orderId;
        final OrderListAdapter mAdapter;

        public OrderViewHolder(View itemView, OrderListAdapter adapter) {
            super(itemView);
            partnerName = (TextView) itemView.findViewById(R.id.partner_name);
            orderTime = (TextView) itemView.findViewById(R.id.order_time);
            joggingPlace = (TextView) itemView.findViewById(R.id.jogging_place);
            orderData = itemView.findViewById(R.id.json_container);
            orderId = itemView.findViewById(R.id.order_id);
            this.mAdapter = adapter;
        }
    }
}
