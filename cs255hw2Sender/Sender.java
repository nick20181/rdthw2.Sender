package cs255hw2Sender;

import java.io.IOException;

/**
 *
 * @author Nicholas Bohm
 * @author Dakota Vanwormer
 */
public class Sender {

    public Sender() {
        int drops = 0;
        int corrupts = 0;
        String ip = "10.15.1.21";
        int srcPort = 4466;
        int desPort = 4467;
        String fileName = "test.txt";
        String directory = "C:\\Users\\nick201\\Desktop\\toSend";
        int payLoadSize = 1004;
        Packet current;
        Packet prev;
        Packet ACK = null;
        long timeStart;
        boolean timeout = true;
        int seqNum = 0;
        boolean run = true;
        Client client = new Client(ip, srcPort);
        byte[] recived;

        DataHandler dataHandler = new DataHandler(directory, fileName);
        long fileSize = (dataHandler.getFileLength(fileName) 
                / payLoadSize) + 1;
        for (int i = 0; i <= fileSize; i++) {
            byte[] currentPayload = dataHandler.FileToByte(
                    fileName, payLoadSize);
            current = new Packet(seqNum, 0, srcPort,
                    desPort, currentPayload);
            client.sendPacket(current.makePckt());
            while (run) {
                try {
                    timeStart = (System.currentTimeMillis() / 1000);
                    while (timeout) {
                        if (1024 == client.getInput().available()) {
                            ACK = null;
                            recived = client.receivePckt();
                            if (current.notCorrupt(recived)) {
                                ACK = new Packet(recived);
                                timeout = false;
                            } else {
                                client.sendPacket(current.makePckt());
                                timeStart = (System.currentTimeMillis() / 1000);
                            }
                        } else if (((System.currentTimeMillis() / 1000) - 
                                timeStart) >= 3) {
                            client.sendPacket(current.makePckt());
                            timeStart = (System.currentTimeMillis() / 1000);
                            drops++;
                        }
                    }
                    timeout = true;
                    if (dataHandler.byteToInt(ACK.getPayload()) == 1) {
                        if (seqNum == 1) {
                            seqNum = 0;
                        } else {
                            seqNum++;
                        }

                        run = false;
                        prev = current;
                        current = null;

                    } else {
                        client.sendPacket(current.makePckt());
                    }
                } catch (IOException ex) {
                }
            }
            run = true;
            currentPayload = null;

        }
        System.out.println("Sender is finished. Thank you!");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Sender();
    }
    
}
