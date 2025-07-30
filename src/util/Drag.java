package util;


import app.Chat;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;

/**
 * 鼠标拖拽界面的处理
 *
 * @author tianxing
 */
public class Drag {
    private double xOffset;
    private double yOffset;

    public void handleDrag(BorderPane borderPane) {
        borderPane.setOnMousePressed(event -> {
            xOffset = Chat.getPrimaryStage().getX() - event.getScreenX();
            yOffset = Chat.getPrimaryStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });
        borderPane.setOnMouseDragged(event -> {
            Chat.getPrimaryStage().setX(event.getScreenX() + xOffset);
            Chat.getPrimaryStage().setY(event.getScreenY() + yOffset);

        });
        borderPane.setOnMouseReleased(event -> borderPane.setCursor(Cursor.DEFAULT));
    }
}
