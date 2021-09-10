package com.example.canihealthyou;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CategoryAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    /**
     * Create a new {@link CategoryAdapter} object.
     *
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     * @param context is the context of the app
     *
     */
    public CategoryAdapter(FragmentManager fm, Context context) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    public CalendarFragment calendarFragment;
    public RecipesFragment recipesFragment;

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                calendarFragment = new CalendarFragment();
                return calendarFragment;
            case 1:
                recipesFragment = new RecipesFragment();
                return recipesFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.Nutrition_Diet);
            case 1:
                return mContext.getString(R.string.Nutrition_Recipes);
            default:
                return null;
        }
    }
}
