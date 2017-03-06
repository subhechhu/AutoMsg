package com.subhechhu.automessage.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.subhechhu.automessage.R;

/**
 * Created by subhechhu on 3/4/2017.
 */

class SpinnerAdapter extends BaseAdapter {
    private Context context;
    private int[] appIcons;
    private String[] appName;

    SpinnerAdapter(Context context, int[] icons, String[] appName) {
        this.context = context;
        this.appIcons = icons;
        this.appName = appName;
    }

    private class ViewHolder {
        private ImageView appIconIV;
        private TextView appNameTV;
    }

    @Override
    public int getCount() {
        return appIcons.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_spinner_items, parent, false);
            mViewHolder.appNameTV = (TextView) convertView.findViewById(R.id.textView_appName);
            mViewHolder.appIconIV = (ImageView) convertView.findViewById(R.id.imageView_appIcon);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.appIconIV.setImageResource(appIcons[position]);
        mViewHolder.appNameTV.setText(appName[position]);
        return convertView;
    }
}
