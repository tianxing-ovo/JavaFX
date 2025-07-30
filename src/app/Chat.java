package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.FXMLUtil;

/**
 * 在线聊天
 *
 * @author tianxing
 */
public class Chat extends Application {

    private static final String FXML_PATH = "/fxml/LoginView.fxml";
    private static final String ICON_PATH = "/png/logo.png";
    private static Stage primaryStageObj;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }

    @Override
    public void start(Stage stage) {
        primaryStageObj = stage;
        Parent root = FXMLUtil.getParent(FXML_PATH);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("一起来聊天");
        stage.getIcons().add(new Image(ICON_PATH));
        Scene scene = new Scene(root, 350, 420);
        scene.setRoot(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> Platform.exit());
    }
}
