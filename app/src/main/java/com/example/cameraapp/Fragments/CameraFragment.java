package com.example.cameraapp.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cameraapp.MainActivity;
import com.example.cameraapp.R;
import com.example.cameraapp.utils.DataBaseHandler;

import java.io.ByteArrayOutputStream;

public class CameraFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    TextView text, text1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    String photo;
    DataBaseHandler databaseHandler;
    private SQLiteDatabase db;
    Bitmap theImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment, container, false);

        text = view.findViewById(R.id.text);
        text1 = view.findViewById(R.id.text1);
        databaseHandler = new DataBaseHandler(getContext());

        text.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        text1.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((MainActivity) getActivity()).loadFragment(new LocalFragment(), true);
            }
        });
        return view;
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void setDataToDataBase() {
        db = databaseHandler.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(databaseHandler.KEY_IMG_URL, getEncodedString(theImage));

        long id = db.insert(databaseHandler.TABLE_NAME, null, cv);
        if (id < 0) {
            Toast.makeText(getContext(), "Something went wrong. Please try again later...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Add successful", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Camera permission granted", Toast.LENGTH_LONG).show();
                openCamera();
            } else {
                Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            theImage = (Bitmap) data.getExtras().get("data");
            photo = getEncodedString(theImage);
            setDataToDataBase();
        }
    }

    private String getEncodedString(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] imageArr = os.toByteArray();
        return Base64.encodeToString(imageArr, Base64.URL_SAFE);
    }
}

