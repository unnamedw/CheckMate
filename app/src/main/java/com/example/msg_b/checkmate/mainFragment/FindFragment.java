package com.example.msg_b.checkmate.mainFragment;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.ExActivity;
import com.example.msg_b.checkmate.HomeActivity;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.server.FcmLikeRequest;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.ExUser;
import com.example.msg_b.checkmate.util.User;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class FindFragment extends androidx.fragment.app.Fragment implements Serializable{

    public FindFragment() {
        // Required empty public constructor
    }


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
    private final String TAG_LATI ="lati";
    private final String TAG_LONGI ="longi";

    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;




    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private ArrayList<ExUser> array;
    private SwipeFlingAdapterView flingContainer;

    GetData getData;



    /** 액션바 설정 **/
    private final int ID_HEART = 999;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add(Menu.NONE, ID_HEART, 0, "♥ " + HomeActivity.heart); // 임시코드
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        super.onCreateOptionsMenu(menu, inflater);
    }
    void updateMenuTitle(int addedHeart) {
        HomeActivity.heart += addedHeart;
        getActivity().invalidateOptionsMenu();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        androidx.appcompat.app.ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle("인연찾기");
        View v = inflater.inflate(R.layout.fragment_find, null);

        //액션바를 숨기고 보이는 코드; hide() or show()
//        android.support.v7.app.ActionBar actionBar = ((HomeActivity)getActivity()).getSupportActionBar();
//        actionBar.show();

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage("오른쪽으로 스와이프 시 상대방에게 호감을 표시하게 되며 하트 3개가 소모됩니다.");
        adb.setCancelable(false);

        //Positive[오른쪽]
        adb.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });

        adb.show();






        flingContainer = v.findViewById(R.id.SwipeFrame);

        array = new ArrayList<>();

        myAppAdapter = new MyAppAdapter(array, getContext());
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            ExUser selectedUser;
            String[] likeInfo = new String[3];
            User nowUser = CurrentUserManager.getCurrentUser(getContext());
            String nowID = nowUser.getId();
            boolean heartOk; // 하트 갯수가 충분한 경우

            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("swipeT", "removeFirstObjectInAdapter : " + array.size());

                if(HomeActivity.heart<3)
                    heartOk = false;
                else
                    heartOk = true;

                selectedUser = array.get(0);
//                if(heartOk) {
//                    selectedUser = array.get(0);
//                    array.remove(0);
//                    Log.d("swipeT", "heartOk : " + array.size());
//                    myAppAdapter.notifyDataSetChanged();
//                } else {
//                    Log.d("swipeT", "heartNotOk : " + array.size());
//                    myAppAdapter.notifyDataSetChanged();
//                }

            }

            //비호감인 경우
            @Override
            public void onLeftCardExit(Object dataObject) {

                Log.d("swipeT", "onLeftCardExit");

                likeInfo[0] = nowID; // id_from 파라미터
                likeInfo[1] = selectedUser.getId(); // id_to 파라미터
                likeInfo[2] = "dislike"; // status 파라미터
                new SelectUserThread(likeInfo).start();
                array.remove(0);
                myAppAdapter.notifyDataSetChanged();



            }

            //호감인 경우
            @Override
            public void onRightCardExit(Object dataObject) {


                if(heartOk) {
                    updateMenuTitle(-3); // 호감을 보낼 시 하트 차감
                    array.remove(0);

                    likeInfo[0] = nowID; // id_from 파라미터
                    likeInfo[1] = selectedUser.getId(); // id_to 파라미터
                    likeInfo[2] = "like"; // status 파라미터
                    new SelectUserThread(likeInfo).start();
                    new FcmLikeRequest(getContext(), nowUser.getNickname(), selectedUser.getNickname())
                            .execute("http://115.71.238.160/novaproject1/fcmnotification.php", selectedUser.getId());
                } else {
                    Toast.makeText(getContext(), "하트가 부족합니다.", Toast.LENGTH_SHORT).show();
                }

                myAppAdapter.notifyDataSetChanged();

                //Log.d("swipeT", tokenId);
                Log.d("swipeT", "onRightCardExit");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }


        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                User tmpUser = array.get(itemPosition);
                Intent intent = new Intent(getActivity(), ExActivity.class);
                intent.putExtra("usertype", "find");
                intent.putExtra("userdata", tmpUser);
                startActivity(intent);
            }
        });




        getData = new GetData();
        getData.execute("http://115.71.238.160/novaproject1/HomeActivity/getalluser.php");
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    class SelectUserThread extends Thread {

        String[] params;
        SelectUserThread(String[] info) {
            this.params = info;
        }

        @Override
        public void run() {
            URL url = null;
            HttpURLConnection conn = null;

            try {
                /** 요청 보내기 **/
                url = new URL("http://115.71.238.160/novaproject1/setlike.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                conn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

                String outData = "id_from="+params[0]+"&id_to="+params[1]+"&status="+params[2];
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

                Log.d("thTT", page.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }

        }
    }









    @Override
    public void onDestroy() {
        super.onDestroy();

        if(getData.getStatus() == AsyncTask.Status.RUNNING) {
            try {
                getData.cancel(true);
                Log.d("tasklog", "getData is canceled");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MyAppAdapter extends BaseAdapter {


        public List<ExUser> itemList;
        public Context context;

        private MyAppAdapter(List<ExUser> items, Context context) {
            this.itemList = items;
            this.context = context;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;

            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item_swipe, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);

                // 이미지뷰에 background 속성 없이 커스텀 round 를 적용한 부분. 보류.
//                final float curveRadius = 40;
//                viewHolder.cardImage.setOutlineProvider(new ViewOutlineProvider() {
//                    @Override
//                    public void getOutline(View view, Outline outline) {
//                        outline.setRoundRect(0, 0, view.getWidth(), Float.floatToIntBits(view.getHeight()+curveRadius), curveRadius);
//                    }
//                });
                viewHolder.cardImage.setClipToOutline(true);
                viewHolder.cardStatus = rowView.findViewById(R.id.Iv_status);
                viewHolder.cardNickname = (TextView) rowView.findViewById(R.id.cardNickname);
                viewHolder.cardAge = (TextView) rowView.findViewById(R.id.cardAge);
                viewHolder.cardLive = (TextView) rowView.findViewById(R.id.cardLive);
                viewHolder.cardJob = rowView.findViewById(R.id.cardJob);
                viewHolder.cardHeight = rowView.findViewById(R.id.cardHeigt);
                viewHolder.backgroundCard1 = (LinearLayout) rowView.findViewById(R.id.backgroundCard1);
                viewHolder.backgroundCard2 = (LinearLayout) rowView.findViewById(R.id.backgroundCard2);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if(array.size()<3) {
                //카드가 2장이 남은 경우
                viewHolder.backgroundCard1.setVisibility(View.INVISIBLE);
                if(array.size()<2) {
                    //카드가 1장이 남은 경우
                    viewHolder.backgroundCard2.setVisibility(View.INVISIBLE);
                }
            }

            Glide.with(getActivity()).load(itemList.get(position).getImg_profile()).into(viewHolder.cardImage);
            viewHolder.cardNickname.setText(itemList.get(position).getNickname() + " ");
            viewHolder.cardAge.setText(itemList.get(position).getAge() + "세, ");
            viewHolder.cardHeight.setText(itemList.get(position).getHeight() + "cm");
            viewHolder.cardJob.setText(itemList.get(position).getJob());

//            Double mlati = Double.valueOf(itemList.get(position).getLati());
//            Double mlongi = Double.valueOf(itemList.get(position).getLongi());
//            String mdistance = getDistanceFromAtoB(37.5868821,127.0111881, mlati, mlongi);


            viewHolder.cardLive.setText(itemList.get(position).getLive());

//            if(itemList.get(position).getStatus().equals("1")) {
//                Glide.with(getActivity()).load(R.drawable.color_online).into(viewHolder.cardStatus);
//            } else {
//                Glide.with(getActivity()).load(R.drawable.color_offline).into(viewHolder.cardStatus);
//            }



            return rowView;
        }
    }




    public class ViewHolder {
        public FrameLayout background;
        public LinearLayout backgroundCard1;
        public LinearLayout backgroundCard2;
        public ImageView cardImage;
        public ImageView cardStatus;
        public TextView cardNickname;
        public TextView cardAge;
        public TextView cardLive;
        public TextView cardJob;
        public TextView cardHeight;
    }





    /** 전체 이성유저 데이터 받아오는 태스크 **/
    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getContext(),
                    "Please Wait", null, true, true);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            User cUser = CurrentUserManager.getCurrentUser(getContext());
            String id = cUser.getId();
            String sex = cUser.getSex();

            try {

                // Http 커넥션을 만들기
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");

                String data = "id="+id+"&sex="+sex;
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes("UTF-8"));
                os.flush();
                os.close();


                // InputStream 받아오기
                int responseStatusCode = conn.getResponseCode();
                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                }
                else{
                    inputStream = conn.getErrorStream();
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
                ParseResult();
            }
        }
    }


    private void ParseResult(){
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
                String lati = item.getString(TAG_LATI);
                String longi = item.getString(TAG_LONGI);

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
                ExUser mUser = new ExUser();
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
                mUser.setLati(lati);
                mUser.setLongi(longi);

                array.add(mUser);


            }
            for(int j=0; j<array.size(); j++) {
                Log.d("filterT", "받아온 유저" +(Integer.valueOf(j)+1)+ " = "+array.get(j).getLati());
            }
            Filter();
            myAppAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public double getDistanceFromAtoB(double latiA, double longiA, double latiB, double longiB) {
        Location locationA = new Location("point A");
        locationA.setLatitude(latiA); //위도
        locationA.setLongitude(longiA); //경도
        Log.d("locT", "latiA : " + latiA);
        Log.d("locT", "longiA : " + longiA);

        Location locationB = new Location("point B");
        locationB.setLatitude(latiB);
        locationB.setLongitude(longiB);
        Log.d("locT", "latiB : " + latiB);
        Log.d("locT", "longiB : " + longiB);

        double distance = locationA.distanceTo(locationB);
        Log.d("locT", "distance : " + distance);

        String result = String.valueOf(distance);
        return distance/1000;
    }



    /** 현재 저장되어 있는 이상형 조건에 맞는 회원만을 선택해준다.
     * 조건에 맞지 않는 이성은 List 에서 제거하는 방식이다. **/
    public void Filter() {
        String myId = CurrentUserManager.getCurrentUser(getContext()).getId();
        SharedPreferences sf = getContext().getSharedPreferences("TypeOf"+myId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        int minAge = Integer.valueOf(sf.getString("sf_minAge", "18"));
        int maxAge = Integer.valueOf(sf.getString("sf_maxAge", "49"));
        int minHeight = Integer.valueOf(sf.getString("sf_minHeight", "140"));
        int maxHeight = Integer.valueOf(sf.getString("sf_maxHeight", "200"));
        int minLive = Integer.valueOf(sf.getString("sf_minLive", "0"));
        int maxLive = Integer.valueOf(sf.getString("sf_maxLive", "300"));
//        boolean sw = Boolean.valueOf(sf.getString("sf_sw", true+""));
//        boolean sdg = Boolean.valueOf(sf.getString("sf_sdg", true+""));
//        boolean gw = Boolean.valueOf(sf.getString("sf_gw", true+""));
//        boolean cb = Boolean.valueOf(sf.getString("sf_cb", true+""));
//        boolean cn = Boolean.valueOf(sf.getString("sf_cn", true+""));
//        boolean jb = Boolean.valueOf(sf.getString("sf_jb", true+""));
//        boolean jn = Boolean.valueOf(sf.getString("sf_jn", true+""));
//        boolean gb = Boolean.valueOf(sf.getString("sf_gb", true+""));
//        boolean gn = Boolean.valueOf(sf.getString("sf_gn", true+""));
//        boolean jj = Boolean.valueOf(sf.getString("sf_jj", true+""));
//        boolean etc = Boolean.valueOf(sf.getString("sf_etc", true+""));

        Log.d("filterT2", "arraySize = "+ array.size());
        Log.d("filterT2", "minAge = "+minAge);
        Log.d("filterT2", "maxAge = "+maxAge);
        Log.d("filterT2", "minHeight = "+minHeight);
        Log.d("filterT2", "maxHeight = "+maxHeight);
        Log.d("filterT2", "minLive = "+minLive);
        Log.d("filterT2", "maxLive = "+maxLive);
//        Log.d("filterT", "minHeight = "+minHeight);
//        Log.d("filterT", "maxHeight = "+maxHeight);
//        Log.d("filterT", "서울 = "+ sw);
//        Log.d("filterT", "수도권 = "+sdg);
//        Log.d("filterT", "강원 = "+gw);
//        Log.d("filterT", "충북 = "+cb);
//        Log.d("filterT", "충남 = "+cn);
//        Log.d("filterT", "전북 = "+jb);
//        Log.d("filterT", "전남 = "+jn);
//        Log.d("filterT", "경북 = "+gb);
//        Log.d("filterT", "경남 = "+gn);
//        Log.d("filterT", "제주 = "+jj);
//        Log.d("filterT", "해외 = "+etc);

        // 유저정보가 들어있는 arrayList 를 일시적으로 array 로 바꾼다.
        // 원래 arrayList 는 비운 후에 필터링 된 유저를 다시 넣어줌.
        ExUser[] ExUserArray = array.toArray(new ExUser[array.size()]);
        array.clear();

        for(int i=0; i<ExUserArray.length; i++) {
            ExUser tmpUser = ExUserArray[i];
            Log.d("filterT", "해당유저 필터링 시작 : " + tmpUser.getNickname());


            //상대와의 거리가 1보다 작은 경우 '1km 이내'를, 그게 아니면 값 그대로를 i번째 ExUserArray 에 저장한다.


            if(Integer.valueOf(tmpUser.getAge())>=minAge && Integer.valueOf(tmpUser.getAge())<=maxAge &&
                   Integer.valueOf(tmpUser.getHeight())>=minHeight && Integer.valueOf(tmpUser.getHeight())<=maxHeight) {
                Log.d("filterD", "필터링 통과 : " + tmpUser.getNickname());

                if(!ExUserArray[i].getLongi().isEmpty() && !ExUserArray[i].getLati().isEmpty()){
                    double mlati = Double.valueOf(ExUserArray[i].getLati());
                    double mlongi = Double.valueOf(ExUserArray[i].getLongi());
                    int mdistance = (int) getDistanceFromAtoB(37.5868821,127.0111881, mlati, mlongi);
                    Log.d("filterD", "mlati : "+mlati);
                    Log.d("filterD", "mlongi : "+mlongi);
                    Log.d("filterD", "mdistance : "+mdistance);
                    if(mdistance<1) {
                        ExUserArray[i].setLive("1km 이내");
                    } else {
                        ExUserArray[i].setLive(String.valueOf(mdistance)+"km");
                    }

                    if(mdistance>=minLive && mdistance<=maxLive) {
                        array.add(tmpUser);
                    }
                }

//                switch(tmpUser.getLive()) {
//                    case "서울":
//                        //Log.d("filterT", tmpUser.getNickname()+"은 "+tmpUser.getLive()+" 거주");
//                        if(sw){}else{array.remove(i);}
//                        break;
//                    case "수도권":
//                        if(sdg){}else{array.remove(i);}
//                        break;
//                    case "강원":
//                        if(gw){}else{array.remove(i);}
//                        break;
//                    case "충북":
//                        if(cb){}else{array.remove(i);}
//                        break;
//                    case "충남":
//                        if(cn){}else{array.remove(i);}
//                        break;
//                    case "전북":
//                        if(jb){}else{array.remove(i);}
//                        break;
//                    case "전남":
//                        if(jn){}else{array.remove(i);}
//                        break;
//                    case "경북":
//                        if(gb){}else{array.remove(i);}
//                        break;
//                    case "경남":
//                        if(gn){}else{array.remove(i);}
//                        break;
//                    case "제주":
//                        if(jj){}else{array.remove(i);}
//                        break;
//                    case "해외":
//                        if(etc){}else{array.remove(i);}
//                        break;
//
//                        default:
//                            break;
//                }
            } else {
                Log.d("filterT", "필터링 탈락 : " + tmpUser.getNickname());
                ExUserArray[i] = null;
            }
        }


        Log.d("filterT", "필터링 후 arraySize = "+ array.size()+"");



    }



    /** 경도와 위도 속성을 추가한 임시 유저 클래스 **/
    class ExUser2 extends User implements Serializable{

        private String lati;
        private String longi;

        public String getLati() {
            return lati;
        }

        public void setLati(String lati) {
            this.lati = lati;
        }

        public String getLongi() {
            return longi;
        }

        public void setLongi(String longi) {
            this.longi = longi;
        }
    }






}
