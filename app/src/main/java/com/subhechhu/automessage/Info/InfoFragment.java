package com.subhechhu.automessage.Info;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.subhechhu.automessage.R;

/**
 * Created by subhechhu on 2/14/2017.
 */

public class InfoFragment extends Fragment {
    public static final String AboutApp = "AboutSplash";
    public static final String ContextImage = "SplashTheme";
    public static final String Position = "Position";
    int position;

    OnSubmitListener mListener;

    interface OnSubmitListener {
        void setOnSubmitListener(String arg);
    }

    public static InfoFragment newInstance(int position, String messageBody, int contextImage) {
        InfoFragment splashFragment = new InfoFragment();
        Bundle bundle = new Bundle(); //Store all the information coming from the Activity

        bundle.putString(AboutApp, messageBody);
        bundle.putInt(ContextImage, contextImage);
        bundle.putInt(Position, position);
        splashFragment.setArguments(bundle);
        return splashFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String messageBody = getArguments().getString(AboutApp);
        int contextImage = getArguments().getInt(ContextImage);
        position = getArguments().getInt(Position);

        mListener= (OnSubmitListener) getActivity();

        View view = inflater.inflate(R.layout.splash_fragment_layout, container, false); //Link the fragment with layout
        TextView messageTV = (TextView) view.findViewById(R.id.textView_body);
        ImageView contextImageIV = (ImageView) view.findViewById(R.id.imageView_context);
        final Button contactPermissionBtn = (Button) view.findViewById(R.id.button_contactPermission);
        final Button smsPermissionBtn = (Button) view.findViewById(R.id.button_smsPermission);
        final Button proceedBtn = (Button) view.findViewById(R.id.button_proceed);
        LinearLayout linearLayoutBtn = (LinearLayout) view.findViewById(R.id.linearlayout_buttons);

        if (position == 2) {
            linearLayoutBtn.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                contactPermissionBtn.setVisibility(View.INVISIBLE);
                smsPermissionBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            linearLayoutBtn.setVisibility(View.INVISIBLE);
        }

        contactPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener("contact");
            }
        });
        smsPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener("sms");
            }
        });
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener("proceed");
            }
        });

        messageTV.setText(messageBody);
        contextImageIV.setBackgroundResource(contextImage);
        return view;
    }
}