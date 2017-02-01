//
//package com.example.han.myapplication001.XMPP;
//import android.util.Log;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Collection;
//import java.util.Date;
//
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.PacketListener;
//import org.jivesoftware.smack.Roster;
//import org.jivesoftware.smack.RosterEntry;
//import org.jivesoftware.smack.RosterListener;
//import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
//import org.jivesoftware.smack.SmackException.NotConnectedException;
//import org.jivesoftware.smack.filter.MessageTypeFilter;
//import org.jivesoftware.smack.filter.PacketFilter;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Packet;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smackx.delay.packet.DelayInformation;
//import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
//import org.jivesoftware.smackx.filetransfer.FileTransferListener;
//import org.jivesoftware.smackx.filetransfer.FileTransferManager;
//import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
//import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
//import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
//
//
//public class tesss extends Thread {
//    private XMPPConnection connection;
//    FileTransferManager manager;
//    private static final String USERID = "valariebeshada";
//    private static final String PASSWORD = "becky11";
//    private static final String RESOURCE = "CAD";
//    private Log log;
//
//    public static void main(String[] args) {
//        new tesss();
//    }
//
//    public tesss() {
//
//        // Set up logging through log4j.
//
//        this.start();
//    }
//
//    public void run() {
//        this.connectionSetup();
//
//        this.admitFriendsRequest();
//        this.setPresence();
//        this.setPresenceSubscription("jimbeshada@jamess-macbook-air.local", true);
//        this.startRosterListener();
//        this.showRoster();
//        this.startFileTransferManager();
//        this.listenForChatMessages();
//        this.listenForNormalMessages();
//
//        this.pause(30000);
//
//        log.info("XmppReceiver has stopped");
//    }
//
//
//    public boolean connectionSetup() {
//
//        try {
//            ConnectionConfiguration config = new ConnectionConfiguration("localhost", 5222);
//            config.setSecurityMode(SecurityMode.disabled);
//            connection = new XMPPTCPConnection(config);
//            DeliveryReceiptManager.getInstanceFor(connection).enableAutoReceipts();
//            connection.connect();
//            log.info("Connected");
//        } catch (Exception e) {
//            log.error("Error connecting: " + e.toString());
//            return false;
//        }
//
//        try {
//            connection.login(USERID, PASSWORD, RESOURCE);
//            return true;
//        } catch (Exception e) {
//            log.error("Login error: " + e.toString());
//            return false;
//        }
//    }
//
//    private boolean setPresence() {
//
//        Presence presence = new Presence(Presence.Type.available);
//        presence.setStatus("Ready to chat");
//        presence.setPriority(42);
//        presence.setMode(Presence.Mode.available);
//
//        try {
//            connection.sendPacket(presence);
//            log.info("Presence set");
//            return true;
//        } catch (NotConnectedException e) {
//            log.error("Error setting Presence: " + e.toString());
//            return false;
//        }
//
//    }
//
//    private boolean setPresenceSubscription(String user, boolean subscriptionEnabled) {
//        Presence presence;
//        if (subscriptionEnabled) {
//            presence = new Presence(Presence.Type.subscribe);
//            log.info("Subscribed to " + user);
//        } else {
//            presence = new Presence(Presence.Type.unsubscribe);
//            log.info("Unsubscribed from " + user);
//        }
//
//        presence.setFrom(USERID);
//        presence.setTo(user);
//
//        try {
//            connection.sendPacket(presence);
//            return true;
//        } catch (NotConnectedException e) {
//            log.error("Error subscribing to user presence: " + e.toString());
//            return false;
//        }
//
//    }
//
//    private void startRosterListener() {
//
//        connection.getRoster().addRosterListener(new RosterListener() {
//
//            public void presenceChanged(Presence presence) {
//
//                log.info("Presence change detected for:" + presence.getFrom()
//                        + "\tType: " + presence.getType()
//                        + "\tStatus: " + presence.getStatus());
//            }
//
//            public void entriesUpdated(Collection<String> presence) {
//                log.info("entriesUpdated");
//
//            }
//
//            public void entriesDeleted(Collection<String> presence) {
//                log.info("entriesDeleted");
//
//            }
//
//            public void entriesAdded(Collection<String> presence) {
//                log.info("entriesAdded");
//            }
//        });
//
//        log.info("Listening to roster changes");
//    }
//
//
//
//    public void showRoster() {
//
//        Roster roster = connection.getRoster();
//        Collection<RosterEntry> entries = roster.getEntries();
//
//        for (RosterEntry entry : entries) {
//
//            log.info("RosterEntry " + entry);
//            log.info("User: [" + entry.getUser() + "]");
//            log.info("Name: " + entry.getName());
//
//            Presence presence = roster.getPresence(entry.getUser());
//            if (presence == null) {
//                log.info("User is off-line");
//            } else {
//                log.info("User is on-line");
//            }
//
//            log.info("Present Type: " + presence.getType());
//            log.info("Present Status: " + presence.getStatus());
//
//        }
//    }
//
//    public void listenForChatMessages() {
//
//        if (connection != null) {
//
//            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
//            connection.addPacketListener(new PacketListener() {
//
//                @Override
//                public void processPacket(Packet packet) {
//                    Message message = (Message) packet;
//                    if (message.getBody() != null) {
//                        log.info("Received chat message [" + message.getBody() + "] from [" +  message.getFrom() + "]");
//                    }
//                }
//            }, filter);
//        }
//
//        log.info("Listening for Chat Messages");
//    }
//
//    public void listenForNormalMessages() {
//
//        if (connection != null) {
//
//            PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
//            connection.addPacketListener(new PacketListener() {
//
//                @Override
//                public void processPacket(Packet packet) {
//                    Message message = (Message) packet;
//                    if (message.getBody() != null) {
//                        log.info("Received normal message [" + message.getBody() + "] from [" +  message.getFrom() + "]");
//                        DelayInformation inf = null;
//                        try {
//                            inf = (DelayInformation)packet.getExtension("x","jabber:x:delay");
//                        } catch (Exception e) {
//                            System.out.println("Error retrieving delay information: " + e.toString());
//                        }
//                        // get offline message timestamp
//                        if(inf!=null){
//                            Date date = inf.getStamp();
//                            log.info("Packet Date: " + date);
//                        }
//                    }
//                }
//            }, filter);
//        }
//
//        log.info("Listening for Normal Messages");
//    }
//
//    private void startFileTransferManager() {
//
//        manager = new FileTransferManager(connection);
//        manager.addFileTransferListener(new FileTransferListener() {
//
//            public void fileTransferRequest(final FileTransferRequest request) {
//                log.info("File transfer request received");
//                new Thread() {
//
//                    @Override
//                    public void run() {
//                        log.info("Received request from: " + request.getRequestor() + " to transfer file: " + request.getFileName());
//                        //FileTransferNegotiator.IBB_ONLY = true;
//                        IncomingFileTransfer transfer = request.accept();
//                        log.info("File transfer request accepted");
//                        String path = getRunDirectory() + File.separator + System.currentTimeMillis() + ".jpg";
//                        File file = new File(path);
//                        try {
//                            transfer.recieveFile(file);
//                            while (!transfer.isDone()) {
//                                pause(1000);
//                                if (transfer.getStatus().equals(Status.error)) {
//                                    log.error("File transfer error: " + transfer.getError());
//                                    if (transfer.getException() != null) {
//                                        log.error("Transfer exception: " + transfer.getException().getMessage());
//                                    }
//                                    break;
//                                }
//                            }
//                        } catch (Exception e) {
//                            log.error("File transfer exception caught: " + e.getMessage());
//                        }
//
//                        if (transfer.getStatus().equals(Status.complete)) {
//                            log.info("File transfer complete");
//                        }
//                    };
//                }.start();
//            }
//        });
//
//        log.info("File Transfer Manager is listening for requests");
//    }
//
//    public void admitFriendsRequest() {
//
//        connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
//        connection.addPacketListener(new PacketListener() {
//
//            public void processPacket(Packet paramPacket) {
//
//                if (paramPacket instanceof Presence) {
//                    Presence presence = (Presence) paramPacket;
//                    String email = presence.getFrom();
//                    log.info("chat invite status changed by user: : "
//                            + email + " calling listner");
//                    log.info("presence: " + presence.getFrom()
//                            + "; type: " + presence.getType() + "; to: "
//                            + presence.getTo() + "; " + presence.toXML());
//                    Roster roster = connection.getRoster();
//                    for (RosterEntry rosterEntry : roster.getEntries()) {
//                        log.info("jid: " + rosterEntry.getUser()
//                                + "; type: " + rosterEntry.getType()
//                                + "; status: " + rosterEntry.getStatus());
//                    }
//
//                    if (presence.getType().equals(Presence.Type.subscribe)) {
//
//                        Presence newp = new Presence(Presence.Type.subscribed);
//                        newp.setMode(Presence.Mode.available);
//                        newp.setPriority(24);
//                        newp.setTo(presence.getFrom());
//                        try {
//                            connection.sendPacket(newp);
//                        } catch (Exception e) {
//
//                        }
//
//                        Presence subscription = new Presence(
//                                Presence.Type.subscribe);
//                        subscription.setTo(presence.getFrom());
//
//
//                        try {
//                            connection.sendPacket(subscription);
//                        } catch (Exception e) {
//
//                        }
//
//                    } else if (presence.getType().equals(Presence.Type.unsubscribe)) {
//
//                        Presence newp = new Presence(Presence.Type.unsubscribed);
//                        newp.setMode(Presence.Mode.available);
//                        newp.setPriority(24);
//                        newp.setTo(presence.getFrom());
//
//                        try {
//                            connection.sendPacket(newp);
//                        } catch (Exception e) {
//
//                        }
//                    } else {
//                        log.info("Other type of presence packet received");
//                    }
//                }
//
//            }
//        }, new PacketFilter() {
//            public boolean accept(Packet packet) {
//                if (packet instanceof Presence) {
//                    Presence presence = (Presence) packet;
//                    if (presence.getType().equals(Presence.Type.subscribed)
//                            || presence.getType().equals(
//                            Presence.Type.subscribe)
//                            || presence.getType().equals(
//                            Presence.Type.unsubscribed)
//                            || presence.getType().equals(
//                            Presence.Type.unsubscribe)) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//    }
//
//    private String getRunDirectory() {
//
//        try {
//            File cur_dir = new File(".");
//            return cur_dir.getCanonicalPath();
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//    private void pause(int delay){
//        try {
//            Thread.sleep(delay);
//        } catch (Exception e) {
//            // ignore
//        }
//    }
//
//}