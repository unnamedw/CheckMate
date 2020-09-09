package com.example.msg_b.checkmate.mainFragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msg_b.checkmate.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EtcFragment extends androidx.fragment.app.Fragment {
    View v;
    public EtcFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_etc, container, false);
        return v;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
