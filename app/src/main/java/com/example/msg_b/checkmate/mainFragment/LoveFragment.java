package com.example.msg_b.checkmate.mainFragment;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.ExActivity;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoveFragment extends androidx.fragment.app.Fragment implements View.OnClickListener{
    View v;
    public LoveFragment() {
    }


    ArrayList<User> userArrayList;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView recyclerView;
    TextView tv_from, tv_to;
    String currentID;
    boolean isFrom;
    enum Type {
        FROM, TO
    }
    Type mType;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Tv_from:
                mType = Type.FROM;
                tv_from.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                tv_to.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                new GetUserArrayTask(currentID, "from").execute();
                Log.d("rvT", "R.id.Tv_from");
                break;

            case R.id.Tv_to:
                mType = Type.TO;
                tv_from.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                tv_to.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                new GetUserArrayTask(currentID, "to").execute();
                Log.d("rvT", "R.id.Tv_to");
                break;

                default:
                    break;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        androidx.appcompat.app.ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle("관심카드");
        currentID = CurrentUserManager.getCurrentUserId(getContext());
        v = inflater.inflate(R.layout.fragment_love, container, false);
        Log.d("rvT", "onCreateView");


        tv_from = v.findViewById(R.id.Tv_from);
        tv_to = v.findViewById(R.id.Tv_to);
        tv_from.setOnClickListener(this);
        tv_to.setOnClickListener(this);


        initRecyclerView();
//        new GetUserArrayTask(currentID, "from").execute();

//        new GetUserArrayTask(currentID, "to").execute();
        return v;
    }





    class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyRecyclerViewHolder>{

        private Context mContext;
        private ArrayList<User> mList;

        public MyRecyclerViewAdapter(Context mContext, ArrayList<User> mList) {
            this.mContext = mContext;
            this.mList = mList;
        }

        void setData(ArrayList<User> List) {
            this.mList = List;
        }

        @Override
        public MyRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(mContext).inflate(R.layout.item_match2, parent, false);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
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
            final User tmpUser = mList.get(position);

            Glide.with(mContext)
                    .asBitmap()
                    .load(mList.get(position).getImg_profile())
                    .into(holder.iv_profile);

            holder.tv_nickname.setText(tmpUser.getNickname());
            holder.iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ExActivity.class);
                    intent.putExtra("userdata", tmpUser);
                    switch (mType) {
                        case FROM:
                            intent.putExtra("usertype", "from");
                            break;
                        case TO:
                            intent.putExtra("usertype", "to");
                            break;

                            default:
                                break;
                    }
                    startActivity(intent);
                    exActivityCalled = true;

                }
            });
            String tmpEtc = tmpUser.getLive()+", "+tmpUser.getJob();
            holder.tv_etc.setText(tmpEtc);
        }


        @Override
        public int getItemCount() {
            Log.d("matchT", "mList.size() = "+ mList.size());
            return mList.size();

        }



        class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

            ImageView iv_profile;
            TextView tv_nickname, tv_etc;

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public MyRecyclerViewHolder(View itemView) {
                super(itemView);

                iv_profile = itemView.findViewById(R.id.Iv_profile);
                iv_profile.setClipToOutline(true);
                tv_nickname = itemView.findViewById(R.id.Tv_nickname);
                tv_etc = itemView.findViewById(R.id.Tv_etc);

            }
        }

    }


    class GetUserArrayTask extends AsyncTask<String, Void, ArrayList<User>> {

        /**
         * mId는 인연을 불러올 유저의 id, mMatch 는 어떠한 타입의 인연을 불러올 것인지를 정한다.
         * mMatch 는 match, from, to로 나누어진다.
         * match - 서로 연결된 인연
         * from - 내가 호감을 표시한 인연
         * to - 나에게 호감을 표시한 인연
         *
         * **/

        private String mId;
        private String mMatch;

        public GetUserArrayTask(String mId, String mMatch) {
            this.mId = mId;
            this.mMatch = mMatch;
        }

        @Override
        protected ArrayList<User> doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            String strUrl = "http://115.71.238.160/novaproject1/getlike.php";
            ArrayList<User> array = new ArrayList<>();

            RequestBody body = new FormBody.Builder()
                    .add("id", mId)
                    .add("match", mMatch)
                    .build();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();
            Call mCall = client.newCall(request);
            try {
                Response mResponse = mCall.execute();
                String data = mResponse.body().string();

                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("users");

                for(int i=0;i<jsonArray.length();i++){

                    JSONObject item = jsonArray.getJSONObject(i);
                    User mUser = new Gson().fromJson(item.toString(), User.class);

                    array.add(mUser);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return array;
        }


        @Override
        protected void onPostExecute(ArrayList<User> userList) {
            super.onPostExecute(userList);

            if(userList != null) {
                userArrayList = userList;
                Log.d("matchT", "lstFrom size : " + userArrayList.size());
            }

            else {
                userArrayList.clear();
                Toast.makeText(getContext(), mMatch+"인연이 없음", Toast.LENGTH_SHORT).show();
            }


            myRecyclerViewAdapter.setData(userArrayList);
            myRecyclerViewAdapter.notifyDataSetChanged();



        }
    }


    public void initRecyclerView() {
        userArrayList = new ArrayList<>();
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(getContext(), userArrayList);
        recyclerView = v.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }



    private boolean exActivityCalled = false;
    @Override
    public void onStart() {
        super.onStart();
        String type;
        if(exActivityCalled) {
            if(mType == Type.FROM)
                type = "from";
            else
                type = "to";
            new GetUserArrayTask(currentID, type).execute();
            exActivityCalled = false;
        }
    }

}
