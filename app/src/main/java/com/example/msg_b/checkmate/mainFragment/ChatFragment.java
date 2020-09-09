package com.example.msg_b.checkmate.mainFragment;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.ChatActivity;
import com.example.msg_b.checkmate.HomeActivity;
import com.example.msg_b.checkmate.R;
import com.example.msg_b.checkmate.server.CallerToReceiverTask;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.Room;
import com.example.msg_b.checkmate.util.SQLiteHelper2;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.util.Util;
import com.example.msg_b.checkmate.videocall.ConnectActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends androidx.fragment.app.Fragment{
    View v;
    public ChatFragment() {
        // Required empty public constructor
    }


    private ArrayList<User> lstMatch;
    private ArrayList<Room> lstRoom;
    private ArrayList<User> lstFrom;
    private ArrayList<User> lstTo;
    User thumbFrom;
    User thumbTo;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter; //유저
    RecyclerView recyclerView2;
    MyRoomAdapter recyclerViewAdapter2; //룸
    EditText et_find;

    String CurrentId;
    User CurrentUser;

    SQLiteHelper2 helper2;
    SQLiteDatabase wdb;
    SQLiteDatabase rdb;
    String RoomId;

    BroadcastReceiver mReceiver = null;
    int num = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        androidx.appcompat.app.ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle("대화방");
        v = inflater.inflate(R.layout.fragment_chat, container, false);

        helper2 = new SQLiteHelper2(getContext());
        wdb = helper2.getWritableDatabase();
        rdb = helper2.getReadableDatabase();
        et_find = v.findViewById(R.id.Et_find);


        lstMatch = new ArrayList<>();
        lstRoom = getAllRooms();

        User testUser = new User();
        testUser.setNickname("테스트");
        testUser.setImg_profile("https://newvitruvian.com/images/avatar-transparent-female-5.png");
        lstMatch.add(testUser);
//        lstFrom = new ArrayList<>();
//        lstTo = new ArrayList<>();

//        thumbFrom = new User();
//        thumbTo = new User();
//        thumbFrom.setNickname("내가좋아하는");
//        thumbFrom.setImg_profile("https://newvitruvian.com/images/avatar-transparent-female-5.png");
//        thumbTo.setNickname("나를좋아하는");
//        thumbTo.setImg_profile("https://newvitruvian.com/images/avatar-transparent-female-5.png");
//        lstMatch.add(thumbFrom);
//        lstMatch.add(thumbTo);

        CurrentId = CurrentUserManager.getCurrentUserId(getContext());
        CurrentUser = CurrentUserManager.getCurrentUser(getContext());

        new GetUserArrayTask(CurrentId, "couple").execute();
//        new GetUserArrayTask(CurrentId, "from").execute();
//        new GetUserArrayTask(CurrentId, "to").execute();

        initRecyclerView();
        startBroadCast();
        Log.d("roomT", "init");
        return v;
    }



    @Override
    public void onStart() {
        super.onStart();
//        lstRoom.add(new Room("1","2","3","4","5","6","7","0"));
//        lstRoom.removeAll(lstRoom);
//        lstRoom.addAll(getAllRooms());
        lstRoom = getAllRooms();
        for(int i=0; i<lstRoom.size(); i++) {
            Log.d("roomTT", lstRoom.get(i).toString());
        }
        recyclerViewAdapter2.setData(lstRoom);
        recyclerViewAdapter2.notifyDataSetChanged();
        Log.d("fragT", "onStart");
    }



    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter2.notifyDataSetChanged();
        Log.d("fragT", "onResume");
    }



    /** recyclerView **/
    public void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = v.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), lstMatch);
        recyclerView.setAdapter(recyclerViewAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView2 = v.findViewById(R.id.RecyclerView2);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerViewAdapter2 = new MyRoomAdapter(getActivity(), lstRoom);
        recyclerView2.setAdapter(recyclerViewAdapter2);

        if(recyclerView2.getAdapter() != null && recyclerView2.getAdapter().getItemCount()>=0)
        recyclerView2.scrollToPosition(0);

        recyclerViewAdapter2.setOnMyItemClickListener(new MyRoomAdapter.OnMyItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Room selectedRoom = lstRoom.get(position);
                String otherId = selectedRoom.getUser2();

                if(CurrentId.equals(selectedRoom.getUser2())) {
                    otherId = selectedRoom.getUser();
                }
                new EnterChatroomTask().execute(otherId);
            }
        });

//        et_find.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Toast.makeText(getContext(), "before", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(getContext(), "on", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Toast.makeText(getContext(), "after", Toast.LENGTH_SHORT).show();
//
//            }
//        });


    }


    @SuppressLint("StaticFieldLeak")
    class EnterChatroomTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... strings) {

            User result = null;
            OkHttpClient client = new OkHttpClient();
            String strUrl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/getuser.php";
            String id = strings[0];

            RequestBody body = new FormBody.Builder()
                    .add("id", id)
                    .build();
            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(body)
                    .build();
            Call mCall = client.newCall(request);
            try {
                Response mResponse = mCall.execute();
                String data = mResponse.body().string();
                result = new Gson().fromJson(data, User.class);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("userdata", user);
            startActivity(intent);
        }
    }







    /** 방 정보 업데이트를 위한 브로드 캐스팅 **/
    private void startBroadCast() {
        Log.d("roomT", "bc");

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("test2");

        this.mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals("test2")) {
                    Log.d("roomT", "onReceived 정상");
                    Toast.makeText(context, "메시지 " + num, Toast.LENGTH_SHORT).show();
                    String msg = intent.getStringExtra("msg");
                    String room = intent.getStringExtra("room");
//                    helper2.updateRoomMsg(room, msg, Util.getCurrentTime(), 1); // FCM 부분으로 이동시켰음
                    lstRoom = getAllRooms();
                    recyclerViewAdapter2.setData(lstRoom);
                    recyclerViewAdapter2.notifyDataSetChanged();
                }

                num++;
                
            }

        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }




    /** 매치된 유저들 **/
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private static final String TAG = "RecyclerViewAdapter";

        //vars
        private ArrayList<User> mUsers;
        private Context mContext;

        public RecyclerViewAdapter(Context mContext, ArrayList<User> mUsers) {
            this.mContext = mContext;
            this.mUsers = mUsers;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
            return new ViewHolder(view);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final User tmpUser = mUsers.get(position);


            Glide.with(mContext)
                    .asBitmap()
                    .load(tmpUser.getImg_profile())
                    .into(holder.civ_profile);
            holder.tv_nickname.setText(tmpUser.getNickname());

            //테스트 유저
            if(position == 0) {
                holder.civ_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tmpRoomId = String.valueOf(Util.getRandomNum(100000, 999999));
                        Intent intent = new Intent(getActivity(), ConnectActivity.class);
                        intent.putExtra("userdata", tmpUser);
                        intent.putExtra("opentype", "caller");
                        intent.putExtra("roomid", tmpRoomId);
                        startActivity(intent);
                        Log.d("VideoCallRoom", tmpRoomId);
                        Toast.makeText(mContext, "방 번호 : " + tmpRoomId, Toast.LENGTH_LONG).show();
                    }
                });

            }
            //일반 유저
            else {
                holder.civ_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Toast.makeText(mContext, tmpUser.getNickname(), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
//                    adb.setMessage("대화를 위해서 하트 30개가 소모됩니다.");
                        adb.setCancelable(false);

                        //Positive[오른쪽]
                        adb.setPositiveButton("대화하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //방이 존재하는 경우 바로 연결한다.
                                if (Util.isRoomExist(getActivity(), CurrentId, tmpUser.getId())) {
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("userdata", tmpUser);
                                    startActivity(intent);
                                }
                                //방이 존재하지 않는 경우
                                else {
                                    if (HomeActivity.heart < 30)
                                        Toast.makeText(mContext, "하트가 부족합니다.", Toast.LENGTH_SHORT).show();
                                    else {
                                        helper2.insertROOM(helper2.getWritableDatabase(),
                                                Util.getRoomCode(CurrentId, tmpUser.getId()),
                                                CurrentId,
                                                tmpUser.getId(),
                                                tmpUser.getNickname(),
                                                tmpUser.getImg_profile(),
                                                "대화를 시작해 주세요!",
                                                Util.getCurrentTime(), "0");
                                        HomeActivity.heart -= 30;
                                        getActivity().invalidateOptionsMenu();
                                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                                        intent.putExtra("userdata", tmpUser);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });

                        //Negative[왼쪽]
                        adb.setNegativeButton("영상통화", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do something when user press no button from alert dialog
                                String tmpRoomId = String.valueOf(Util.getRandomNum(100000, 999999));
                                Intent intent = new Intent(getActivity(), ConnectActivity.class);
                                intent.putExtra("userdata", tmpUser);
                                intent.putExtra("opentype", "caller");
                                intent.putExtra("roomid", tmpRoomId);
                                startActivity(intent);
                                Log.d("VideoCallRoom", tmpRoomId);
                                Toast.makeText(mContext, "방 번호 : " + tmpRoomId, Toast.LENGTH_LONG).show();

                                new CallerToReceiverTask(tmpRoomId, CurrentId, tmpUser.getId(), CurrentUser.getNickname(), CurrentUser.getImg_profile()).execute();
                            }
                        });
                        adb.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        adb.show();
//                    if(Util.isRoomExist(getContext(), CurrentId, tmpUser.getId())) {
//                        Intent intent = new Intent(getActivity(), ChatActivity.class);
//                        intent.putExtra("userdata", tmpUser);
//                        startActivity(intent);
//                    }
//                    else {
//
//                    }

                    }
                });
            }

//            if(position == 0) {
//                holder.tv_nickname.setTextAppearance(R.style.MatchText);
//                holder.civ_profile.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext, "내가 좋아함", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getActivity(), MatchActivity.class);
//                        intent.putExtra("type", "from");
//                        intent.putExtra("users", lstFrom);
//                        startActivity(intent);
//                    }
//                });
//
//            }
//            else if(position == 1) {
//                holder.tv_nickname.setTextAppearance(R.style.MatchText);
//                holder.civ_profile.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext, "나를 좋아함", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getActivity(), MatchActivity.class);
//                        intent.putExtra("type", "to");
//                        intent.putExtra("users", lstTo);
//                        startActivity(intent);
//                    }
//                });
//
//            }
//            else {
//                holder.civ_profile.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext, tmpUser.getNickname(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getActivity(), ChatActivity.class);
//                        intent.putExtra("userdata", tmpUser);
////                        if(isRoomExist(CurrentId, tmpUser.getId()))
////                        {
////                            intent.putExtra("room", getRoomId(CurrentId, tmpUser.getId()));
////                        }
//                        startActivity(intent);
//                    }
//                });
//            }


        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }




        class ViewHolder extends RecyclerView.ViewHolder {

            CircleImageView civ_profile;
            TextView tv_nickname;

            public ViewHolder(View itemView) {
                super(itemView);

                civ_profile = itemView.findViewById(R.id.Civ_profile);
                tv_nickname = itemView.findViewById(R.id.Tv_nickname);
            }
        }

    }






    /** 채팅방 리스트 **/
//    class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.ViewHolder> {
//
//        Context context;
//        ArrayList<Room> items;
//
//
//        public RoomRecyclerViewAdapter(Context context, ArrayList<Room> items) {
//            this.context = context;
//            this.items = items;
//        }
//
//
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);
//
//            return new RoomRecyclerViewAdapter.ViewHolder(view);
//        }
//
//
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//
//            Room tmpItem = items.get(position);
//
//            Glide.with(context).load(tmpItem.getTo_profile()).into(holder.civ_profile);
//            holder.tv_nickname.setText(tmpItem.getTo_nickname());
//            holder.tv_msg.setText(tmpItem.getLastMsg());
//            holder.tv_count.setText(tmpItem.getStatus());
//        }
//
//
//
//        @Override
//        public int getItemCount() {
//            return items.size();
//        }
//
//
//
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//
//            CircleImageView civ_profile;
//            TextView tv_nickname;
//            TextView tv_msg;
//            TextView tv_count;
//
//            public ViewHolder(View itemView) {
//                super(itemView);
//
//                civ_profile = itemView.findViewById(R.id.Civ_profile);
//                tv_nickname = itemView.findViewById(R.id.Tv_nickname);
//                tv_msg = itemView.findViewById(R.id.Tv_msg);
//                tv_count = itemView.findViewById(R.id.Tv_count);
//
//            }
//        }
//
//    }




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


                if(mMatch.equals("couple")) {
                    lstMatch.addAll(userList);
                    Log.d("matchT", "lstMatch size : " + lstMatch.size());
                }
                else if(mMatch.equals("from")) {
                    lstFrom.addAll(userList);
                    Log.d("matchT", "lstFrom size : " + lstFrom.size());
                }
                else if(mMatch.equals("to")) {
                    lstTo.addAll(userList);
                    Log.d("matchT", "lstTo size : " + lstTo.size());
                }


            }

            else {
                Toast.makeText(getContext(), mMatch+"인연이 없음", Toast.LENGTH_SHORT).show();
            }



//                Log.d("chatT2", "listsize : " + lstMatch.size());
//            if(userList != null) {
//                Log.d("chatT", "listsize : " + userList.size());
//                for (int i=0; i<userList.size(); i++) {
//                    User tmpUser = userList.get(i);
//
//                    if(tmpUser.getImg_profile() != null) {
//                        ProfileUrls.add(tmpUser.getImg_profile());
//                    } else {
//                        ProfileUrls.add("");
//                    }
//                    if(tmpUser.getNickname() != null) {
//                        Nicknames.add(tmpUser.getNickname());
//                    } else {
//                        Nicknames.add("");
//                    }
//
//                    Log.d("chatT", ProfileUrls.size()+"");
//                    Log.d("chatT", Nicknames.size()+"");
//                }
//            } else {
//                Log.d("chatT", "users are empty");
//            }

            recyclerViewAdapter.notifyDataSetChanged();


        }
    }




    public boolean isRoomExist(String id, String id2) {
        boolean result = true;

        String query = "SELECT * FROM " + SQLiteHelper2.TABLENAME +
                " WHERE (USER='" + id + "' AND USER2='" + id2 +"') OR (USER='" + id2 + "' AND USER2='" + id + "')";

        Cursor cursor = rdb.rawQuery(query, null);
        Log.d("dbT2", "getCount = " + cursor.getCount());

        if(cursor.getCount() == 0)
            result = false;


        return result;
    }


    public String getRoomId(String id, String id2) {
        String result = null;

        String query = "SELECT * FROM " + SQLiteHelper2.TABLENAME +
                " WHERE (USER='" + id + "' AND USER2='" + id2 +"') OR (USER='" + id2 + "' AND USER2='" + id + "')";

        Cursor cursor = rdb.rawQuery(query, null);
        Log.d("dbT2", "getCount = " + cursor.getCount());

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            result = cursor.getString(1);
        }


        return result;
    }





    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> items = new ArrayList<>();

        String query = "SELECT * FROM " + SQLiteHelper2.TABLENAME + " ORDER BY " + "CAST(lasttime AS LONG)" + " DESC";
        Cursor cursor = rdb.rawQuery(query, null);
        while(cursor.moveToNext()) {
            Log.d("roomT", "커서위치 : "+cursor.getPosition());
            Room room = new Room(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8)
            );
            items.add(room);
        }
        cursor.close();
        Log.d("roomT", items.size()+"");
        return items;
    }


////////////////////


}
