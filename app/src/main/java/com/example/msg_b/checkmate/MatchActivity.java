package com.example.msg_b.checkmate;

//import android.support.v7.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MatchActivity extends AppCompatActivity {


    ArrayList<User> userArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //유저리스트 만들기
        Intent intent = getIntent();
        ArrayList<?> tmpArrayList = (ArrayList<?>) intent.getSerializableExtra("users");
        userArrayList = new ArrayList<>();
        userArrayList.clear();
        for (Object x : tmpArrayList) {
            userArrayList.add((User) x);
        }

        //액션바 설정
        String type = intent.getStringExtra("type");
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        if(type.equals("from")) {
            ab.setTitle("내가 관심있는 인연");
        }
        else  {
            ab.setTitle("나에게 관심있는 인연");
        }

        //뷰생성
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        initRecyclerView();



        Toast.makeText(this, type + "Users size = " + userArrayList.size(), Toast.LENGTH_SHORT).show();


    }




    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyRecyclerViewHolder>{

        private Context mContext;
        private ArrayList<User> mList;

        public MyRecyclerViewAdapter(Context mContext, ArrayList<User> mList) {
            this.mContext = mContext;
            this.mList = mList;
        }


        @Override
        public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(mContext).inflate(R.layout.item_match2, parent, false);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int itemWidth = size.x/2;
            int itemHeight = size.y/3;

            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;

            v.setLayoutParams(params);
            return new MyRecyclerViewHolder(v);
        }


        @Override
        public void onBindViewHolder(MyRecyclerViewHolder holder, final int position) {

            Glide.with(mContext)
                    .asBitmap()
                    .load(mList.get(position).getImg_profile())
                    .into(holder.iv_profile);

            holder.tv_nickname.setText(mList.get(position).getNickname());
            holder.iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ExActivity.class);
                    intent.putExtra("userdata", mList.get(position));
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }



        class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_profile;
            TextView tv_nickname;

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public MyRecyclerViewHolder(View itemView) {
                super(itemView);

                iv_profile = itemView.findViewById(R.id.Iv_profile);
                iv_profile.setClipToOutline(true);
                tv_nickname = itemView.findViewById(R.id.Tv_nickname);

            }
        }

    }

    public void initRecyclerView() {
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, userArrayList);
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }








}
