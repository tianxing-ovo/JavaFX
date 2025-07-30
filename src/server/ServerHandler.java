package server;


import entity.Message;
import entity.MessageType;
import entity.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 服务端的处理器
 *
 * @author tianxing
 */
public class ServerHandler implements Runnable {
    // {key=用户名;value=用户}
    public static HashMap<String, User> userMap = new HashMap<>();
    // 存储所有用户的输出流对象
    public static HashSet<ObjectOutputStream> writers = new HashSet<>();
    private final Socket socket;
    private User user;
    private ObjectOutputStream oos;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             ObjectInputStream ois = new ObjectInputStream(inputStream);
             OutputStream outputStream = socket.getOutputStream()) {
            oos = new ObjectOutputStream(outputStream);
            // 将新加入的用户的输出流对象放入到set中
            writers.add(oos);
            // 获取客户端第一次登录的消息
            Message firstMessage = (Message) ois.readObject();
            // 校验用户昵称是否重复
            if (!checkDuplicateUsername(firstMessage)) {
                return;
            }
            // 通知其他用户当前用户加入群聊
            sendNotification(firstMessage);
            // 向所有登录用户展示在线用户列表
            showOnlineUser();
            while (socket.isConnected()) {
                Message message = (Message) ois.readObject();
                // 广播聊天消息
                if (message.getType() == MessageType.TEXT) {
                    write(message);
                }
            }
        } catch (IOException | ClassNotFoundException ignored) {
        } finally {
            closeConnection();
        }
    }

    /**
     * 同步校验用户昵称是否重复
     *
     * @param message 消息
     * @return true-不重复 false-重复
     * @throws IOException IO异常
     */
    private synchronized boolean checkDuplicateUsername(Message message) throws IOException {
        if (userMap.containsKey(message.getName())) {
            message.setMsg("用户名重复");
            message.setType(MessageType.ERROR);
            // 将消息返回给当前登录者
            oos.writeObject(message);
            return false;
        }
        user = new User();
        user.setName(message.getName());
        user.setPicture(message.getPicture());
        userMap.put(message.getName(), user);
        return true;
    }

    /**
     * 客户端显示加入群聊消息
     *
     * @param message 消息
     * @throws IOException IO异常
     */
    private void sendNotification(Message message) throws IOException {
        message.setMsg("加入群聊");
        message.setType(MessageType.NOTIFICATION);
        write(message);
    }

    /**
     * 向客户端展示当前在线用户
     *
     * @throws IOException IO异常
     */
    private void showOnlineUser() throws IOException {
        Message message = new Message();
        message.setType(MessageType.JOINED);
        write(message);
    }

    /**
     * 向客户端发送消息
     *
     * @param message 消息
     * @throws IOException IO异常
     */
    private void write(Message message) throws IOException {
        // 设置在线用户
        message.setOnlineUsers(new ArrayList<>(userMap.values()));
        // 广播消息到所有在线客户端
        for (ObjectOutputStream writer : writers) {
            writer.writeObject(message);
        }
    }

    /**
     * 关闭连接
     */
    private void closeConnection() {
        try {
            if (user != null && user.getName() != null) {
                // 移除退出的用户
                userMap.remove(user.getName());
            }
            if (oos != null) {
                // 移除退出用户的输出流
                writers.remove(oos);
                oos.close();
            }
            // 通知其他在线用户
            Message message = new Message();
            message.setMsg("离开聊天");
            message.setType(MessageType.DISCONNECTED);
            message.setName("SERVER");
            write(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
