package com.niharika.android.secretgallery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *  Receives two arguments(AllPhotoFragment-> RecyclerView-> PhotoAdapter) a PhotoList for
 *  viewpager to work and the position of the photo ie the photo d user clicked will be shown
 */
public class PhotoPagerFragment extends Fragment {
    private ViewPager2 mViewPager;
    private ArrayList<Parcelable> mPhotoList;
    public static final String ARG_POSITION="position",ARG_PHOTOLIST="photolist";
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Receives two arguments a PhotoList for viewpager to work and the position of the photo ie the photo d user clicked will be shown
        if (getArguments() != null && getArguments().containsKey(ARG_PHOTOLIST)) {
            mPhotoList = getArguments().<Parcelable>getParcelableArrayList(ARG_PHOTOLIST);
            position=getArguments().getInt(ARG_POSITION);
        }
        View view= inflater.inflate(R.layout.fragment_photo_pager, container, false);
        mViewPager=view.findViewById(R.id.photoPager);
        //ViewPager shows the photographs from the list in a sliding view
        mViewPager.setPageTransformer(new DepthPageTransformer());
        mViewPager.setAdapter(new PhotoViewPager(getActivity(),mPhotoList));
        mViewPager.setCurrentItem(position);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Full screen
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //No full screen
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


}
