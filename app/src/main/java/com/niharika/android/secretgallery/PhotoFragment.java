package com.niharika.android.secretgallery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {
    public static final String ARG_PHOTO = "photo_data";
    private Photo mPhoto;
    private PhotoView mImageView;
    private ImageButton mDelButton, mSaveButton;
    private static final String ARG_IMG_TYPE = "imgType";
    private boolean selfie = false;
    private String imgType = "selfie";
    private View view;

    public static PhotoFragment newInstance(Photo photo) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO, photo);
        final PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.pagerFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        if (getArguments() != null && getArguments().containsKey(ARG_PHOTO))
            mPhoto = (Photo) getArguments().getParcelable(ARG_PHOTO);
      /*  if (getArguments() != null && getArguments().containsKey(ARG_IMG_TYPE))
            if (getArguments().getString(ARG_IMG_TYPE) != null || getArguments().getString(ARG_IMG_TYPE).equals(imgType)) {
                selfie = true;
            }*/
         view = inflater.inflate(R.layout.fragment_photo, container, false);
        mImageView = view.findViewById(R.id.imageView);
        mSaveButton = view.findViewById(R.id.saveButton);
        mDelButton = view.findViewById(R.id.delButton);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        Glide.with(getContext()).load(mPhoto.getFileName())
                .apply(new RequestOptions().override(mImageView.getWidth(), mImageView.getHeight())
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(mImageView);
        handleBackButton();
        //Button click listeners
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), getString(R.string.folder_name));
                File oldFile = new File(mPhoto.getFileName());
                //if the photo is already in same dir
                if (TextUtils.equals(imagesFolder.getAbsolutePath(), oldFile.getParent())) {
                    return;
                }
                else{
                    File newFile = new File(imagesFolder, oldFile.getName());
                    if (!imagesFolder.exists())
                        imagesFolder.mkdirs();
                    if (!newFile.exists())
                        try {
                            newFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    oldFile.renameTo(newFile);
                        oldFile.delete();
                        callBroadCast(oldFile.getAbsolutePath());
                }
                showSnackBarMsg(getString(R.string.savePhoto));
            }
        });
        mDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelDialog();
            }
        });
        return view;
    }

    private void showDelDialog() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delMsg))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delFile();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
    private void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.pagerFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }


    private void delFile() {
        File file = new File(mPhoto.getFileName());
        if (file.exists()) {
            file.delete();
            Navigation.findNavController(getView()).navigate(R.id.pagerFragment);
        }
    }

    public void callBroadCast(String path) {
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(path))));
    }

    private void showSnackBarMsg(String msg) {
        Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor((R.color.SG_textColor_primary)));
        snackbar.setBackgroundTint(getResources().getColor(R.color.SG_light_yellow));
        snackbar.show();
    }

}
