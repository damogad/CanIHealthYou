package com.example.canihealthyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.google.android.material.tabs.TabLayout;



public class NutritionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NutritionFragment() {
        // Required empty public constructor
    }


/*    // TODO: Rename and change types and number of parameters
    public static NutritionFragment newInstance(String param1, String param2) {
        NutritionFragment fragment = new NutritionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
*/
    public TabLayout tabLayout;
    public CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SharedPreferences prefs = getActivity().getSharedPreferences(ContainerActivity.PREF_NAME, Context.MODE_PRIVATE);
        // Inflate the layout for this fragment
        getActivity().setTitle(getActivity().getResources().getString(R.string.nutrition));
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new CategoryAdapter(getChildFragmentManager(), getActivity());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (prefs.getBoolean(ContainerActivity.VIBRATION_KEY, false)) {
                    viewPager.setHapticFeedbackEnabled(true);
                    viewPager.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                }

                viewPager.setCurrentItem(tab.getPosition());
                int position = tab.getPosition();
                switch (position){
                    case 0:
                        Log.e("Position",String.valueOf(0));
                        getActivity().getIntent().putExtra("position", 0);
                        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                            AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                            audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_LEFT);
                        }
                        break;
                    case 1:
                        Log.e("Position",String.valueOf(1));
                        getActivity().getIntent().putExtra("position", 1);
                        if (prefs.getBoolean(ContainerActivity.SOUND_KEY, false)) {
                            AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                            audioManager.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(getArguments().getInt("position", 0));
        return view;
    }
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
