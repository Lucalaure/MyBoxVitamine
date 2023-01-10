package com.example.vitamin_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.vitamin_app.Fragments.NewsFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    int pick;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        pick = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new NewsFragment();
            case 1:
                return new NewsFragment();
            case 2:
                return new NewsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return pick;
    }
}