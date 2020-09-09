package com.example.msg_b.checkmate.util;

import java.io.Serializable;

public class ExUser extends User implements Serializable {

    private String lati;
    private String longi;

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }
}
