package com.example.han.myapplication001;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;

public class JoinActivity extends AppCompatActivity {

    TextView join;
    Button next;
        Button cancel;
    EditText id;
    EditText pass;
    EditText nick;
    EditText emails;

    public static final String HOST = Global.HOST;
    public static final int PORT = Global.PORT;
    public static final String SERVICE = Global.SERVICE;

    String id_st;
    String ps_st;
        String nick_st;
        String email_st;
    XMPPConnection connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Wallpaper_NoTitleBar);
        setContentView(R.layout.join);

        id= (EditText)findViewById(R.id.editText3);
        pass = (EditText)findViewById(R.id.editText4);
        nick = (EditText)findViewById(R.id.editText5);
        emails = (EditText)findViewById(R.id.editText6);
        next = (Button)findViewById(R.id.join_next);
        cancel = (Button)findViewById(R.id.join_cancel);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_st = id.getText().toString();
                ps_st = pass.getText().toString();
                nick_st = nick.getText().toString();
                email_st = emails.getText().toString();
                if(id_st.length() <= 5){
                    Toast.makeText(getApplicationContext(),"아이디는 6글자 이상이어야 합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ps_st.length() <= 5){
                    Toast.makeText(getApplicationContext(),"비밀번호는 6글자 이상이어야 합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(nick_st.length() < 2){
                    Toast.makeText(getApplicationContext(),"닉네임은 2글자 이상이어야 합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(email_st.contains("@")) || !(email_st.contains("."))){
                    Toast.makeText(getApplicationContext(),"이메일주소를 정확히 입력하여 주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!id_st.equals("") && !ps_st.equals("") && !nick_st.equals("") && !email_st.equals("")) {
                    connect();
                }
                else{
                    Toast.makeText(getApplicationContext(),"모두 입력하여 주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Intent MainIntent = new Intent(getApplicationContext(),LoginActivity.class);
                //startActivity(MainIntent);
            }
        });
    }

    public void disconnect() {
        if ((connection != null) && (connection.isConnected())) {
            Presence presence = new Presence(Presence.Type.unavailable);
            presence.setStatus("offline");
            connection.disconnect(presence);
        }
    }
    private IQ requestBlocking(IQ request) {
        PacketCollector collector = connection.createPacketCollector(new PacketIDFilter(request.getPacketID()));
        connection.sendPacket(request);
        IQ response = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        return response;
    }
    public void setConnection(XMPPConnection connection) throws XMPPException {
        this.connection = connection;
        if (connection != null) {
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i("XMPPChatDemoActivity ", " Text Recieved " + message.getBody() + " from " +  fromName);
                    }
                }
            }, filter);
        }
    }

    public void connect() {
        final ProgressDialog dialog = ProgressDialog.show(this, "Connecting...", "Please wait...", false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create a connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
                connection = new XMPPConnection(connConfig);
                try {
                    connection.connect();
                    Log.i("XMPPChatDemoActivity",  "[SettingsDialog] Connected to "+connection.getHost());
                } catch (XMPPException ex) {
                    Log.e("XMPPChatDemoActivity",  "[SettingsDialog] Failed to connect to "+ connection.getHost());
                    Log.e("XMPPChatDemoActivity", ex.toString());
                }
                try {
                    setConnection(connection);
                    Registration query = new Registration();
                    query.addAttribute("username", id_st);
                    query.addAttribute("password", ps_st);
                    query.addAttribute("email", email_st);
                    query.addAttribute("name", nick_st);
                    query.setType(IQ.Type.SET);
                    Packet reply = requestBlocking(query);


                    if (reply != null && (reply instanceof IQ)) {
                        IQ result = (IQ) reply;
                        if (result.getType().equals(IQ.Type.RESULT)) {
                            Log.i("HAAAAAAAAAAAAAAAN", "게정생성 성공");
                            finish();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    // 메시지 큐에 저장될 메시지의 내용
                                    Toast.makeText(getApplicationContext(),"이미 존재하는 아이디 입니다.",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    disconnect();

                } catch (XMPPException e) {
                    disconnect();
                }
                dialog.dismiss();
            }
        });
        t.start();
        dialog.show();
    }
}
