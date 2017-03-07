package com.subhechhu.automessage.message;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.subhechhu.automessage.Details;
import com.subhechhu.automessage.R;

import java.util.List;

/**
 * Created by subhechhu on 3/6/2017.
 */

public class CustomListAdapter extends ArrayAdapter<Details>{

    List<Details> details;
    Context context;


    public CustomListAdapter(Context context, int resource, List<Details> objects) {
        super(context, resource, objects);
        this.details = objects;
        this.context = context;
    }
    private static class ViewHolder {
        TextView nameTV, numberTV, dateTV, timeTV, messageTV;
        Button button_messenger, button_delete;
        String id;
        int pos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Details rowItem = getItem(position);
        final ViewHolder viewHolder;
        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.recyclerview_content, parent, false);
                viewHolder.nameTV = (TextView) convertView.findViewById(R.id.textView_userName);
                viewHolder.numberTV = (TextView) convertView.findViewById(R.id.textview_userNumber);
                viewHolder.dateTV = (TextView) convertView.findViewById(R.id.textView_date);
                viewHolder.timeTV = (TextView) convertView.findViewById(R.id.textview_time);
                viewHolder.messageTV = (TextView) convertView.findViewById(R.id.textView_message);
                viewHolder.button_messenger = (Button) convertView.findViewById(R.id.button_messenger);
//                viewHolder.button_delete = (Button) convertView.findViewById(R.id.button_delete_row);

                viewHolder.id = "";
                viewHolder.pos = 0;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.id = rowItem.getId();
            viewHolder.pos = position;
            viewHolder.nameTV.setText(rowItem.getName());
            viewHolder.numberTV.setText(rowItem.getNumber());
            viewHolder.dateTV.setText(rowItem.getDate());
            viewHolder.timeTV.setText(rowItem.getTime());
            viewHolder.messageTV.setText(rowItem.getMessage());
            if (rowItem.getMediumSelected() == null) {
                viewHolder.button_messenger.setBackgroundResource(R.drawable.message_icon);
            } else {
                if (rowItem.getMediumSelected().equals("Messenger")) {
                    viewHolder.button_messenger.setBackgroundResource(R.drawable.message_icon);
                } else {
                    viewHolder.button_messenger.setBackgroundResource(R.drawable.whatsapp_icon);
                }
            }
//            viewHolder.button_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("subhechhu","onClick on adapter");
////                    ((MainListActivity) context).DeleteRemainder(rowItem.getId(),viewHolder.pos);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
