package app;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.FXMLUtil;

/**
 * JavaFX程序基类
 *
 * @author tianxing
 */
public abstract class BaseApplication extends Application {

    private static final double WIDTH = 1200;
    private static final double HEIGHT = 900;

    public abstract String getFxmlPath();

    public abstract String getIconPath();

    protected double getWidth() {
        return WIDTH;
    }

    protected double getHeight() {
        return HEIGHT;
    }

    @Override
    public void start(Stage stage) {
        // 加载fxml文件并获取根节点
        Parent root = FXMLUtil.getParent(getFxmlPath());
        // 设置场景
        stage.setScene(new Scene(root, getWidth(), getHeight()));
        // 设置图标
        String iconPath = getIconPath();
        if (iconPath != null && !iconPath.isEmpty()) {
            stage.getIcons().add(new Image(getIconPath()));
        }
        // 显示窗口
        stage.show();
    }
}
