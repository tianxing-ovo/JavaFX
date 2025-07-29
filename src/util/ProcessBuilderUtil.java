package util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.*;
import java.util.Locale;

/**
 * 执行CMD命令工具类
 *
 * @author tianxing
 */
public class ProcessBuilderUtil {

    /**
     * 执行CMD命令并将输出结果添加到文本区域中
     *
     * @param command    命令
     * @param textArea   文本区域
     * @param charset    字符集
     * @param onComplete 回调方法
     */
    public static void exec(String command, TextArea textArea, String charset, Runnable onComplete) {
        String[] cmdarray = {"cmd", "/c", command};
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(cmdarray);
                // 设置工作目录为当前项目根目录
                processBuilder.directory(new File(System.getProperty("user.dir")));
                // 合并标准输出和错误输出
                processBuilder.redirectErrorStream(true);
                // 启动进程
                Process process = processBuilder.start();
                // 获取输入流(包含标准输出和错误输出)
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    String replaceLine;
                    if (line.contains("\\")) {
                        replaceLine = line.replace("\\", "/");
                    } else if ("help".equalsIgnoreCase(command)) {
                        replaceLine = line.trim().toLowerCase(Locale.ROOT);
                    } else {
                        replaceLine = line.trim();
                    }
                    Platform.runLater(() -> textArea.appendText(replaceLine + "\n"));
                }
                Platform.runLater(onComplete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}