package com.company;

/**
 * Created by kyleharris on 4/26/18.
 */
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UDPServerThreads {

    public class UDPClientHandler1 implements Runnable {

        String sentence;
        InetAddress address;
        int port;

        public UDPClientHandler1(String sentence, InetAddress address, int port) {
            this.sentence=sentence;
            this.address=address;
            this.port=port;
        }

        public void run() {


            try {
                int count = 0;
                int MAX_SIZE = 1024;
                DatagramSocket cSocket = new DatagramSocket();
                byte[] sendData = new byte[MAX_SIZE];
                Path path = Paths.get(sentence);

                // create a new output stream and open it
                FileInputStream inputStream = new FileInputStream(path.toFile());

                // file length
                int fileLength = 0;
                while ((count = inputStream.read(sendData)) != -1) {
                    fileLength += count;
                }

                System.out.println("File length : " + fileLength);

                int numPackets = fileLength / MAX_SIZE;
                int offet = numPackets * MAX_SIZE;
                int lastLen = fileLength - offet;

                byte[] lastPacket = new byte[lastLen - 1];
                inputStream.close();


                // opening a new file input stream
                FileInputStream sendingStream = new FileInputStream(path.toFile());
                while ((count = sendingStream.read(sendData)) != -1) {
                    if (numPackets <= 0)
                        break;
                    System.out.println(new String(sendData));
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                    cSocket.send(sendPacket);
                    System.out.println("========");
                    System.out.println("last pack sent" + sendPacket);
                    numPackets--;
                }

                //check
                System.out.println("\nlast packet\n");
                System.out.println(new String(sendData));

                lastPacket = Arrays.copyOf(sendData, lastLen);

                System.out.println("\nActual last packet\n");
                System.out.println(new String(lastPacket));
                //send the correct packet now. but this packet is not being send.
                DatagramPacket sendPacket1 = new DatagramPacket(lastPacket, lastPacket.length, address, port);
                cSocket.send(sendPacket1);
                System.out.println("last pack sent" + sendPacket1);



                   }catch (IOException e) {
                        System.out.println("Exception being caught");
                }



            }
        }






    public void nonStatic(String udpmessage, InetAddress address, int port) {
        Thread t = new Thread(new UDPClientHandler1(udpmessage,address,port));
        t.start();
    }

    public static void main(String args[]) throws Exception
    {
        UDPServerThreads udpserver= new UDPServerThreads();
        try {
            // changing port to 31213
            DatagramSocket serverSocket=new DatagramSocket(31213);
            byte[] receiveData=new byte[1024];
            int count=0;
            while(true)
            {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                System.out.println("after rcv in server");
                // udpmessage should be the filename
                String udpmessage=new String(receivePacket.getData());
                //System.out.println("sentence"+udpmessage);

                /* this fileName is not compatable with Unix File Formats
                 so we're not using that.. just remove it for now */

                // String fileName = ("c:\\c350s18a2\\client\\"+udpmessage).trim();
                String fileName = udpmessage.trim();
                System.out.println("Filename : "+fileName);

                /* start a new thread to handle the client - looks like this
                * is already kind of done in the run() method of the UDPClientHandler
                * but we have to run it in nonstatic context using nonStatic() */
                // starts the thread and runs it

                InetAddress address=receivePacket.getAddress();
                int port=receivePacket.getPort();
                // create new thread handler
                udpserver.nonStatic(fileName,address,port);

                count++;
                System.out.println("after start thread"+count);
            }
        }
        catch (IOException e) {}
    }
}