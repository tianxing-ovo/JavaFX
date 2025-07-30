package client;


import controller.ChatController;
import controller.LoginController;
import entity.Message;
import entity.MessageType;

import java.io.*;
import java.net.Socket;


/**
 * 客户端的处理器
 *
 * @author tianxing
 */
public class ClientHandler implements Runnable {

    public static ClientHandler instance;
    private final String hostname;
    private final int port;
    private final String username;
    private final String picture;
    private final LoginController loginController;
    private final ChatController chatController;
    private ObjectOutputStream oos;

    public ClientHandler(String hostname, int port, String username, String picture,
                         LoginController loginController, ChatController chatController) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.picture = picture;
        this.loginController = loginController;
        this.chatController = chatController;
        instance = this;
    }

    @Override
    public void run() {
        // 获取socket对象
        try (Socket socket = new Socket(hostname, port)) {
            OutputStream outputStream = socket.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            // 向服务器发送首次登录消息
            sendFirstMessage();
            while (socket.isConnected()) {
                Message message = (Message) ois.readObject();
                if (message != null) {
                    switch (message.getType()) {
                        case NOTIFICATION:
                            // 切换到聊天界面
                            loginController.showScene();
                            chatController.notify(message.getName() + message.getMsg(), message.getPicture(), "新朋友加入", "wav/Global.wav");
                            break;
                        case ERROR:
                            chatController.notify(message.getMsg(), message.getPicture(), "出问题了", "wav/system.wav");
                            break;
                        case JOINED:
                        case DISCONNECTED:
                            // 设置在线用户列表
                            chatController.setUserList(message);
                            break;
                        case TEXT:
                            chatController.showMsg(message);
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }

    /**
     * 发送消息给服务端
     *
     * @param msg 消息
     * @throws IOException IO异常
     */
    public void send(String msg) throws IOException {
        Message message = new Message();
        message.setName(username);
        message.setType(MessageType.TEXT);
        message.setMsg(msg);
        message.setPicture(picture);
        oos.writeObject(message);
        oos.flush();
    }

    public void sendFirstMessage() throws IOException {
        Message message = new Message();
        message.setName(username);
        message.setPicture(picture);
        oos.writeObject(message);
    }
}
