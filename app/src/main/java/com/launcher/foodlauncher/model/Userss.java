package com.launcher.foodlauncher.model;

import java.util.List;

public class Userss {

    private String username;
    private String password;
    private String phone;
    private String email;

    public Userss(){

    }
    public Userss(String username,String password,String phone,String email){
        this.username=username;
        this.email=email;
        this.password=password;
        this.phone=phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}