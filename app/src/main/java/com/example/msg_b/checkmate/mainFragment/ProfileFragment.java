package com.example.msg_b.checkmate.mainFragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.mainFragment.profileFragment.Sub1ProfileFragment;
import com.example.msg_b.checkmate.mainFragment.profileFragment.Sub2ProfileFragment;
import com.example.msg_b.checkmate.mainFragment.profileFragment.Sub3ProfileFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ProfileFragment extends androidx.fragment.app.Fragment {
    View v;
    public ProfileFragment() {}
/////////////////////////////////////////////////////////////////////


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        androidx.appcompat.app.ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle("마이페이지");
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        tabLayout = v.findViewById(R.id.TabLayout);
        viewPager = v.findViewById(R.id.ViewPager);

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new Sub1ProfileFragment(), "프로필");
        adapter.AddFragment(new Sub2ProfileFragment(), "검색조건");
        adapter.AddFragment(new Sub3ProfileFragment(), "앱설정");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_bubble);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_edit);


//        Toast.makeText(getContext(), CurrentUserManager.getCurrentUser(getContext()).getNickname(), Toast.LENGTH_SHORT).show();





        return v;
    }





    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<androidx.fragment.app.Fragment> lstFragment = new ArrayList<>();
        private ArrayList<String> lstTitle = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public androidx.fragment.app.Fragment getItem(int position) {
            return lstFragment.get(position);
        }

        @Override
        public int getCount() {
            return lstTitle.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return lstTitle.get(position);
        }


        public void AddFragment(androidx.fragment.app.Fragment fragment, String title) {
            lstFragment.add(fragment);
            lstTitle.add(title);
        }


    }





}
