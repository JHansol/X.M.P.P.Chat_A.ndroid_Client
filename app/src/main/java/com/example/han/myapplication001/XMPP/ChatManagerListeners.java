package com.example.han.myapplication001.XMPP;


import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.example.han.myapplication001.BubbleAdapter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

public class ChatManagerListeners extends MessageListener2 implements ChatManagerListener {
  public ChatManagerListeners(ArrayList<BubbleAdapter> a,ListView b){
    super(a);
    super.setListview(b);
  }

//  public void chatCreated(Chat chat, boolean createdLocally) {
//    if ( !createdLocally )  chat.addMessageListener(new MessageListener2(m_Adapter));
//  }
  public void chatCreated(Chat chat, boolean createdLocally) {
    if ( !createdLocally )  chat.addMessageListener(new MessageListener(){
      @Override
      public void processMessage( Chat chat, Message msg )
      {
        Log.i("Hansol Log", "메시지옴 from : " + chat.getParticipant() + " message - " + msg.getBody());
        if(!msg.getBody().isEmpty()) {
          msg2 = msg;
          chat2 = chat;
          names = chat.getParticipant().substring(0,chat.getParticipant().indexOf("/"));
          //names = chat.getParticipant().substring(0,chat.getParticipant().indexOf("@"));
          BubbleAdapter temp = FindName();
          if(temp == null){
            Log.i("Hansol Log", names + " 버블어댑터 생성 후 값 추가");
            temp = new BubbleAdapter(names);
            temp.add(msg2.getBody(), 0);
            m_Adapter.add(temp);

//              mHandler.post(new Runnable() {
//                public void run() {
//                  //if(!(m_ListView == null)) {
//                  //  m_ListView.setAdapter(FindName(names));
//                 // }
//                }
//              });

            }
          else{
            Log.i("Hansol Log", names + " 버블어댑터 값 추가");
            temp.add(msg2.getBody(), 0);// add에 다가 조건문만 넣어서 내가보내는건  1  서버에서 상대가 보내는건 0 공적으로 보내는 시간같은건 2 로 하면됨
          }
        }
      }
    });
  }
}