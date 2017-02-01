package com.example.han.myapplication001.XMPP;

import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.example.han.myapplication001.BubbleAdapter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by han on 2016-08-13.
 */
public class MessageListener2 implements MessageListener {
    Message msg2;
    Chat chat2;
    String names;
    ArrayList<BubbleAdapter> m_Adapter;
    ListView m_ListView;
    Handler mHandler = new Handler();
    public MessageListener2(ArrayList<BubbleAdapter> a){
        m_Adapter = a;
        m_ListView = null;
    }
    public void setListview(ListView m_ListView2){
        m_ListView = m_ListView2;
    }

    public BubbleAdapter FindName(){
        Iterator<BubbleAdapter> its = m_Adapter.iterator();
        while (its.hasNext()){
            BubbleAdapter temp = its.next();
            if(temp.name.equals(names)) { // 해당 이름의 어댑터가 있으면 true
                return temp;
            }
        }
        return null; //해당이름 어댑터가 없으면 null
    }
    public BubbleAdapter FindName(String Input_name){
        Iterator<BubbleAdapter> its = m_Adapter.iterator();
        while (its.hasNext()){
            BubbleAdapter temp = its.next();
            if(temp.name.equals(Input_name)) { // 해당 이름의 어댑터가 있으면 true
                return temp;
            }
        }
        return null; //해당이름 어댑터가 없으면 null
    }
    @Override
    public void processMessage(Chat chat, Message msg )
    {
        Log.i("Hansol Log", "메시지옴 from : " + chat.getParticipant() + " message - " + msg.getBody());
        if(!msg.getBody().isEmpty()) {
            msg2 = msg;
            chat2 = chat;
            names = chat.getParticipant().substring(0,chat.getParticipant().indexOf("/"));
//            names = chat.getParticipant().substring(0,chat.getParticipant().indexOf("@"));
            BubbleAdapter temp = FindName();
            if(temp == null){
                Log.i("Hansol Log", names + " 버블어댑터 생성 후 값 추가");
                temp = new BubbleAdapter(names);
                temp.add(msg2.getBody(), 0);
                m_Adapter.add(temp);

//                mHandler.post(new Runnable() {
//                    public void run() {
//                        if(!(m_ListView == null)) {
//                            m_ListView.setAdapter(m_Adapter.get(0));
//                        }
//                    }
//                });

            }
            else{
                Log.i("Hansol Log", names + " 버블어댑터 값 추가");
                temp.add(msg2.getBody(), 0);// add에 다가 조건문만 넣어서 내가보내는건  1  서버에서 상대가 보내는건 0 공적으로 보내는 시간같은건 2 로 하면됨
            }
        }
    }
}
