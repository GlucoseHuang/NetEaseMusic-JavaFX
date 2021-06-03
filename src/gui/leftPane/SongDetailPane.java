package gui.leftPane;

import basic.Song;
import data.DataAPI;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

// 歌曲详细列表Pane（左下角的）
public class SongDetailPane extends Pane {

    // 需要的四个组件
    private ImageView img;
    private ImageView favTag;
    private Label songName;
    private Label singer;

    // 当前播放的song
    private Song currentSong;

    // 其所属的PlaylistListPane
    private final PlaylistListPane playlistListPane;

    // 构造方法
    public SongDetailPane(Song song, PlaylistListPane playlistListPane) throws SQLException {

        // 设定PlaylistListPane
        this.playlistListPane = playlistListPane;

        // 设定位置、宽高和背景颜色
        this.setLayoutX(0);
        this.setPrefSize(200, 60);
        this.setStyle("-fx-background-color: #f3f3f3;");

        // 设定当前播放的Song
        this.currentSong = song;

        // 初始化img, songName, singer, favTag
        initImg(song);
        initSongName(song);
        initSinger(song);
        initFavTag(song);

        // 将img, songName, singer, favTag添加到SongDetailPane中
        this.getChildren().addAll(img, songName, singer, favTag);
    }

    // 初始化favSong
    private void initImg(Song song) {

        // 实例化img
        img = new ImageView(new Image(song.getImgSrc()));

        // 设定img的位置和长宽
        img.setLayoutX(5);
        img.setLayoutY(5);
        img.setFitHeight(50);
        img.setFitWidth(50);
    }

    // 初始化songName
    private void initSongName(Song song) {

        // 实例化songName
        songName = new Label(song.getSongName());

        // 设置字体，大小，字体颜色
        songName.setFont(new Font("Microsoft YaHei", 13));
        songName.setPrefSize(120, 25);
        songName.setStyle("-fx-text-fill: #000000");

        // 设置位置
        songName.setLayoutX(65);
        songName.setLayoutY(5);
    }

    // 初始化singer
    private void initSinger(Song song) {

        // 实例化singer
        singer = new Label(song.getArtistName());

        // 设置字体，大小，字体颜色
        singer.setFont(new Font("Microsoft YaHei", 12));
        singer.setPrefSize(120, 25);
        singer.setStyle("-fx-text-fill: #545454");

        // 设置位置
        singer.setLayoutX(65);
        singer.setLayoutY(30);
    }

    // 初始化favTag
    private void initFavTag(Song song) throws SQLException {

        // 实例化favTag，按照song.isFav设定图片内容
        if (DataAPI.isFavSong(song.getSongID())) {
            favTag = new ImageView((new Image(Objects.requireNonNull(getClass().getResource("../image/favSong.png")).toExternalForm())));
        } else {
            favTag = new ImageView((new Image(Objects.requireNonNull(getClass().getResource("../image/notfav.png")).toExternalForm())));
        }

        // 设定图片位置，长宽
        favTag.setFitWidth(15);
        favTag.setFitHeight(15);
        favTag.setLayoutY(40);
        favTag.setLayoutX(180);

        // 鼠标进入时，变为手形
        favTag.setOnMouseEntered(event -> favTag.setCursor(Cursor.HAND));

        // 鼠标移出时，变为默认样式
        favTag.setOnMouseExited(event -> favTag.setCursor(Cursor.DEFAULT));

        // 鼠标单击时
        favTag.setOnMouseClicked(event -> {

            // 判断这首歌是否已被喜欢
            try {
                if (DataAPI.isFavSong(currentSong.getSongID())) {

                    // 如果这首歌已经被喜欢，则设为不喜欢
                    favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/notfav.png")).toExternalForm()));
                    try {
                        playlistListPane.removeFavSong(currentSong);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    // 如果这首歌未被喜欢，则设为喜欢
                    favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/favSong.png")).toExternalForm()));
                    try {
                        playlistListPane.addFavSong(currentSong);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 改变SongDetailPane中的歌曲
    public void changeSong(Song song) throws SQLException {

        // 改变当前歌曲
        this.currentSong = song;

        // 获取新的img图像
        img.setImage(new Image(song.getImgSrc() + "?50y50"));

        // 按song.isFav设定favTag图像
        if (DataAPI.isFavSong(song.getSongID())) {
            favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/favSong.png")).toExternalForm()));
        } else {
            favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/notfav.png")).toExternalForm()));
        }

        // 设定singer文本内容
        singer.setText(song.getArtistName());

        // 设定songName文本内容
        songName.setText(song.getSongName());
    }

    // 刷新favTag图像
    public void refreshTavTag() throws SQLException {
        // 按song.isFav设定favTag图像
        if (DataAPI.isFavSong(currentSong.getSongID())) {
            favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/favSong.png")).toExternalForm()));
        } else {
            favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/notfav.png")).toExternalForm()));
        }
    }
}