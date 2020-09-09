package com.example.msg_b.checkmate.util;

public class ChatMessage {

    private String id;
    private String room;
    private String type;
    private String time_sent;
    private String time_server;
    private String time_received;
    private String status;
    private String msg;


    public ChatMessage(String id, String room, String type, String time_sent, String time_server, String time_received, String status, String msg) {
        this.id = id;
        this.room = room;
        this.type = type;
        this.time_sent = time_sent;
        this.time_server = time_server;
        this.time_received = time_received;
        this.status = status;
        this.msg = msg;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime_sent() {
        return time_sent;
    }

    public void setTime_sent(String time_sent) {
        this.time_sent = time_sent;
    }

    public String getTime_server() {
        return time_server;
    }

    public void setTime_server(String time_server) {
        this.time_server = time_server;
    }

    public String getTime_received() {
        return time_received;
    }

    public void setTime_received(String time_received) {
        this.time_received = time_received;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String toString() {
        String result = null;
        result =
            this.id+"&"+
            this.room+"&"+
            this.type+"&"+
            this.time_sent+"&"+
            this.time_server+"&"+
            this.time_received+"&"+
            this.status+"&"+
            this.msg;

        return result;
    }
}
