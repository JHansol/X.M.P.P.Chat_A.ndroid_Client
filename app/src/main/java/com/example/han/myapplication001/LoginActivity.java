package com.example.han.myapplication001;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.StringUtils;

import java.util.Collection;

public class LoginActivity extends AppCompatActivity {

    TextView join;
    Button login;
    EditText id;
    EditText pass;

    Global myApp;

    public static final String HOST = Global.HOST;
    public static final int PORT = Global.PORT;
    public static final String SERVICE = Global.SERVICE;

    String id_st;
    String ps_st;
    XMPPConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Wallpaper_NoTitleBar);
        setContentView(R.layout.login_main);
        myApp = (Global)getApplicationContext();

        join= (TextView)findViewById(R.id.join);
        login = (Button)findViewById(R.id.login);
        id = (EditText)findViewById(R.id.editText);
        pass = (EditText)findViewById(R.id.editText2);

        id.setText("hansol");
        pass.setText("123123");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),JoinActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_st = id.getText().toString();
                ps_st = pass.getText().toString();
                if(!id_st.equals("") && !ps_st.equals("")) {
                    connect(id.getText().toString(), pass.getText().toString());
                }
                else{
                    Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호를 입력하여 주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean login(String apiKey, String accessToken) throws XMPPException {
        if ((connection != null) && (connection.isConnected())) {
            connection.login(apiKey, accessToken);
            return true;
        }
        return false;
    }
    public void disconnect() {
        if ((connection != null) && (connection.isConnected())) {
            Presence presence = new Presence(Presence.Type.unavailable);
            presence.setStatus("offline");
            connection.disconnect(presence);
        }
    }
    public void connect(String a, String b)  {
        id_st = a;
        ps_st = b;
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
                        if (login(id_st, ps_st)) {
                            Log.i("XMPPChatDemoActivity", "성공소리질러");
                        }
                        else {
                            Log.i("XMPPChatDemoActivity", "tttttttttttttttttt실패");
                            throw new XMPPException("No response from the server.");
                        }
                    connection.disconnect();

                    myApp.connect(id_st, ps_st);
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){}
                    Intent MainIntent = new Intent(getApplicationContext(),FriendActivity.class);
                    startActivity(MainIntent);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            // 메시지 큐에 저장될 메시지의 내용
                            Toast.makeText(getApplicationContext(),id_st + "님이 정상 로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (XMPPException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // 메시지 큐에 저장될 메시지의 내용
                            Toast.makeText(getApplicationContext(),"해당 아이디가 없거나 비밀번호가 맞지 않습니다",Toast.LENGTH_SHORT).show();
                        }
                    });
                    disconnect();
                }
                dialog.dismiss();
            }
        });
        t.start();
        dialog.show();
    }

}

