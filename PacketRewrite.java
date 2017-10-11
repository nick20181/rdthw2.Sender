/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 *
 * @author nicholas.bohm
 */
public class PacketRewrite {

    private DataHandlerRewrite dataHandler = new DataHandlerRewrite();
    private byte[] seqNum = new byte[4];
    private byte[] checkSum = new byte[4];
    private byte[] srcPortNum = new byte[4];
    private byte[] desPortNum = new byte[4];
    private byte[] dataLength = new byte[4];
    private byte[] payload = new byte[1004];

    public PacketRewrite(int seqNum, int checkSum, int srcPort, int desPort, byte[] payLoad) {
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

//        ByteBuffer buffer = ByteBuffer.allocate(4);
//        buffer.put(this.checkSum);
//        buffer.flip();
//        int testCheck = buffer.getInt();
////        System.out.println("Checksum Sender: " + testCheck + " ReceiverChecksum :" + this.makeCheckRecieved());
//        if (testCheck == this.makeCheckRecieved()) {
//            return true;
//        } else {
//            return false;
//        }
    }

//    /**
//     * makes a check sum from a packet with a checksum of zero in the packet.
//     *
//     * @return
//     */
//    public int makeCheckRecieved() {
//        CRC32 crc32 = new CRC32();
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        //loads all fields into one byte array
//        buffer.put(this.seqNum);
//        buffer.putInt(0);
//        buffer.put(this.srcPortNum);
//        buffer.put(this.desPortNum);
//        buffer.put(this.dataLength);
//        if (this.payload.length != 1004) {
//            buffer.put(paddPayLoad(this.payload));
//        } else {
//            buffer.put(this.payload);
//        }
//
//        buffer.flip();
//        //gets the packet out of the buffer
//
//        crc32.update(buffer);
//        return (int) crc32.getValue();
//    }
    public PacketRewrite(byte[] pckt) {

        breakPacket(pckt);
//        System.out.println("packtretwerret: " + this.dataHandler.byteToInt(getDataLength()));
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
//        System.out.println("test: " + this.dataHandler.byteToInt(getDataLength()));
        System.out.println("buffer size: " + buffer.capacity());
        int start = 20;
        for (int i = 0; i != dataHandler.byteToInt(getDataLength()); i++) {

            buffer.put(pcktContents[start]);
            start++;
        }
        buffer.flip();
        buffer.get(this.payload);
    }

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

    private int createChecksum() {
        CRC32 crc32 = new CRC32();
        crc32.update(this.makePckt());
        return (int) crc32.getValue();

    }

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
