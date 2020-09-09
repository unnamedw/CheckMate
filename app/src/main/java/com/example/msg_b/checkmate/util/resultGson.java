package com.example.msg_b.checkmate.util;

public class resultGson {

    private String success;
    private String message;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "resultGson{" +
                "success='" + success + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
