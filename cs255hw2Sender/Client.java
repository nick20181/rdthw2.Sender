package cs255hw2Sender;

import java.io.*;
import java.net.*;

/**
 *
 * @author Nicholas Bohm
 * @author Dakota Vanwormer
 */
public class Client {

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    public Client(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.input = this.socket.getInputStream();
            this.output = this.socket.getOutputStream();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * sends a packet over the socket.
     * @param pckt 
     */
    public void sendPacket(byte[] pckt) {
        try {
            this.output.write(pckt);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * receives the packet over the socket.
     * @return 
     */
    public byte[] receivePckt() {
        byte[] toReturn = new byte[1024];
        try {
            this.input.read(toReturn);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return toReturn;
    }
    /**
     * gets the inputstream of the socket.
     * @return 
     */
    public InputStream getInput(){
        return this.input;
    }
}
