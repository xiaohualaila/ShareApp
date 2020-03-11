package com.example.shareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermiss();
        tv =  findViewById(R.id.tv);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
        //获取intent
        Intent intent =getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        //设置接收类型为文本
        if (Intent.ACTION_SEND.equals(action) && type != null){
            if ("text/plain".equals(type)) {
                handlerText(intent);
            }
        }
    }

    //该方法用于获取intent所包含的文本信息，并显示到APP的Activity界面上
    private void handlerText(Intent intent) {
        String data = intent.getStringExtra(Intent.EXTRA_TEXT);
        tv.setText(data);
    }

    @Override
    public void onClick(View v) {

        final Uri uri;
        final File file = new File(getPath() + "a.doc");
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 24) {//若SDK大于等于24  获取uri采用共享文件模式
            uri = FileProvider.getUriForFile(this.getApplicationContext(), "com.example.shareapp.fileprovider", file);
//            uri = getImageContentUri(this,file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType(/*"application/pdf"*/getMimeType(file.getAbsolutePath()));//此处可发送多种文件
//        share.setType("application/msword");
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.addCategory(Intent.CATEGORY_DEFAULT);
        share.setPackage("com.tencent.mm");


        //若需要分享到微信，只需要更改Package即可
//share.setPackage("com.tencent.mm")
        if (share.resolveActivity(MainActivity.this.getPackageManager()) != null)
        {
            MainActivity.this.startActivity(share);
        } else {
            Toast.makeText(MainActivity.this, "没有可以处理该pdf文件的应用", Toast.LENGTH_SHORT).show();
        }


//        Intent sendIntent =new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT,"This is my text to send.");
//        sendIntent.setType("text/plain");
//        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

//        ShareUtils.shareWechatFriend(this,file);
    }

    private static final int PERMISSIONS_REQUEST = 1;
    /**
     * 请求权限
     */
    private void requestPermiss() {
        PermissionGen.with(this)
                .addRequestCode(PERMISSIONS_REQUEST)
                .permissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = PERMISSIONS_REQUEST)
    public void requestPhotoSuccess() {
        Log.e("sss", "requestPhotoSuccess: ");
        //成功之后的处理

    }

    @PermissionFail(requestCode = PERMISSIONS_REQUEST)
    public void requestPhotoFail() {
        Log.e("sss", "requestPhotoFail: ");
        //失败之后的处理，我一般是跳到设置界面
        showMissingPermissionDialog();
    }
    /** * 根据文件后缀获取文件MIME类型
     *
     * @param filePath
     * @return
     */
    private static String getMimeType(String filePath)
    {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    /**
     * 获取存储路径，自定义目录
     *
     * @return
     */
    public String getPath() {
        String state = Environment.getExternalStorageState();
        String path;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath()
                    + File.separator
                    + "my"
                    + File.separator;
        } else {
            path = getCacheDir().getPath() + File.separator + "my" + File.separator;
//CHINARES_UT:保存文件和照片的目录
        }
        Log.i("sss",path);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("取消", (dialog, which) -> finish());
        builder.setPositiveButton("设置", (dialog, which) -> startAppSettings());
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }
}
