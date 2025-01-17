package app;

/**
 * 音乐播放器
 *
 * @author tianxing
 */
public class MusicPlayer extends BaseApplication {

    private static final String FXML_PATH = "/fxml/MusicPlayer.fxml";
    private static final String ICON_PATH = "/png/MusicPlayer.png";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public String getFxmlPath() {
        return FXML_PATH;
    }

    @Override
    public String getIconPath() {
        return ICON_PATH;
    }
}