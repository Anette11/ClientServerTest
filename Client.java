package clientservertest;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Client implements Runnable {
    private ObjectOutputStream objectOutputStream;
    private JTextArea jTextArea;
    private final List<StringBuilder> stringList = new LinkedList<>();

    public Client() {
        setFrameWindow();
    }

    private void setFrameWindow() {
        JFrame jFrame = new JFrame(Names.CLIENT);
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
        JTextField jTextField = new JTextField(25);
        JButton jButton = new JButton(Names.SEND);
        jFrame.add(jTextArea);
        jFrame.add(jTextField);
        jFrame.add(jButton);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);
        jButtonAddActionListener(jButton, jTextField);
    }

    private void jButtonAddActionListener(JButton jButton, JTextField jTextField) {
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == jButton) {
                    String textField = jTextField.getText();
                    StringBuilder sb = new StringBuilder()
                            .append(Names.CLIENT)
                            .append(Names.COLON)
                            .append(textField)
                            .append(Names.NEW_LINE);
                    stringList.add(sb);
                    jTextArea.setText(getAllRecordsOfChatFromLinkedList().toString());
                    setDelayTimerSchedule(textField);
                }
            }
        });
    }

    private void setDelayTimerSchedule(String textField) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        sendData(textField);
                    }
                }, 100);
    }

    private StringBuilder getAllRecordsOfChatFromLinkedList() {
        StringBuilder stringBuilder = new StringBuilder();
        for (StringBuilder builder : stringList) {
            stringBuilder.append(builder);
        }
        return stringBuilder;
    }

    private void sendData(Object object) {
        try {
            objectOutputStream.flush();
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        continueConnection();
    }

    private void continueConnection() {
        while (true) {
            try {
                Socket socket = new Socket(InetAddress.getByName(Names.LOCALHOST), 1234);
                this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(objectInputStream.readObject().toString());
                stringList.add(stringBuilder);
                jTextArea.setText(getAllRecordsOfChatFromLinkedList().toString());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
