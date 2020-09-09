package com.example.msg_b.checkmate.util;

public class Data {

    private String image;
    private String introduce;
    private String Age;
    private String Live;

    public Data(String image, String introduce, String age, String live) {
        this.image = image;
        this.introduce = introduce;
        Age = age;
        Live = live;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getLive() {
        return Live;
    }

    public void setLive(String live) {
        Live = live;
    }
}
