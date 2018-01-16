package com.zzh.chatroom;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ClientUI extends JFrame {
    private JTextField jtfName = new JTextField(); // Enter name
    private JTextField jtfMessage = new JTextField(); // Enter message to send

    // a Text Area to display message
    private JTextArea jta = new JTextArea();

    private JScrollPane msgScrollPane = new JScrollPane(jta);

    // a Button to send massage
    private JButton jbSend = new JButton("Send");

    // IO stream
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public static void main(String[] args) {
        new ClientUI();
    }

    public ClientUI() {
        // Panel p to hold the label and text field
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.add(new JLabel(" Enter Name: "));
        p.add(jtfName);
        p.add(new JLabel(" Enter Message: "));
        p.add(jtfMessage);
        jtfName.setHorizontalAlignment(JTextField.LEFT);
        jtfMessage.setHorizontalAlignment(JTextField.LEFT);

        setLayout(new BorderLayout());
        add(p, BorderLayout.NORTH);
        add(msgScrollPane, BorderLayout.CENTER);
        add(jbSend, BorderLayout.SOUTH);

        // Register listener for jbSend button
        jbSend.addActionListener(new ButtonListener());

        // Register listener for jtfName JTextField
        jtfName.addActionListener(new ButtonListener());

        // Register listener for jtfMessage JTextField
        jtfMessage.addActionListener(new ButtonListener());

        setTitle("Client");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!



        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("192.168.0.242", 2014);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());

            // Receive data from the server all the time
            while (true) {
                // read data from client
                String sentence = fromServer.readUTF();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date();
                String time = format.format(d);

                // Display to the text area
                jta.append("\n " + time + "\n");
                jta.append(" " + sentence + "\n");
                System.out.println("message：" + time + "  " + sentence);
            }
        } catch (Exception ex) {
            jta.append(" ***服务器连接失败!***" + '\n');
            System.out.println("exception：" + ex.toString());
        }
    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                // Get the Name and Message from the text field
                String Name = jtfName.getText().trim();
                String Message = jtfMessage.getText().trim();
                // Prevent send a blank message
                if (Name != null && Name.length() != 0 && Message != null && Message.length() != 0) {
                    String data = Name + "： " + Message;
                    // Send the data to the server
                    toServer.writeUTF(data);
                    toServer.flush();
                } else
                    jta.append(" ***警告：名称和内容均不能为空!*** " + "\n");

            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}
