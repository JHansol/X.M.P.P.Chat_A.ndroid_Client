package com.example.han.myapplication001;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.han.myapplication001.XMPP.ChatManagerListeners;
import com.example.han.myapplication001.XMPP.MessageListener2;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

// 채팅방 소스입니다. AppCompatActivity Activity
public class ChatActivity extends Activity {

    ListView m_ListView;
    BubbleAdapter  m_Adapter;
    EditText message;
    Button send ;

    String id;
    Global myApp;

    XMPPConnection connection;

    private Handler mHandler = new Handler();
    ChatManagerListeners chat_Listener;

    @Override
    protected void onDestroy() {
        Log.i("hansol LOG", "패킷 리스너 정상 삭제" + this);
        connection.removePacketListener(PL);
        finish();
        super.onDestroy();
    }

    PacketListener PL = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            Message message = (Message) packet;
            if (message.getBody() != null) {
                mHandler.post(new Runnable() {
                    public void run() {
                        Log.i("hansol LOG", "수신받았습니다. 어댑터 갱신!!!" + this);
                        setListAdapter();
                    }
                });
            }
        }
    };

    public void setConnection(XMPPConnection connection) {
        //this.connection = connection;
        if (connection != null) {
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(PL, filter);
        }
    }
    private void setListAdapter() {
        m_ListView.setAdapter(m_Adapter);
        m_ListView.setSelection(m_Adapter.getCount() - 1);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_message);

        myApp = (Global)getApplicationContext();
        Intent log_i = getIntent();
        id = log_i.getExtras().getString("id");

        m_ListView = (ListView) findViewById(R.id.listview_chat);
        chat_Listener = new ChatManagerListeners(myApp.m_Adapter,m_ListView);

        send = (Button)findViewById(R.id.send_bt);
        message = (EditText)findViewById(R.id.message_edittext);
        m_Adapter = chat_Listener.FindName(id);
        if(m_Adapter == null){
            m_Adapter  = new BubbleAdapter(id);
            myApp.m_Adapter.add(m_Adapter);
        }

        m_ListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        m_ListView.setAdapter(m_Adapter);
        m_ListView.setSelection(m_Adapter.getCount() - 1);


        connection = myApp.connection;
        setConnection(connection);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String to = id;
                String text = message.getText().toString();
                if(text.isEmpty()) return;
                message.setText("");

                Message msg = new Message(to, Message.Type.chat);
                msg.setBody(text);

                ChatManager chatmanager = connection.getChatManager(); //- 대화 ( Chat 객체는 두 사용자간에 새로운 스레드를 만들게 된다. )
                Chat newChat = chatmanager.createChat(to, new MessageListener2(myApp.m_Adapter));
//                Chat newChat = chatmanager.createChat(to, new MessageListener() {
//                    public void processMessage(Chat chat, Message message) {// chat : 대화상대객체 , message : 수신메시지객체
//                        Log.i("HSLOG", "Got XMPP message from chat " + chat.getParticipant() + " message - " + message.getBody());
//                    }
//                });
                if(myApp.presenceCheck(id)) {
                    try {
                        Log.i("HSLOG", "asd" + newChat.getParticipant());
                        newChat.sendMessage(text);

                        m_Adapter.add(text, 1);
                        m_ListView.setAdapter(m_Adapter);
                        m_ListView.setSelection(m_Adapter.getCount() - 1);
                    } catch (XMPPException e) {// 예외처리
                    }
                }else{
                    m_Adapter.add(text, 1);
                    m_Adapter.add("현재 상대방이 접속중이 아닙니다.", 2);
                    m_ListView.setAdapter(m_Adapter);
                    m_ListView.setSelection(m_Adapter.getCount() - 1);
                    Log.i("HSLOG", "현재 상대방이 접속중이 아닙니다.");
                }
            }
        });
    }

}

