package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * fxml工具类
 *
 * @author tianxing
 */
public class FXMLUtil {

    /**
     * 获取fxml加载器
     *
     * @param fxmlPath fxml文件路径
     * @return fxml加载器
     */
    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(FXMLUtil.class.getResource(fxmlPath));
    }

    /**
     * 加载fxml文件并获取根节点
     *
     * @param fxmlPath fxml文件路径
     * @return 根节点
     */
    public static Parent getParent(String fxmlPath) {
        try {
            // 加载fxml文件
            return getLoader(fxmlPath).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
