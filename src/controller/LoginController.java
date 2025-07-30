package controller;


import app.Chat;
import client.ClientHandler;
import com.trynotifications.util.ResizeHelper;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import util.Drag;
import util.FXMLUtil;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 登录控制器
 *
 * @author tianxing
 */
public class LoginController {
    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3, 4, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(2));
    private static ChatController chatController;
    public TextField hostnameTextField;
    public Button closeButton;
    public ImageView defaultView;
    public ImageView girlView;
    public ImageView boyView;
    public TextField portTextField;
    public TextField usernameTextField;
    public ChoiceBox<String> imagePicker;
    public Label selectedPicture;
    public BorderPane borderPane;
    private Scene scene;

    /**
     * 登录按钮事件
     *
     * @throws IOException IO异常
     */
    public void loginButtonAction() throws IOException {
        String hostname = hostnameTextField.getText();
        int port = Integer.parseInt(portTextField.getText());
        String username = usernameTextField.getText();
        String picture = selectedPicture.getText();
        // 加载聊天界面
        FXMLLoader fxmlLoader = FXMLUtil.getLoader("/fxml/ChatView.fxml");
        Parent window = fxmlLoader.load();
        // 获取聊天界面控制器
        chatController = fxmlLoader.getController();
        // 创建聊天界面场景
        scene = new Scene(window);
        ClientHandler clientHandler = new ClientHandler(hostname, port, username, picture, this, chatController);
        poolExecutor.execute(clientHandler);
    }

    /**
     * 显示界面场景
     */
    public void showScene() {
        Platform.runLater(() -> {
            Stage stage = (Stage) hostnameTextField.getScene().getWindow();
            if (stage == null) {
                return;
            }
            stage.setResizable(true);
            stage.setWidth(1040);
            stage.setHeight(620);
            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(300);
            ResizeHelper.addResizeListener(stage);
            stage.centerOnScreen();
            // 设置用户名
            chatController.setUsernameLabel(usernameTextField.getText());
            // 设置头像
            chatController.setImageLabel(selectedPicture.getText());
        });
    }

    /**
     * 初始化方法(由FXML加载器自动调用)
     */
    public void initialize() {
        imagePicker.getSelectionModel().selectFirst();
        selectedPicture.textProperty().bind(imagePicker.getSelectionModel().selectedItemProperty());
        selectedPicture.setVisible(false);
        // 处理鼠标拖拽界面
        new Drag().handleDrag(borderPane);
        // 处理头像
        imagePicker.getSelectionModel().selectedItemProperty().addListener((selected, oldPicture, newPicture) -> {
            if (!oldPicture.equals(newPicture)) {
                // 隐藏所有头像
                defaultView.setVisible(false);
                boyView.setVisible(false);
                girlView.setVisible(false);
                // 展示用户选中的头像
                switch (newPicture) {
                    case "default":
                        defaultView.setVisible(true);
                        break;
                    case "boy":
                        boyView.setVisible(true);
                        break;
                    case "girl":
                        girlView.setVisible(true);
                        break;
                }
            }
        });
        int numberOfSquares = 30;
        while (numberOfSquares > 0) {
            generateAnimation();
            numberOfSquares--;
        }
    }


    /**
     * 生成随机动画
     */
    public void generateAnimation() {
        Random random = new Random();
        int size = random.nextInt(50) + 1;
        int speed = random.nextInt(10) + 5;
        int startX = random.nextInt(420);
        int startY = random.nextInt(350);
        int direction = random.nextInt(6) + 1;
        KeyValue moveX = null;
        KeyValue moveY = null;
        Rectangle rectangle = null;
        switch (direction) {
            case 1:
                // MOVE LEFT TO RIGHT
                rectangle = new Rectangle(0, startY, size, size);
                moveX = new KeyValue(rectangle.xProperty(), 350 - size);
                break;
            case 2:
                // MOVE TOP TO BOTTOM
                rectangle = new Rectangle(startX, 0, size, size);
                moveY = new KeyValue(rectangle.yProperty(), 420 - size);
                break;
            case 3:
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                rectangle = new Rectangle(startX, 0, size, size);
                moveX = new KeyValue(rectangle.xProperty(), 350 - size);
                moveY = new KeyValue(rectangle.yProperty(), 420 - size);
                break;
            case 4:
                // MOVE BOTTOM TO TOP
                rectangle = new Rectangle(startX, 420 - size, size, size);
                moveY = new KeyValue(rectangle.xProperty(), 0);
                break;
            case 5:
                // MOVE RIGHT TO LEFT
                rectangle = new Rectangle(420 - size, startY, size, size);
                moveX = new KeyValue(rectangle.xProperty(), 0);
                break;
            case 6:
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                rectangle = new Rectangle(startX, 0, size, size);
                moveX = new KeyValue(rectangle.yProperty(), 420 - size);
                moveY = new KeyValue(rectangle.xProperty(), 350 - size);
                break;
        }
        rectangle.setFill(Color.web("#F89406"));
        rectangle.setOpacity(0.1);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(speed * 1000), moveX, moveY);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        borderPane.getChildren().add(borderPane.getChildren().size() - 1, rectangle);
    }

    /**
     * 关闭
     */
    public void closeSystem() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * 最小化
     */
    public void minimizeWindow() {
        Chat.getPrimaryStage().setIconified(true);
    }
}
