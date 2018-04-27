package com.company;

/**
 * Created by kyleharris on 4/26/18.
 */
import java.io.*;
import java.net.*;
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
            byte[] sendData=new byte[1024];
            try{
                String threadName = Thread.currentThread().getName();
                String message="in HandleClient";
                System.out.format("%s: %s%n", threadName, message);
                long cstarttime = System.currentTimeMillis();
                System.out.println("before csocket");
                DatagramSocket csocket=new DatagramSocket();
                String capitalizedSentence = new String(sentence.toUpperCase());
                sendData=capitalizedSentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                csocket.send(sendPacket);
                System.out.println("after send in thread "+"IPAddress="+address+" port="+port);
                long cendtime = System.currentTimeMillis();
                System.out.println("time="+(cendtime-cstarttime));
            }
            catch (IOException e) {}
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
                String fileName = ("c:\\c350s18a2\\client\\"+udpmessage).trim();
                System.out.println(fileName);

                // start a new thread to handle the client
                
                InetAddress address=receivePacket.getAddress();
                int port=receivePacket.getPort();
                udpserver.nonStatic(udpmessage,address,port);
                count++;
                System.out.println("after start thread"+count);
            }
        }
        catch (IOException e) {}
    }
}