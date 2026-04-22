package TP5;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class p3 {

    public static void main(String[] args) {

        try {

            DatagramSocket s = new DatagramSocket(2004);
            System.out.println("P3 running, waiting for UDP from P2...");

            while (true) {

                // 1. Receive UDP from P2
                byte[] receiveBuffer = new byte[1024]; // fixed: was 50
                DatagramPacket q = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                s.receive(q);

                // fixed: use q.getLength() to avoid null bytes
                String ch = new String(q.getData(), 0, q.getLength());
                ch = ch.trim();

                System.out.println("P3 received from P2: " + ch);
                System.out.println("P2 address: " + q.getAddress() + " Port: " + q.getPort());

                // 2. Connect to P4 as a CLIENT (fixed: was wrongly opening a ServerSocket)
                Socket p4Socket = new Socket("P4_IP_ADDRESS", 2005); // replace with actual P4 IP

                ObjectOutputStream out = new ObjectOutputStream(p4Socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(p4Socket.getInputStream());

                // 3. Send message to P4
                out.writeObject(ch);
                out.flush();
                System.out.println("P3 sent to P4: " + ch);

                // 4. Receive result from P4
                String result = (String) in.readObject();
                System.out.println("P3 received from P4: " + result);

                // 5. Send result back to P2 via UDP
                byte[] dataToSend = result.getBytes();
                DatagramPacket q1 = new DatagramPacket(
                        dataToSend,
                        dataToSend.length,
                        q.getAddress(), // send back to P2's address
                        q.getPort()     // send back to P2's port
                );
                s.send(q1);
                System.out.println("P3 sent result back to P2 via UDP: " + result);

                // 6. Close TCP connection to P4
                in.close();
                out.close();
                p4Socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
