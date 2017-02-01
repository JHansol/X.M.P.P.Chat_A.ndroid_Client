package com.example.han.myapplication001.chatlist;

public class chatListClass {
    public String names;
    public String message;
    public Integer count;
    public chatListClass(String a, int bs,String c){
        names = a;
        count = bs;
        if(c.equals("Offline") || c.isEmpty()) message = "";
        else message = c;
    }
}
