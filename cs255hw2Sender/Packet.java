package cs255hw2Sender;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 *
 * @author Nicholas Bohm
 * @author Dakota Vanwormer
 */
public class Packet {

    private DataHandler dataHandler = new DataHandler();
    private byte[] seqNum = new byte[4];
    private byte[] checkSum = new byte[4];
    private byte[] srcPortNum = new byte[4];
    private byte[] desPortNum = new byte[4];
    private byte[] dataLength = new byte[4];
    private byte[] payload = new byte[1004];

    public Packet(int seqNum, int checkSum, int srcPort, int desPort, byte[] payLoad) {
        fillHeader(this.seqNum, seqNum);
        fillHeader(this.srcPortNum, srcPort);
        fillHeader(this.desPortNum, desPort);
        fillHeader(this.checkSum, checkSum);
        fillHeader(this.dataLength, payLoad.length);
        if (payLoad.length == 1004) {
            this.payload = payLoad;
        } else {
            this.payload = paddPayLoad(payLoad);
        }
        fillHeader(this.checkSum, this.createChecksum());

    }

    /**
     * Returns true if the file is not corrupt
     *
     * @param pckt
     * @return
     */
    public boolean notCorrupt(byte[] pckt) {
        CRC32 crc32 = new CRC32();
        int recivedChecksum;
        int start = 4;
        int end = 7;
        ByteBuffer buffer = ByteBuffer.allocate(4);
        for (int i = 0; i < 4; i++) {
            buffer.put(pckt[start]);
            start++;
        }
        buffer.flip();
        recivedChecksum = buffer.getInt();
        buffer.clear();
        start = 4;
        for (int i = 0; i < 4; i++) {
            pckt[start] = 0;
            start++;
        }
        crc32.update(pckt);
        if ((int) crc32.getValue() == recivedChecksum) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param pckt
     */
    public Packet(byte[] pckt) {

        breakPacket(pckt);
        this.payload = new byte[this.dataHandler.byteToInt(getDataLength())];
        extractPayload(pckt);

    }

    /**
     * breaks a packets header apart from raw packet.
     *
     * @param pcktContents
     * @param target
     */
    public void breakPacket(byte[] pcktContents) {
        int currentByte = 0;
        ByteBuffer buffer = ByteBuffer.allocate(4);
        if (currentByte >= 0) {
            for (int i = 0; i != 4; i++) {
                buffer.put(pcktContents[currentByte]);

                currentByte++;
            }
            buffer.flip();
            buffer.get(this.seqNum);
            buffer.clear();
        }
        if (currentByte <= 4) {
            for (int i = 0; i != 4; i++) {
                buffer.put(pcktContents[currentByte]);
                currentByte++;
            }
            buffer.flip();
            buffer.get(this.checkSum);
            buffer.clear();

        }
        if (currentByte <= 8) {
            for (int i = 0; i != 4; i++) {
                buffer.put(pcktContents[currentByte]);
                currentByte++;
            }
            buffer.flip();
            buffer.get(this.srcPortNum);
            buffer.clear();

        }
        if (currentByte <= 12) {
            for (int i = 0; i != 4; i++) {
                buffer.put(pcktContents[currentByte]);
                currentByte++;
            }
            buffer.flip();
            buffer.get(this.desPortNum);
            buffer.clear();
        }
        if (currentByte <= 16) {
            for (int i = 0; i != 4; i++) {
                buffer.put(pcktContents[currentByte]);
                currentByte++;
            }
            buffer.flip();
            buffer.get(this.dataLength);
            buffer.clear();
        }
    }

    /**
     * extracts packets payload from raw recived packet
     *
     * @param pcktContents
     * @return
     */
    public void extractPayload(byte[] pcktContents) {
        ByteBuffer buffer = ByteBuffer.allocate(this.dataHandler.byteToInt(getDataLength()));
        int start = 20;
        for (int i = 0; i != dataHandler.byteToInt(getDataLength()); i++) {

            buffer.put(pcktContents[start]);
            start++;
        }
        buffer.flip();
        buffer.get(this.payload);
    }

    /**
     * makes a packet from the data given.
     *
     * @return
     */
    public byte[] makePckt() {
        byte[] toReturn = new byte[1024];
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(this.seqNum);
        buffer.put(this.checkSum);
        buffer.put(this.srcPortNum);
        buffer.put(this.desPortNum);
        buffer.put(this.dataLength);
        buffer.put(this.payload);
        buffer.flip();
        buffer.get(toReturn);
        return toReturn;
    }

    /**
     * creates a checksum for the packet
     *
     * @return
     */
    private int createChecksum() {
        CRC32 crc32 = new CRC32();
        crc32.update(this.makePckt());
        return (int) crc32.getValue();

    }

    /**
     * padds the payload of the packet if the packet payload is not 1004.
     *
     * @param beforePadd
     * @return
     */
    public byte[] paddPayLoad(byte[] beforePadd) {
        ByteBuffer buffer = ByteBuffer.allocate(1004);
        int counter = 0;
        int toAdd = (1004 - beforePadd.length);

        byte[] toReturn = new byte[1004];
        byte padding = 0;

        buffer.put(beforePadd);
        for (int i = 0; i != (toAdd); i++) {
            buffer.put(padding);
            counter++;
        }
        buffer.flip();
        buffer.get(toReturn);
        return toReturn;
    }
    /**
     * fills the header of the packet with the needed data that is given.
     * @param target
     * @param dataToConvert 
     */
    public void fillHeader(byte[] target, int dataToConvert) {
        if (target == this.seqNum) {
            this.seqNum = dataHandler.intToByte(dataToConvert, 4);
        } else if (target == this.srcPortNum) {
            this.srcPortNum = dataHandler.intToByte(dataToConvert, 4);
        } else if (target == this.desPortNum) {
            this.desPortNum = dataHandler.intToByte(dataToConvert, 4);
        } else if (target == this.checkSum) {
            this.checkSum = dataHandler.intToByte(dataToConvert, 4);
        } else if (target == this.dataLength) {
            this.dataLength = dataHandler.intToByte(dataToConvert, 4);
        } else {
            this.payload = dataHandler.intToByte(dataToConvert, 1004);
        }
    }

    public byte[] getSeqNum() {
        return this.seqNum;
    }

    public byte[] getSrcPort() {
        return this.srcPortNum;
    }

    public byte[] getDesPort() {
        return this.desPortNum;
    }

    public byte[] getCheckSum() {
        return this.checkSum;
    }

    public byte[] getDataLength() {
        return this.dataLength;
    }

    public byte[] getPayload() {
        return this.payload;
    }
}
