package controller;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;
import util.ProcessBuilderUtil;

import java.io.File;
import java.util.*;

/**
 * WinShell控制器
 *
 * @author tianxing
 */
public class WinShellController {

    private static final String JAR_SUFFIX = ".jar";
    private final String path = System.getProperty("user.dir");
    private final String prefix = path + ">";
    private final List<String> tabCompletionCandidates = new ArrayList<>();
    public VBox root;
    public TextField textField;
    public TextArea textArea;
    public ListView<String> listView;
    public TextField searchTextField;
    public Button previousMatchButton;
    public Button nextMatchButton;
    private int lastSearchIndex = -1;
    private FilteredList<String> filteredList;
    private int tabCompletionIndex = -1;
    private PauseTransition searchDebounce;

    /**
     * 初始化方法(由FXML加载器自动调用)
     */
    public void initialize() {
        // 设置文本格式化器
        setTextFormatter();
        // 取消焦点 -> 避免输入框文本被全选
        textField.setFocusTraversable(false);
        // 添加文件名到ListView中
        addFileName();
        ObservableList<String> observableList = listView.getItems();
        filteredList = new FilteredList<>(observableList, p -> true);
        listView.setItems(filteredList);
        // 设置ListView的单元格工厂
        listView.setCellFactory(param -> new HighlightListCell());
        // 初始化时禁用按钮
        updateMatchButtonsState();
        // 初始化防抖定时器
        initDebounce();
    }

    private void initDebounce() {
        // 300ms延迟
        searchDebounce = new PauseTransition(Duration.millis(300));
        // 延迟结束后执行搜索
        searchDebounce.setOnFinished(event -> search());
    }

    private void addFileName() {
        File file = new File(path);
        File[] files = file.listFiles();
        List<String> suffixList = Arrays.asList(".apk", ".bat", ".exe", ".jar");
        for (File f : Objects.requireNonNull(files)) {
            String filename = f.getName();
            int lastDotIndex = filename.lastIndexOf(".");
            if (lastDotIndex == -1) {
                continue;
            }
            String suffix = filename.substring(lastDotIndex).toLowerCase(Locale.ROOT);
            if (suffixList.contains(suffix)) {
                listView.getItems().add(filename);
            }
        }
    }

    /**
     * 统计搜索文本在内容中的匹配次数
     *
     * @param content    内容
     * @param searchText 搜索文本
     * @return 匹配次数
     */
    private int countMatches(String content, String searchText) {
        if (content.isEmpty() || searchText.isEmpty()) {
            return 0;
        }
        String lowerContent = content.toLowerCase();
        String lowerSearchText = searchText.toLowerCase();
        int count = 0;
        int fromIndex = 0;
        int searchLength = lowerSearchText.length();
        // 循环查找所有非重叠匹配
        while ((fromIndex = lowerContent.indexOf(lowerSearchText, fromIndex)) != -1) {
            count++;
            // 开始索引移动到当前匹配项之后
            fromIndex += searchLength;
        }
        return count;
    }

    /**
     * 执行CMD命令
     */
    public void execute() {
        // 清空输出文本域
        textArea.clear();
        String command = textField.getText().substring(prefix.length());
        String charset = "GBK";
        if (command.isEmpty()) {
            return;
        }
        if (command.contains("apktool")) {
            charset = "UTF-8";
        }
        // 执行CMD命令
        ProcessBuilderUtil.exec(command, textArea, charset, () -> {
            // 若搜索框有内容自动触发搜索
            if (!searchTextField.getText().isEmpty()) {
                search();
            }
        });
    }

    /**
     * 清空输入文本框
     */
    public void clear() {
        textField.setText(prefix);
    }

    /**
     * 复制输入文本框
     */
    public void copy() {
        // 复制输入文本框内容到剪贴板
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(textField.getText().substring(prefix.length()));
        clipboard.setContent(clipboardContent);
        // 弹出提示框
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText("复制成功");
        alert.initOwner(root.getScene().getWindow());
        alert.show();
        // 500ms后关闭提示框
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        pauseTransition.setOnFinished(event -> alert.close());
        pauseTransition.play();
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
     * 文本域滚动到底部
     */
    public void scrollToBottom() {
        textArea.setScrollTop(Double.MAX_VALUE);
    }

    private void setTextFormatter() {
        textField.setTextFormatter(new TextFormatter<>(new DefaultStringConverter(), prefix, change -> {
            // 获取当前文本
            String text = change.getControlNewText();
            // 禁止更改路径前缀
            if (!text.startsWith(prefix)) {
                return null;
            }
            // 允许更改
            return change;
        }));
    }

    public void onMouseClicked() {
        String item = listView.getSelectionModel().getSelectedItem();
        if (item == null || item.isEmpty()) {
            return;
        }
        if (item.endsWith(JAR_SUFFIX)) {
            item = "java -jar " + item;
        }
        textField.setText(prefix + item);
    }

    private void clearHighlight() {
        textArea.setStyle("");
        // 清除选中状态
        textArea.deselect();
        lastSearchIndex = -1;
        // 滚动到顶部
        textArea.setScrollTop(0);
        listView.scrollTo(0);
        filteredList.setPredicate(p -> true);
        listView.refresh();
        listView.getSelectionModel().clearSelection();
        // 更新按钮状态
        updateMatchButtonsState();
    }

    public void onSearchInputChanged() {
        // 重置定时器
        searchDebounce.playFromStart();
    }

    /**
     * 搜索文本并高亮显示
     */
    public void search() {
        // 获取搜索框内容
        String searchText = searchTextField.getText();
        // 获取文本域内容
        String content = textArea.getText();
        // 如果搜索文本为空
        if (searchText.isEmpty()) {
            // 清除高亮并返回顶部
            clearHighlight();
            return;
        }
        lastSearchIndex = -1;
        String lowerContent = content.toLowerCase();
        String lowerSearchText = searchText.toLowerCase();
        // 过滤器设置为搜索文本
        filteredList.setPredicate(item -> item.toLowerCase().contains(lowerSearchText));
        // 刷新ListView以应用高亮
        listView.refresh();
        // 如果有匹配项
        if (!filteredList.isEmpty()) {
            // 滚动到第一个匹配项
            listView.scrollTo(0);
            listView.getSelectionModel().select(0);
        }
        // 尝试搜索下一个匹配项
        int searchIndex = findNextMatch(content, lowerContent, searchText, lowerSearchText, lastSearchIndex + 1);
        // 如果没找到 -> 从头开始搜索
        if (searchIndex == -1) {
            searchIndex = findNextMatch(content, lowerContent, searchText, lowerSearchText, 0);
        }
        // 如果找到了匹配文本
        if (searchIndex != -1) {
            // 更新上一次搜索位置
            lastSearchIndex = searchIndex;
            // 选中并高亮显示匹配文本
            textArea.selectRange(searchIndex, searchIndex + searchText.length());
            // 确保选中的文本可见 -> 不使用requestFocus
            double lineHeight = textArea.getFont().getSize() * 1.5;
            int lineNumber = textArea.getText().substring(0, searchIndex).split("\n").length - 1;
            textArea.setScrollTop(lineHeight * lineNumber);
            // 将焦点设置回搜索框
            searchTextField.requestFocus();
        } else {
            // 如果没有找到匹配项，清除选中状态
            textArea.deselect();
        }
        // 更新按钮状态
        updateMatchButtonsState();
    }

    /**
     * 滚动到匹配项所在行
     *
     * @param searchIndex 匹配项的索引
     */
    private void scrollToMatch(int searchIndex) {
        double lineHeight = textArea.getFont().getSize() * 1.5;
        int lineNumber = textArea.getText().substring(0, searchIndex).split("\n").length - 1;
        textArea.setScrollTop(lineHeight * lineNumber);
    }

    /**
     * 查找上一个匹配项
     *
     * @param content         内容
     * @param lowerContent    小写内容
     * @param searchText      搜索文本
     * @param lowerSearchText 小写搜索文本
     * @param startIndex      起始索引
     * @return 上一个匹配项的索引
     */
    private int findPreviousMatch(String content, String lowerContent, String searchText, String lowerSearchText,
                                  int startIndex) {
        while (startIndex >= 0) {
            int searchIndex = lowerContent.lastIndexOf(lowerSearchText, startIndex);
            if (searchIndex == -1) {
                return -1;
            }
            // 验证是否为完整匹配
            String actualMatch = content.substring(searchIndex, searchIndex + searchText.length());
            if (actualMatch.toLowerCase().equals(lowerSearchText)) {
                return searchIndex;
            }
            startIndex = searchIndex - 1;
        }
        return -1;
    }

    /**
     * 查找下一个匹配项
     *
     * @param content         内容
     * @param lowerContent    小写内容
     * @param searchText      搜索文本
     * @param lowerSearchText 小写搜索文本
     * @param startIndex      起始索引
     * @return 下一个匹配项的索引
     */
    private int findNextMatch(String content, String lowerContent, String searchText, String lowerSearchText,
                              int startIndex) {
        while (startIndex < lowerContent.length()) {
            int searchIndex = lowerContent.indexOf(lowerSearchText, startIndex);
            if (searchIndex == -1) {
                return -1;
            }
            // 验证是否为完整匹配
            String actualMatch = content.substring(searchIndex, searchIndex + searchText.length());
            if (actualMatch.toLowerCase().equals(lowerSearchText)) {
                return searchIndex;
            }
            startIndex = searchIndex + 1;
        }
        return -1;
    }

    /**
     * 更新匹配按钮状态
     */
    private void updateMatchButtonsState() {
        String searchText = searchTextField.getText();
        String content = textArea.getText();
        boolean hasMatches;
        // 匹配项数量
        int matchCount = 0;
        if (!searchText.isEmpty() && !content.isEmpty()) {
            String lowerContent = content.toLowerCase();
            String lowerSearchText = searchText.toLowerCase();
            hasMatches = lowerContent.contains(lowerSearchText);
            if (hasMatches) {
                matchCount = countMatches(content, searchText);
            }
        }
        // 匹配项数量<=1时禁用按钮
        boolean buttonDisable = matchCount <= 1;
        previousMatchButton.setDisable(buttonDisable);
        nextMatchButton.setDisable(buttonDisable);
    }

    /**
     * 处理菜单项点击事件
     *
     * @param actionEvent 动作事件
     */
    public void handleMenuItemClick(ActionEvent actionEvent) {
        MenuItem menuItem = (MenuItem) actionEvent.getSource();
        String text = menuItem.getText();
        if (text.contains("[PID]")) {
            // 创建文本输入对话框
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("进程终止");
            dialog.setHeaderText("请输入要终止的进程ID");
            dialog.setContentText("PID");
            dialog.initOwner(root.getScene().getWindow());
            // 显示对话框并等待结果
            Optional<String> result = dialog.showAndWait();
            // 如果pid存在
            if (result.isPresent()) {
                String pid = result.get().trim();
                String replaceValue = text.replace("[PID]", pid);
                textField.setText(prefix + replaceValue);
                if (!pid.isEmpty()) {
                    execute();
                }
            } else {
                textField.setText(prefix + text);
            }
            return;
        }
        textField.setText(prefix + text);
        for (String item : Arrays.asList("apktool", "install", "uninstall", "packages")) {
            if (text.contains(item)) {
                return;
            }
        }
        execute();
    }

    public void previousMatch() {
        String searchText = searchTextField.getText();
        String content = textArea.getText();
        if (searchText.isEmpty() || content.isEmpty()) {
            return;
        }
        String lowerContent = content.toLowerCase();
        String lowerSearchText = searchText.toLowerCase();
        int searchIndex = findPreviousMatch(content, lowerContent, searchText, lowerSearchText, lastSearchIndex - 1);
        // 如果没找到 -> 从末尾开始搜索
        if (searchIndex == -1) {
            searchIndex = findPreviousMatch(content, lowerContent, searchText, lowerSearchText, content.length());
        }
        // 如果找到了匹配文本
        if (searchIndex != -1) {
            lastSearchIndex = searchIndex;
            textArea.selectRange(searchIndex, searchIndex + searchText.length());
            scrollToMatch(searchIndex);
            searchTextField.requestFocus();
        }
    }

    public void nextMatch() {
        String searchText = searchTextField.getText();
        String content = textArea.getText();
        if (searchText.isEmpty() || content.isEmpty()) {
            return;
        }
        String lowerContent = content.toLowerCase();
        String lowerSearchText = searchText.toLowerCase();
        int searchIndex = findNextMatch(content, lowerContent, searchText, lowerSearchText, lastSearchIndex + 1);
        // 如果没找到 -> 从头开始搜索
        if (searchIndex == -1) {
            searchIndex = findNextMatch(content, lowerContent, searchText, lowerSearchText, 0);
        }
        // 如果找到了匹配文本
        if (searchIndex != -1) {
            lastSearchIndex = searchIndex;
            textArea.selectRange(searchIndex, searchIndex + searchText.length());
            scrollToMatch(searchIndex);
            searchTextField.requestFocus();
        }
    }

    public void tabCompletion(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.TAB) {
            if (tabCompletionIndex == -1) {
                String input = textField.getText().substring(prefix.length()).trim();
                tabCompletionCandidates.clear();
                if (!input.isEmpty()) {
                    for (String item : listView.getItems()) {
                        if (item.startsWith(input)) {
                            tabCompletionCandidates.add(item);
                        }
                    }
                }
            }
            if (!tabCompletionCandidates.isEmpty()) {
                tabCompletionIndex = (tabCompletionIndex + 1) % tabCompletionCandidates.size();
                String candidate = tabCompletionCandidates.get(tabCompletionIndex);
                String value = candidate;
                if (candidate.endsWith(JAR_SUFFIX)) {
                    value = "java -jar " + candidate;
                }
                textField.setText(prefix + value);
                textField.positionCaret(textField.getText().length());
            }
            keyEvent.consume();
        } else {
            tabCompletionIndex = -1;
        }
    }

    private class HighlightListCell extends ListCell<String> {
        private final TextFlow textFlow = new TextFlow();

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                String searchText = searchTextField.getText().trim().toLowerCase();
                if (searchText.isEmpty()) {
                    // 无搜索词时正常显示
                    textFlow.getChildren().setAll(new Text(item));
                } else {
                    // 处理高亮逻辑
                    String lowerItem = item.toLowerCase();
                    int startIndex = lowerItem.indexOf(searchText);
                    if (startIndex != -1) {
                        // 分割文本: 匹配部分高亮为红色
                        Text before = new Text(item.substring(0, startIndex));
                        Text highlight = new Text(item.substring(startIndex, startIndex + searchText.length()));
                        Text after = new Text(item.substring(startIndex + searchText.length()));
                        highlight.setFill(Color.RED);
                        textFlow.getChildren().setAll(before, highlight, after);
                    } else {
                        textFlow.getChildren().setAll(new Text(item));
                    }
                }
                setGraphic(textFlow);
            }
        }
    }
}