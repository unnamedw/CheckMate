package com.example.msg_b.checkmate;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msg_b.checkmate.R;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_face;
    Button btn_filter;
    Button btn_test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("OpenCV 연습");
        setContentView(R.layout.activity_test);

        btn_face = findViewById(R.id.Btn_face);
        btn_face.setOnClickListener(this);
        btn_filter = findViewById(R.id.Btn_filter);
        btn_filter.setOnClickListener(this);
        btn_test = findViewById(R.id.Btn_test);
        btn_test.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //얼굴인식
            case R.id.Btn_face:
                Intent intent = new Intent(this, OpencvActivity.class);
                startActivity(intent);
                break;
            //이미지필터
            case R.id.Btn_filter:
                Intent intent2 = new Intent(this, Opencv2Activity.class);
                startActivity(intent2);
                break;
            case R.id.Btn_test:
                Intent intent3 = new Intent(this, Opencv3Activity.class);
                startActivity(intent3);
                break;
                default:
                    break;

        }
    }
}
