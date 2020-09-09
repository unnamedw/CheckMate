package com.example.msg_b.checkmate.mainFragment.profileFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.rangebar.RangeBar;
import com.example.msg_b.checkmate.HomeActivity;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.util.CurrentUserManager;

import static android.app.Activity.RESULT_OK;

/** 검색조건 **/
public class Sub2ProfileFragment extends androidx.fragment.app.Fragment
        implements CompoundButton.OnCheckedChangeListener, RangeBar.OnRangeBarChangeListener, View.OnClickListener {
    View v;
    public Sub2ProfileFragment() {}

    TextView tv_leftInt, tv_rightInt, tv_leftInt2, tv_rightInt2, tv_leftInt3, tv_rightInt3, tv_location;
    CheckBox cb_sw, cb_sdg, cb_gw, cb_cb, cb_cn, cb_jb, cb_jn, cb_gb, cb_gn, cb_jj, cb_etc;
    Button btn_location;

    SharedPreferences sf;
    SharedPreferences.Editor editor;
    //SharedPreferences 에 저장하게 되는 설정값들
    int sf_minAge, sf_maxAge, sf_minHeight, sf_maxHeight, sf_minLive, sf_maxLive;
    boolean sf_sw, sf_sdg, sf_gw, sf_cb, sf_cn, sf_jb, sf_jn, sf_gb, sf_gn, sf_jj, sf_etc;

    private final int PLACE_PICKER_REQUEST = 11;
    private final int MAP_REQUEST = 12;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sub2_profile, container, false);

        String myId = CurrentUserManager.getCurrentUser(getContext()).getId();
        sf = getActivity().getSharedPreferences("TypeOf"+myId, Context.MODE_PRIVATE);
        editor = sf.edit();


        //뷰 등록
        cb_sw = v.findViewById(R.id.Cb_sw);
        cb_sdg = v.findViewById(R.id.Cb_sdg);
        cb_gw = v.findViewById(R.id.Cb_gw);
        cb_cb = v.findViewById(R.id.Cb_cb);
        cb_cn = v.findViewById(R.id.Cb_cn);
        cb_jb = v.findViewById(R.id.Cb_jb);
        cb_jn = v.findViewById(R.id.Cb_jn);
        cb_gb = v.findViewById(R.id.Cb_gb);
        cb_gn = v.findViewById(R.id.Cb_gn);
        cb_jj = v.findViewById(R.id.Cb_jj);
        cb_etc = v.findViewById(R.id.Cb_etc);
        tv_location = v.findViewById(R.id.Tv_location);
        tv_leftInt = v.findViewById(R.id.Tv_leftInt); //rb_age 의 왼쪽값
        tv_rightInt = v.findViewById(R.id.Tv_rightInt); //rb_age 의 오른쪽값
        tv_leftInt2 = v.findViewById(R.id.Tv_leftInt2); //rb_age 의 왼쪽값
        tv_rightInt2 = v.findViewById(R.id.Tv_rightInt2); //rb_age 의 오른쪽값
        tv_leftInt3 = v.findViewById(R.id.Tv_leftInt3); //rb_age 의 왼쪽값
        tv_rightInt3 = v.findViewById(R.id.Tv_rightInt3); //rb_age 의 오른쪽값
        RangeBar rb_age = v.findViewById(R.id.Rb_age); //이상형의 나이를 설정하는 Bar.
        RangeBar rb_height = v.findViewById(R.id.Rb_height); //이상형의 키를 설정하는 Bar
        RangeBar rb_live = v.findViewById(R.id.Rb_live); //이상형의 키를 설정하는 Bar
        btn_location = v.findViewById(R.id.Btn_location);

        rb_age.setTickCount(32); //18세~49세 구분, 구분할 숫자의 갯수를 설정. ex) 2부터 15까지 구분한다면 총 14개를 구분하는 것이므로 14을 넣는다.
        rb_age.setTickHeight(0); //구분선을 설정하는 부분, 0으로 하면 구분선이 보이지 않는다.
        rb_height.setTickCount(60); // 140~200cm 구분
        rb_height.setTickHeight(0); // ''
        rb_live.setTickCount(301); // 0~1000km 까지 구분
        rb_live.setTickHeight(0);
        int ADD_TO_AGE = 18;
        int ADD_TO_HEIGHT = 140;
        int ADD_TO_LIVE = 0;

        //왼쪽과 오른쪽값을 초기화함.
        int ageLeft = rb_age.getLeftIndex()+ADD_TO_AGE;
        int ageRight = rb_age.getRightIndex()+ADD_TO_AGE;
        int heightLeft = rb_height.getLeftIndex()+ADD_TO_HEIGHT;
        int heightRight = rb_height.getRightIndex()+ADD_TO_HEIGHT;
        int liveLeft = rb_live.getLeftIndex()+ADD_TO_LIVE;
        int liveRight = rb_live.getRightIndex()+ADD_TO_LIVE;
//        Log.d("sftest", "heightLeft : " + heightLeft);
//        Log.d("sftest", "heightRight : " + heightRight);

        //필드값 초기화
        sf_minAge = Integer.valueOf(sf.getString("sf_minAge", ageLeft+""));
        sf_maxAge = Integer.valueOf(sf.getString("sf_maxAge", ageRight+""));
        sf_minHeight = Integer.valueOf(sf.getString("sf_minHeight", heightLeft+""));
        sf_maxHeight = Integer.valueOf(sf.getString("sf_maxHeight", heightRight+""));
        sf_minLive = Integer.valueOf(sf.getString("sf_minLive", liveLeft+""));
        sf_maxLive = Integer.valueOf(sf.getString("sf_maxLive", liveRight+""));
        sf_sw = Boolean.valueOf(sf.getString("sf_sw", false+""));
        sf_sdg = Boolean.valueOf(sf.getString("sf_sdg", false+""));
        sf_gw = Boolean.valueOf(sf.getString("sf_gw", false+""));
        sf_cb = Boolean.valueOf(sf.getString("sf_cb", false+""));
        sf_cn = Boolean.valueOf(sf.getString("sf_cn", false+""));
        sf_jb = Boolean.valueOf(sf.getString("sf_jb", false+""));
        sf_jn = Boolean.valueOf(sf.getString("sf_jn", false+""));
        sf_gb = Boolean.valueOf(sf.getString("sf_gb", false+""));
        sf_gn = Boolean.valueOf(sf.getString("sf_gn", false+""));
        sf_jj = Boolean.valueOf(sf.getString("sf_jj", false+""));
        sf_etc = Boolean.valueOf(sf.getString("sf_etc", false+""));
        Log.d("sftest", sf_minAge+"");
        Log.d("sftest", sf_maxAge+"");
        Log.d("sftest", sf_minHeight+"");
        Log.d("sftest", sf_maxHeight+"");
        Log.d("sftest", sf_minLive+"");
        Log.d("sftest", sf_maxLive+"");

        //뷰세팅
        rb_age.setThumbIndices(sf_minAge-18, sf_maxAge-18);
        rb_height.setThumbIndices(sf_minHeight-140, sf_maxHeight-140);
        rb_live.setThumbIndices(sf_minLive-0, sf_maxLive-0);
        tv_leftInt.setText(sf_minAge + " - ");
        tv_rightInt.setText(sf_maxAge + "세");
        tv_leftInt2.setText(sf_minHeight + " - ");
        tv_rightInt2.setText(sf_maxHeight + "cm");
        tv_leftInt3.setText(sf_minLive + " - ");
        tv_rightInt3.setText(sf_maxLive + "km");
        cb_sw.setChecked(sf_sw);
        cb_sdg.setChecked(sf_sdg);
        cb_gw.setChecked(sf_gw);
        cb_cb.setChecked(sf_cb);
        cb_cn.setChecked(sf_cn);
        cb_jb.setChecked(sf_jb);
        cb_jn.setChecked(sf_jn);
        cb_gb.setChecked(sf_gb);
        cb_gn.setChecked(sf_gn);
        cb_jj.setChecked(sf_jj);
        cb_etc.setChecked(sf_etc);

        //Listener 설정
        rb_age.setOnRangeBarChangeListener(this);
        rb_height.setOnRangeBarChangeListener(this);
        rb_live.setOnRangeBarChangeListener(this);
        cb_sw.setOnCheckedChangeListener(this);
        cb_sdg.setOnCheckedChangeListener(this);
        cb_gw.setOnCheckedChangeListener(this);
        cb_cb.setOnCheckedChangeListener(this);
        cb_cn.setOnCheckedChangeListener(this);
        cb_gb.setOnCheckedChangeListener(this);
        cb_gn.setOnCheckedChangeListener(this);
        cb_jb.setOnCheckedChangeListener(this);
        cb_jn.setOnCheckedChangeListener(this);
        cb_jj.setOnCheckedChangeListener(this);
        cb_etc.setOnCheckedChangeListener(this);
        btn_location.setOnClickListener(this);


        SharedPreferences sf = getActivity().getSharedPreferences("location", HomeActivity.MODE_PRIVATE);
        String place = "내위치 : "+sf.getString("place", "");
        tv_location.setText(place);

        return v;
    }


    @Override
    public void onStop() {
        super.onStop();
        editor.commit();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("lfT", "onActivityResult");

//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(getContext(), data);
//                String toastMsg = String.format("Place: %s", place.getName());
//                Log.d("apitest", "id : " + place.getId());
//                Log.d("apitest", "address : " + place.getAddress());
//                Log.d("apitest", "attributions : " + place.getAttributions());
//                Log.d("apitest", "latlng : " + place.getLatLng());
//                Log.d("apitest", "locale : " + place.getLocale());
//
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            HttpURLConnection conn = (HttpURLConnection) new URL("").openConnection();
//                            conn.getResponseMessage();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread.start();
//
//                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }


        // 위치변경을 통해 받아온 위치정보
        if(requestCode == MAP_REQUEST) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getContext(),
                        "title : " + data.getStringExtra("title") + "\n" +
                        "snippet : " + data.getStringExtra("snippet") + "\n" +
                        "position : " + data.getStringExtra("position")
                        , Toast.LENGTH_SHORT).show();
                String locationStr = "내위치 : " + data.getStringExtra("title");
                tv_location.setText(locationStr);
                Log.d("lfT", "MAP_REQUEST & " + locationStr);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Btn_location:

                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(intent, MAP_REQUEST);

//                Toast.makeText(getContext(), "테스트버튼", Toast.LENGTH_SHORT).show();
//
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//                try {
//                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }

                break;

                default:
                    break;
        }
    }

    @Override
    public void onIndexChangeListener(RangeBar rangeBar, int leftInt, int rightInt) {
        switch (rangeBar.getId()) {
            case R.id.Rb_age:
                int leftValue = leftInt + 18;
                int rightValue = rightInt + 18;
                tv_leftInt.setText(leftValue + " - ");
                tv_rightInt.setText(rightValue + "세");

                //SharedPreference 에 해당 설정값을 저장한다.
                editor.putString("sf_minAge", leftValue+"");
                editor.putString("sf_maxAge", rightValue+"");
                break;

            case R.id.Rb_height:
                int leftValue2 = leftInt + 140;
                int rightValue2 = rightInt + 140;
                tv_leftInt2.setText(leftValue2 + " - ");
                tv_rightInt2.setText(rightValue2 + "cm");

                //SharedPreference 에 해당 설정값을 저장한다.
                editor.putString("sf_minHeight", leftValue2+"");
                editor.putString("sf_maxHeight", rightValue2+"");
                break;

            case R.id.Rb_live:
                int leftValue3 = leftInt + 0;
                int rightValue3 = rightInt + 0;
                tv_leftInt3.setText(leftValue3 + " - ");
                tv_rightInt3.setText(rightValue3 + "km");

                //SharedPreference 에 해당 설정값을 저장한다.
                editor.putString("sf_minLive", leftValue3+"");
                editor.putString("sf_maxLive", rightValue3+"");
                break;

            default:
                break;

        }
        editor.apply();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.Cb_sw:
                if(isChecked) {
                    sf_sw = true;
                    Toast.makeText(getContext(), "서울", Toast.LENGTH_SHORT).show();
                } else {
                    sf_sw = false;
                }
                editor.putString("sf_sw", sf_sw+"");
                break;

            case R.id.Cb_sdg:
                if(isChecked) {
                    sf_sdg = true;
                    Toast.makeText(getContext(), "수도권", Toast.LENGTH_SHORT).show();
                } else {
                    sf_sdg = false;
                }
                editor.putString("sf_sdg", sf_sdg+"");
                break;

            case R.id.Cb_gw:
                if(isChecked) {
                    sf_gw = true;
                    Toast.makeText(getContext(), "강원", Toast.LENGTH_SHORT).show();
                } else {
                    sf_gw = false;
                }
                editor.putString("sf_gw", sf_gw+"");
                break;

            case R.id.Cb_cb:
                if(isChecked) {
                    sf_cb = true;
                    Toast.makeText(getContext(), "충북", Toast.LENGTH_SHORT).show();
                } else {
                    sf_cb = false;
                }
                editor.putString("sf_cb", sf_cb+"");
                break;

            case R.id.Cb_cn:
                if(isChecked) {
                    sf_cn = true;
                    Toast.makeText(getContext(), "충남", Toast.LENGTH_SHORT).show();
                } else {
                    sf_cn = false;
                }
                editor.putString("sf_cn", sf_cn+"");
                break;

            case R.id.Cb_jb:
                if(isChecked) {
                    sf_jb = true;
                    Toast.makeText(getContext(), "전북" + sf_jb, Toast.LENGTH_SHORT).show();
                } else {
                    sf_jb = false;
                }
                editor.putString("sf_jb", sf_jb+"");
                break;

            case R.id.Cb_jn:
                if(isChecked) {
                    sf_jn = true;
                    Toast.makeText(getContext(), "전남", Toast.LENGTH_SHORT).show();
                } else {
                    sf_jn = false;
                }
                editor.putString("sf_jn", sf_jn+"");
                break;

            case R.id.Cb_gb:
                if(isChecked) {
                    sf_gb = true;
                    Toast.makeText(getContext(), "경북", Toast.LENGTH_SHORT).show();
                } else {
                    sf_gb = false;
                }
                editor.putString("sf_gb", sf_gb+"");
                break;

            case R.id.Cb_gn:
                if(isChecked) {
                    sf_gn = true;
                    Toast.makeText(getContext(), "경남", Toast.LENGTH_SHORT).show();
                } else {
                    sf_gn = false;
                }
                editor.putString("sf_gn", sf_gn+"");
                break;

            case R.id.Cb_jj:
                if(isChecked) {
                    sf_jj = true;
                    Toast.makeText(getContext(), "제주", Toast.LENGTH_SHORT).show();
                } else {
                    sf_jj = false;
                }
                editor.putString("sf_jj", sf_jj+"");
                break;

            case R.id.Cb_etc:
                if(isChecked) {
                    sf_etc = true;
                    Toast.makeText(getContext(), "해외", Toast.LENGTH_SHORT).show();
                } else {
                    sf_etc = false;
                }
                editor.putString("sf_etc", sf_etc+"");
                break;

            default:
                break;

        }
    }
}
