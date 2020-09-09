package com.example.msg_b.checkmate;

//import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.msg_b.checkmate.service.ChatService;
import com.example.msg_b.checkmate.util.ChatMessage;
import com.example.msg_b.checkmate.util.CurrentUserManager;
import com.example.msg_b.checkmate.util.SQLiteHelper;
import com.example.msg_b.checkmate.util.SQLiteHelper2;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.util.Util;
import com.example.msg_b.checkmate.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {


    /** ChatActivity Flow
     *
     * BindService[액티비티] -> onBind[서비스] -> onServiceConnected[액티비티]
     *
     * 1. 기기에 저장된 모든 채팅 내역을 불러온다. -> setChatList()
     * 2. 누락된 메시지를 서버로부터 받아온다. -> getChatDataTask()
     * 3. 채팅 목록을 띄운다. initRecyclerView()
     *
     * onStart 에서 서비스 연결
     * onStop 에서 연결 해제
     * **/


    private final String TYPE_TEXT = "TEXT";
    private final String TYPE_IMG = "IMG";
    private String CURRENT_ROOM_ID = null;

    /** 1은 채팅 메시지, 2는 룸에 관한 DB 임. **/
    SQLiteHelper sqlDB = null;
    SQLiteHelper2 sqlDB2 = null;
    SQLiteDatabase wdb = null;
    SQLiteDatabase rdb = null;
    SQLiteDatabase wdb2 = null;
    SQLiteDatabase rdb2 = null;

    ProgressDialog mDialog;
    private Messenger mRemote;


    /** 메시지를 보낼 때 - 서비스로 메시지 객체를 전달한다. **/
    public void remoteSendMessage(ChatMessage chatMessage) {
        if(mRemote != null) {
            Message msg = new Message();
            msg.what = 2;
            msg.obj = chatMessage.toString();
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /** 소켓을 연결할 때 **/
    public void SocketConnectionMessage(String id, String room) {
        if(mRemote != null) {
            Message msg = new Message();
            msg.what = 3;
            msg.obj = id+"&"+room;
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    public void SendAndConnectionMessage(String id, String room, ChatMessage chatMessage) {
        if(mRemote != null) {
            Message msg = new Message();
            msg.what = 4;
            msg.obj = id+"&"+room+"&"+chatMessage.toString();
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }


    /** 서비스와 액티비티를 Bind **/
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스 쪽의 핸들러를 받아 옴
            mRemote = new Messenger(service);

            // 서비스 쪽으로 액티비티 측의 핸들러를 넘겨 줌
            Message msg = new Message();
            msg.what = 0;
            msg.obj = new Messenger(new RemoteHandler());
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // 소켓 연결
            SocketConnectionMessage(myId, CURRENT_ROOM_ID);
            Log.d("taskT", "onServiceConnected Complete");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemote = null;
        }
    };



    /** 메시지를 받았을 경우 처리 **/
    @SuppressLint("HandlerLeak")
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1 :
                    //Register activity handler

                    // 메시지 받아 옴.
                    String msgStr = msg.obj.toString();

                    String[] splitMsg = msgStr.split("&");
                    String timeSet = Util.getCurrentTime();

                    ChatMessage receivedChatMsg = new ChatMessage(
                            splitMsg[0],
                            splitMsg[1],
                            splitMsg[2],
                            splitMsg[3],
                            splitMsg[4],
                            timeSet,
                            splitMsg[6],
                            splitMsg[7]
                    );
                    Log.d("socketT2", receivedChatMsg.getTime_received());
                    if(isRoomExist(myId, otherId)) {
                        Log.d("sockT", "룸존재");
                        // 방 정보 업데이트
                        sqlDB2.updateRoomMsg(CURRENT_ROOM_ID, receivedChatMsg.getMsg(), timeSet, 0);
                    } else {
                        sqlDB2.insertROOM(wdb2,
                                receivedChatMsg.getRoom(),
                                myId,
                                otherId,
                                otherUser.getNickname(),
                                otherUser.getImg_profile(),
                                receivedChatMsg.getMsg(),
                                timeSet,
                                "0");
                        Log.d("sockT", "룸존재안함");
                    }

                    // 채팅 뷰에 반영
                    messageList.add(receivedChatMsg);
                    mAdapter.notifyItemInserted(messageList.size()-1);
                    mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);

                    // DB에 저장
                    saveChat(receivedChatMsg);



                    break;
                default:
                    break;
            }
        }
    }




    RecyclerView mRecyclerView;
    LinearLayout mLinearLayout;
    ChatRecyclerViewAdapter mAdapter;
    ArrayList<ChatMessage> messageList;
    String myId;
    String otherId;
    EditText editText;
    Button button;
    ProgressBar progressBar;
    User currentUser, otherUser;
    String intentRoom;
    ChatMessage lastmsg;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 바 숨김

        /** 유저 객체와 messageList 객체 생성 **/
        Intent intent = getIntent();
        otherUser = (User) intent.getExtras().get("userdata");
        currentUser = CurrentUserManager.getCurrentUser(getApplicationContext());

        // 인텐트로 받아온 방 정보
        intentRoom = (String) intent.getExtras().get("room");

        myId = currentUser.getId();
        otherId = otherUser.getId();
        messageList = new ArrayList<>(); // chat data

        // 채팅방 이름
        CURRENT_ROOM_ID = Util.getRoomCode(myId, otherId);

        /** 뷰세팅 **/
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("To "+otherUser.getNickname());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        mDialog = new ProgressDialog(this);
//        mDialog.setCancelable(false);
//        mDialog.show();
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);




        /** 뷰등록 **/
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        mRecyclerView = findViewById(R.id.Rv);
//        mLinearLayout = findViewById(R.id.Ll);
//        optimizeView();

        //작업이 완료되기 전까지 버튼을 멈추고 프로그레스 바를 띄움.
        editText.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
        button.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);

        /** db 생성 **/
        sqlDB = new SQLiteHelper(this); // 채팅
        wdb = sqlDB.getWritableDatabase();
        rdb = sqlDB.getReadableDatabase();
        wdb.execSQL(SQLiteHelper.SQL_CREATE_TBL);
        sqlDB2 = new SQLiteHelper2(this); // 방
        wdb2 = sqlDB2.getWritableDatabase();
        rdb2 = sqlDB2.getReadableDatabase();





        // 기기에 저장된 채팅 내역을 불러온다.
        setChatList();


        // 채팅방 카운트를 초기화
        if(isRoomExist(myId, otherId))
        sqlDB2.initRoomCount(CURRENT_ROOM_ID);


    } // onCreate


    /** --생명주기 관리 **/
    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, ChatService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /** 생명주기 관리-- **/



    /** 리스너 **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //채팅 메시지 보내기
            case R.id.button :
                Log.d("checkT","클릭됨");

                // 해당 유저와의 채팅방이 존재하는지 확인한다.
                // 채팅방이 존재하지 않으면 방을 새로 만듦
                String text = editText.getText().toString();
                String timeSet = Util.getCurrentTime();


                // 메시지 입력 칸이 비어있지 않으면 메시지를 만들어 서비스로 전송한다.
                //
                if(!text.isEmpty()) {
                    ChatMessage sentChatMsg = new ChatMessage(
                            myId,
                            CURRENT_ROOM_ID,
                            TYPE_TEXT,
                            timeSet,
                            "",
                            "",
                            "",
                            text);
                    messageList.add(sentChatMsg);
                    editText.setText("");
                    mAdapter.notifyItemInserted(messageList.size()-1);

                    //방이 존재하지 않는 경우 방을 만듬
                    if(isRoomExist(myId, otherId)) {
                        Log.d("sockT", "룸존재");
                        // 방 정보 업데이트
                        sqlDB2.updateRoomMsg(CURRENT_ROOM_ID, text, timeSet, 0);
                    } else {
                        sqlDB2.insertROOM(wdb2,
                                CURRENT_ROOM_ID,
                                myId,
                                otherId,
                                otherUser.getNickname(),
                                otherUser.getImg_profile(),
                                text,
                                timeSet,
                                "0");
                        Log.d("sockT", "룸존재안함");
                    }

                    // DB에 메시지 저장
                    saveChat(sentChatMsg);

                    // 서버로 메시지 전송
                    remoteSendMessage(sentChatMsg);



                    //마지막 메시지로 포커스 이동
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
                        }
                    });
                } else {
                    Toast.makeText(this, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }



    public void setChatList() {
        /** SQLite 에 저장된 채팅 내역을 불러온다. **/



//        SQLiteDatabase db = sqlDB.getReadableDatabase();
        String query = "SELECT * FROM " + SQLiteHelper.TBL_CONTACT + " WHERE (ROOM='" + CURRENT_ROOM_ID + "')";
        Cursor cursor = rdb.rawQuery(query, null);
        ChatMessage dbMessage = null;
        if(cursor.getCount() >0) {
            while (cursor.moveToNext()) {

                dbMessage = new ChatMessage(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                );
                messageList.add(dbMessage);

            }

        }
        cursor.close();
        Log.d("chatT2", "setChatList");
        String sentId = null;
        String sentTime = null;
        if(dbMessage != null) {
            sentId = dbMessage.getId();
            sentTime = dbMessage.getTime_sent();
//            Toast.makeText(this, sentId, Toast.LENGTH_SHORT).show();
        } else {
            sentId = "noValue";
            sentTime = "noValue";
        }
//        Toast.makeText(this, sentId, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, CURRENT_ROOM_ID, Toast.LENGTH_SHORT).show();
        new getChatDataTask().execute(sentId, sentTime); // 누락된 채팅이 있으면 DB 에서 불러와 messageList 에 저장한다.

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_actionbar, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//        switch (item.getItemId()) {
//            case R.id.item1:
//                Toast.makeText(this, "Coin!", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }



    /** 누락된 채팅내역을 서버에서 불러오는 작업 **/
    class getChatDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            Log.d("checkT","getChatDataTask 실행");

            String strUrl = "http://115.71.238.160/novaproject1/getchat.php";
            String id = strings[0];
            String time_sent = strings[1];
            String result = null;

            RequestBody requestBody = new FormBody.Builder()
                    .add("id", id)
                    .add("roomid", CURRENT_ROOM_ID)
                    .add("time_sent", time_sent)
                    .build();

            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient mClient = new OkHttpClient();
            Call mCall = mClient.newCall(request);
            try {
                Response mResponse = mCall.execute();
                if(mResponse.body() != null) {
                    result = mResponse.body().string();
                    Log.d("chatT2", "result : "+result);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String JsonResult) {
            super.onPostExecute(JsonResult);
            Log.d("chatT2", "onPostExecute");
            Log.d("chatT2", "result : "+JsonResult);
            if(JsonResult != null) {
                try {
                    JSONObject jsonObject = new JSONObject(JsonResult);
                    JSONArray jsonArray = jsonObject.getJSONArray("chats");

                    for(int i=0;i<jsonArray.length();i++){

                        Log.d("chatT3", "루프 : "+i);
                        JSONObject item = jsonArray.getJSONObject(i);
                        ChatMessage chatMessage = new Gson().fromJson(item.toString(), ChatMessage.class);

//                        String id = item.getString("id");
//                        String room = item.getString("room");
//                        String type = item.getString("type");
//                        String time_sent = item.getString("time_sent");
//                        String time_server = item.getString("time_server");
//                        String time_received = item.getString("time_received");
//                        String status = item.getString("status");
//                        String msg = item.getString("msg");
//
//                        ChatMessage chatMessage = new ChatMessage(
//                                id,
//                                room,
//                                type,
//                                time_sent,
//                                time_server,
//                                time_received,
//                                status,
//                                msg
//                        );


                        messageList.add(chatMessage);
                        saveChat(chatMessage);
                        if(i==jsonArray.length()-1 && isRoomExist(myId, otherId) && jsonArray.length()>0) {

                            //방 정보 업데이트에 쓰일 수신시각을 설정하는 부분
                            //서버시간이 있으면 서버시간을 적용하고 아니면 현재시각으로 저장한다.
                            /** 임시코드이기 때문에 수정 요망! **/
                            String receivedTime = Util.getCurrentTime();
                            if(chatMessage.getTime_server()!=null && !chatMessage.getTime_server().isEmpty()) {
                                receivedTime = chatMessage.getTime_server();
                            }
                            sqlDB2.updateRoomMsg(CURRENT_ROOM_ID, chatMessage.getMsg(), receivedTime, 0);
                        }

//                        //로컬의 마지막 메시지 이후의 메시지만 저장한다.
//                        if(lastmsg!= null) {
//                            if(Long.valueOf(lastmsg.getTime())< (Long.valueOf(chatMessage.getTime())-15)) {
//                                messageList.add(chatMessage);
//
//                                /** db저장 **/
//                                String tmpQuery = "INSERT INTO " + SQLiteHelper.TBL_CONTACT +" (ID, ROOM, TYPE, TIME, MSG) VALUES ('" +
//                                        chatMessage.getId() + "', '" + chatMessage.getRoom() + "', '" + chatMessage.getType()
//                                        + "', '" + chatMessage.getTime() + "', '" + chatMessage.getMsg() + "')";
//                                wdb.execSQL(tmpQuery);
//                                updateRoomMsg(chatMessage.getMsg(), chatMessage.getTime()); // 방 정보 업데이트
//                            } else {
//                            }
//
//                        } else {
//                            messageList.add(chatMessage);
//
//                            /** db저장 **/
//                            String tmpQuery = "INSERT INTO " + SQLiteHelper.TBL_CONTACT +" (ID, ROOM, TYPE, TIME, MSG) VALUES ('" +
//                                    chatMessage.getId() + "', '" + chatMessage.getRoom() + "', '" + chatMessage.getType()
//                                    + "', '" + chatMessage.getTime() + "', '" + chatMessage.getMsg() + "')";
//                            wdb.execSQL(tmpQuery);
//                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d("chatT2", e.getLocalizedMessage());
                }
            } // if



            initRecyclerView(messageList);
            Log.d("taskT", "AsyncTask Complete");

        }
    }



    /** 채팅 뷰 세팅 **/
    public void initRecyclerView(ArrayList<ChatMessage> mList) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView = findViewById(R.id.Rv);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatRecyclerViewAdapter(getApplicationContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
            }
        });


        /** SoftKeyBoard 가 액티비티 상으로 올라왔을 때 마지막 채팅 메시지로 포커스를 이동시켜 준다. **/
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if ( bottom < oldBottom ) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mRecyclerView.getAdapter().getItemCount()>0)
                                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount()-1);
                        }
                    });
                }


            }
        });

        //작업이 완료되면 프로그레스 바를 지우고 버튼을 활성화
        editText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        button.setEnabled(true);
        Log.d("taskT", "initRecyclerView Complete");
    }








    /** 채팅 리사이클러뷰 **/
    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        final int me = 1;
        final int other = -1;

        private Context mContext;
        private ArrayList<ChatMessage> ChatMessages;

        public ChatRecyclerViewAdapter(Context mContext, ArrayList<ChatMessage> chatMessages) {
            this.mContext = mContext;
            this.ChatMessages = chatMessages;
        }



        @Override
        public int getItemViewType(int position) {

            int viewType;
            if(ChatMessages.get(position).getId().equals(myId))
                viewType = me;
            else
                viewType = other;


            return viewType;
            //뷰 타입을 구분할 수 있는 적절한 int 값을 리턴함
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v;

            switch (viewType) {
                case me:
                    v = inflater.inflate(R.layout.item_mychat, parent, false);
                    viewHolder = new ChatViewHolder1(v);
                    break;

                case other:
                    v = inflater.inflate(R.layout.item_otherchat, parent, false);
                    viewHolder = new ChatViewHolder2(v);
                    break;

                default:
                    v = inflater.inflate(R.layout.item_mychat, parent, false);
                    viewHolder = new ChatViewHolder1(v);
                    break;

            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            ChatMessage item = ChatMessages.get(position);
            String time = null;

            switch (holder.getItemViewType()) {

                case me:
                    // 보낸 시간으로
//                    time = Util.parseTimeHM(item.getTime_sent());

                    ChatViewHolder1 chatViewHolder1 = (ChatViewHolder1) holder;
                    chatViewHolder1.message.setText(item.getMsg());
                    chatViewHolder1.time.setText(time);
                    break;

                case other:
                    // 받은 시간으로
//                    time = Util.parseTimeHM(item.getTime_received());

                    ChatViewHolder2 chatViewHolder2 = (ChatViewHolder2) holder;
                    chatViewHolder2.nickname.setText(otherUser.getNickname());
                    chatViewHolder2.message.setText(item.getMsg());
                    chatViewHolder2.time.setText(time);
                    Glide.with(mContext).load(otherUser.getImg_profile()).into(chatViewHolder2.profile);
                    break;

                default:
                    ChatViewHolder1 chatViewHolder = (ChatViewHolder1) holder;
                    chatViewHolder.message.setText(item.getMsg());
                    break;

            }


        }

        @Override
        public int getItemCount() {
            return ChatMessages.size();
        }


        class ChatViewHolder1 extends RecyclerView.ViewHolder {

            TextView nickname;
            TextView message;
            TextView time;
            CircleImageView profile;
            public ChatViewHolder1(View itemView) {
                super(itemView);

                nickname = itemView.findViewById(R.id.Tv_nickname);
                message = itemView.findViewById(R.id.Tv_me);
                time = itemView.findViewById(R.id.Tv_time);
                profile = itemView.findViewById(R.id.Civ_profile);
            }
        }

        class ChatViewHolder2 extends RecyclerView.ViewHolder {

            TextView nickname;
            TextView message;
            TextView time;
            CircleImageView profile;
            public ChatViewHolder2(View itemView) {
                super(itemView);

                nickname = itemView.findViewById(R.id.Tv_nickname);
                message = itemView.findViewById(R.id.Tv_other);
                time = itemView.findViewById(R.id.Tv_time);
                profile = itemView.findViewById(R.id.Civ_profile);
            }
        }



    }






    /** 만들어 놓은 채팅방이 있는지 확인 **/
    public boolean isRoomExist(String id, String id2) {

        boolean result = true;

        String query = "SELECT * FROM " + SQLiteHelper2.TABLENAME +
                " WHERE ROOMID='" + CURRENT_ROOM_ID + "'";

        Cursor cursor = rdb2.rawQuery(query, null);
        Log.d("dbT2", "getCount = " + cursor.getCount());

        if(cursor.getCount() == 0)
            result = false;

        cursor.close();
        return result;
    }



    /** 현재 채팅방의 마지막 메시지를 구한다. **/
    public ChatMessage getLastChat() {

        ChatMessage chatMessage = null;
        String idxQuery = "SELECT MAX(IDX) FROM " + SQLiteHelper.TBL_CONTACT +
                " WHERE ROOM='" + CURRENT_ROOM_ID + "'";
        Cursor tmpCursor = rdb.rawQuery(idxQuery, null);
        if(tmpCursor.moveToNext()) {
            chatMessage = new ChatMessage(
                    tmpCursor.getString(1),
                    tmpCursor.getString(2),
                    tmpCursor.getString(3),
                    tmpCursor.getString(4),
                    tmpCursor.getString(5),
                    tmpCursor.getString(6),
                    tmpCursor.getString(7),
                    tmpCursor.getString(8)
            );
            tmpCursor.close();
        }

        return chatMessage;
    }


    public void saveChat(ChatMessage chatMessage) {
        // DB에 저장
        String tmpQuery = "INSERT INTO CHAT (id, room, type, time_sent, time_server, time_received, status, msg) VALUES ('" +
                chatMessage.getId() + "', '" +
                chatMessage.getRoom() + "', '" +
                chatMessage.getType() + "', '" +
                chatMessage.getTime_sent() + "', '" +
                chatMessage.getTime_server() + "', '" +
                chatMessage.getTime_received() + "', '" +
                chatMessage.getStatus() + "', '" +
                chatMessage.getMsg() + "')";
        wdb.execSQL(tmpQuery);
    }





    public void optimizeView() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int customH1 = (size.y*105)/120;
        int customH2 = (size.y*1)/12;
        int customH1dp = (520)*(size.y/1280);
        int customH2dp = (50)*(size.y/1280);

        Log.d("viewT", "size.y = "+size.y);
        Log.d("viewT", "customH1 = "+customH1);
        Log.d("viewT", "customH2 = "+customH2);

/*
        // RecyclerView 크기 조절
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
        params.height = customH1;
        mRecyclerView.setLayoutParams(params);

        // EditText 크기 조절
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) mLinearLayout.getLayoutParams();
        params2.height = customH2;
        mLinearLayout.setLayoutParams(params2);
*/

    }

}
