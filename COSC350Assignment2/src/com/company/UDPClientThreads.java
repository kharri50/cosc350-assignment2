package com.company;

/**
 * Created by kyleharris on 4/26/18.
 */
import java.io.*;
import java.net.*;
class UDPClientThreads {
    public static void main(String args[]) throws Exception
    {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket=new DatagramSocket();
        InetAddress IPAddress=InetAddress.getByName("localhost");
        String[] s=new String[5];
        
        /* Commenting out code becuase I'm not really sure what 
         * i'll actually need to use. 
         */
        
        String fileName = "bogusName.mp3";
        
        System.out.println("Sending filename : " + fileName + " to server");
        byte [] sendData = fileName.getBytes();
        byte[] receiveData=new byte[1024];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, 31213);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket =  new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence=new String(receivePacket.getData());
        System.out.println("FROM SERVER:"+modifiedSentence);
        
        /*s[0]="a";
        s[1]="bb";
        s[2]="ccc";
        s[3]="dddd";
        s[4]="eeeee";
        int looplimit=5;
        for (int j=0; j<looplimit; j++) {
            for (int i=0; i<5; i++) {
                byte[] sendData=new byte[1024];
                byte[] receiveData=new byte[1024];
                String sentence=s[i];
                System.out.println("i"+i+"sent"+sentence);
                sendData=sentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, 31213);
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket =  new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String modifiedSentence=new String(receivePacket.getData());
                System.out.println("FROM SERVER:"+modifiedSentence);
            }
        }*/
        
        clientSocket.close();
    }
}