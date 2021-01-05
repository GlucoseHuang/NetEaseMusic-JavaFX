package gui.leftPane;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

// 一个条状播放列表Pane
public class PlaylistPane extends Pane {

    private ImageView img;
    private Label label;

    // 构造方法
    public PlaylistPane(Image image, String title) {

        // 设定大小
        this.setPrefSize(200, 30);

        // 初始化img和label
        initImg(image);
        initLabel(title);

        // 将img和label添加到PlaylisyPane
        this.getChildren().addAll(img, label);

        // 鼠标进入时，改变背景颜色和文字颜色
        this.setOnMouseEntered(event -> {
            setStyle("-fx-background-color: #ec4141");
            label.setTextFill(Paint.valueOf("#ffffff"));
        });

        // 鼠标移出时，还原背景颜色和文字颜色
        this.setOnMouseExited(event -> {
            setStyle("-fx-background-color: null");
            label.setTextFill(Paint.valueOf("#000000"));
        });
    }

    // 初始化img
    private void initImg(Image image) {

        // 实例化img
        img = new ImageView(image);

        // 设置位置、大小
        img.setFitWidth(15);
        img.setFitHeight(15);
        img.setLayoutX(15);
        img.setLayoutY(7.5);
    }

    // 初始化label
    private void initLabel(String title) {

        // 实例化label
        label = new Label(title);

        // 设置字体、大小
        label.setFont(new Font("Microsoft YaHei", 14));
        label.setPrefSize(150, 30);

        // 设置位置
        label.setLayoutX(50);
        label.setLayoutY(0);
    }
}