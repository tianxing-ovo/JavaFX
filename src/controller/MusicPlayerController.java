package controller;

import constant.Constant;
import constant.ImageConstant;
import constant.Operation;
import constant.PlayMode;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import util.MediaUtil;

import java.io.File;

/**
 * 音乐播放器控制器
 *
 * @author tianxing
 */
public class MusicPlayerController {

    public ListView<String> listView;
    public Slider progressSlider;
    public Slider volumeSlider;
    public ImageView playModeImageView;
    public ImageView playStatusImageView;
    public Tooltip playModeTooltip;
    public Tooltip playStatusTooltip;
    public Button playOrPauseButton;
    public Label volumeLabel;
    // 当前时间标签
    public Label currentTimeLabel;
    // 总时间标签
    public Label totalTimeLabel;
    private MediaPlayer mediaPlayer;
    // 当前正在播放的音乐
    private String currentItem;
    // 是否正在拖动滑块
    private boolean isSeeking = false;
    // 当前显示的操作
    private String currentOperation = Operation.PLAY;
    // 当前播放模式
    private String currentPlayMode = PlayMode.LOOP;

    /**
     * 初始化方法(由FXML加载器自动调用)
     */
    public void initialize() {
        // 添加本地音乐到ListView中
        File file = Constant.MUSIC_PATH.toFile();
        addMusic(file);
        // 禁用播放按钮
        playOrPauseButton.setDisable(true);
        // 设置音量标签的文本
        volumeLabel.setText((int) (volumeSlider.getValue()) + "%");
    }

    /**
     * 播放/暂停音乐
     */
    public void playOrPause() {
        if (Operation.PLAY.equals(currentOperation)) {
            playStatusImageView.setImage(ImageConstant.PAUSE);
            currentOperation = Operation.PAUSE;
            playStatusTooltip.setText(Operation.PAUSE);
            play();
        } else if (Operation.PAUSE.equals(currentOperation)) {
            playStatusImageView.setImage(ImageConstant.PLAY);
            currentOperation = Operation.PLAY;
            playStatusTooltip.setText(Operation.PLAY);
            pause();
        }
        // 刷新ListView以更新按钮图标
        listView.refresh();
    }

    /**
     * 切换播放模式
     */
    public void togglePlayMode() {
        if (PlayMode.LOOP.equals(currentPlayMode)) {
            currentPlayMode = PlayMode.ORDER;
            playModeImageView.setImage(ImageConstant.ORDER);
            playModeTooltip.setText(PlayMode.ORDER);
        } else if (PlayMode.ORDER.equals(currentPlayMode)) {
            currentPlayMode = PlayMode.LOOP;
            playModeImageView.setImage(ImageConstant.LOOP);
            playModeTooltip.setText(PlayMode.LOOP);
        }
    }

    /**
     * 启用播放按钮
     */
    public void onMouseClicked() {
        playOrPauseButton.setDisable(false);
    }

    /**
     * 添加音乐到ListView中
     *
     * @param file 文件
     */
    private void addMusic(File file) {
        // 如果是一个目录
        if (file.isDirectory()) {
            // 获取目录下的所有文件
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    // 递归搜索子目录
                    addMusic(f);
                }
            }
        } else if (file.getName().endsWith(".mp3")) {
            listView.getItems().add(file.getName());
        }
    }

    /**
     * 播放音乐
     */
    private void play() {
        // 获取选中的音乐
        String item = listView.getSelectionModel().getSelectedItem();
        if (item == null) {
            return;
        }
        File file = Constant.MUSIC_PATH.resolve(item).toFile();
        // 如果媒体播放器为空则创建一个媒体播放器
        if (mediaPlayer == null) {
            mediaPlayer = MediaUtil.getMediaPlayer(file);
            currentItem = item;
        }
        // 如果选中的音乐和当前播放的音乐不一致
        else if (!item.equals(currentItem)) {
            // 停止当前播放
            mediaPlayer.stop();
            // 释放资源
            mediaPlayer.dispose();
            // 创建一个新的媒体播放器
            mediaPlayer = MediaUtil.getMediaPlayer(file);
            currentItem = item;
        }
        // 设置初始音量
        mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        // 播放音乐
        mediaPlayer.play();
        // 绑定进度条
        bindProgressSlider();
        // 监听播放结束事件
        setOnEndOfMedia();
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * 绑定进度滑块
     */
    private void bindProgressSlider() {
        // 监听播放进度并更新进度条和时间标签
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            // 如果不是用户拖动滑块
            if (!isSeeking) {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                double currentTime = newValue.toSeconds();
                double totalTime = totalDuration.toSeconds();
                double progress = (currentTime / totalTime) * 100;
                progressSlider.setValue(progress);
                // 更新时间标签
                updateTimeLabels(newValue, totalDuration);
            }
        });
        // 监听滑块值变化事件并调整播放进度
        progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // 如果是用户拖动滑块
            if (isSeeking) {
                double totalDuration = mediaPlayer.getTotalDuration().toSeconds();
                double seekTime = (newValue.doubleValue() / 100) * totalDuration;
                mediaPlayer.seek(Duration.seconds(seekTime));
            }
        });
        // 监听滑块的按下事件
        progressSlider.setOnMousePressed(event -> isSeeking = true);
        // 监听滑块的释放事件
        progressSlider.setOnMouseReleased(event -> isSeeking = false);
        // 监听滑块的点击事件(实现点击跳转功能)
        progressSlider.setOnMouseClicked(event -> {
            // 计算点击位置对应的值
            // 鼠标点击的X坐标
            double mouseX = event.getX();
            // 滑块的宽度
            double sliderWidth = progressSlider.getWidth();
            // 点击位置占总宽度的百分比
            double percent = mouseX / sliderWidth;
            // 计算新的值
            double newValue = percent * progressSlider.getMax();
            // 设置滑块的值
            progressSlider.setValue(newValue);
            // 调整播放进度
            double totalDuration = mediaPlayer.getTotalDuration().toSeconds();
            double seekTime = (newValue / 100) * totalDuration;
            mediaPlayer.seek(Duration.seconds(seekTime));
        });
    }

    /**
     * 监听播放结束事件
     */
    private void setOnEndOfMedia() {
        mediaPlayer.setOnEndOfMedia(() -> {
            // 循环播放
            if (PlayMode.LOOP.equals(currentPlayMode)) {
                // 重置播放进度
                mediaPlayer.seek(Duration.ZERO);
                // 重置进度条
                progressSlider.setValue(0);
            }
            // 顺序播放
            else if (PlayMode.ORDER.equals(currentPlayMode)) {
                int currentIndex = listView.getSelectionModel().getSelectedIndex();
                int nextIndex = (currentIndex + 1) % listView.getItems().size();
                // 选择下一首音乐
                listView.getSelectionModel().select(nextIndex);
                play();
            }
        });
    }

    /**
     * 监控音量滑块拖动事件
     */
    public void onMouseDragged() {
        double volume = volumeSlider.getValue();
        // 调节歌曲的音量
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume / 100);
        }
        volumeLabel.setText((int) volume + "%");
    }

    /**
     * 更新时间标签
     *
     * @param current 当前时间
     * @param total   总时间
     */
    private void updateTimeLabels(Duration current, Duration total) {
        currentTimeLabel.setText(formatTime(current));
        totalTimeLabel.setText(formatTime(total));
    }

    /**
     * 格式化时间
     */
    private String formatTime(Duration duration) {
        int minutes = (int) Math.floor(duration.toMinutes());
        int seconds = (int) Math.floor(duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}