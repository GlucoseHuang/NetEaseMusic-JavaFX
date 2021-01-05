package gui.leftPane;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

// 显示“发现”、“我的音乐”、“我的收藏”的Pane
public class InfoPane extends Pane {

    // 包含的label
    private Label info;

    // 构造方法
    public InfoPane(String text) {

        // 设定位置、大小
        this.setLayoutX(0);
        this.setPrefSize(200, 40);

        // 初始化label
        initInfo(text);

        // 将info添加到InfoPane
        this.getChildren().add(info);
    }

    // 初始化info
    private void initInfo(String text) {

        // 实例化info
        info = new Label(text);

        // 设定字体，颜色，位置，大小
        info.setFont(new Font("Microsoft YaHei", 14));
        info.setLayoutX(5);
        info.setLayoutY(10);
        info.setPrefSize(195, 10);
        info.setStyle("-fx-text-fill: #000000");
    }
}