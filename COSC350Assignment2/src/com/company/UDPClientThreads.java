package com.company;

/**
 * Created by kyleharris on 4/26/18.
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;
class UDPClientThreads {
    public static void main(String args[]) throws Exception
    {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket=new DatagramSocket();
        InetAddress IPAddress=InetAddress.getByName("localhost");
        String[] s=new String[5];
        
        /* Commenting out code because I'm not really sure what 
         * i'll actually need to use. 
         */
        
        Scanner sc= new Scanner(System.in);
        System.out.println("Enter a filename: ");
        String fileName = sc.nextLine();
        
        System.out.println("Sending filename : " + fileName + " to server");
        byte [] sendData = fileName.getBytes();       
        byte[] receiveData=new byte[1024];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, 31213);
        clientSocket.send(sendPacket);
        
        DatagramPacket receivePacket =  new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence=new String(receivePacket.getData());
        System.out.println("FROM SERVER:"+modifiedSentence);
        byte [] received = modifiedSentence.getBytes();
        //s[0] = "c:\c350s18a2\client\" + fileName;
        
        //System.out.println("Recieved length : "+received.length);
        /* Testing a new fileoutput stream to save the bytes to a file*/
        // some code to get the current root path
        String dir = System.getProperty("user.dir");
        FileOutputStream fos  = new FileOutputStream(new File(dir+"/client2.mp3"));
        // write it out to the fileoutput stream
        while(receiveData!=null) {
            receivePacket =  new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            fos.write(receivePacket.getData());
        }
        
        //Sending NTP Message
        System.out.println("Enter NTP Server Name: ");
        String ntpServer = sc.nextLine();
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(ntpServer);
        byte[] buf = new NtpMessage().toByteArray();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 123);
        socket.send(packet);
        
        
        fos.close();

        


        
        clientSocket.close();
    }
}
