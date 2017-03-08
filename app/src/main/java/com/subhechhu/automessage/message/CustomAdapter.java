package com.subhechhu.automessage.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.subhechhu.automessage.Details;
import com.subhechhu.automessage.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by subhechhu on 3/5/2017.
 */

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private java.util.List<Details> detailsList;
    private Context context;

    CustomAdapter(Context context, List<Details> detailsList) {
        this.detailsList = detailsList;
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, numberTV, dateTV, timeTV, messageTV;
        Button button_messenger, button_deleteRow;
        LinearLayout linearLayout_main;

        MyViewHolder(View view) {
            super(view);
            nameTV = (TextView) view.findViewById(R.id.textview_userName);
            numberTV = (TextView) itemView.findViewById(R.id.textview_userNumber);
            dateTV = (TextView) itemView.findViewById(R.id.textview_date);
            timeTV = (TextView) itemView.findViewById(R.id.textview_time);
            messageTV = (TextView) itemView.findViewById(R.id.textview_msg);
            button_messenger = (Button) itemView.findViewById(R.id.button_messenger);
            button_deleteRow = (Button) itemView.findViewById(R.id.imageView_deleteRow);
            linearLayout_main = (LinearLayout) itemView.findViewById(R.id.linearLayout_main);
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
        final Details details = detailsList.get(position);
        final int pos = position;
        holder.nameTV.setText(details.getName());
        holder.numberTV.setText(details.getNumber());
        holder.dateTV.setText(details.getDate());
        holder.timeTV.setText(details.getTime());
        holder.messageTV.setText(details.getMessage());

        switch (details.getMediumSelected()) {
            case "Messenger":
                holder.button_messenger.setBackgroundResource(R.drawable.message_icon);
                break;
            case "Whatsapp":
                holder.button_messenger.setBackgroundResource(R.drawable.whatsapp_icon);
                break;
            case "Viber":
                holder.button_messenger.setBackgroundResource(R.drawable.viber);
                break;
        }
        holder.button_deleteRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainListActivity) context).DeleteRow(details.getId(), pos);
            }
        });
        Log.e("subhechhudev", "time: " + details.getTimelong());

        Calendar currentCalendar = Calendar.getInstance();
        Calendar remainderCalendar = Calendar.getInstance();
        remainderCalendar.setTimeInMillis(Long.parseLong(details.getTimelong()));

        if (currentCalendar.after(remainderCalendar)) {
            holder.linearLayout_main.setBackgroundColor(context.getResources().getColor(R.color.light_grey)); //past
        } else {
            holder.linearLayout_main.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }
}
