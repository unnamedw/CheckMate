package com.example.msg_b.checkmate.deprecation;

//import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msg_b.checkmate.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class Opencv2Activity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }


    ImageView imageViewInput;
    ImageView imageViewOutput;
    private Mat img_input;
    private Mat img_output;

    private static final String TAG = "opencv2";
    static final int PERMISSION_REQUEST_CODE = 1;
    private final int GET_GALLERY_IMAGE = 200;
    String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;

        for (String perms : permissions) {
            ret = checkCallingOrSelfPermission(perms);
            if(!(ret == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!writeAccepted)
                        {
                            showDialogforPermissions("퍼미션 필요!");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermissions(String s) {
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  Opencv2Activity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(s);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv2);

        imageViewInput = findViewById(R.id.imageViewInput);
        imageViewOutput = findViewById(R.id.imageViewOutput);

        Button sendImageButton = findViewById(R.id.button);
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        Log.e("opencv3", "getRealPathFromURI :: ");
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("opencv3", "onActivityResult :: ");

        if ( requestCode == GET_GALLERY_IMAGE) {
            Log.e("opencv3", "onActivityResult :: requestCode checked ");
            String imagePath = getRealPathFromURI(data.getData());
            img_input = new Mat();
            img_output = new Mat();
            Log.e("opencv3", "onActivityResult :: "+imagePath);

            Log.e("opencv3", "onActivityResult :: start native code ");
            loadImage(imagePath, img_input.getNativeObjAddr());
            imageprocess_and_showResult();
        }
    }



    private void imageprocess_and_showResult() {
        Log.e("opencv3", "imageprocess_and_showResult :: ");
        Log.e("opencv3", "imageprocess_and_showResult :: "+img_input.getNativeObjAddr());
        imageprocessing(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());
        Log.e("opencv3", "imageprocess_and_showResult :: after imageprocessing");
        Bitmap bitmapInput = Bitmap.createBitmap(img_input.cols(), img_input.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_input, bitmapInput);
        imageViewInput.setImageBitmap(bitmapInput);

        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);
        imageViewOutput.setImageBitmap(bitmapOutput);
    }

    private native void loadImage(String imagePath, long nativeObjAddr);
    private native void imageprocessing(long nativeObjAddr, long nativeObjAddr1);




}













