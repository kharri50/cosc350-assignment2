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

            /*
            byte[] sendData=new byte[1024];
                 try{
                  String threadName =
                  Thread.currentThread().getName();
                  String message="in HandleClient";
                  System.out.format("%s: %s%n", threadName, message);
                  long cstarttime = System.currentTimeMillis();
                  System.out.println("before csocket");
                  DatagramSocket csocket=new DatagramSocket();
                  String capitalizedSentence=new String(sentence.toUpperCase());
                  sendData=capitalizedSentence.getBytes();
                  DatagramPacket sendPacket=  new DatagramPacket(sendData, sendData.length, address, port);
                  csocket.send(sendPacket);
                  System.out.println("after send in thread "+"IPAddress="+address+" port="+port);
                  long cendtime = System.currentTimeMillis();
                  System.out.println("time="+(cendtime-cstarttime));
                 }
                 catch (IOException e) {}
               }
             */

            byte[] sendData = new byte[1024];
            try{
                String threadName = Thread.currentThread().getName();
                String message="in HandleClient - modified to send file";
                System.out.format("%s: %s%n", threadName, message);
                // get a file object

                // trying to open the file by the fileName

                Path path = Paths.get(sentence);
                byte [] fileBytes = Files.readAllBytes(path); // reads the file as bytes

                /*for(int i  = 0; i < fileBytes.length;i++){
                    System.out.print(fileBytes[i]);
                }*/

                /* Only sends the first 1024 bytes. The file is much larger than this */
                for(int i = 0; i<sendData.length; i++){
                    // set the data to the first 1024 bits of the file
                    sendData[i] = fileBytes[i];
                }


                System.out.print("Send data length: " + sendData.length + "\n");

                /* S0 I"M NOT SURE THAT THIS WORKS. It probably needs to be touched up... */

                long cstarttime = System.currentTimeMillis();
                System.out.println("before csocket");
                DatagramSocket csocket=new DatagramSocket();


                /* the datagram is the packet that we created with the sendData
                   where the array indicies are initalized so the data can be sent
                 */

                // make a new datagram send packed
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                // send the packet
                csocket.send(sendPacket);

                System.out.println("after send in thread "+"IPAddress="+address+" port="+port);

                //System.out.println("Transfer time : " + (endTime-cstarttime));

            }
            catch (IOException e) {
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