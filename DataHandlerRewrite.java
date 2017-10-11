/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author nicholas.bohm
 */
public class DataHandlerRewrite {

    private FileOutputStream fos;
    private FileInputStream fis;
    private ByteArrayOutputStream baos;

    //this.fos = new FileOutputStream(this.directory + fileName);
    public DataHandlerRewrite(String directory) {
        try {
            this.fos = new FileOutputStream(directory);
            this.baos = new ByteArrayOutputStream();

        } catch (FileNotFoundException ex) {
        }
    }

    public DataHandlerRewrite(String directory, String fileName) {
        try {
            this.fis = new FileInputStream(directory);
        } catch (FileNotFoundException ex) {
        }
    }

    public DataHandlerRewrite() {

    }

    public long getFileLength(String fileName) {
        File hold = new File(fileName);
//        System.out.println("hold: " + hold.length());
        return hold.length();
    }

    public void ByteToFile(byte[] data) {
        try {

            this.baos.write(data);
            this.baos.writeTo(fos);
            this.baos.reset();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public byte[] FileToByte(String fileName, int size) {
        int counter = 0;
        byte[][] store = new byte[size][1];
        try {
            for (int i = 0; i < size; i++) {
                if (this.fis.read(store[i], 0, 1) != -1) {
                    counter++;
                }
            }
            byte[] toReturn = new byte[counter];
            for (int i = 0; i < counter; i++) {
                toReturn[i] = store[i][0];
            }
            return toReturn;
        } catch (FileNotFoundException ex) {
        } catch (IOException io) {
        }
        return null;
    }

    public int byteToInt(byte[] convert) {
        ByteBuffer buffer = ByteBuffer.allocate(convert.length);
        for (int i = 0; i != convert.length; i++) {
            buffer.put(convert[i]);
        }
        buffer.flip();
        return buffer.getInt();
    }

    public byte[] intToByte(int convert, int toAllocate) {
        byte[] dst = new byte[toAllocate];
        ByteBuffer buffer = ByteBuffer.allocate(toAllocate);
        buffer.putInt(convert);
        buffer.flip();
        buffer.get(dst);
        return dst;
    }
}
