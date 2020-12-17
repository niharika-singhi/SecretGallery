package com.niharika.android.secretgallery;

import android.content.Context;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

class PhotoViewPager extends FragmentStateAdapter {
    ArrayList<Parcelable> mPhotoList;
    public PhotoViewPager(FragmentActivity fa, ArrayList<Parcelable> photoList) {
        super(fa);
        mPhotoList=photoList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return PhotoFragment.newInstance((Photo) mPhotoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }
}
