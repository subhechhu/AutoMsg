package com.subhechhu.automessage.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.subhechhu.automessage.Details;
import com.subhechhu.automessage.R;

import java.util.List;

/**
 * Created by subhechhu on 3/5/2017.
 */

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private java.util.List<Details> detailsList;

    CustomAdapter(List<Details> detailsList) {
        this.detailsList = detailsList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, numberTV, dateTV, timeTV, messageTV;
        Button button_messenger;
        MyViewHolder(View view) {
            super(view);
            nameTV = (TextView) view.findViewById(R.id.textview_userName);
            numberTV = (TextView) itemView.findViewById(R.id.textview_userNumber);
            dateTV = (TextView) itemView.findViewById(R.id.textview_date);
            timeTV = (TextView) itemView.findViewById(R.id.textview_time);
            messageTV = (TextView) itemView.findViewById(R.id.textview_msg);
            button_messenger= (Button) itemView.findViewById(R.id.button_messenger);
        }
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_content, parent, false);
        return new CustomAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomAdapter.MyViewHolder holder, int position) {
        Details details = detailsList.get(position);
        holder.nameTV.setText(details.getName());
        holder.numberTV.setText(details.getNumber());
        holder.dateTV.setText(details.getDate());
        holder.timeTV.setText(details.getTime());
        holder.messageTV.setText(details.getMessage());
        if(details.getMediumSelected().equals("Messenger")){
            holder.button_messenger.setBackgroundResource(R.drawable.message_icon);
        }else if(details.getMediumSelected().equals("Whatsapp")){
            holder.button_messenger.setBackgroundResource(R.drawable.whatsapp_icon);
        }
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }
}
