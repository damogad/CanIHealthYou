package com.example.canihealthyou;


import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.muddzdev.styleabletoast.StyleableToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRateStopFragment extends Fragment {

    public HeartRateStopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getActivity().getResources().getString(R.string.heart_rate));
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_heart_rate, container, false);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    view.setY(motionEvent.getRawY() - view.getHeight());
                    view.setX(motionEvent.getRawX() - view.getWidth());
                }
                return false;
            }
        };

        CardView stop = (CardView) v.findViewById(R.id.stop_button);
        stop.setOnTouchListener(onTouchListener);

        CardView play = (CardView) v.findViewById(R.id.play_button);
        play.setOnTouchListener(onTouchListener);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HeartRateStopFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HeartRateMonitor()).commit();
            }
        });

        return v;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            try {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
