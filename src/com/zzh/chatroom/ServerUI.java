package com.zzh.chatroom;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/*http://club.huawei.com/thread-5458799-1-1.html
* 流式布局FlowLayout
* 边界布局管理器BorderLayout
* 表格布局GridLayout
* 卡片布局CardLayout
* */

public class ServerUI extends JFrame {
    // Text area for displaying data
    private JTextArea jta = new JTextArea();
    ArrayList<Client> clients = new ArrayList<Client>();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date d = new Date();
    String time = format.format(d);
    // Statistics the number of clients
    int clientNo = 0;

    public static void main(String[] args) {
        new ServerUI();
    }

    public ServerUI() {
        // Place text area on the frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);

        setTitle("ServerUI");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!

        ServerSocket serverSocket = null;
        try {
            // Create a server socket
            serverSocket = new ServerSocket(2014);
        } catch (IOException ex) {
            jta.append(" ***端口已被占用!*** " + '\n');
        }
        jta.append(" ***服务器启动时间： " + time + "***" + '\n');

        while (true) {
            Socket socket = null;
            try {
                // Listen for a connection request
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // To display client inetAddress
            InetAddress inetAddress = socket.getInetAddress();
            clientNo++;
            jta.append(" Client " + clientNo + '\n');
            jta.append(" Host name is " + inetAddress.getHostName() + '\n');
            jta.append(" IP Address is " + inetAddress.getHostAddress() + '\n');

            // Create a new Thread for client
            Client task = new Client(socket);
            clients.add(task);
            new Thread(task).start();
        }
    }

    class Client implements Runnable {
        Socket socket;
        // Create data input and output streams
        DataInputStream inputFromClient = null;
        DataOutputStream outputToClient = null;
        Client c = null;

        public Client(Socket socket) {
            this.socket = socket;
            try {
                inputFromClient = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputToClient = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sentAll(String str) {
            try {
                // Receive message from the client
                outputToClient.writeUTF(str);
            } catch (SocketException e) {
                if (c != null)
                    clients.remove(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    // Receive sentence from the client
                    String sentence = inputFromClient.readUTF();
                    // Send sentence back to the client
                    for (int i = 0; i < clients.size(); i++) {
                        c = clients.get(i);
                        c.sentAll(sentence);
                    }

                    // Display to the text area
                    jta.append(" " + time + "\n");
                    jta.append(" " + sentence + '\n');
                }
            } catch (IOException e) {
                // e.printStackTrace();
                clientNo--;
                if (clientNo == 0) {
                    jta.append(" ***无客户端连接!***" + "\n");
                    clients.remove(c); // 防止新用户使用
                } else {
                    jta.append(" ***退出一用户! 剩余用户: " + clientNo + "***" + "\n");
                }
            }
        }
    }
}
