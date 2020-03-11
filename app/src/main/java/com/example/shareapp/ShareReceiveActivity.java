package com.example.shareapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class ShareReceiveActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           getReceive();
    }

    private void getReceive() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.i("sss",type);
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if ("audio/".equals(type)) {
                // 处理发送来音频
            //    ToastUtils.showToast(getContext(),"");
            }else if(type.equals("text/plain")){
                handleSendText(intent); // Handle text being sent
            }
            else if(type.equals("image/")){
                handleSendImage(intent);
            }
            else if (type.startsWith("video/")) {
                // 处理发送来的视频
            } else if (type.startsWith("*/")) {
                //处理发送过来的其他文件
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            ArrayList<Uri> arrayList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (type.startsWith("audio/")) {
                // 处理发送来的多个音频
            } else if (type.startsWith("video/")) {
                //处理发送过来的多个视频
            } else if (type.startsWith("*/")) {
                //处理发送过来的多个文件
            }
        }

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            Log.i("sss",sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }


}
