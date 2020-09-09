package com.example.msg_b.checkmate.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatService extends Service {


    private static String ip3 = "58.122.116.192:5050"; //집 공인아이피
    private static String ip = "192.168.0.169"; // 집
    private static String ip2 = "192.168.56.1"; // 6사
    private static String ip4 = "192.168.0.116"; // 3사
    private static String ip5 = "115.71.238.160"; // 호스팅서버
    private static int port = 5000;
    public Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;
    public DataInputStream in;
    public DataOutputStream out;

    private Messenger mRemote;
    boolean isRun;
    Thread serverThread;


    public void remoteSendMessage(String data) {
        if(mRemote != null) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = data;
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                // ChatActivity 와 연결
                case 0 :
                    //Register activity handler
                    mRemote = (Messenger) msg.obj;
                    break;

                // 서버로 메시지를 전송함
                case 2 :
                    final String chatMessage = (String) msg.obj;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                out.writeUTF(chatMessage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;

                // 서버에 소켓을 연결함
                case 3 :
                    final String socketInfo = (String) msg.obj;
                    serverThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                socket = new Socket(ip5, port);
                                in = new DataInputStream(socket.getInputStream());
                                out = new DataOutputStream(socket.getOutputStream());
                                out.writeUTF(socketInfo);

                                while(isRun) {
                                    Log.d("socketT", "Waiting for MSG");
                                    Log.d("socketT", "isRun = " + isRun);
                                    String text = in.readUTF();
                                    remoteSendMessage(text);
                                    Log.d("socketT", "ReceivedMSG : " + text);

                                }

                                Log.d("socketT", "quit");


                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("socketT", "socket Disconnected");
                            } finally {
                                Log.d("socketT", "Thread close");
                            }

                        }
                    });
                    serverThread.start();

                    break;

                //소켓 연결과 동시에 메시지 전송
                case 4 :

                    final String msgs = (String) msg.obj;
                    String[] strs = msgs.split("&");
                    final String socketinfo = strs[0]+"&"+strs[1];
                    final String chatmsg = strs[2];
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isRun = true;

                            try {
                                socket = new Socket(ip5, port);
                                in = new DataInputStream(socket.getInputStream());
                                out = new DataOutputStream(socket.getOutputStream());
                                out.writeUTF(socketinfo);
                                Thread.sleep(100);
                                out.writeUTF(chatmsg);
                                while(isRun) {
                                    Log.d("socketT", "Waiting for MSG");
                                    String text = in.readUTF();
                                    remoteSendMessage(text);
                                    Log.d("socketT", "ReceivedMSG : " + text);

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("socketT2", "socket Disconnected");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                Log.d("socketT2", "Thread close");
                            }

                        }
                    }).start();

                    break;

                default:
                    remoteSendMessage("TEST");
                    break;
            }
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        Log.d("serviceT", "onBind");
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        isRun = true;
        return new Messenger(new RemoteHandler()).getBinder();
    }

    public ChatService() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("serviceT", "onCreate");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    socket = new Socket(ip, port);
//                    in = new DataInputStream(socket.getInputStream());
//                    out = new DataOutputStream(socket.getOutputStream());
//                    out.writeUTF(CurrentUserManager.getCurrentUserId(ChatService.this)+"&"+"123485613");
//
//                    while(isRun) {
//                        Log.d("socketT", "Waiting for MSG");
//                        String text = in.readUTF();
//                        remoteSendMessage(text);
//                        Log.d("socketT", "ReceivedMSG : " + text);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("serviceT", "onStartCommand");



        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("serviceT", "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("serviceT", "onUnbind");

        try {
            if(socket != null)
            socket.close();
            isRun = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("serviceT", "onRebind");
    }


//    IBinder mBinder = new ChatBinder();
//    class ChatBinder extends Binder {
//
//        ChatService getMyService() {
//            return ChatService.this;
//        }
//    }




}
