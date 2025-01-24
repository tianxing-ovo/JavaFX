package util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * 执行CMD命令工具类
 *
 * @author tianxing
 */
public class RuntimeUtil {

    private static final String GBK = "GBK";

    /**
     * 执行CMD命令并将输出结果添加到文本区域中
     *
     * @param command  命令
     * @param textArea 文本区域
     */
    public static void exec(String command, TextArea textArea) {
        String[] cmdarray = {"cmd", "/c", command};
        new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec(cmdarray);
                // 获取输入流
                InputStream inputStream = process.getInputStream();
                // 获取错误输入流
                InputStream errorStream = process.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, GBK));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, GBK));
                String line;
                while ((line = reader.readLine()) != null) {
                    String replaceLine;
                    if (line.contains("\\")) {
                        replaceLine = line.replace("\\", "/");
                    } else {
                        replaceLine = line.trim().toLowerCase(Locale.ROOT);
                    }
                    Platform.runLater(() -> textArea.appendText(replaceLine + "\n"));
                }
                while ((line = errorReader.readLine()) != null) {
                    String finalLine = line;
                    Platform.runLater(() -> textArea.appendText(finalLine.trim().toLowerCase(Locale.ROOT) + "\n"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}