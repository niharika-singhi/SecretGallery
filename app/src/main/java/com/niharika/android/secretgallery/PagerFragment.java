package com.niharika.android.secretgallery;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagerFragment extends Fragment {
    TabLayout mTabLayout;
    ViewPager2 mViewPager;
    private GalleryAdapter mPagerAdapter;
    public static final String [] mArgumentsArray={"M","A"};
    public ArrayList<String> mTabNames=new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_pager, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_secretgallery);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager=view.findViewById(R.id.viewpager);
        getTabNames();
        mPagerAdapter = new GalleryAdapter(getActivity(),mArgumentsArray);
        mViewPager.setAdapter(mPagerAdapter);
       handleBackButton();
        return view;
    }

    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void getTabNames() { //get tabnames from strings files it has two tabs now All Photos & My Photos
        mTabNames.add(getString(R.string.MyPhotosLabel));
        mTabNames.add(getString(R.string.AllPhotosLabel));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) ->
                    tab.setText(mTabNames.get(position))).attach();
    }
}
