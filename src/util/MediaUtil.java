package util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * 媒体工具类
 *
 * @author tianxing
 */
public class MediaUtil {

    /**
     * 获取媒体播放器
     *
     * @param file 文件
     * @return 媒体播放器
     */
    public static MediaPlayer getMediaPlayer(File file) {
        Media media = new Media(file.toURI().toString());
        return new MediaPlayer(media);
    }
}
