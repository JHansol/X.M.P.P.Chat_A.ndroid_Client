package com.example.han.myapplication001;

        import java.util.ArrayList;
        import java.util.Collection;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Map;

        import org.jivesoftware.smack.Chat;
        import org.jivesoftware.smack.ChatManager;
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

        import android.app.Activity;
        import android.app.ProgressDialog;
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

// http://devroid.com/80130736119
//XMPPChatDemoActivity
public class MainActivity extends Activity {
    Global myApp;

//    public static final String HOST = "119.207.193.184";
//    public static final int PORT = 5222;
//    public static final String SERVICE = "han";
//    public static final String USERNAME = "hansol";
//    public static final String PASSWORD = "123123";

    private ArrayList<String> messages = new ArrayList<String>();
    private Handler mHandler = new Handler();

    private EditText recipient;
    private EditText textMessage;
    public ListView listview;
    public ScrollView sv;
    String id, pass;

    XMPPConnection connection;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myApp = (Global)getApplicationContext();
        connection = myApp.connection;

        Intent log_i = getIntent();
        id = log_i.getExtras().getString("id");

        sv = (ScrollView)findViewById(R.id.scrollView1);
        recipient = (EditText) this.findViewById(R.id.toET);
        textMessage = (EditText) this.findViewById(R.id.chatET);
        listview = (ListView) this.findViewById(R.id.listMessages);

        recipient.setText(id);
        setListAdapter();

        // Set a listener to send a chat text message
        Button send = (Button) this.findViewById(R.id.sendBtn);
        setConnection(connection);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //String to = recipient.getText().toString();
                String to = id + "@han-pc";
                String text = textMessage.getText().toString();
                //Log.i("XMPPChatDemoActivity ", "Sending text " + text + " to " + to);
                Message msg = new Message(to, Message.Type.chat);
                msg.setBody(text);

                ChatManager chatmanager = connection.getChatManager();
                Chat newChat = chatmanager.createChat(to, new MessageListener() {
                    public void processMessage(Chat chat, Message message) {// chat : 대화상대객체 , message : 수신메시지객체
                        Log.i("HSLOG","Got XMPP message from chat " + chat.getParticipant() + " message - " + message.getBody());
                    }
                } );
                try {
                    newChat.sendMessage(text);
                    //messages.add(connection.getUser() + ":" + text);
                    messages.add("ME : " + text);
                    setListAdapter();
                } catch( XMPPException e ) {// 예외처리
                }

//                if (connection != null) {
////                    connection.sendPacket(msg);
////                    messages.add(connection.getUser() + ":");
////                    messages.add(text);
////                    setListAdapter();
//                }

//                Roster roster = connection.getRoster();
//                Collection<RosterEntry> entries = roster.getEntries();
//                for (RosterEntry entry : entries) {
//                    Log.i("XMPPChatDemoActivity",  "--------------------------------------");
//                    Log.i("XMPPChatDemoActivity", "RosterEntry " + entry);
//                    Log.i("XMPPChatDemoActivity", "User: " + entry.getUser());
//                    Log.i("XMPPChatDemoActivity", "Name: " + entry.getName());
//                    Log.i("XMPPChatDemoActivity", "Status: " + entry.getStatus());
//                    Log.i("XMPPChatDemoActivity", "Type: " + entry.getType());
//                    Presence entryPresence = roster.getPresence(entry.getUser());
//                }
//                refreshFriends(ShowMode.OFFLINE);
//                //Log.i("HAAAAAAAAAAAAAAAN", "Type: " + filteredEntries.get(0).getUser());
//
//                RosterEntry rosterEntry = roster.getEntry("hyewon@han-pc");
//                if (rosterEntry == null) {
//                    // Create and send roster entry creation packet.
//                    RosterPacket.Item item = new RosterPacket.Item("hyewon@han-pc", "닉넴ㅋㅋ");
//                    item.addGroupName("Major");
//                    RosterPacket rosterPacket = new RosterPacket();
//                    rosterPacket.setType(IQ.Type.SET);
//                    rosterPacket.addRosterItem(item);
//                   // connection.sendPacket(rosterPacket);
//                }
            }
        });
    }

    private IQ requestBlocking(IQ request) {
        PacketCollector collector = connection.createPacketCollector(new PacketIDFilter(request.getPacketID()));
        connection.sendPacket(request);
        IQ response = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
        collector.cancel();
        return response;
    }

    public enum ShowMode {
        ONLINE, OFFLINE
    };
    public ArrayList<RosterEntry> filteredEntries;
    protected ShowMode displayMode;

    public void refreshFriends(ShowMode sm) {
        this.displayMode = sm;
        this.filteredEntries = new ArrayList<RosterEntry>();
        this.filteredEntries.clear();
        Roster roster = connection.getRoster();
        Iterator<RosterEntry> it = roster.getEntries().iterator();
        while (it.hasNext()) {
            RosterEntry friend = it.next();
            Presence p = roster.getPresence(friend.getUser());
            if ((this.displayMode == ShowMode.ONLINE) && (p.isAvailable())) {
                this.filteredEntries.add(friend);
            }
            if ((this.displayMode == ShowMode.OFFLINE) && (!p.isAvailable())) {
                this.filteredEntries.add(friend);
            }
        }
    }

    public RosterEntry getElementAt(int index) {
        return this.filteredEntries.get(index);
    }

    public int getSize() {
        return this.filteredEntries.size();
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
        if (connection != null) {
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        int m = fromName.indexOf("@");
                        fromName = fromName.substring(0,m);
                        Log.i("XMPPChatDemoActivity ", " Text Recieved " + message.getBody() + " from " +  fromName);
                        messages.add(fromName + "님 : " + message.getBody());
                        // messages.add(message.getBody());
                        // Add the incoming message to the list view
                        mHandler.post(new Runnable() {
                            public void run() {
                                setListAdapter();
                            }
                        });
                    }
                }
            }, filter);
        }
    }

    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
        listview.setAdapter(adapter);
        sv.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            connection.disconnect();
        } catch (Exception e) {

        }
    }
    /** Are we using compression ? **/
    private boolean enableCompression = false;
    /** Are we using attempting to reconnect ? **/
    private boolean enableReconnect = true;
    private RosterListener rosterlistener =
            new RosterListener(){

                @Override
                public void entriesAdded(Collection<String> arg0) {
                    Log.e("XMPPChatDemoActivity",  "[entriesAdded"+ connection.getHost());
                    // TODO Auto-generated method stub

                }

                @Override
                public void entriesDeleted(Collection<String> arg0) {
                    Log.e("XMPPChatDemoActivity",  "entriesDeleted "+ connection.getHost());
                    // TODO Auto-generated method stub

                }

                @Override
                public void entriesUpdated(Collection<String> arg0) {
                    Log.e("XMPPChatDemoActivity",  "entriesUpdated "+ connection.getHost());
                    // TODO Auto-generated method stub

                }

                @Override
                public void presenceChanged(Presence p) {
                }

            };

//    public void connect() {
//
//        final ProgressDialog dialog = ProgressDialog.show(this, "Connecting...", "Please wait...", false);
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // Create a connection
//                ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
//                connConfig.setCompressionEnabled(enableCompression);
//                connConfig.setReconnectionAllowed(enableReconnect);
//                XMPPConnection connection = new XMPPConnection(connConfig);
//                try {
//                    connection.connect();
//                    Log.i("XMPPChatDemoActivity",  "[SettingsDialog] Connected to "+connection.getHost());
//                } catch (XMPPException ex) {
//                    Log.e("XMPPChatDemoActivity",  "[SettingsDialog] Failed to connect to "+ connection.getHost());
//                    Log.e("XMPPChatDemoActivity", ex.toString());
//                    setConnection(null);
//                }
//                try {
//                    setConnection(connection);
//                    Registration query = new Registration();
//                    query.addAttribute("username", "sole");
//                    query.addAttribute("password", "1234");
//                    query.addAttribute("email", "mail@naver.com");
//                    query.addAttribute("name", "test1");
//                    query.setType(IQ.Type.SET);
////                    connection.sendPacket(query);
//                    Packet reply = requestBlocking(query);
//
//
//                    if (reply != null && (reply instanceof IQ)) {
//                        IQ result = (IQ) reply;
//                        if (result.getType().equals(IQ.Type.RESULT)) {
//                            Log.i("HAAAAAAAAAAAAAAAN", "게정생성 성공");
//                        }
//                    }
//
//                    connection.login(id, pass);
////                    connection.login(USERNAME, PASSWORD);
//                    Log.i("XMPPChatDemoActivity",  "Logged in as" + connection.getUser());
//
//                    // Set the status to available
//                    Presence presence = new Presence(Presence.Type.available);
//                    connection.sendPacket(presence);
////                    setConnection(connection);
//
//                    Roster roster = connection.getRoster();
//                    //roster.addRosterListener(rosterlistener);
//                    roster.reload();
//                    Log.i("XMPPChatDemoActivity",  "비비" + roster.getEntryCount());
//                    Collection<RosterEntry> entries = roster.getEntries();
//                    if(entries.isEmpty()){
//                        Log.i("XMPPChatDemoActivity",  "비엇당게요ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ");
//                    }
//                    for (RosterEntry entry : entries) {
//
//                        Log.i("XMPPChatDemoActivity",  "--------------------------------------");
//                        Log.i("XMPPChatDemoActivity", "RosterEntry " + entry);
//                        Log.i("XMPPChatDemoActivity", "User: " + entry.getUser());
//                        Log.i("XMPPChatDemoActivity", "Name: " + entry.getName());
//                        Log.i("XMPPChatDemoActivity", "Status: " + entry.getStatus());
//                        Log.i("XMPPChatDemoActivity", "Type: " + entry.getType());
//                        Presence entryPresence = roster.getPresence(entry.getUser());
//
//                        Log.d("XMPPChatDemoActivity", "Presence Status: "+ entryPresence.getStatus());
//                        Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());
//
//                        Presence.Type type = entryPresence.getType();
//                        if (type == Presence.Type.available)
//                            Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
//                        Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);
//                    }
//                } catch (XMPPException ex) {
//                    Log.e("XMPPChatDemoActivity", "Failed to log in as "+  USERNAME);
//                    Log.e("XMPPChatDemoActivity", ex.toString());
//                    setConnection(null);
//                }
//                dialog.dismiss();
//            }
//        });
//        t.start();
//        dialog.show();
//    }
}

/*
public class FacebookChat {

    public static final String FB_XMPP_HOST = "chat.facebook.com";
    public static final int FB_XMPP_PORT = 5222;
    private NotificationManager mNotimanager;
    private ConnectionConfiguration config;
    private XMPPConnection connection;
    private BidiMap friends = new DualHashBidiMap();
    private FBMessageListener fbml;
    private static FacebookChat instance;
    private static Context mCtx;
    */
/**
     * Facebook Chat Instance 생성
     * @return
     *//*

    public static FacebookChat getInstance(Context ctx) {
        // TODO Auto-generated constructor stub
        mCtx = ctx;
        if (instance == null) {
            return new FacebookChat();
        }
        return instance;
    }

    public void setNotimanager(NotificationManager nm){
        this.mNotimanager = nm;
    }

    */
/**
     * @return
     * @throws XMPPException
     *//*

    public String connect() throws XMPPException {
        config = new ConnectionConfiguration(FB_XMPP_HOST, FB_XMPP_PORT);
        SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM", SASLXFacebookPlatformMechanism.class);
        SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);
        config.setSASLAuthenticationEnabled(true);
        config.setRosterLoadedAtLogin(true);
        config.setDebuggerEnabled(false);
        connection = new XMPPConnection(config);
        connection.connect();
        fbml = new FBMessageListener(connection);
        return connection.getConnectionID();
    }

    */
/**
     * Connection 종료
     *//*

    public void disconnect() {
        if ((connection != null) && (connection.isConnected())) {
            Presence presence = new Presence(Presence.Type.unavailable);
            presence.setStatus("offline");
            connection.disconnect(presence);
        }
    }

    */
/**
     * Chat server Login
     * @param apiKey
     * @param accessToken
     * @return
     * @throws XMPPException
     *//*

    public boolean login(String apiKey, String accessToken) throws XMPPException {
        if ((connection != null) && (connection.isConnected())) {
            connection.login(apiKey, accessToken);
            return true;
        }
        return false;
    }

    */
/**
     * 연결여부
     * @return
     *//*

    public boolean isConnected(){
        if (connection == null || !connection.isConnected()) {
            return false;
        }
        return true;
    }
    */
/**
     * 친구목록 조회
     *//*

    public void getFriends(Presence.Type type) {
        if ((connection != null) && (connection.isConnected())) {
            Roster roster = connection.getRoster();
            int i = 1;
            for (RosterEntry entry : roster.getEntries()) {
                Presence presence = roster.getPresence(entry.getUser());
                if ((presence != null) && (presence.getType() != type)) {
                    friends.put("#" + i, entry);
                    i++;
                }
            }
            fbml.setFriends(friends);
        }
    }

    */
/**
     * userName으로 대상 찾기
     * @param userName
     * @return
     *//*

    public RosterEntry getFriend(String userName){
        if (connection != null && connection.isConnected()) {
            Roster rost = connection.getRoster();
            Collection<RosterEntry> entries = rost.getEntries();
            for (RosterEntry rosterEntry : entries) {
                if (rosterEntry.getUser().equals(userName)) {
                    return rosterEntry;
                }
            }
        }
        return null;
    }

    */
/**
     * userName으로 메세지 보내기
     * @param userName
     * @param message
     * @throws XMPPException
     * @throws IOException
     * @throws FacebookChatException
     *//*

    public void sendMessage(String userName,String message) throws XMPPException, IOException, FacebookChatException {
        sendMessage(getFriend(userName), message);
    }

    */
/**
     * Friend rosterentry로 text메세지 전송
     * @param friend
     * @param text
     * @throws XMPPException
     * @throws FacebookChatException
     *//*

    public void sendMessage(final RosterEntry friend, String text) throws XMPPException, FacebookChatException {
        if (friend == null) {
            return;
        }

        ChatManager chatmanager = conn.getChatManager();
        Chat newChat = chatmanager.createChat("대화상대ID", new MessageListener() {
            public void processMessage(Chat chat, Message message) {
// chat : 대화상대객체 , message : 수신메시지객체
            }
        } );
        try {
            newChat.sendMessage("보내는 메시지");
        } catch( XMPPException e ) {
// 예외처리
        }


        if ((connection != null) && (connection.isConnected())) {
            ChatManager chatManager = connection.getChatManager();
            Chat chat = chatManager.createChat(friend.getUser(), fbml);
            chat.sendMessage(text);
        }else {
            try {
                if (mNotimanager != null) {
                    Notification noti = new Notification(R.drawable.facebook_icon, "페이스북 연결 에러", System.currentTimeMillis());
                    mNotimanager.notify(R.string.csr_facebook_error, noti);
                }

                connect();
                sendMessage(friend, text);//재시도
            } catch (Exception e) {
                // TODO: handle exception

            }
            FacebookChatException exception = new FacebookChatException(ERRORTYPE.LOGINERR, mCtx.getString(R.string.face_except_connect));
            throw exception;
        }
    }

    */
/**
     * Connect & Login
     * @throws FacebookChatException
     *//*

    public void connectAndLogin(String apiKey,String accessToken) throws FacebookChatException {
        try {
            connect();
            if (!login(apiKey, accessToken)) {
                FacebookChatException exception = new FacebookChatException(ERRORTYPE.LOGINERR, mCtx.getString(R.string.face_except_login));
                throw exception;
            }
        } catch (XMPPException e) {
            if (e.getXMPPError() != null) {
                Log.e("1", "ERROR-CODE : " + e.getXMPPError().getCode());
                Log.e("1", "ERROR-CONDITION : " + e.getXMPPError().getCondition());
                Log.e("1", "ERROR-MESSAGE : " + e.getXMPPError().getMessage());
                Log.e("1", "ERROR-TYPE : " + e.getXMPPError().getType());
            }
            disconnect();
        }
    }

    public static enum ERRORTYPE{
        LOGINERR,MESSAGESENDERROR,CONN_ERR
    }
}
    view rawFacebookChat.java hosted with ❤ by GitHub
public class FacebookChatException extends Exception{
    ERRORTYPE types;
    String errorMessage;
    public FacebookChatException(ERRORTYPE errorType,String message) {
        // TODO Auto-generated constructor stub
        types = errorType;
        errorMessage = message;
    }
    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return errorMessage;
    }
}
    view rawFacebookChatException.java hosted with ❤ by GitHub
public class SASLXFacebookPlatformMechanism extends SASLMechanism
{

    private static final String NAME              = "X-FACEBOOK-PLATFORM";

    private String              apiKey            = "";
    private String              accessToken = "";
    private String              sessionKey        = "";

    */
/**
     * Constructor.
     *//*

    public SASLXFacebookPlatformMechanism(SASLAuthentication saslAuthentication)
    {
        super(saslAuthentication);
    }

    @Override
    protected void authenticate() throws IOException, XMPPException
    {

        getSASLAuthentication().send(new AuthMechanism(NAME, ""));
    }

    @Override
    public void authenticate(String  apiKey, String host, String accessToken) throws IOException, XMPPException {
        // TODO Auto-generated method stub
        if (apiKey == null || accessToken == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        this.apiKey = apiKey;
        this.accessToken = accessToken;
        this.hostname = host;

        String[] mechanisms = { "DIGEST-MD5" };
        Map<String, String> props = new HashMap<String, String>();
        this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, this);
        authenticate();
    }

    @Override
    public void authenticate(String username, String host, CallbackHandler cbh)
            throws IOException, XMPPException
    {
        String[] mechanisms = { "DIGEST-MD5" };
        Map<String, String> props = new HashMap<String, String>();
        this.sc =
                Sasl.createSaslClient(mechanisms, null, "xmpp", host, props,
                        cbh);
        authenticate();
    }

    @Override
    protected String getName()
    {
        return NAME;
    }

    @Override
    public void challengeReceived(String challenge) throws IOException
    {
        byte[] response = null;

        if (challenge != null) {
            String decodedChallenge = new String(Base64.decode(challenge));
            Map<String, String> parameters = getQueryMap(decodedChallenge);

            String version = "1.0";
            String nonce = parameters.get("nonce");
            String method = parameters.get("method");

            String composedResponse =
                    "method=" + URLEncoder.encode(method, "utf-8") +
                            "&nonce=" + URLEncoder.encode(nonce, "utf-8") +
                            "&access_token=" + URLEncoder.encode(accessToken, "utf-8") +
                            "&api_key=" + URLEncoder.encode(apiKey, "utf-8") +
                            "&call_id=0" +
                            "&v=" + URLEncoder.encode(version, "utf-8");
            response = composedResponse.getBytes();
        }

        String authenticationText = "";

        if (response != null) {
            authenticationText = Base64.encodeBytes(response);
        }

        // Send the authentication to the server
        getSASLAuthentication().send(new Response(authenticationText));
    }

    private Map<String, String> getQueryMap(String query)
    {
        Map<String, String> map = new HashMap<String, String>();
        String[] params = query.split("\\&");

        for (String param : params)
        {
            String[] fields = param.split("=", 2);
            map.put(fields[0], (fields.length > 1 ? fields[1] : null));
        }

        return map;
    }

    private String md5(String text) throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes("utf-8"), 0, text.length());
        return convertToHex(md.digest());
    }

    private String convertToHex(byte[] data)
    {
        StringBuilder buf = new StringBuilder();
        int len = data.length;

        for (int i = 0; i < len; i++)
        {
            int halfByte = (data[i] >>> 4) & 0xF;
            int twoHalfs = 0;

            do
            {
                if (0 <= halfByte && halfByte <= 9)
                {
                    buf.append((char) ('0' + halfByte));
                }
                else
                {
                    buf.append((char) ('a' + halfByte - 10));
                }
                halfByte = data[i] & 0xF;
            } while (twoHalfs++ < 1);
        }

        return buf.toString();
    }
}
*/
