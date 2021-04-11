package clientservertest;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server implements Runnable {
    private JTextArea jTextArea;
    private final List<StringBuilder> stringList = new LinkedList<>();

    public Server() {
        setFrameWindow();
    }

    @Override
    public void run() {
        continueConnection();
    }

    private void setFrameWindow() {
        JFrame jFrame = new JFrame(Names.SERVER);
        jFrame.setLayout(new FlowLayout());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jTextArea = new JTextArea(19, 35);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setForeground(Color.BLACK);
        jTextArea.setEditable(false);
        Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
        jTextArea.setBorder(border);
        jFrame.add(jTextArea);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);
    }

    private StringBuilder getAllRecordsOfChatFromLinkedList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (StringBuilder builder : stringList) {
            stringBuilder.append(builder);
        }
        return stringBuilder;
    }

    private void continueConnection() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            while (true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                StringBuilder stringBuilder1 = new StringBuilder().append(objectInputStream.readObject().toString());

                StringBuilder sb = new StringBuilder()
                        .append(Names.CLIENT)
                        .append(Names.COLON)
                        .append(stringBuilder1)
                        .append(Names.NEW_LINE);

                stringList.add(sb);
                jTextArea.setText(getAllRecordsOfChatFromLinkedList().toString());

                StringBuilder stringBuilder = new StringBuilder()
                        .append(Names.SERVER)
                        .append(Names.COLON)
                        .append(Names.I_RECEIVED_MESSAGE)
                        .append(stringBuilder1)
                        .append(Names.NEW_LINE);

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    objectOutputStream.writeObject(stringBuilder);
                                    stringList.add(stringBuilder);
                                    jTextArea.setText(getAllRecordsOfChatFromLinkedList().toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 100);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}