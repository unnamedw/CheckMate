package com.example.msg_b.checkmate.util;

public class ChatRoom extends Room {

    User otherUser;

    public ChatRoom(String roomId, String user, String user2, String to_nickname, String to_profile, String lastMsg, String lastTime, String status, User mUser) {
        super(roomId, user, user2, to_nickname, to_profile, lastMsg, lastTime, status);
        this.otherUser = mUser;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }
}
