package com.niharika.android.secretgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<Photo> mPhotoList;

    public static final String ARG_POSITION="position",ARG_PHOTOLIST="photolist";


    public PhotoAdapter(Context context, ArrayList<Photo> photoList) {
        mContext = context;
        inflater=LayoutInflater.from(mContext);
        mPhotoList=photoList;
    }

    void setPhotoList(ArrayList<Photo> pList){
        mPhotoList=pList;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.list_photo_item,parent,false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        Photo photo=mPhotoList.get(position);
        holder.bindPhoto(photo,position);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private Photo mPhoto;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            mImageView=(ImageView) itemView.findViewById(R.id.imageView);
        }

        public void bindPhoto(Photo photo,int position){
            mPhoto=photo;
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_POSITION,position);
            bundle.putParcelableArrayList(ARG_PHOTOLIST, mPhotoList);
            mImageView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.photoPagerFragment,bundle));
            Glide.with(mContext).load(mPhoto.getFileName())
                    .apply(new RequestOptions().override(mImageView.getWidth(), mImageView.getHeight())
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(mImageView);
        }
    }
}
