package service;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import org.apache.log4j.Logger;
import process.ClientGUI;

/**
 * Main method that opens the user interface of a Client
 */
public class Client {
    static Logger log = Logger.getLogger(Client.class);
    public static void main(String[] args){
        log.info("Client started");
        ClientGUI app = new ClientGUI();
        app.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
