package com.example.msg_b.checkmate;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.msg_b.checkmate.mainFragment.ChatFragment;
import com.example.msg_b.checkmate.mainFragment.EtcFragment;
import com.example.msg_b.checkmate.mainFragment.FindFragment;
import com.example.msg_b.checkmate.mainFragment.LoveFragment;
import com.example.msg_b.checkmate.mainFragment.ProfileFragment;
import com.example.msg_b.checkmate.server.SetStatusTask;
import com.example.msg_b.checkmate.service.MyService;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {



    /** 파싱테스트 **/
    private final String TAG_JSON="users";
    private final String TAG_ID = "id";
    private final String TAG_TYPE = "type";
    private final String TAG_STATUS ="status";
    private final String TAG_NICKNAME ="nickname";
    private final String TAG_SEX ="sex";
    private final String TAG_AGE ="age";
    private final String TAG_IMG_PROFILE ="img_profile";
    private final String TAG_IMG_PROFILE2 ="img_profile2";
    private final String TAG_IMG_PROFILE3 ="img_profile3";
    private final String TAG_IMG_PROFILE4 ="img_profile4";
    private final String TAG_IMG_PROFILE5 ="img_profile5";
    private final String TAG_IMG_PROFILE6 ="img_profile6";
    private final String TAG_INTRODUCE ="introduce";
    private final String TAG_LIVE ="live";
    private final String TAG_JOB ="job";
    private final String TAG_HEIGHT ="height";

    private final int ID_HEART = 999;

    String mJsonString;
    public static ArrayList<User> uArray;
    Double longi, lati;


    /** 임시변수 **/
    public static int heart = 999; // 임시코드



    /** 네비게이션 & 프래그먼트 **/
    BottomNavigationView bottomNavigationView;
    FindFragment findFragment;
    EtcFragment etcFragment;
    LoveFragment loveFragment;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    TextView tv_checkmate;
    FloatingActionButton floatingActionButton;

    private boolean hackActionBarReset = false;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //a hack to reset the items in the action bar.
        hackActionBarReset = true;
        invalidateOptionsMenu();
        hackActionBarReset = false;
        invalidateOptionsMenu();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar, menu);
//        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem item_heart = menu.findItem(R.id.item1);
        item_heart.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        item_heart.setTitle(R.string.item_money);*/


        MenuItem item = menu.add(Menu.NONE, ID_HEART, 0, "♥ " + heart); // 임시코드
//        item.setIcon(R.drawable.ic_favorite_black_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT | (hackActionBarReset ? MenuItem.SHOW_AS_ACTION_NEVER : MenuItem.SHOW_AS_ACTION_IF_ROOM));

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case ID_HEART:
                Toast.makeText(this, "하트를 충전하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, BillingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 타이틀 바를 숨김
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //uArray = new ArrayList<>();




        findFragment = new FindFragment();
        etcFragment = new EtcFragment();
        loveFragment = new LoveFragment();
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v ->
                Toast.makeText(this, "Deprecated Test Activity", Toast.LENGTH_LONG).show());

        tv_checkmate = findViewById(R.id.Tv_checkmate);
        tv_checkmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
//        BottomNavigationHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_item3);
        //액티비티 시작시, 특정 프래그먼트를 자동으로 선택함.

        String CurrentID = CurrentUserManager.getCurrentUserId(this);
//        UpdateCurrentUserTask mTask = new UpdateCurrentUserTask(CurrentID);
//        mTask.execute();

        //new GetData().execute("http://115.71.238.160/novaproject1/HomeActivity/getalluser.php");
//        longi = null;
//        lati = null;
//        getLocation();

        startBroadCast();

        stopService(new Intent(this, MyService.class));
    }



    BroadcastReceiver mReceiver = null;
    int num = 0;
    public void startBroadCast() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("test");

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals("test"))
                    Toast.makeText(context, "메시지 받음 "+num, Toast.LENGTH_SHORT).show();

                num++;

            }
        };

//        this.registerReceiver(mReceiver, intentFilter);

//        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }




    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
        new SetStatusTask().execute(CurrentUserManager.getCurrentUserId(this), "1");
    }



    @Override
    protected void onStop() {
        super.onStop();
        new SetStatusTask().execute(CurrentUserManager.getCurrentUserId(this), "0");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("text5", "HomeDestroy");
    }






    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case(R.id.navigation_item1):
                getSupportFragmentManager().beginTransaction().replace(R.id.container, findFragment).commit();
                return true;

            case(R.id.navigation_item2):
                getSupportFragmentManager().beginTransaction().replace(R.id.container, loveFragment).commit();
                return true;

            case(R.id.navigation_item3):
                getSupportFragmentManager().beginTransaction().replace(R.id.container, etcFragment).commit();
                return true;

            case(R.id.navigation_item4):
                getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
                return true;

            case(R.id.navigation_item5):
                getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                return true;

            default:
                return false;
        }


    }




    /** HomeActivity 를 실행시 현재 로그인 된 유저 정보를 받아오는 Task **/
//    class getCurrentUserAndSetTask extends AsyncTask<String, Void, User> {
//
//        private String mId;
//        public getCurrentUserAndSetTask(String id) {
//            this.mId = id;
//        }
//
//        ProgressDialog mDialog;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mDialog = new ProgressDialog(HomeActivity.this);
//            mDialog.setCancelable(false);
//            mDialog.show();
//        }
//
//
//        @Override
//        protected User doInBackground(String... strings) {
//
//            OkHttpClient client = new OkHttpClient();
//            String strUrl = "http://115.71.238.160/novaproject1/HomeActivity/getUser.php";
//            User result = null;
//
//            RequestBody body = new FormBody.Builder()
//                    .add("id", mId) //현재 로그인 된 유저의 id를 'id'라는 키값으로 보냄
//                    .build();
//            Request request = new Request.Builder()
//                    .url(strUrl)
//                    .post(body)
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                String resultJson = response.body().string();
//
//                Gson gson = new Gson();
//                User UG = gson.fromJson(resultJson, User.class);
//                result = UG;
//
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            return result;
//        }
//
//
//        @Override
//        protected void onPostExecute(User user) {
//            super.onPostExecute(user);
//            CurrentUserManager.setCurrentUser(HomeActivity.this, user);
//            mDialog.dismiss();
//        }
//    }



    /** 뒤로가기 버튼을 눌렀을 때 종료여부를 확인하는 메소드 **/
    private long backPressTime;
    private Toast backToast;
    @Override
    public void onBackPressed() {

        if(backPressTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressTime = System.currentTimeMillis();
    }





    /** 전체 여성유저 데이터 받아오는 태스크 **/
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(HomeActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                String result = sb.toString().trim();
                return result;

            } catch (Exception e) {
                errorString = e.toString();
                return null;
            }

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result == null){

                Log.d("JsonTest", errorString);
            }
            else {
                mJsonString = result;
                showResult();

            }
        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String type = item.getString(TAG_TYPE);
                String status = item.getString(TAG_STATUS);
                String nickname = item.getString(TAG_NICKNAME);
                String sex = item.getString(TAG_SEX);
                String age = item.getString(TAG_AGE);
                String img_profile = item.getString(TAG_IMG_PROFILE);
                String img_profile2 = item.getString(TAG_IMG_PROFILE2);
                String img_profile3 = item.getString(TAG_IMG_PROFILE3);
                String img_profile4 = item.getString(TAG_IMG_PROFILE4);
                String img_profile5 = item.getString(TAG_IMG_PROFILE5);
                String img_profile6 = item.getString(TAG_IMG_PROFILE6);
                String introduce = item.getString(TAG_INTRODUCE);
                String live = item.getString(TAG_LIVE);
                String job = item.getString(TAG_JOB);
                String height = item.getString(TAG_HEIGHT);

//                HashMap<String,String> hashMap = new HashMap<>();
//
//                hashMap.put(TAG_ID, id);
//                hashMap.put(TAG_TYPE, type);
//                hashMap.put(TAG_STATUS, status);
//                hashMap.put(TAG_NICKNAME, nickname);
//                hashMap.put(TAG_SEX, sex);
//                hashMap.put(TAG_AGE, age);
//                hashMap.put(TAG_IMG_PROFILE, img_profile);
//                hashMap.put(TAG_IMG_PROFILE2, img_profile2);
//                hashMap.put(TAG_IMG_PROFILE3, img_profile3);
//                hashMap.put(TAG_IMG_PROFILE4, img_profile4);
//                hashMap.put(TAG_IMG_PROFILE5, img_profile5);
//                hashMap.put(TAG_IMG_PROFILE6, img_profile6);
//                hashMap.put(TAG_INTRODUCE, introduce);
//                hashMap.put(TAG_LIVE, live);
//                hashMap.put(TAG_JOB, job);
//                hashMap.put(TAG_HEIGHT, height);
//
//                mArrayList.add(hashMap);
                User mUser = new User();
                mUser.setId(id);
                mUser.setType(type);
                mUser.setStatus(status);
                mUser.setNickname(nickname);
                mUser.setSex(sex);
                mUser.setAge(age);
                mUser.setImg_profile(img_profile);
                mUser.setImg_profile2(img_profile2);
                mUser.setImg_profile3(img_profile3);
                mUser.setImg_profile4(img_profile4);
                mUser.setImg_profile5(img_profile5);
                mUser.setImg_profile6(img_profile6);
                mUser.setIntroduce(introduce);
                mUser.setLive(live);
                mUser.setJob(job);
                mUser.setHeight(height);

                uArray.add(mUser);

            }
            Toast.makeText(this, "유저정보 세팅 완료", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }






    public void getLocation() {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!

        final LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //여기서 위치값이 갱신되면 이벤트가 발생한다.
                //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

                Log.d("locT", "onLocationChanged, location:" + location);
                double longitude = location.getLongitude(); //경도
                double latitude = location.getLatitude();   //위도
                double altitude = location.getAltitude();   //고도
                float accuracy = location.getAccuracy();    //정확도
                String provider = location.getProvider();   //위치제공자
                //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
                //Network 위치제공자에 의한 위치변화
                //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
                String result = "위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                        + "\n고도 : " + altitude + "\n정확도 : "  + accuracy;

                Log.d("locT", result);
//                Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
                longi = location.getLongitude();
                lati = location.getLatitude();

                String id = CurrentUserManager.getCurrentUserId(getApplicationContext());
                String status = "1";

                new updateLocationTask().execute(id, status, longi+"", lati+"");

            }
            public void onProviderDisabled(String provider) {
                // Disabled시
                Log.d("locT", "onProviderDisabled, provider:" + provider);
            }

            public void onProviderEnabled(String provider) {
                // Enabled시
                Log.d("locT", "onProviderEnabled, provider:" + provider);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // 변경시
                Log.d("locT", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
            }
        };



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            Log.d("locT", "권한있음");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    600000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    600000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);

        } else {
            Log.d("locT", "권한없음");
        }

    }


    /** 위치정보를 업데이트하는 Task **/
    class updateLocationTask extends AsyncTask<String, Void, String> {

        String _id;
        String _status;
        String _longi;
        String _lati;

        @Override
        protected String doInBackground(String... strings) {
            _id = strings[0];
            _status = strings[1];
            _longi = strings[2];
            _lati = strings[3];

            String result = null;
            try {
                URL url = new URL("http://115.71.238.160/novaproject1/setlocation.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                String outData = "id="+_id+"&status="+_status+"&longi="+_longi+"&lati="+_lati;
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                /** 응답 받기 **/
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("thTT", conn.getResponseMessage());
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                StringBuilder page = new StringBuilder();

                // 라인을 받아와 합친다.
                while ((line = bufferedReader.readLine()) != null){
                    page.append(line);
                }

                result = page.toString();
                Log.d("thTT", page.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("1")) {
                Toast.makeText(getApplicationContext(), "위치정보 저장 성공", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "에러코드 : "+s, Toast.LENGTH_SHORT).show();
            }

        }
    }

}
