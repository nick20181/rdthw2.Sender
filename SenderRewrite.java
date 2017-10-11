/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;

/**
 *
 * @author Nicholas Bohm <your.name at your.org>
 *
 * http://10.15.1.21/RDT-Four.log
 */
public class SenderRewrite {

    public SenderRewrite(String[] file) {
        int drops = 0;
        int corrupts = 0;
        String ip = "10.15.1.21";
        int srcPort = 4466;
        int desPort = 4467;
        String directory = file[0];
        int payLoadSize = 1004;
        PacketRewrite current;
        PacketRewrite prev;
        PacketRewrite ACK = null;
        long timeStart;
        boolean timeout = true;
        int seqNum = 0;
        boolean run = true;
        ClientRewrite client = new ClientRewrite(ip, srcPort);
        byte[] recived;

        DataHandlerRewrite dataHandler = new DataHandlerRewrite(directory);
        long fileSize = (dataHandler.getFileLength(directory) / payLoadSize) + 1;
        for (int i = 0; i <= fileSize; i++) {
            byte[] currentPayload = dataHandler.FileToByte(directory, payLoadSize);
            current = new PacketRewrite(seqNum, 0, srcPort, desPort, currentPayload);
            System.out.println("Packet: " + seqNum + " CheckSum: " + dataHandler.byteToInt(current.getCheckSum()) + " srcPort: " + dataHandler.byteToInt(current.getSrcPort()) + " desPort: " + dataHandler.byteToInt(current.getDesPort()) + " datalength: " + dataHandler.byteToInt(current.getDataLength()));
            client.sendPacket(current.makePckt());
            while (run) {
                try {
                    timeStart = (System.currentTimeMillis() / 1000);
                    System.out.println("Time start: " + timeStart);
                    while (timeout) {
                        //System.out.println("drop?: " + (1024 == client.getInput().available()));
                        if (1024 == client.getInput().available()) {
                            System.out.println("recived ack");
                            ACK = null;
                            recived = client.receivePckt();
                            if (current.notCorrupt(recived)) {
                                ACK = new PacketRewrite(recived);
                                timeout = false;
                            } else {
                                System.out.println("corrupted");
                                client.sendPacket(current.makePckt());
                                timeStart = (System.currentTimeMillis() / 1000);
                            }
                        } else if (((System.currentTimeMillis() / 1000) - timeStart) >= 3) {
                            System.out.println("Timeout");
                            client.sendPacket(current.makePckt());
                            timeStart = (System.currentTimeMillis() / 1000);
                            drops++;
                        }
                    }
                    timeout = true;
                    System.out.println("Ack Checksum: " + dataHandler.byteToInt(ACK.getCheckSum()));
                    if (dataHandler.byteToInt(ACK.getPayload()) == 1) {
                        System.out.println("recived Ack: " + ACK.getPayload() + " Saved Ack: " + ACK);
                        //iftimeout
                        System.out.println("Ack recived!.");
                        if (seqNum == 1) {
                            seqNum = 0;
                        } else {
                            seqNum++;
                        }

                        run = false;
                        prev = current;
                        current = null;

                    } else {
                        System.out.println("Nak Recived!");
                        client.sendPacket(current.makePckt());
                    }
                } catch (IOException ex) {
                }
            }
            run = true;
            currentPayload = null;

        }
        System.out.println("Drops: " + drops + " corrupts: " + corrupts);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] file) {
        new SenderRewrite(file);

    }
    
}
