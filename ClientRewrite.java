/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.*;

/**
 *
 * @author nicholas.bohm
 */
public class ClientRewrite {

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    public ClientRewrite(String ip, int port) {
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

    public void sendPacket(byte[] pckt) {
        try {
            this.output.write(pckt);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public byte[] receivePckt() {
        byte[] toReturn = new byte[1024];
        try {
            this.input.read(toReturn);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return toReturn;
    }
    
    public InputStream getInput(){
        return this.input;
    }
}
