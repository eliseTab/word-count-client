package process;

import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
import org.apache.log4j.Logger;
import service.Client;

/*
* User Interface of the client where the following can be done:
* View IP address and port to connect to the server
* Send and view messages to the server upon successful connection
*/
public class ClientGUI extends JFrame{
    static Logger log = Logger.getLogger(ClientGUI.class);
    private JLabel ipAddressL, portL, messagesSentL, messageL;
    private JTextField ipAddressTf, portTf;
    private TextArea messagesSentTa = new TextArea("",15,40,TextArea.SCROLLBARS_VERTICAL_ONLY), messageTa;
    private JButton connect, send;
    private JPanel panelA_1, panelA, panelB;
    private Box vBox1, vBox2, hBox;
    private String ipAddress = "127.0.0.1";
    private int port = 31190;
    private Socket client;
    private ClientThread sendThread;
    /*
    * Lays out the user interface of the Client
    */
    public ClientGUI(){
        super("Client");
        Handler handler = new Handler();
	ipAddressL = new JLabel("IP Address");
	portL = new JLabel("Port");
	messagesSentL = new JLabel("Messages Sent");
	messageL = new JLabel("Message");
	ipAddressTf = new JTextField(ipAddress);
        ipAddressTf.setEditable(false);
        ipAddressTf.setBackground(Color.white);
	portTf = new JTextField(""+port);
        portTf.setEditable(false);
        portTf.setBackground(Color.white);
	messagesSentTa.setEditable(false);
	messageTa = new TextArea("",1,30,TextArea.SCROLLBARS_VERTICAL_ONLY);
	messageTa.addKeyListener(handler);
	messageTa.setEnabled(false);
	connect = new JButton("Connect");
	connect.addActionListener(handler);
	send = new JButton("Send");
	send.addActionListener(handler);
	send.setEnabled(false);
	vBox1 = Box.createVerticalBox();
	vBox1.add(Box.createVerticalStrut(15));
	vBox1.add(ipAddressL);
	vBox1.add(Box.createVerticalStrut(5));
	vBox1.add(portL);
	vBox2 = Box.createVerticalBox();
	vBox1.add(Box.createVerticalStrut(17));
	vBox2.add(ipAddressTf);	
	vBox2.add(Box.createVerticalStrut(3));
	vBox2.add(portTf);
	panelA_1 = new JPanel(new GridLayout(1,2,20,0));
	panelA_1.add(vBox2);
	panelA_1.add(connect);
	panelA = new JPanel(new FlowLayout());
	panelA.add(vBox1);
	panelA.add(panelA_1);
	hBox = Box.createHorizontalBox();
	hBox.add(messageTa);
	hBox.add(Box.createHorizontalStrut(8));
	hBox.add(send);		
	panelB = new JPanel();
	panelB.add(messagesSentL);
	panelB.add(messagesSentTa);
	panelB.add(messageL);
	panelB.add(hBox);
	Container container = getContentPane();
	container.setLayout(new BorderLayout());
	container.add(panelA, BorderLayout.NORTH);
	container.add(panelB, BorderLayout.CENTER);
	setResizable(false);
	setVisible(true);
	setSize(350, 450);
	setLocation(750, 100);
        log.info("Opening client user interface");
    }
    
    /*
    * Event handler that handles events from the buttons and message text field
    */
    private class Handler extends KeyAdapter implements ActionListener{
        /*
        * Reacting method once an action is received from any of the buttons
        *
        * @param    e   ActionEvent that was received
        */
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == connect){
                try{
                    if(connect.getText().equals("Connect")){
                        log.info("Instantiating client socket with port " + port);
			client = new Socket(ipAddressTf.getText(),Integer.parseInt(portTf.getText()));
                        log.info("Successfully connected to server");
			sendThread = new ClientThread();
			sendThread.start();
			connect.setText("Disconnect");
			messageTa.setEnabled(true);
			send.setEnabled(true);
                        messagesSentTa.setBackground(Color.WHITE);	
                    }
                    else{
                        log.info("Exiting client application");
			client.close();
			System.exit(0);
                    }
		} catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            }
            if(e.getSource() == send)
		sendMessage();
        }
        
        /*
        * Reacting method once the key typed is 'Enter'
        *
        * @param    kE   KeyEvent that was received
        */
        public void keyTyped(KeyEvent kE){
            if(kE.getKeyChar() == KeyEvent.VK_ENTER)
            	sendMessage();		
            }
	}
        
        /*
        * Allows the message to be sent to the server and displays the sent message
        */
	public void sendMessage(){
            String msg = messageTa.getText().trim();
            if(msg.length() > 0 && !msg.equals("\n")){
		sendThread.sendMessage = msg;
		sendThread.canSend = true;
		messagesSentTa.append(msg+"\n");
		messageTa.setCaretPosition(0);
		messageTa.setText("");
            }
	}
    
    /*
    * Thread that sends allowed messages to the server
    */
    class ClientThread extends Thread{
        private String sendMessage;
        private boolean canSend = false;		
        
        /*
        * Sends the message to the server
        */
        public void run(){
            DataOutputStream output;
            try {
                output = new DataOutputStream(client.getOutputStream());
                while(!client.isClosed()){
                    if(canSend){
                        log.info("Sending " + sendMessage + " to server");
                        output.write(sendMessage.getBytes());
                        canSend = false;
                    }
                }				
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
