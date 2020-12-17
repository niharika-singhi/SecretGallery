package com.niharika.android.secretgallery;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GalleryAdapter extends FragmentStateAdapter {
    String [] mArgumentsArray;

    public GalleryAdapter(@NonNull FragmentActivity fragmentActivity, String [] argumentsArray) {
        super(fragmentActivity);
        mArgumentsArray=argumentsArray;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return GalleryFragment.newInstance(mArgumentsArray[position]);
    }

    @Override
    public int getItemCount() {
        return mArgumentsArray.length;
    }
}
