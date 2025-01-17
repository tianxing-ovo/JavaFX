package controller;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import util.RuntimeUtil;

/**
 * WinShell控制器
 *
 * @author tianxing
 */
public class WinShellController {

    public VBox root;
    public TextField textField;
    public TextArea textArea;
    public ChoiceBox<String> choiceBox;

    /**
     * 初始化方法(由FXML加载器自动调用)
     */
    public void initialize() {
        // 文本框设置默认值
        textField.setText(choiceBox.getValue());
        // 取消焦点 -> 避免输入框文本被全选
        textField.setFocusTraversable(false);
    }

    /**
     * 执行CMD命令
     */
    public void execute() {
        // 清空文本域
        textArea.clear();
        // 执行CMD命令
        RuntimeUtil.exec(textField.getText(), textArea);
    }

    /**
     * 监听鼠标点击事件
     */
    public void onMouseClicked() {
        execute();
    }

    /**
     * 监听键盘回车事件
     *
     * @param keyEvent 键盘事件
     */
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            execute();
        }
    }

    /**
     * 监听下拉框选中事件
     */
    public void onAction() {
        textField.setText(choiceBox.getValue());
        execute();
    }
}
