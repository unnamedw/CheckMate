package com.example.msg_b.checkmate;

//import android.support.v7.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.server.SelectUserThread;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExActivity extends AppCompatActivity {


    ImageView iv_profile;
    TextView tv_nickname, tv_age, tv_sex, tv_introduce, tv_job, tv_live, tv_height, tv_bottom;
    ViewPager vp_profile;

    ProgressDialog mDialog;
    String[] imgArray;

    LinearLayout slideDotLayout;
    ImageView[] dots;
    int dotCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 타이틀 바를 숨김
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex);

        iv_profile = findViewById(R.id.Iv_profile);
        tv_nickname = findViewById(R.id.Tv_nickname);
        tv_age = findViewById(R.id.Tv_age);
        tv_sex = findViewById(R.id.Tv_sex);
        tv_introduce = findViewById(R.id.Tv_introduce);
        tv_job = findViewById(R.id.Tv_job);
        tv_live = findViewById(R.id.Tv_live);
        tv_height = findViewById(R.id.Tv_height);
        tv_bottom = findViewById(R.id.Tv_bottom);
        vp_profile = findViewById(R.id.Vp_profile);


        /** 인텐트 객체에서 넘어온 유저 데이터를 받는다.
         * 유저 데이터는 User 객체의 형태로 받아오며 userdata 라는 키 값으로 되어있다.
         * 만약 받아온 유저가 현재 로그인 된 유저라면 tv_bottom 을 보여주고
         * 현재 유저가 아니라면 tv_bottom 을 다르게 수정한다.
         * 여기서 tv_bottom 은 "현재 회원님의 프로필 모습입니다." 라는 예시 문구임.
         * 프로필 관리 창에서 자기 프로필이 실제로 어떻게 보이는 지를 원할 때 필요함. **/
        Intent intent = getIntent();
        String userType = intent.getStringExtra("usertype");
        final User exUser = (User) intent.getSerializableExtra("userdata");
        final String CurrentUserId = CurrentUserManager.getCurrentUserId(getApplicationContext());

        if(!exUser.getId().equals(CurrentUserId)) {
            switch (userType) {
                case "from":
                    //내가 좋아하는 경우
                    String tmpText = exUser.getNickname()+"님의 응답을 기다리고 있어요";
                    tv_bottom.setText(tmpText);
                    break;
                case "to":
                    //상대가 좋아하는 경우
                    tv_bottom.setText("♡이곳을 눌러 호감을 표시하세요♡");
                    tv_bottom.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                    tv_bottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new SelectUserThread(new String[]{CurrentUserId, exUser.getId(), "like"}).start();
                            Toast.makeText(ExActivity.this, exUser.getNickname()+"님과 매치되었습니다!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    break;
                case "find":
                    String tmpText2 = exUser.getNickname()+"님의 프로필입니다.";
                    tv_bottom.setText(tmpText2);
                    break;
                    default:
                        break;
            }
        }

        /** 프로필 정보를 세팅하는 부분 **/
        tv_nickname.setText(exUser.getNickname());
        tv_age.setText(exUser.getAge() + "세/");
        tv_sex.setText(exUser.getSex());
        tv_introduce.setText(exUser.getIntroduce());
        tv_job.setText(exUser.getJob());
        tv_live.setText(exUser.getLive());
        tv_height.setText(exUser.getHeight()+"cm");


        /** ViewPager 를 만들고 Indicator 를 넣어주는 부분 **/
        //이미지 정보 세팅
        setImgArray(exUser);
        mViewPagerAdapter viewPagerAdapter = new mViewPagerAdapter(getApplicationContext(), imgArray);
        slideDotLayout = findViewById(R.id.SlideDotLayout);
        vp_profile.setAdapter(viewPagerAdapter);
        dotCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotCount];
        for (int i=0; i<dotCount; i++) {
            dots[i] = new ImageView(getApplicationContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            slideDotLayout.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));

        vp_profile.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i=0; i<dotCount; i++) {
                    dots[i].setImageDrawable(
                            ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



//        String cID = CurrentUserManager.getCurrentUserId(this);
//        new getCurrentUserTask(cID).execute();


    }



    /** [현재유저 정보를 받아오는 Task]
     * 파라미터로 id 값을 전달하면 해당 id 에 해당하는 회원정보를 User 객체의 형태로 반환한다.
     * 반환된 값은 onPostExecute 에서 처리하게 되며
     * 여기서는 넘어온 User 객체를 이용하여 프로필 정보를 세팅한다. **/
    class getExUserTask extends AsyncTask<String, Void, User> {

        private String mId;
        public getExUserTask(String id) {
            this.mId = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getApplicationContext());
            mDialog.setCancelable(false);
            mDialog.show();
        }


        @Override
        protected User doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            String strUrl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/getuser.php";
            User result = null;

            RequestBody body = new FormBody.Builder()
                    .add("id", mId)
                    .build();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String resultJson = response.body().string();

                Gson gson = new Gson();
                User UG = gson.fromJson(resultJson, User.class);
                result = UG;

                CurrentUserManager.setCurrentUser(getApplicationContext(), result);
                Log.d("arrayT", " s0 : '"+CurrentUserManager.getCurrentUser(getApplicationContext()).getImg_profile()+"'");
                Log.d("arrayT", " s1 : '"+CurrentUserManager.getCurrentUser(getApplicationContext()).getImg_profile2()+"'");
                Log.d("arrayT", " s2 : '"+CurrentUserManager.getCurrentUser(getApplicationContext()).getImg_profile3()+"'");
                Log.d("arrayT", " s3 : '"+CurrentUserManager.getCurrentUser(getApplicationContext()).getImg_profile4()+"'");
                Log.d("arrayT", " s4 : '"+CurrentUserManager.getCurrentUser(getApplicationContext()).getImg_profile5()+"'");
                Log.d("arrayT", " s5 : '"+CurrentUserManager.getCurrentUser(getApplicationContext()).getImg_profile6()+"'");
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }


        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            setImgArray(user);

            /** ViewPager 를 만들고 Indicator 를 넣어주는 부분 **/
            mViewPagerAdapter viewPagerAdapter = new mViewPagerAdapter(getApplicationContext(), imgArray);
            slideDotLayout = findViewById(R.id.SlideDotLayout);
            vp_profile.setAdapter(viewPagerAdapter);
            dotCount = viewPagerAdapter.getCount();
            dots = new ImageView[dotCount];
            for (int i=0; i<dotCount; i++) {
                dots[i] = new ImageView(getApplicationContext());
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                slideDotLayout.addView(dots[i], params);
            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));

            vp_profile.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (int i=0; i<dotCount; i++) {
                        dots[i].setImageDrawable(
                                ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive));
                    }
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            //텍스트 정보를 세팅하는 부분
            tv_nickname.setText(user.getNickname());
            tv_age.setText(user.getAge() + "세/");
            tv_sex.setText(user.getSex());
            tv_introduce.setText(user.getIntroduce());
            tv_job.setText(user.getJob());
            tv_live.setText(user.getLive());
            tv_height.setText(user.getHeight()+"cm");


            mDialog.dismiss();
        }
    }


    public void setImgArray(User user) {

        int size = 0;
        String[] imgContainer = new String[6];

        /** 이미지가 존재하는 경우 size 에 1씩 더하고 임시 저장공간에 해당 이미지 주소를 담아놓음 **/
        if(user.getImg_profile().length()>3) {
            size = size+1;
            imgContainer[0] = user.getImg_profile();
        }
        if(user.getImg_profile2().length()>3) {
            size = size+1;
            imgContainer[1] = user.getImg_profile2();
        }
        if(user.getImg_profile3().length()>3) {
            size = size+1;
            imgContainer[2] = user.getImg_profile3();
        }
        if(user.getImg_profile4().length()>3) {
            size = size+1;
            imgContainer[3] = user.getImg_profile4();
        }
        if(user.getImg_profile5().length()>3) {
            size = size+1;
            imgContainer[4] = user.getImg_profile5();
        }
        if(user.getImg_profile6().length()>3) {
            size = size+1;
            imgContainer[5] = user.getImg_profile6();
        }

        imgArray = new String[size];
//        Log.d("arrayT", Integer.toString(size));
//        Log.d("arrayT", Integer.toString(imgContainer.length));
        for (int i=0; i<size; i++) {
            imgArray[i] = imgContainer[i];
        }
    }




    class mViewPagerAdapter extends PagerAdapter {

        private Context context;
        private String[] item;
        private LayoutInflater layoutInflater;
        public mViewPagerAdapter(Context mContext, String[] mitem) {
            this.context = mContext;
            this.item = mitem;
        }


        @Override
        public int getCount() {
            return item.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_viewpager_ex, null);
            ImageView iv_profile = view.findViewById(R.id.iv_vp);
            iv_profile.setClipToOutline(true);
//            if(iv_profile == null) {
//                Log.d("vptest", "null임");
//            } else {
//                Log.d("vptest", "객체있음");
//            }
//            iv_profile.setImageResource(R.drawable.icon_check);

//            ImageView iv = new ImageView(context);
            Glide.with(ExActivity.this).load(item[position]).into(iv_profile);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);

//            ViewPager vp = (ViewPager) container;
//            View view = (View) object;
//            vp.removeView(view);
        }
    }

}
