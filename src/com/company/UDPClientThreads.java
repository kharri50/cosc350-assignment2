package com.company;

/**
 * Created by kyleharris on 4/26/18.
 */
import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
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
        String fileName = sc.nextLine() + ".mp3";

        System.out.println("Sending filename : " + fileName + " to server");
        byte [] sendData = fileName.getBytes();
        byte[] receiveData=new byte[1024];
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, 31213);
        clientSocket.send(sendPacket);

        DatagramPacket receivePacket =  new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence=new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        byte [] received = modifiedSentence.getBytes();
        //s[0] = "c:\c350s18a2\client\" + fileName;

        //System.out.println("Recieved length : "+received.length);
        /* Testing a new fileoutput stream to save the bytes to a file*/
        // some code to get the current root path
        String dir = System.getProperty("user.dir");
        FileOutputStream fos  = new FileOutputStream(new File(dir+ "/client/" + fileName));
        // write it out to the fileoutput stream

        while(receiveData!=null) {
        try {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.setSoTimeout(1000);
            clientSocket.receive(receivePacket);
            fos.write(receivePacket.getData());
        }
        catch(Exception e){
            break;
        }

        }
        
        //Sending NTP Message
        System.out.println("Enter NTP Server Name: ");
        String ntpServer = sc.nextLine();
       ContactNTP(ntpServer);
        
        fos.close();

        


        
        clientSocket.close();
    }



    public static void ContactNTP(String serverName) throws IOException {

        // Send request
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(serverName);
        byte[] buf = new NtpMessage().toByteArray();
        DatagramPacket packet =
                new DatagramPacket(buf, buf.length, address, 123);

        // Set the transmit timestamp *just* before sending the packet
        // ToDo: Does this actually improve performance or not?
        NtpMessage.encodeTimestamp(packet.getData(), 40,
                (System.currentTimeMillis()/1000.0) + 2208988800.0);

        socket.send(packet);


        // Get response
        System.out.println("NTP request sent, waiting for response...\n");
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // Immediately record the incoming timestamp
        double destinationTimestamp = (System.currentTimeMillis()/1000.0) + 2208988800.0;


        // Process response
        NtpMessage msg = new NtpMessage(packet.getData());

        // Corrected, according to RFC2030 errata
        double roundTripDelay = (destinationTimestamp-msg.originateTimestamp) -
                (msg.transmitTimestamp-msg.receiveTimestamp);

        double localClockOffset =
                ((msg.receiveTimestamp - msg.originateTimestamp) +
                        (msg.transmitTimestamp - destinationTimestamp)) / 2;


        // Display response
        System.out.println("NTP server: " + serverName);
        System.out.println(msg.toString());

        System.out.println("Dest. timestamp:     " +
                NtpMessage.timestampToString(destinationTimestamp));

        System.out.println("Round-trip delay: " +
                new DecimalFormat("0.00").format(roundTripDelay*1000) + " ms");

        System.out.println("Local clock offset: " +
                new DecimalFormat("0.00").format(localClockOffset*1000) + " ms");

        socket.close();
    }
}
