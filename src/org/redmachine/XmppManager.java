package org.redmachine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	public void login(String userName, String password) throws XMPPException
	{
		ConnectionConfiguration config = new ConnectionConfiguration("im.server.here",5222, "Work");
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
            panel.add(new JLabel(r.getName()));
		}

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

	public static void main(String args[]) throws XMPPException, IOException {
        // declare variables
        XmppManager c = new XmppManager();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = false;

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("CCChat");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Login to CC Chat!");
        JLabel userLabel = new JLabel("Username");
        JLabel passLabel = new JLabel("Password");
        JTextField userField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton button = new JButton();
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
                System.out.println("Username - " + userField.getText());
                System.out.println("Password - " + passField.getPassword());
                String pass = new String(passField.getPassword());
                c.startXMP(c, userField.getText(), pass, frame);
                panel.setVisible(false);
            }
        });
    }

    private static void startXMP(XmppManager c,String user, String pass, JFrame frame){
        try {
            c.login(user, pass);
        }catch(XMPPException ex){
            System.out.println(ex);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Welcome - " + user));
        panel.add(new JLabel(" "));  //used for poor mans space
        panel.add(new JLabel("Friends List"));
        c.displayBuddyList(panel);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

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
	

