package org.redmachine;

import java.awt.*;
import java.awt.event.*;
import java.net.ConnectException;
import java.util.*;
import java.io.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;

public class XmppManager implements MessageListener{

	XMPPConnection connection;
    private int lastPosition = 0;

	public void login(String userName, String password) throws XMPPException
	{
            ConnectionConfiguration config = new ConnectionConfiguration("redmachine.no-ip.org", 5222, "Work");
            connection = new XMPPConnection(config);

            connection.connect();
            connection.login(userName, password);
	}

	public void sendMessage(String message, String to) throws XMPPException
	{
		Chat chat = connection.getChatManager().createChat(to, this);
		chat.sendMessage(message);
	}

    public Collection<RosterEntry> buddyListCount(){
        Roster roster = connection.getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        return entries;
    }

	public void displayBuddyList(JPanel panel)
    {
        Collection<RosterEntry> entries = buddyListCount();

		for(RosterEntry r:entries)
		{
            JLabel friend = new JLabel(r.getName());
            friend.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JComponent tab1 = makeTextPanel("Talking to " + r.getName());
                    newTab(r.getName(), tab1);
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            panel.add(friend);
		}

	}

    private void newTab(String title, JComponent component){
        lastPosition = chatsPane.getTabCount();
        chatsPane.add(title, new JLabel(title));
        initTabComponent(lastPosition, component);
        lastPosition++;
    }
    private void initTabComponent(int i, JComponent component){
        chatsPane.setTabComponentAt(i, new ButtonTabComponent(chatsPane));
        chatsPane.setComponentAt(i, component);
    }

	public void disconnect()
	{
		connection.disconnect();
	}

	public void processMessage(Chat chat, Message message)
	{
		if(message.getType() == Message.Type.chat)
            if(message.getBody() != null)
			    System.out.println(chat.getParticipant() + " says: " + message.getBody());
	}

    private static String username;
    private static char[] password;
    private static JFrame frame = new JFrame("Friends List");
    private static JFrame chats = new JFrame("Chat Windows");
    private static JTabbedPane chatsPane = new JTabbedPane();

    private static JPanel panel = new JPanel();
    private static JLabel label = new JLabel("Login to CC Chat!");
    private static JLabel userLabel = new JLabel("Username");
    private static JLabel passLabel = new JLabel("Password");
    private static JButton button = new JButton();
    private static JTextField userField = new JTextField(20);
    private static JPasswordField passField = new JPasswordField(20);

    static XmppManager c = new XmppManager();

	public static void main(String args[]) throws XMPPException, IOException {
        // declare variables
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = false;

        JFrame.setDefaultLookAndFeelDecorated(true);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        button.setText("Login");

        panel.add(label);
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(button);

        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pass = new String(passField.getPassword());
                boolean loggedIn = c.startXMP(userField.getText(), pass);
                if(loggedIn)
                    panel.setVisible(false);
                else
                    panel.setVisible(true);
            }
        });
    }

    protected static JComponent makeTextPanel(String text){
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    private static boolean startXMP(String user, String pass){
        boolean loggedIn = false;
        try {
            System.out.println("Logging in");
            c.login(user, pass);
            System.out.println("after login");
            loggedIn = true;
        }catch(XMPPException ex){
            label.setText("Unable to login, try again");
            loggedIn = false;
        }

        if(loggedIn) {
            JPanel panel2 = new JPanel();
            panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
            panel2.add(new JLabel("Welcome - " + user));
            panel2.add(new JLabel(" "));  //used for poor mans space
            panel2.add(new JLabel("Friends List"));
            c.displayBuddyList(panel2);

            frame.add(panel2);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setVisible(true);

            JPanel openChat = new JPanel();
            openChat.setLayout(new GridLayout(1, 1));

            openChat.add(chatsPane);
            chats.add(openChat);
            chats.setSize(100, 100);
//            chats.pack();
            chats.setLocationRelativeTo(frame);
            chats.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            chats.setVisible(true);
        }

        return loggedIn;

    }
//
//		// Enter your login information here
//		c.login("redmachine", "husker");
//
//		c.displayBuddyList();
//
//		System.out.println("-----");
//
//		System.out.println("Who do you want to talk to? - Type contacts full email address:");
//		String talkTo = br.readLine();
//
//		System.out.println("-----");
//		System.out.println("All messages will be sent to " + talkTo);
//		System.out.println("Enter your message in the console:");
//		System.out.println("-----\n");
//
//		while( !(msg=br.readLine()).equals("bye"))
//		{
//			c.sendMessage(msg, talkTo);
//		}
//
//		c.disconnect();
//		System.exit(0);

	class MyMessageListener implements MessageListener {

		@Override
		public void processMessage(Chat chat, Message message) {
			String from = message.getFrom();
			String body = message.getBody();
			System.out.println(String.format("Received message '%1$s' from %2$s", body, from));
		}

	}
}
	

