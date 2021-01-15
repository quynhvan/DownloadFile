package com.example.downloadfile;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_STORAGE_CODE = 111 ;
    private EditText edtUrl;
    private Button btnDownload;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtUrl = findViewById(R.id.edt_url);
        registerForContextMenu(edtUrl);
        btnDownload = findViewById(R.id.btn_download);
        handleButtonClick();
    }

    public void handleButtonClick(){
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        //permission denied, request it
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permission, PERMISSION_STORAGE_CODE);
                    }
                    else{
                        // permission already granted, perform download
                        startDownloading();
                    }
                }
                else{
                    //system os is less than marshmallow, perform download
                    startDownloading();
                }
            }
        });
    }

    private void startDownloading() {
        String url = edtUrl.getText().toString().trim();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //allow types of network to download files
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE );
        request.setTitle("Download"); // set title in download notification
        request.setDescription("Downloading file...");
        request.notifyAll();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis());
        //get download service and enque file
        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }
    // handler permission result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted from popup, perform download
                    startDownloading();
                }
                else{
                    //permission denied from popup , show error message
                    Toast.makeText(this, "Permisson denied!", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
}
