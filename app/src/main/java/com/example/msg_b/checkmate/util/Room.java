package com.example.msg_b.checkmate.util;

public class Room {

    String roomId;
    String user;
    String user2;
    String to_nickname;
    String to_profile;
    String lastMsg;
    String lastTime;
    String status;

    public Room(String roomId, String user, String user2, String to_nickname, String to_profile, String lastMsg, String lastTime, String status) {
        this.roomId = roomId;
        this.user = user;
        this.user2 = user2;
        this.to_nickname = to_nickname;
        this.to_profile = to_profile;
        this.lastMsg = lastMsg;
        this.lastTime = lastTime;
        this.status = status;
    }


    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getTo_nickname() {
        return to_nickname;
    }

    public void setTo_nickname(String to_nickname) {
        this.to_nickname = to_nickname;
    }

    public String getTo_profile() {
        return to_profile;
    }

    public void setTo_profile(String to_profile) {
        this.to_profile = to_profile;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
