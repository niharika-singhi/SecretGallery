package com.niharika.android.secretgallery;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.core.ZoomState;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraView;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {
    private static final String ARG_IMG_TYPE = "imgType";
    private String imgType = "selfie";
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private PreviewView mPreviewView;
    private ImageView mCaptureImage;
    private ImageAnalysis imageAnalysis;
    private ImageCapture imageCapture;
    private CameraSelector cameraSelector;
    private ImageButton mTorchButton, mSwitchButton;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private int cameraMode = 0;
    private Camera mCamera;
    private MaterialCardView mCardView;
    public static final String ARG_PHOTO = "photo_data";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mPreviewView = view.findViewById(R.id.previewView);
        mCaptureImage = view.findViewById(R.id.captureImg);
        mSwitchButton = view.findViewById(R.id.switch_camera_button);
        mTorchButton = view.findViewById(R.id.torch_button);
        mCardView = view.findViewById(R.id.captureCardView);
        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        return view;
    }

    private boolean allPermissionsGranted() {//fn checks whether user has necessary permissions
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Asks runtime permissions
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraMode);
                    addButtonListeners();
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void addButtonListeners() {
        //Button Listensers
        mSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraMode == 0)
                    cameraMode = 1;
                else
                    cameraMode = 0;
                bindPreview(cameraMode);
            }
        });
        mTorchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveData<Integer> torchState = mCamera.getCameraInfo().getTorchState();
                if (torchState.getValue().equals(TorchState.OFF))
                    mCamera.getCameraControl().enableTorch(true);
                else
                    mCamera.getCameraControl().enableTorch(false);
            }
        });
        mCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(imageCapture);
            }
        });
    }

    void bindPreview(final int switchMode) {
        //Preview
        preview = new Preview.Builder()
                .build();
        //Gets a surface provider and set on preview
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        // Select back camera as a default
        if (cameraMode == 0) {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
        } else {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
        }
        imageAnalysis = new ImageAnalysis.Builder()
                .build();
        ImageCapture.Builder builder = new ImageCapture.Builder();
        imageCapture = builder
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();
        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }
        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll();
            // Bind use cases to camera
            mCamera = cameraProvider.bindToLifecycle((LifecycleOwner) getActivity(),
                    cameraSelector, preview, imageAnalysis, imageCapture);
            setZoom();
            CameraInfo cameraInfo = mCamera.getCameraInfo();
        } catch (Exception e) {
            Log.e("TAG", "Use case binding failed", e);
        }
    }

    private void setZoom() {
        SimpleOnScaleGestureListener listener = new SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                ZoomState zoomState = mCamera.getCameraInfo().getZoomState().getValue();
                float scale = zoomState.getZoomRatio() * detector.getScaleFactor();
                mCamera.getCameraControl().setZoomRatio(scale);
                return true;
            }
        };
        ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(getContext(), listener);
        mPreviewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void takePhoto(ImageCapture imageCapture) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        String fileName = mDateFormat.format(new Date()) + ".jpg";
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), getString(R.string.folder_name));
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
            File noMediaFile = new File(imagesFolder, ".nomedia");
            if (!noMediaFile.exists()) {
                try {
                    noMediaFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File file = new File(imagesFolder, fileName);
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
            }
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });

        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Uri uriSavedImage = Uri.fromFile(file);
                        //front camera den transform the image
                        Photo photo = new Photo(file.getAbsolutePath());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ARG_PHOTO, photo);


                        //Font facing camera den flip image
                     /*  if(mCameraView.getCameraLensFacing()==0){
                            bundle.putString(ARG_IMG_TYPE,imgType);
                           Intent flipServiceIntent = new Intent(getContext(), FlipPhotoIntentService.class);
                           flipServiceIntent.putExtra(ARG_PHOTO,file.getAbsolutePath());
                           getActivity().startService(flipServiceIntent);
                           Log.d("tag"," selfie mode");
                       }*/
                        Navigation.findNavController(getView()).navigate(R.id.photoFragment, bundle);
                        //Manually request a re scan to add photo into db
                        //getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }
                });
            }
            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
            }
        });
    }

    private void showSnackBarMsg() {
        Snackbar snackbar = Snackbar.make(getView(), "Image Saved successfully", Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor((R.color.SG_textColor_primary)));
        snackbar.setBackgroundTint(getResources().getColor(R.color.SG_light_yellow));
        mCardView.setVisibility(View.INVISIBLE);
        snackbar.show();
    }
}
