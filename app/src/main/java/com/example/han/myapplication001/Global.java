package com.example.han.myapplication001;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.han.myapplication001.XMPP.FriendListClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.RemoteRosterEntry;
import org.jivesoftware.smackx.RosterExchangeListener;
import org.jivesoftware.smackx.RosterExchangeManager;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;

public class Global extends Application {
    ArrayList<BubbleAdapter> m_Adapter;

    public static final String HOST = "210.99.254.154";
    public static final int PORT = 5222;
    public static final String SERVICE = "@han-pc";

    public XMPPConnection connection;
    private int state;

    String id_st;
    String ps_st;
    public Context Friend_Context;

    FriendActivity.CustomList adapter;
    ListView list;

    @Override
    public void onCreate() {
        //전역 변수 초기화
        m_Adapter = new ArrayList<BubbleAdapter>();
        state = 0;
        id_st = "";
        ps_st = "";
        super.onCreate();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setState(int state){
        this.state = state;
    }

    public int getState(){
        return state;
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
    public boolean IsConnected(){
        if ((connection != null) && (connection.isConnected())) {
            return true;
        }
        return false;
    }

    Handler mHandler;
    RosterListener Rosters = new RosterListener(){
        public void entriesAdded(Collection<String> addresses) {}
        public void entriesDeleted(Collection<String> addresses) {}
        public void entriesUpdated(Collection<String> addresses) {}
        public void presenceChanged(Presence presence) {
            int count =0;
            String temp_name = presence.getFrom().substring(0,presence.getFrom().indexOf("/"));
            Iterator<FriendListClass> iter = Friends.iterator();
            while(iter.hasNext()){
                FriendListClass item = iter.next();
                count = count + 1;
                if(item.names.equals(temp_name)){
                    iter.remove();
                    break;
                }
            }
            ArrayList<RosterEntry> sm = refreshFriend();
            for (RosterEntry entry : sm) {
                Roster roster = connection.getRoster();
                if(temp_name.equals(entry.getUser())) { // 변경된 presense 이름과 로스터 이름 비교
                    if (presence.isAvailable()) Friends.add((count-1),new FriendListClass(entry.getUser(), 1,presence.getStatus()));
                    else Friends.add((count-1),new FriendListClass(entry.getUser(), 0,""));
                }

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post( new Runnable() {
                            public void run() {
                                list.setAdapter(adapter);
                            }
                        });
                    }
                });t.start();
            }
            count = 0;
            Log.i("HANSOL----------LOG","Presence change detected for:" + presence.getFrom()+ "Type: " + presence.getType() + "Status: " + presence.getStatus());
        }
    };

    public void connect(String a, String b) {
        id_st = a;
        id_st = id_st + "@han-pc";
        ps_st = b;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create a connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT,"han-pc");
                connConfig.setSASLAuthenticationEnabled(true);
                connConfig.setSendPresence(true);
                connection = new XMPPConnection(connConfig);
                try {
                    connection.connect();
                    Log.i("XMPPChatDemoActivity",  "[SettingsDialog] Connected to "+connection.getHost());
                } catch (XMPPException ex) {
                    disconnect();
                    Log.e("XMPPChatDemoActivity",  "[SettingsDialog] Failed to connect to "+ connection.getHost());
                    Log.e("XMPPChatDemoActivity", ex.toString());
                }
                try {
                    if (login(id_st, ps_st)) {
                        Log.i("XMPPChatDemoActivity", "성공소리질러222");
                    }
                    else {
                        Log.i("XMPPChatDemoActivity", "tttttttttttttttttt실패");
                    }
                    Roster roster = connection.getRoster();
                    roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

                    Log.i("XMPPChatDemoActivity",  "Logged in as" + connection.getUser());
                    Presence presence = new Presence(Presence.Type.available);
                    presence.setType(Presence.Type.available);
                    presence.setMode(Presence.Mode.available);
                    presence.setPriority(24);
                    presence.setStatus("online");

                    connection.sendPacket(presence);
                    try{
                       Thread.sleep(100);
                    }catch (Exception e){}
                   roster.addRosterListener(Rosters);

//                    Presence p = new Presence(Presence.Type.available, "I am busy", 42, Presence.Mode.dnd);
//                    connection.sendPacket(p);

//                    OfflineMessageManager omm = new OfflineMessageManager(connection);
//                    DiscoverInfo var1 = ServiceDiscoveryManager.getInstanceFor(connection).discoverInfo((String)null);
//                    if (!omm.supportsFlexibleRetrieval()) {
//                        Log.i("XMPPChatDemoActivity",  "Offline messages not supported");
//                    }
//                    if (omm.getMessageCount() == 0) {
//                        Log.i("XMPPChatDemoActivity",  "No offline messages found on server");
//                    }
//                    OfflineMessageRequest omr = new OfflineMessageRequest();
//                    connection.sendPacket(omr);
//                    Iterator itr = omm.getMessages();
//                    while(itr.hasNext())
//                    {
//                        Message m =(Message) itr.next();
//                        Log.i("zzzzzzzzzzzzzzzz", m.getBody());
//                    }

                } catch (XMPPException e) {
                    disconnect();
                }
            }
        });
        t.start();
    }
    private boolean setPresenceSubscription(String user, boolean subscriptionEnabled) {
        Presence presence;
        if (subscriptionEnabled) {
            presence = new Presence(Presence.Type.subscribe);
        } else {
            presence = new Presence(Presence.Type.unsubscribe);
        }

        presence.setFrom(id_st);
        presence.setTo(user);

        try {
            connection.sendPacket(presence);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public enum ShowMode {
        ONLINE, OFFLINE
    };
    public void Logs(String n){
        Log.i("HANSOL_LOG",n);
    }
    public ArrayList<RosterEntry> filteredEntries;
    protected ShowMode displayMode;
    public boolean presenceCheck(String names){
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            Presence entryPresence = roster.getPresence(entry.getUser());
//            Log.i("XMPPChatDemoActivity",  "--------------------------------------");
//           Log.i("XMPPChatDemoActivity", "RosterEntry " + entryPresence.getType());
//           Log.i("XMPPChatDemoActivity", "User: " + entryPresence.getStatus());
//            Log.i("XMPPChatDemoActivity", "Name: " + entry.getName());
//            Log.i("XMPPChatDemoActivity", "Status: " + entry.getStatus());
//            Log.i("XMPPChatDemoActivity", "Type: " + entry.getType());
            if(entryPresence.isAvailable()) {
                if (entry.getUser().equals(names)) {
                    Log.i("hs log", "User: " + entry.getUser() + "비교 : " + names + "  비교 " + entryPresence.getType() + entryPresence.getStatus());
                        return true;
                }
            }
        }
        return false;
    }

    public void refreshFriends(ShowMode sm) {
        this.displayMode = sm;
        this.filteredEntries = new ArrayList<RosterEntry>();
        this.filteredEntries.clear();
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
//            Log.i("XMPPChatDemoActivity",  "--------------------------------------");
//            Log.i("XMPPChatDemoActivity", "RosterEntry " + entry);
//            Log.i("XMPPChatDemoActivity", "User: " + entry.getUser());
//            Log.i("XMPPChatDemoActivity", "Name: " + entry.getName());
//            Log.i("XMPPChatDemoActivity", "Status: " + entry.getStatus());
//            Log.i("XMPPChatDemoActivity", "Type: " + entry.getType());
            this.filteredEntries.add(entry);

//            Presence p = roster.getPresence(entry.getUser());
//
//            if ((this.displayMode == ShowMode.ONLINE) && (p.isAvailable())) {
//                this.filteredEntries.add(entry);
//            }
//            if ((this.displayMode == ShowMode.OFFLINE) && (!p.isAvailable())) {
////                Log.i("HAAAAAAAAAAAAAAAN", "Type: " + entry.getUser());
//                this.filteredEntries.add(entry);
//            }
        }
    }
    public ArrayList<RosterEntry> refreshFriend(){
        refreshFriends(ShowMode.OFFLINE);
        return filteredEntries;
    }
    ArrayList<FriendListClass> Friends = new ArrayList<FriendListClass>();

    String Temp_names;
    public ArrayList<FriendListClass> refreshFriend2(){
        Friends.clear();
        ArrayList<RosterEntry> sm = refreshFriend();
        for (RosterEntry entry : sm) {
            Roster roster = connection.getRoster();
            Presence p = roster.getPresence(entry.getUser());
                if(entry.getUser().indexOf("@") == -1)
                    Temp_names = entry.getUser();
                else
                    Temp_names = entry.getUser().substring(0,entry.getUser().indexOf("@"));

            if(p.isAvailable()) Friends.add(new FriendListClass(entry.getUser(),1,p.getStatus()));
            else Friends.add(new FriendListClass(entry.getUser(),0,""));
        }
        return Friends;
    }
    public int FriendNew(String name){
        Roster roster = connection.getRoster();
        RosterEntry rosterEntry = roster.getEntry(name);
        name= name+SERVICE;
        if (rosterEntry == null) {
            try {
                RosterPacket.Item item = new RosterPacket.Item(name, name + "님"); // 뒤에는 닉네임
                item.setItemType(RosterPacket.ItemType.both);
                item.addGroupName("Major"); // 그룹 몌이저로
                item.setItemStatus(RosterPacket.ItemStatus.SUBSCRIPTION_PENDING);
                RosterPacket rosterPacket = new RosterPacket();
                rosterPacket.setType(IQ.Type.SET);
                rosterPacket.addRosterItem(item);

                PacketCollector var10 = this.connection.createPacketCollector(new PacketIDFilter(rosterPacket.getPacketID()));
                connection.sendPacket(rosterPacket);

                IQ var11 = (IQ)var10.nextResult((long) SmackConfiguration.getPacketReplyTimeout());
                var10.cancel();

                if(var11 == null) {
                    throw new XMPPException("No response from the server.");
                } else if(var11.getType() == IQ.Type.ERROR) {
                    throw new XMPPException(var11.getError());
                } else {
                    Presence var12 = new Presence(Presence.Type.subscribe);
                    var12.setTo(name);
                    var12.setFrom(name);
                    this.connection.sendPacket(var12); // 구독하고 있다고 업데이트 패킷 전송
                }
                return 1;
            }
            catch (XMPPException e) {
                return 2;
            }
        }
        else{
            return 0;
        }

    }
}

/* 친구추가
public static void admitFriendsRequest() {
         connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet paramPacket) {
                System.out.println("\n\n");
                if (paramPacket instanceof Presence) {
                    Presence presence = (Presence) paramPacket;
                    String email = presence.getFrom();
                    System.out.println("chat invite status changed by user: : "
                            + email + " calling listner");
                    System.out.println("presence: " + presence.getFrom()
                            + "; type: " + presence.getType() + "; to: "
                            + presence.getTo() + "; " + presence.toXML());
                    Roster roster = connection.getRoster();
                    for (RosterEntry rosterEntry : roster.getEntries()) {
                        System.out.println("jid: " + rosterEntry.getUser()
                                + "; type: " + rosterEntry.getType()
                                + "; status: " + rosterEntry.getStatus());
                    }
                    System.out.println("\n\n\n");
                    if (presence.getType().equals(Presence.Type.subscribe)) {
                        Presence newp = new Presence(Presence.Type.subscribed);
                        newp.setMode(Presence.Mode.available);
                        newp.setPriority(24);
                        newp.setTo(presence.getFrom());
                        connection.sendPacket(newp);
                        Presence subscription = new Presence(
                                Presence.Type.subscribe);
                        subscription.setTo(presence.getFrom());
                        connection.sendPacket(subscription);

                    } else if (presence.getType().equals(
                            Presence.Type.unsubscribe)) {
                        Presence newp = new Presence(Presence.Type.unsubscribed);
                        newp.setMode(Presence.Mode.available);
                        newp.setPriority(24);
                        newp.setTo(presence.getFrom());
                        connection.sendPacket(newp);
                    }
                }

            }
        }, new PacketFilter() {
            public boolean accept(Packet packet) {
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    if (presence.getType().equals(Presence.Type.subscribed)
                            || presence.getType().equals(
                                    Presence.Type.subscribe)
                            || presence.getType().equals(
                                    Presence.Type.unsubscribed)
                            || presence.getType().equals(
                                    Presence.Type.unsubscribe)) {
                        return true;
                    }
                }
                return false;
            }
        });

        connection.getRoster().addRosterListener(new RosterListener() {
            public void presenceChanged(Presence presence) {
                System.out.println(presence.getFrom() + "presenceChanged");

            }

            public void entriesUpdated(Collection<String> presence) {
                System.out.println("entriesUpdated");

            }

            public void entriesDeleted(Collection<String> presence) {
                System.out.println("entriesDeleted");

            }

            public void entriesAdded(Collection<String> presence) {
                System.out.println("entriesAdded");
            }
        });
    }
 */