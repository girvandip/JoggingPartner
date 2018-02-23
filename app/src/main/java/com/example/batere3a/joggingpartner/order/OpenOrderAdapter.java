package com.example.batere3a.joggingpartner.order;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batere3a.joggingpartner.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Aldrich on 2/15/2018.
 */

public class OpenOrderAdapter extends RecyclerView.Adapter<OpenOrderAdapter.OrderViewHolder> {
    private JSONArray orderDataArray = null;
    private JSONArray orderIdArray = null;

    public OpenOrderAdapter(String data, String username, String id) {
        try {
            JSONObject orderData = new JSONObject(data);
            orderIdArray = orderData.names();
            orderDataArray = orderData.toJSONArray(orderIdArray);
            int i = 0;
            while(i < orderDataArray.length()){
                if (((JSONObject) orderDataArray.get(i)).getString("id_runner")
                        .equals(id) || ((JSONObject) orderDataArray.get(i))
                        .getString("id_partner").equals(id)) {
                    orderDataArray.remove(i);
                    orderIdArray.remove(i);
                } else {
                    i++;
                }
            }

            i = 0;
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
                        .equals("Progress")) {
                    orderDataArray.remove(i);
                    orderIdArray.remove(i);
                } else {
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public OpenOrderAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(OpenOrderAdapter.OrderViewHolder holder, int position) {
        try {
            JSONObject temp = (JSONObject) orderDataArray.get(position);

            holder.partnerName.setText(temp.getString("runner"));

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
        final OpenOrderAdapter mAdapter;

        public OrderViewHolder(View itemView, OpenOrderAdapter adapter) {
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
