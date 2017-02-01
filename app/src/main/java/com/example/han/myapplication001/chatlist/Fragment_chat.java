package com.example.han.myapplication001.chatlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.han.myapplication001.ChatActivity;

import java.util.ArrayList;

public class Fragment_chat extends ListFragment {
    String titleStr;
    Handler mHandler;
    custom_list_chat adapter = new custom_list_chat() ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Adapter 생성 및 Adapter 지정.
//        adapter = new customlist_chat() ;
        setListAdapter(adapter);
//        adapter.addItem();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id) {
        // get TextView's Text.
        chatListClass item = (chatListClass) l.getItemAtPosition(position) ;
        titleStr = item.names;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post( new Runnable() {
                    public void run() {
                        Intent MainIntent = new Intent(getActivity().getBaseContext(),ChatActivity.class);
                        MainIntent.putExtra("id",titleStr);
                        startActivity(MainIntent);
                    }
                });
            }
        });t.start();
    }
    public void adds(ArrayList<chatListClass> temp) {
        adapter.adds(temp);
        setListAdapter(adapter);
    }
    public void addsi() {
        adapter.addItem();
    }
    public void refresh() {
        setListAdapter(adapter);
    }
    public void setmHandler(Handler H1){ mHandler = H1;}
}

