package gui.playlistBlockListPane;

import basic.Playlist;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

// 块状的播放列表的Pane
public class PlaylistBlockPane extends Pane {

    // PlaylistBlockPane的三个组件
    private ImageView img;
    private Label title;
    private Label playCount;

    // 常量
    private final double width = 180;
    private final double height = 180;
    private final double textHeight = 30;
    private final double playCountHeight = 20;
    private final double sumH = height + textHeight + playCountHeight;

    // playlistBlockPane所对应的playlist
    private final Playlist playlist;

    // 构造方法
    public PlaylistBlockPane(Playlist playlist) {

        // 设置对应的playlist
        this.playlist = playlist;

        // 设置大小
        this.setPrefSize(width, sumH);

        // 初始化img, title, playCount
        initImg();
        initTitle();
        initPlayCount();

        // 将img, title, playCount添加到PlaylistBlockPane
        this.getChildren().addAll(img, title, playCount);
    }

    // 初始化img图像
    private void initImg() {

        // 实例化img
        img = new ImageView(new Image(playlist.getImgSrc()));

        // 设置位置和宽高
        img.setLayoutX(0);
        img.setLayoutY(0);
        img.setFitHeight(height);
        img.setFitWidth(width);

        // 鼠标进入时，设为手形
        img.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，设为默认样式
        img.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
    }

    // 初始化title标签
    private void initTitle() {

        // 实例化title
        title = new Label(playlist.getPlaylistName());

        // 设置字体
        title.setFont(new Font("Microsoft YaHei", 20));

        // 设置位置和宽高
        title.setLayoutX(0);
        title.setLayoutY(height);
        title.setPrefSize(width, textHeight);
    }

    // 初始化playCount标签
    private void initPlayCount() {

        // 实例化playCount
        playCount = new Label("播放量：" + playlist.playCountString());

        // 设置字体和颜色
        playCount.setFont(new Font("Microsoft YaHei", 15));
        playCount.setTextFill(Paint.valueOf("#08ad53"));

        // 设置位置和宽高
        playCount.setPrefSize(width, playCountHeight);
        playCount.setLayoutX(0);
        playCount.setLayoutY(height + textHeight);
    }

    // 提供给topListPane使用，方便排版
    public double getSumH() {
        return sumH;
    }

    // 提供给topListPane使用，方便排版
    public double getBlockPaneWidth() {
        return width;
    }

    // 获取当前PlaylistBlockPane的playlist
    public Playlist getPlaylist() {
        return playlist;
    }
}