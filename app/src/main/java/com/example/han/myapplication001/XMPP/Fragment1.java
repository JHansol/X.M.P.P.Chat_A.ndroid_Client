package com.example.han.myapplication001.XMPP;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.ListFragment;
import android.widget.Toast;

import com.example.han.myapplication001.ChatActivity;
import com.example.han.myapplication001.FriendActivity;
import com.example.han.myapplication001.Global;
import com.example.han.myapplication001.R;

import java.util.ArrayList;

public class Fragment1 extends ListFragment {
    String titleStr;
    Handler mHandler;
    customlist_chat adapter = new customlist_chat() ;

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
        FriendListClass item = (FriendListClass) l.getItemAtPosition(position) ;
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
    public void adds(ArrayList<FriendListClass> temp) {
        adapter.adds(temp);
        setListAdapter(adapter);
    }
    public void refresh() {
        setListAdapter(adapter);
    }
    public void setmHandler(Handler H1){ mHandler = H1;}
}

