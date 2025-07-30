package util;


import entity.User;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Objects;

/**
 * 单元格渲染器
 *
 * @author tianxing
 */
public class CellRenderer implements Callback<ListView<User>, ListCell<User>> {
    @Override
    public ListCell<User> call(ListView<User> listView) {
        return new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean bln) {
                super.updateItem(user, bln);
                setGraphic(null);
                setText(null);
                if (user != null) {
                    HBox hBox = new HBox();
                    Text name = new Text(user.getName());
                    ImageView statusImageView = new ImageView();
                    ImageView pictureImageView = new ImageView();
                    Image image = new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("png/" + user.getPicture().toLowerCase() + ".png")).toString(), 50, 50, true, true);
                    pictureImageView.setImage(image);
                    hBox.getChildren().addAll(statusImageView, pictureImageView, name);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hBox);
                }
            }
        };
    }
}