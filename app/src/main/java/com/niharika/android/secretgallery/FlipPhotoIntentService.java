package com.niharika.android.secretgallery;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class FlipPhotoIntentService extends IntentService {

    public static final String ARG_PHOTO="photo_data";
    File file;
    public FlipPhotoIntentService() {
        super("FlipPhotoIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        File file=new File(intent.getStringExtra(ARG_PHOTO));
        flip(file);

    }
    public static void flip(File file)
    {
        Log.d("tag","In flip...");
        Bitmap bitmapSrc = BitmapFactory.decodeFile(file.getAbsolutePath());
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        matrix.postRotate(90.f);

        // return transformed image
        Bitmap bitmapNew=Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
        try (FileOutputStream out = new FileOutputStream(file.getAbsoluteFile())) {
            bitmapNew.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("tag","In flip finishing...");

    }




}
