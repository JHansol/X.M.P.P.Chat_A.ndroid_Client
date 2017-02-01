package com.example.han.myapplication001;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.han.myapplication001.XMPP.ChatManagerListeners;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

import com.example.han.myapplication001.XMPP.Fragment1;
import com.example.han.myapplication001.XMPP.FriendListClass;
import com.example.han.myapplication001.chatlist.Fragment_chat;

import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {
    FragmentManager manager;  //Fragment를 관리하는 클래스의 참조변수
    FragmentTransaction tran;  //실제로 Fragment를 추가/삭제/재배치 하는 클래스의 참조변수
    Fragment  frag1, frag2, frag3; //3개의 Fragment 참조변수

//    ListView list;
    Handler mHandler;

    private ArrayList<FriendListClass> Friends = new ArrayList<FriendListClass>();
    CustomList adapter , adapter2;
    String[] titles = {"정한솔","박재하","이가원","123","456","789","123123"};

    Integer a1 = R.drawable.online;
    Integer[] images = {a1,a1,a1,a1,a1,a1,a1};

    Button chat, setting, NfClcik,friendb;
    EditText newfriendet;
    ListView m_ListView;

    String idm;
    Global myApp;

    ChatManagerListeners chat_Listener;
    Fragment1 customListFrgmt;
    Fragment_chat customListFrgmt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(android.R.style.Theme_Light);
        setContentView(R.layout.friend_list);
        customListFrgmt = new Fragment1();
        customListFrgmt2 = new Fragment_chat();

//        adapter = new CustomList(FriendActivity.this);
        myApp = (Global)getApplicationContext();
        myApp.Friend_Context = getApplicationContext();
//        list = (ListView) findViewById(R.id.list);
        m_ListView = (ListView) findViewById(R.id.listview_chat);
        // - 한번 대화를 나눈 상대라면 기존에 대화하던 스레드가 있다면 그것을 이용한다.
        chat_Listener = new ChatManagerListeners(myApp.m_Adapter,m_ListView);
        myApp.connection.getChatManager().addChatListener(chat_Listener);

        if(myApp.IsConnected() == true){
            Friends =  myApp.refreshFriend2();
//            ArrayList<RosterEntry> sm = myApp.refreshFriend();
//            for (RosterEntry entry : sm) {
//                Friends.add(new FriendListClass(entry.getUser(),0));
//            }
            adapter = new CustomList(FriendActivity.this);
            myApp.adapter = adapter;
//            myApp.list = list;
            mHandler = new Handler();
            myApp.mHandler = mHandler;
            customListFrgmt.setmHandler(mHandler);

//            list.setAdapter(adapter);
         }

        //list.setAdapter(adapter);

        chat = (Button)findViewById(R.id.chat);
        friendb = (Button)findViewById(R.id.friend);
        setting = (Button)findViewById(R.id.setting);
        NfClcik = (Button)findViewById(R.id.newfriendbt);
        newfriendet = (EditText) findViewById(R.id.newfriendet) ;

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myApp.IsConnected() == true){
                    customListFrgmt2.addsi();

                    manager= getSupportFragmentManager();
                    tran = manager.beginTransaction();
                    tran.replace(R.id.container2, customListFrgmt2);
                    tran.commit();
                }
            }
        });

        friendb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                customListFrgmt = (Fragment1) getSupportFragmentManager().findFragmentById(R.id.customlistfragment);
//                customListFrgmt2 = (Fragment1) getSupportFragmentManager().findFragmentById(R.id.customlistfragment);
//                args.putString("title", contents[0][0]);
//                args.putString("details", contents[0][1]);
//                fr.setArguments(args) ;

                if(myApp.IsConnected() == true){
//                    Friends.clear();
//
//                    ArrayList<RosterEntry> sm = myApp.refreshFriend();
//                    for (RosterEntry entry : sm) {
//                        Roster roster = myApp.connection.getRoster();
//                        Presence p = roster.getPresence(entry.getUser()+myApp.SERVICE);
//                        if(p.isAvailable()) Friends.add(new FriendListClass(entry.getUser(),1));
//                        else Friends.add(new FriendListClass(entry.getUser(),0));
//                    }
                    Friends =  myApp.refreshFriend2();

                    customListFrgmt.adds(Friends);

                    manager= getSupportFragmentManager();
                    tran = manager.beginTransaction();
                    tran.replace(R.id.container2, customListFrgmt);
                    tran.commit();
//                    customListFrgmt.refresh();
//                    customListFrgmt.adds(Friends);

//                    list.setAdapter(adapter);
                }
            }
        });
        NfClcik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myApp.IsConnected() == false){
                    finish();
                }
                if(newfriendet.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(),"친구 추가할 아이디를 적어주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newfriendet.getText().toString().length() < 6){
                    Toast.makeText(getBaseContext(),"6글자 이상 적어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                int m = myApp.FriendNew(newfriendet.getText().toString());
                if(m == 1) { // 정상 추가
                    Friends =  myApp.refreshFriend2();

//                    Friends.clear();
//                    ArrayList<RosterEntry> sm = myApp.refreshFriend();
//                    for (RosterEntry entry : sm) {
//                        Friends.add(new FriendListClass(entry.getUser(),0));
//                    }
//                    list.setAdapter(adapter);
                    Toast.makeText(getBaseContext(),newfriendet.getText().toString() + "이 친구 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    newfriendet.setText("");
                }
                else if(m == 0){
                    Toast.makeText(getBaseContext(),"이미 추가되어 있는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getBaseContext(),"오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Intent MainIntent = new Intent(getApplicationContext(),ChatActivity.class);
//                MainIntent.putExtra("id",Friends.get(position).names);
//                startActivity(MainIntent);
//                return false;
//            }
//        });
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getBaseContext(),Friends.get(position).names, Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public class CustomList extends ArrayAdapter<FriendListClass> {
        private final Activity context;

        public CustomList(Activity context) {
            super(context, R.layout.friend_list, Friends);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = (View) getLayoutInflater().inflate(R.layout.friend_list_flator, null);
            FriendListClass temp = Friends.get(position);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.Profile_image);
            ImageView imageView2 = (ImageView) rowView.findViewById(R.id.Status_image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView title2 = (TextView) rowView.findViewById(R.id.title2);
            title.setText(temp.names);
            title2.setText(temp.status_name);
            imageView.setImageResource(R.drawable.online);
            if(temp.status == 1) {
                imageView2.setImageResource(R.drawable.online_status);
            }
            else {
                imageView2.setImageResource(R.drawable.offline_status);
            }
            return rowView;
        }
    }

}


//    //Connect to xmpp server when internet comes
//    private void intConnection()
//    {
//        xMPPConfig = new ConnectionConfiguration(HOST,PORT);
//        if (connection == null)
//            connection = new XMPPTCPConnection(xMPPConfig);
//        SASLAuthentication.supportSASLMechanism("PLAIN",0);
//        xMPPConfig.setSecurityMode(SecurityMode.disabled);
//
//        xMPPConfig.setReconnectionAllowed(false);
//        xMPPConfig.setSendPresence(false);
//
//        KeyStore trustStore;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            trustStore = KeyStore.getInstance("AndroidCAStore");
//        } else {
//            trustStore = KeyStore.getInstance("BKS");
//        }
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        trustManagerFactory.init(trustStore);
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//            this.xMPPConfig.setCustomSSLContext(sc);
//        } catch (java.security.GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//        connection.connect();
//        connection.login(Uname, Pass, "Smack");
//        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);;//This is the method get the offline message
//        Log.i(TAG, offlineMessageManager.getMessageCount()); //Here always getting 0
//
//        if (connectionListener!=null)
//            connection.removeConnectionListener(connectionListener);
//        connection.addConnectionListener(connectionListener);     //Here i add connectionListener
//
//        if (packetListener!=null)
//            connection.removePacketListener(packetListener);
//        connection.addPacketListener(packetListener, PacketFilter);
//
//        Presence presence = new Presence(Type.available);
//        presence.setMode(Presence.Mode.available);
//        connection.sendPacket(presence);
//
//        try {
//            DeliveryReceiptManager.getInstanceFor(connection).enableAutoReceipts();
//            DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(ne w ReceiptManager());
//        } catch (Exception e) {
//        }
//
//    }
//
//    //Remove connect when internet off
//    public synchronized void doDisconnect(Context context) {
//        if (connection != null) {
//            if (packetListener != null)
//                connection.removePacketListener(packetListener);
//            if (connectionListener != null)
//                connection.removeConnectionListener(connectionListener);
//            try {
//                connection.disconnect();
//            } catch (NotConnectedException e) {
//                e.printStackTrace();
//            }
//            connection = null;
//            connectionListener = null;
//            packetListener = null;
//        }
//    }
//
//public class OfflineTest {
//
//    public static void main(String[] args) throws Exception{
//        ConnectionConfiguration cc = new ConnectionConfiguration("localhost",5222);
//        cc.setSASLAuthenticationEnabled(false);
//        XMPPConnection con = new XMPPConnection(cc);
//
////        con.addPacketListener(new PacketListener(){
////
////            public void processPacket(Packet pack) {
////                System.out.println("Paket : "+pack.toXML() );
////            }
////        },null);
//
//
//        con.login("jitu","jitu","res",false);
//        OfflineMessageManager omm = new OfflineMessageManager(con);
//        System.err.println(omm.getMessageCount());
////        OfflineMessageRequest omr = new OfflineMessageRequest();
////        con.sendPacket(omr);
//        Iterator itr = omm.getMessages();
//        while(itr.hasNext())
//        {
//            Message m =(Message) itr.next();
//            System.err.println(m.getBody());
//        }
//
//    }
//}
