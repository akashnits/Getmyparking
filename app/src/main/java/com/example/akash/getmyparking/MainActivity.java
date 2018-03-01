package com.example.akash.getmyparking;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akash.getmyparking.utils.GMPSharedPrefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<String[]>{

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int LOADER_ID= 7384;

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 222;
    private static final int SELECT_PICTURES = 123;
    private ImageAdapter mImageAdapter;
    private BroadcastReceiver mReceiver;

    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    private ArrayList<String> mPictureList;
    @BindView(R.id.postFab)
    FloatingActionButton postFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("REFRESH_ADAPTER");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("REFRESH_ADAPTER"))
                    reload();
            }
        };
        registerReceiver(mReceiver, filter);

        if (!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.imageSize, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        mImageAdapter= new ImageAdapter(this);

        StaggeredGridLayoutManager staggeredGridLayoutManager= new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvFeed.setLayoutManager(staggeredGridLayoutManager);
        rvFeed.setAdapter(mImageAdapter);

        LoaderManager loaderManager= getSupportLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);
    }


    @OnClick(R.id.postFab)
    public void onViewClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select pictures to upload"), SELECT_PICTURES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == SELECT_PICTURES && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                mPictureList = new ArrayList<String>();

                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    ArrayList<Uri> arrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < clipData.getItemCount(); i++) {

                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        arrayUri.add(uri);
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imageEncoded = cursor.getString(columnIndex);
                            mPictureList.add(imageEncoded);
                            cursor.close();
                        }
                    }
                    Log.v("LOG_TAG", "Selected Images" + arrayUri.size());
                    startUpload();
                } else if (data.getData() != null) {

                    Uri imageUri = data.getData();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(imageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imageEncoded = cursor.getString(columnIndex);
                        mPictureList.add(imageEncoded);

                        //start service to upload files
                        startUpload();
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            Log.v(TAG, e.getMessage());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startUpload() {
        Intent uploadIntent = new Intent(this, UploadService.class);
        uploadIntent.putStringArrayListExtra("pictureList", mPictureList);
        startService(uploadIntent);
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent != null && parent.getChildCount() != 0) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            ((TextView) parent.getChildAt(0)).setTextSize(14);

            switch (position){
                case 0:
                    mImageAdapter.setmWidth(100);
                    mImageAdapter.setmHeight(100);
                    mImageAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    mImageAdapter.setmWidth(400);
                    mImageAdapter.setmHeight(400);
                    mImageAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    mImageAdapter.setmWidth(600);
                    mImageAdapter.setmHeight(600);
                    mImageAdapter.notifyDataSetChanged();
                    break;
                default:break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Nothing is selected", Toast.LENGTH_SHORT).show();
    }



    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            String[] mImagesData= null;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(mImagesData == null){
                    forceLoad();
                }
                else{
                    deliverResult(mImagesData);
                }
            }

            @Override
            public String[] loadInBackground() {
               //Load full urls

                LinkedHashSet<String> someStringSet = GMPSharedPrefs.getGmpPhotosPrefSet(getApplicationContext());
                if(someStringSet != null){
                    mImagesData= Arrays.copyOf(someStringSet.toArray(), someStringSet.toArray().length, String[].class);
                    return mImagesData;
                }
                return null;
            }

            @Override
            public void deliverResult(String[] data) {
                mImagesData= data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mImageAdapter.setImagessData(data);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    private void reload(){
        Log.v(TAG, "Reload called");
        LoaderManager loaderManager= getSupportLoaderManager();
        loaderManager.restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }
}
