package com.example.han.myapplication001.XMPP;

public class FriendListClass {
    public String names;
    public String status_name;
    public int status;
    public FriendListClass(String a, int bs,String c){
        names = a;
        status = bs;
        if(c.equals("Offline")) status_name = "";
        else status_name = c;
    }
}
