package gui.songListPane;

import basic.Playlist;
import data.DataAPI;
import gui.playerPane.PlayerPane;
import gui.leftPane.PlaylistListPane;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.SQLException;

// 播放列表的头部，显示播放列表的基本信息
public class PlaylistHeaderPane extends Pane {

    // 所需的组件
    private Label title;
    private Label playCount;
    private ImageView img;
    private ImageView fav;
    private ImageView playAll;

    // playlistHeaderPane正在展示的Playlist
    private Playlist playlist;

    // 用于player.addFavPlaylist(playlist)
    private final PlayerPane playerPane;

    // 用于更改leftPane中喜欢的歌单
    private final PlaylistListPane playlistListPane;

    // 构造方法
    public PlaylistHeaderPane(PlayerPane playerPane, PlaylistListPane playlistListPane) {

        double width = 800;
        double height = 250;

        this.setPrefSize(width, height);

        // 设置listPane和player
        this.playlistListPane = playlistListPane;
        this.playerPane = playerPane;

        // 初始化所有组件
        initImg(width);
        initPlayAll();
        initFav();
        initTitle();
        initPlayCount();

        // 将所有组件添加到playlistHeaderPane
        this.getChildren().addAll(img, playAll, fav, title, playCount);
    }

    // 初始化Img图像
    public void initImg(double width) {

        // 实例化img
        img = new ImageView();

        // 设置位置和宽高
        img.setLayoutX(width / 64);
        img.setLayoutY(25);
        img.setFitWidth(200);
        img.setFitHeight(200);
    }

    // 初始化playCount
    private void initPlayCount() {

        // 实例化playCount
        playCount = new Label("");

        // 设置位置
        playCount.setLayoutX(250);
        playCount.setLayoutY(80);

        // 设置字体和颜色
        playCount.setFont(new Font("Microsoft YaHei", 20));
        playCount.setTextFill(Paint.valueOf("#08ad53"));
    }

    // 初始化title
    private void initTitle() {

        // 实例化title
        title = new Label("");

        // 设置位置和宽高
        title.setLayoutX(250);
        title.setLayoutY(25);
        title.setPrefWidth(500);

        // 设置字体
        title.setFont(new Font("Microsoft YaHei", 30));
    }

    // 初始化fav图像
    private void initFav() {

        // 实例化fav
        fav = new ImageView(new Image(getClass().getResource("../image/fav.png").toExternalForm()));

        // 设置位置和宽高
        fav.setLayoutX(520);
        fav.setLayoutY(175);
        fav.setFitWidth(180);
        fav.setFitHeight(53);

        // 鼠标进入时，变成手形
        fav.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，变成默认样式
        fav.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

        // 鼠标单击时
        fav.setOnMouseClicked(event -> {

            // 判断歌单是否已被收藏
            try {
                if (DataAPI.isFavPlaylist(playlist.getPlaylistID())) {

                    // 如果已被收藏，则取消收藏，从leftPane的playlistListPane中删除该歌单，更改图片
                    try {
                        playlistListPane.delFavPlaylist(playlist);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    fav.setImage(new Image(getClass().getResource("../image/fav.png").toExternalForm()));

                } else {

                    // 如果未被收藏，则收藏，向leftPane的playlistListPane中添加该歌单，更改图片
                    try {
                        playlistListPane.addFavPlaylist(playlist);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    fav.setImage(new Image(getClass().getResource("../image/faved.png").toExternalForm()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 初始化playAll图像
    private void initPlayAll() {

        // 实例化playAll
        playAll = new ImageView(new Image(getClass().getResource("../image/playAll.png").toExternalForm()));

        // 设置位置和宽高
        playAll.setLayoutX(250);
        playAll.setLayoutY(175);
        playAll.setFitWidth(180);
        playAll.setFitHeight(53);

        // 鼠标进入时，变成手形
        playAll.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，变成默认样式
        playAll.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

        // 鼠标单击时，将当前播放列表替换为该歌单进行播放
        playAll.setOnMouseClicked(event -> {
            try {
                playerPane.addPlaylist(playlist);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 设置playlistHeaderPane中的playlist
    public void setPlaylist(Playlist playlist) throws SQLException {

        // 设置playlist
        this.playlist = playlist;

        // 设置img, title, playCount, playlist.isFav, fav
        img.setImage(new Image(playlist.getImgSrc()));
        title.setText(playlist.getPlaylistName());
        playCount.setText("播放量：" + playlist.playCountString());

        // 如果该歌单没有被收藏，图片为fav.png，否则为faved.png
        if (!DataAPI.isFavPlaylist(playlist.getPlaylistID())) {
            fav.setImage(new Image(getClass().getResource("../image/fav.png").toExternalForm()));
        } else {
            fav.setImage(new Image(getClass().getResource("../image/faved.png").toExternalForm()));
        }
    }
}
