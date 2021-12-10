package com.niharika.android.secretgallery;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    RecyclerView mRecyclerView;
    FloatingActionButton mFloatingCameraButton;
    ArrayList<Photo> mPhotoList;
    String arg;
    public static final String ARG_TABS = "tabs";
    private PhotoAdapter mPhotoAdapter;

    public static GalleryFragment newInstance(String tab) {
        Bundle args = new Bundle();
        args.putString(ARG_TABS, tab);
        final GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateUI();
    }

    private void updateUI() {
        if (mPhotoAdapter != null) {
            getPhotos();
            mPhotoAdapter.setPhotoList(mPhotoList);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_photo, container, false);
        if (getArguments() != null && getArguments().containsKey(ARG_TABS)) {
            arg = getArguments().getString(ARG_TABS);
        }
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mFloatingCameraButton = view.findViewById(R.id.floatingCameraButton);
        mFloatingCameraButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.cameraFragment, null));
        setLayoutManager();
        mPhotoList = new ArrayList<Photo>();
        getPhotos();
        mPhotoAdapter = new PhotoAdapter(getContext(), mPhotoList);
        mRecyclerView.setAdapter(mPhotoAdapter);
        return view;
    }

    private void setLayoutManager() {
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
    }

    private void getPhotos() {
        if (arg.equals(getString(R.string.MyPhotos)))
            mPhotoList = getMyPhotos();
        else
            mPhotoList = loadPhotos();
    }

    private ArrayList<Photo> loadPhotos() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<Photo> listOfAllImages = new ArrayList<Photo>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //  MediaStore.media-type.Media.EXTERNAL_CONTENT_URI,
        //media-database columns to retrieve
        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";
        ; //sql order by clause getting the latest images
        cursor = getActivity().getContentResolver().query(uri, projection, null, null, sortOrder);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Photo photo = new Photo(absolutePathOfImage);
            listOfAllImages.add(photo);
        }
        return listOfAllImages;
    }

    ArrayList<Photo> getMyPhotos() {

        ArrayList<Photo> listOfAllImages = new ArrayList<Photo>();
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), getString(R.string.folder_name));
        File[] fileArray = imagesFolder.listFiles();
        if(fileArray!=null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Arrays.sort(fileArray, Comparator.comparingLong(File::lastModified).reversed());
            for (File f : fileArray) {
                Photo photo = new Photo(f.getAbsolutePath());
                listOfAllImages.add(photo);
            }
        } else if (fileArray != null) //Displaying the files in desc order so dat the latest is shown first
            for (int i = fileArray.length - 1; i > -1; i--) {
                Photo photo = new Photo(fileArray[i].getAbsolutePath());
                listOfAllImages.add(photo);
            }
        return listOfAllImages;
    }
}
