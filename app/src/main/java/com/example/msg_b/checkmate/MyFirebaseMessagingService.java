package com.example.msg_b.checkmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.msg_b.checkmate.util.SQLiteHelper2;
import com.example.msg_b.checkmate.util.User;
import com.example.msg_b.checkmate.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "myMessagingServiceTAG";
    private static final String TAG_LIKE = "LIKE";
    SQLiteHelper2 roomDB;

    SharedPreferences sf;
    SharedPreferences.Editor editor;




    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        Log.d(TAG, FirebaseInstanceId.getInstance().getToken());
        sf = getSharedPreferences("test", MODE_PRIVATE);
        editor = sf.edit();
        editor.putString("1", "안녕");
        editor.apply();
        roomDB = new SQLiteHelper2(this);
    }





    /**
     * 메시지가 도착하면 방이 있는지 없는지 먼저 체크
     * 방이 없으면 DB 에서 모자란 정보를 가져와 방을 생성
     *
     *
     *
     * **/



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        Log.d(TAG, "onMessageReceived");
        String mToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("fcmtest", mToken);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

//        sendBroadcast(new Intent("test"));

        String tmp = sf.getString("1", "Nope!");
        //좋아요를 받은 경우
        if(remoteMessage.getData().get("type").equals("LikeNotification"))
        sendLikeNotification(
                remoteMessage.getData().get("type"),
                remoteMessage.getData().get("message")
//                remoteMessage.getData().get(tmp)
        );
        //채팅알림을 받은 경우
        else if(remoteMessage.getData().get("type").equals("ChatNotification")) {
            sendChatNotification(
                    remoteMessage.getData().get("nickname"),
                    remoteMessage.getData().get("message")
            );

            String timeSet = Util.getCurrentTime();

            // 해당 채팅방이 존재하는지 확인
            if(isRoomExist(remoteMessage.getData().get("room"))) {
                Log.d("RoomT", "방 존재함");
                // 존재하는 경우
                SQLiteDatabase writeROOM = roomDB.getWritableDatabase();
                String query = "UPDATE " + SQLiteHelper2.TABLENAME + " SET " +
                        "lastmsg='" + remoteMessage.getData().get("message") + "', " +
                        "lasttime='" + timeSet + "', " +
                        "status=status+" + 1 +
                        " WHERE ROOMID='" + remoteMessage.getData().get("message") + "'";
                writeROOM.execSQL(query);
//                roomDB.updateRoomMsg(
//                        remoteMessage.getData().get("room"),
//                        remoteMessage.getData().get("message"),
//                        timeSet,
//                        1);
                Intent intent = new Intent("test2");
                intent.putExtra("msg", remoteMessage.getData().get("message"));
                intent.putExtra("room", remoteMessage.getData().get("room"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            } else {
                Log.d("RoomT", "방 존재안함");

                // 존재하지 않는 경우
                new CreateRoomTask().execute(
                        remoteMessage.getData().get("fromid"),
                        remoteMessage.getData().get("room"),
                        remoteMessage.getData().get("message"),
                        timeSet);
            }


        }

        /**
         * Video call 기능 삭제
         * */
//        //영통 발신자인 경우
//        else if(remoteMessage.getData().get("type").equals("respond")) {
//
//        }
//        //영통 수신자인 경우
//        else if(remoteMessage.getData().get("type").equals("received")) {
//
//            Intent intent = new Intent(this, ConnectActivity.class);
//            intent.putExtra("opentype", "receiver");
//            intent.putExtra("nickname_caller", remoteMessage.getData().get("nickname"));
//            intent.putExtra("profile_caller", remoteMessage.getData().get("profile"));
//            intent.putExtra("roomid", remoteMessage.getData().get("roomid"));
//            startActivity(intent);
//
//            Log.d("videoT", remoteMessage.getData().get("nickname"));
//            Log.d("videoT", remoteMessage.getData().get("profile"));
//            Log.d("videoT", remoteMessage.getData().get("roomid"));
//        }


//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            if(remoteMessage.getData().isEmpty()) {
//                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//            } else {
//                sendNotification(remoteMessage.getData());
//            }
//        }




        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(Map<String, String> data) {

        String title = data.get("title").toString();
        String body = data.get("message").toString();

        String NOTIFICATION_CHANNEL_ID = "com.example.msg_b.test";
        NotificationManager notificationManager = (NotificationManager) getSystemService((Context.NOTIFICATION_SERVICE));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("MSG Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.MAGENTA);
            notificationChannel.setVibrationPattern(new long[]{100, 0, 100, 0});
//            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }

//        //클릭했을 때 시작할 액티비티에게 전달하는 Intent 객체 생성
//        Intent intent = new Intent(this, ExActivity.class);
//        //클릭할 때까지 액티비티 실행을 보류하고 있는 PendingIntent 객체 생성
//        PendingIntent pending = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_love)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void scheduleJob() {
    }



    private void sendChatNotification(String messageNickname, String messageBody) {

        NotificationManager notificationManager = (NotificationManager) getSystemService((Context.NOTIFICATION_SERVICE));
        String NOTIFICATION_CHANNEL_ID = "com.example.msg_b.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("MSG Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 500});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_chat)
                .setContentTitle(messageNickname)
                .setContentText(messageBody)
                .setContentInfo("info");

        notificationManager.notify(777, notificationBuilder.build());

    }





    private void sendLikeNotification(String messageTitle, String messageBody) {

        NotificationManager notificationManager = (NotificationManager) getSystemService((Context.NOTIFICATION_SERVICE));
        String NOTIFICATION_CHANNEL_ID = "com.example.msg_b.test";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("MSG Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 500});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_love)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentInfo("info");

        notificationManager.notify(776, notificationBuilder.build());


//        Intent intent = new Intent(this, ConnectActivity.class);
//        intent.putExtra("userdata", CurrentUserManager.getCurrentUser(this));
//        startActivity(intent);




//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("FCM Message")
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelName = getString(R.string.default_notification_channel_name);
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//        }
//        notificationManager.notify(0, notificationBuilder.build());

    }




    class CreateRoomTask extends AsyncTask<String, Void, User> {

        String roomid;
        String user;
        String user2;
        String to_nickname;
        String to_profile;
        String lastmsg;
        String lasttime;
        String status;

        @Override
        protected User doInBackground(String... strings) {

            String strUrl = "http://115.71.238.160/novaproject1/HomeActivity/ProfileFragment/ProfileActivity/getuser.php";
            User result = null;
            user = strings[0];
            roomid = strings[1];
            lastmsg = strings[2];
            lasttime = strings[3];

            String[] roomParse = Util.parseRoomCode(roomid);
            if(roomParse[0].equals(user))
                user2 = roomParse[1];
            else
                user2 = roomParse[0];



            RequestBody requestBody = new FormBody.Builder()
                    .add("id", user)
                    .build();

            Request request = new Request.Builder()
                    .url(strUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient mClient = new OkHttpClient();
            Call mCall = mClient.newCall(request);
            try {
                Response mResponse = mCall.execute();
                String data = mResponse.body().string();
                User mUser = new Gson().fromJson(data, User.class);
                result = mUser;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }


        @Override
        protected void onPostExecute(User resultUser) {
            super.onPostExecute(resultUser);

            to_profile = resultUser.getImg_profile();
            to_nickname = resultUser.getNickname();

            roomDB.insertROOM(roomDB.getWritableDatabase(), roomid, user, user2, to_nickname, to_profile, lastmsg, lasttime, "1");

            Intent intent = new Intent("test2");
            intent.putExtra("msg", lastmsg);
            intent.putExtra("room", roomid);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }


    /** 만들어 놓은 채팅방이 있는지 확인 **/
    public boolean isRoomExist(String roomid) {

        boolean result = true;

        String query = "SELECT * FROM " + SQLiteHelper2.TABLENAME +
                " WHERE ROOMID='" + roomid + "'";

        Cursor cursor = roomDB.getReadableDatabase().rawQuery(query, null);
        Log.d("dbT3", "getCount = " + cursor.getCount());

        if(cursor.getCount() == 0)
            result = false;
        else {
            cursor.moveToFirst();
            Log.d("dbT3", "room = " + cursor.getString(1));
        }


        cursor.close();
        return result;
    }









    /**
     * 영상통화
     * **/





}
