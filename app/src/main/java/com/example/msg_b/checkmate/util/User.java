package com.example.msg_b.checkmate.util;

import java.io.Serializable;

public class User implements Serializable{

    private String id;
    private String type;
    private String status;
    private String sex;
    private String nickname;
    private String img_profile;
    private String img_profile2;
    private String img_profile3;
    private String img_profile4;
    private String img_profile5;
    private String img_profile6;
    private String introduce;
    private String live;
    private String job;
    private String age;
    private String height;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_profile) {
        this.img_profile = img_profile;
    }

    public String getImg_profile2() {
        return img_profile2;
    }

    public void setImg_profile2(String img_profile2) {
        this.img_profile2 = img_profile2;
    }

    public String getImg_profile3() {
        return img_profile3;
    }

    public void setImg_profile3(String img_profile3) {
        this.img_profile3 = img_profile3;
    }

    public String getImg_profile4() {
        return img_profile4;
    }

    public void setImg_profile4(String img_profile4) {
        this.img_profile4 = img_profile4;
    }

    public String getImg_profile5() {
        return img_profile5;
    }

    public void setImg_profile5(String img_profile5) {
        this.img_profile5 = img_profile5;
    }

    public String getImg_profile6() {
        return img_profile6;
    }

    public void setImg_profile6(String img_profile6) {
        this.img_profile6 = img_profile6;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getLive() {


        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", sex='" + sex + '\'' +
                ", nickname='" + nickname + '\'' +
                ", img_profile='" + img_profile + '\'' +
                ", img_profile2='" + img_profile2 + '\'' +
                ", img_profile3='" + img_profile3 + '\'' +
                ", img_profile4='" + img_profile4 + '\'' +
                ", img_profile5='" + img_profile5 + '\'' +
                ", img_profile6='" + img_profile6 + '\'' +
                ", introduce='" + introduce + '\'' +
                ", live='" + live + '\'' +
                ", job='" + job + '\'' +
                ", age='" + age + '\'' +
                ", height='" + height + '\'' +
                '}';
    }

}
