package com.example.msg_b.checkmate.mainFragment.profileFragment;

//import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private Marker mMarker;

    private View marker_root_view;
    private TextView tv_marker;
    private boolean isClickable = true;

    String myId;
    String place_now;
    LatLng latlng_now;
    double radius_max;
    double radius_min;
    private SharedPreferences sf_location;
    private SharedPreferences sf_radius;
    private SharedPreferences.Editor editor_location;
    private SharedPreferences.Editor editor_radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //타이틀 바를 숨김
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //커스텀 마커 생성
        setCustomMarkerView();

        //위치 설정 불러오기
        sf_location = this.getSharedPreferences("location", MODE_PRIVATE);
        editor_location = sf_location.edit();
        String strLat = sf_location.getString("latitude", "");
        String strLng = sf_location.getString("longitude", "");
        place_now = sf_location.getString("place", "현재위치");
        if(!strLat.isEmpty() && !strLng.isEmpty()) {
            latlng_now = new LatLng(
                    Double.valueOf(strLat),
                    Double.valueOf(strLng)
            );
        }

        //유저거리 설정 불러오기
        myId = CurrentUserManager.getCurrentUser(this).getId();
        sf_radius = this.getSharedPreferences("TypeOf"+myId, MODE_PRIVATE);
        //km로 저장된 것을 m 단위로 표시함.
        radius_min = Double.valueOf(sf_radius.getString("sf_minLive", "0"))*1000; //최소 거리
        radius_max = Double.valueOf(sf_radius.getString("sf_maxLive", "300"))*1000; //최대 거리


        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);

        //지도 설정
        mMap.setBuildingsEnabled(false);
        mMap.setTrafficEnabled(false);
        mMap.setMyLocationEnabled(false);
        mMap.setIndoorEnabled(false);

        //리스너 등록
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);

        //맵 줌 설정
        if(latlng_now != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng_now));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(5));
        }
        else {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(1));
        }


        //마커 생성
        createMarker(latlng_now, "현재위치 : " + place_now);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        mMap.clear();
        //카메라 시점을 클릭한 곳으로 이동
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        if(mMarker != null) {
            mMarker.remove();
        }


        try {
            List<Address> result = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(result.size()>0) {

                String countryName = result.get(0).getCountryName();
                String adminArea = result.get(0).getAdminArea();
                String subAdminArea = result.get(0).getSubAdminArea();
                String locality = result.get(0).getLocality();
                String addressLine = result.get(0).getAddressLine(1);
                String subLocality = result.get(0).getSubLocality();
                String featureName = result.get(0).getFeatureName();
                String premises = result.get(0).getPremises();
                String thoroughfare = result.get(0).getThoroughfare();
                String subThoroughfare = result.get(0).getSubThoroughfare();


                Log.d("mapT", "adminArea : " + adminArea);
                Log.d("mapT", "subAdminArea : " + subAdminArea);
                Log.d("mapT", "addressLine : " + addressLine);
                Log.d("mapT", "countryName : " + countryName);
                Log.d("mapT", "locality : " + locality);
                Log.d("mapT", "subLocality : " + subLocality);
                Log.d("mapT", "featureName : " + featureName);
                Log.d("mapT", "premises : " + premises);
                Log.d("mapT", "thoroughfare : " + thoroughfare);
                Log.d("mapT", "subThoroughfare : " + subThoroughfare);

                String markerText;
                String resultIntenttString;
                if(countryName == null) {
                    Toast.makeText(this, "알 수 없는 장소입니다.", Toast.LENGTH_SHORT).show();
                    markerText = "알 수 없는 장소";
                    resultIntenttString = "";
                    isClickable = false;
                }
                else {
                    if(adminArea == null) {
                        if(locality != null) {
                            markerText = "["+locality + "] 에서 찾기\n" + countryName;
                            resultIntenttString = locality + ", " + countryName;
                        }
                        else{
                            markerText = countryName;
                            resultIntenttString = countryName;
                        }
                        
                    }
                    else {
                        if(locality != null) {
                            markerText = "["+locality + "] 에서 찾기\n" + adminArea;
                            resultIntenttString = locality + ", " + adminArea;
                        }
                        else{
                            markerText = "["+adminArea + "] 에서 찾기\n" + countryName;
                            resultIntenttString = adminArea + ", " + countryName;
                        }

                    }
                }







                //커스텀 마커 내용
//                tv_marker.setText(markerText);

                //마커생성
                createMarker(latLng, resultIntenttString);

                //위치설정 저장
                SharedPreferences sf = this.getSharedPreferences("location", MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.putString("place", resultIntenttString);
                editor.putString("latlng", latLng+"");
                editor.putString("latitude", latLng.latitude+"");
                editor.putString("longitude", latLng.longitude+"");
                editor.commit();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", mMarker.getTitle());
                resultIntent.putExtra("position", mMarker.getPosition());
                resultIntent.putExtra("latitude", mMarker.getPosition().latitude);
                resultIntent.putExtra("longitude", mMarker.getPosition().longitude);
                setResult(RESULT_OK, resultIntent);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if(isClickable) {
            //설정 저장
        }
        else {
//            Toast.makeText(this, "다른 장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }

        return true;
    }


    public void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.view_custom_marker, null);
//        tv_marker = marker_root_view.findViewById(R.id.Tv_marker);
//        tv_marker.setTextColor(this.getResources().getColor(R.color.browser_actions_bg_grey));
    }


    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

        Log.d("dragT", "start");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d("dragT", "drag");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d("dragT", "end");
    }


    void createMarker(LatLng latLng, String titleString) {
        //마커 추가
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(titleString);
//        markerOptions.snippet(snippetString);
        markerOptions.alpha(0.5f);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
        mMarker = mMap.addMarker(markerOptions);
        mMarker.showInfoWindow();

        Toast.makeText(this, radius_min/1000 + "km ~ " + radius_max/1000 + "km", Toast.LENGTH_SHORT).show();
        //서클 추가
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius_max)
                .strokeWidth(0)
                .fillColor(0x7Df04455)
        );
        mMap.addCircle((new CircleOptions()
                .center(latLng)
                .radius(radius_min)
                .strokeWidth(0)
                .fillColor(0x7DFFFFFF)
        ));
    }

}
