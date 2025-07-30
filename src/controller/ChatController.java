package controller;


import client.ClientHandler;
import com.trynotifications.animations.AnimationType;
import com.trynotifications.bean.BubbleSpec;
import com.trynotifications.bean.BubbledLabel;
import com.trynotifications.notification.TrayNotification;
import entity.Message;
import entity.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import util.CellRenderer;
import util.Drag;
import util.MediaUtil;

import java.io.IOException;
import java.util.Objects;


/**
 * @author tianxing
 */
public class ChatController {

    public ListView<HBox> chatPane;
    public BorderPane borderPane;
    public TextArea messageBox;
    // 用户名
    public Label usernameLabel;
    // 在线用户总数
    public Label onlineCountLabel;
    // 在线用户列表
    public ListView<User> userList;
    // 头像
    public ImageView userImageView;

    /**
     * 发送按钮事件
     *
     * @throws IOException IO异常
     */
    public void sendButtonAction() throws IOException {
        String text = messageBox.getText();
        if (text.isEmpty()) {
            return;
        }
        // 发送消息
        ClientHandler.instance.send(text);
        // 清空输入框
        messageBox.clear();
    }


    /**
     * 显示发送的消息
     *
     * @param message 消息
     */
    public void showMsg(Message message) {
        // 判断是谁发的消息
        boolean isMyself = message.getName().equals(usernameLabel.getText());
        // 更新在线人数
        setOnlineLabel(String.valueOf(message.getOnlineUsers().size()));
        // 整个UI构建过程直接放到JavaFX线程
        Platform.runLater(() -> {
            HBox hBox = new HBox();
            hBox.setMaxWidth(chatPane.getWidth() - 20);
            // 头像
            ImageView profileImage = new ImageView();
            profileImage.setFitHeight(32);
            profileImage.setFitWidth(32);
            // 气泡
            BubbledLabel bubbledLabel = new BubbledLabel();
            if (isMyself) {
                // 设置头像
                profileImage.setImage(userImageView.getImage());
                // 设置气泡
                bubbledLabel.setText(message.getMsg());
                bubbledLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
                bubbledLabel.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                hBox.setAlignment(Pos.TOP_RIGHT);
                hBox.getChildren().addAll(bubbledLabel, profileImage);
            } else {
                // 设置头像
                String path = "png/" + message.getPicture().toLowerCase() + ".png";
                Image image = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource(path)).toString());
                profileImage.setImage(image);
                // 设置气泡
                bubbledLabel.setText(message.getName() + ": " + message.getMsg());
                bubbledLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                bubbledLabel.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                hBox.setAlignment(Pos.TOP_LEFT);
                hBox.getChildren().addAll(profileImage, bubbledLabel);
            }
            // 将消息容器添加到聊天面板
            chatPane.getItems().add(hBox);
        });
    }

    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    public void setOnlineLabel(String count) {
        Platform.runLater(() -> onlineCountLabel.setText(count));
    }

    /**
     * 设置在线用户列表
     *
     * @param message 消息
     */
    public void setUserList(Message message) {
        Platform.runLater(() -> {
            ObservableList<User> users = FXCollections.observableList(message.getOnlineUsers());
            userList.setItems(users);
            userList.setCellFactory(new CellRenderer());
            // 设置在线用户数
            setOnlineLabel(String.valueOf(message.getOnlineUsers().size()));
        });
    }


    /**
     * 用户提示
     *
     * @param message 提示信息
     * @param picture 图片
     * @param title   标题
     * @param sound   声音
     */
    public void notify(String message, String picture, String title, String sound) {
        Platform.runLater(() -> {
            Image profileImg = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("png/" + picture + ".png")).toString(), 50, 50, false, false);
            TrayNotification tray = new TrayNotification();
            tray.setTitle(title);
            tray.setMessage(message);
            tray.setRectangleFill(Paint.valueOf("#2C3E50"));
            tray.setAnimationType(AnimationType.POPUP);
            tray.setImage(profileImg);
            tray.showAndDismiss(Duration.seconds(5));
            try {
                MediaPlayer mediaPlayer = MediaUtil.getMediaPlayer(sound);
                mediaPlayer.play();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    public void initialize() {
        // 处理鼠标拖拽界面
        new Drag().handleDrag(borderPane);
        // 处理按下回车事件
        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    sendButtonAction();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ke.consume();
            }
        });

    }

    public void setImageLabel(String selectedPicture) {
        String path = "";
        switch (selectedPicture) {
            case "boy":
                path = "png/boy.png";
                break;
            case "girl":
                path = "png/girl.png";
                break;
            case "default":
                path = "png/default.png";
                break;
        }
        // 显示自己选择的头像
        Image image = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource(path)).toString());
        userImageView.setImage(image);
    }
}