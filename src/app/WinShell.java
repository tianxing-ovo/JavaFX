package app;

/**
 * 执行CMD命令
 *
 * @author tianxing
 */
public class WinShell extends BaseApplication {

    private static final String ICON_PATH = "/png/WinShell.png";
    private static final String FXML_PATH = "/fxml/WinShell.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public String getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getFxmlPath() {
        return FXML_PATH;
    }
}