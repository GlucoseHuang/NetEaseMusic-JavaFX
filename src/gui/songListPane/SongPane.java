package gui.songListPane;

import basic.Song;
import data.DataAPI;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

// 歌曲列表SongListPane中的一首歌
public class SongPane extends Pane {

    // songPane中的组件
    private Label sequence, songName, singer, duration;
    private ImageView favTag, download;

    // songPane的宽度
    private final double width = 800;

    // songPane对应的song
    private Song song;

    // 其所属的songListPane
    private final SongListPane songListPane;

    // 构造方法
    public SongPane(int index, Song song, SongListPane songListPane) throws SQLException {

        // 设置需要的内容
        this.song = song;
        this.songListPane = songListPane;

        // 设置宽高和背景颜色
        this.setPrefSize(width, 30);
        if (index % 2 == 0) {
            setStyle("-fx-background-color: #fafafa;");
        } else {
            setStyle("-fx-background-color: #ffffff;");
        }

        // 初始化sequence, songName, singer, duration, favTag, download
        initSequence(index);
        initSongName();
        initSinger();
        initDuration();
        initFavTag();
        initDownload();

        // 将sequence, songName, singer, duration, favTag, download添加到SongPane
        this.getChildren().addAll(sequence, songName, singer, duration, favTag, download);

        // 当鼠标进入songPane时，favTag和download设为可见
        this.setOnMouseEntered(event -> {
            favTag.setVisible(true);
            download.setVisible(true);
        });

        // 当鼠标移出songPane时，favTag和download设为不可见
        this.setOnMouseExited(event -> {
            favTag.setVisible(false);
            download.setVisible(false);
        });
    }

    // 初始化sequence
    private void initSequence(int index) {

        // 实例化sequence
        sequence = new Label(index + "");

        // 设置字体和颜色
        sequence.setFont(new Font("Microsoft YaHei", 20));
        sequence.setTextFill(Paint.valueOf("#a4a4a4"));

        // 设置位置
        sequence.setLayoutX(width / 64);
        sequence.setLayoutY(2.5);
    }

    // 初始化songName
    private void initSongName() {

        // 实例化songName
        songName = new Label(song.getSongName());

        // 设置字体和颜色
        songName.setFont(new Font("Microsoft YaHei", 20));
        songName.setStyle("-fx-text-fill: #000000;");

        // 设置位置和宽度
        songName.setLayoutX(width / 8);
        songName.setLayoutY(2.5);
        songName.setPrefWidth(3 * width / 8);

        // 鼠标进入时变成手形，字体变色，songListPane.song设为这首歌，用于单击播放
        songName.setOnMouseEntered(event -> {
            songName.setStyle("-fx-text-fill: #08ad53;");
            setCursor(Cursor.HAND);
            songListPane.setSong(song);
        });

        // 鼠标移出时变成手形，字体变色
        songName.setOnMouseExited(event -> {
            songName.setStyle("-fx-text-fill: #000000;");
            setCursor(Cursor.DEFAULT);
        });
    }

    // 初始化Singer
    private void initSinger() {

        // 实例化singer
        singer = new Label(song.getArtistName());

        // 设置字体和颜色
        singer.setFont(new Font("Microsoft YaHei", 20));
        singer.setStyle("-fx-text-fill: #000000;");

        // 设置位置和宽度
        singer.setLayoutX(5 * width / 8);
        singer.setLayoutY(2.5);
        singer.setPrefWidth(width / 4);
    }

    // 初始化duration
    private void initDuration() {

        // 实例化duration
        duration = new Label(song.durationString());

        // 设置字体和颜色
        duration.setFont(new Font("Microsoft YaHei", 20));
        duration.setTextFill(Paint.valueOf("#a4a4a4"));

        // 设置位置
        duration.setLayoutX(7 * width / 8);
        duration.setLayoutY(2.5);
    }

    // 初始化favTag图像
    private void initFavTag() throws SQLException {

        // 实例化favTag
        if (DataAPI.isFavSong(song.getSongID())) {
            favTag = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/lovedPlaylist.png")).toExternalForm()));
        } else {
            favTag = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/lovePlaylist.png")).toExternalForm()));
        }

        // 设置位置和宽高
        favTag.setLayoutX(5 * width / 8 - 57);
        favTag.setLayoutY(3.5);
        favTag.setFitHeight(23);
        favTag.setFitWidth(24);

        // 鼠标进入时，变成手形
        favTag.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，变成默认样式
        favTag.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

        // 鼠标点击时
        favTag.setOnMouseClicked(event -> {

            // 获取歌曲详细信息
            try {
                song = DataAPI.getSong(song.getSongID());
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }

            // 判断这首歌是不是在喜欢列表中
            try {
                if (DataAPI.isFavSong(song.getSongID())) {

                    // 如果在，则取消喜欢，更改图片，让songListPane从playlistListPane中删除这首歌
                    favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/lovePlaylist.png")).toExternalForm()));
                    try {
                        songListPane.getPlaylistListPane().removeFavSong(song);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    // 如果不在，则喜欢，更改图片，让songListPane向playlistListPane中添加这首歌
                    favTag.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/lovedPlaylist.png")).toExternalForm()));
                    try {
                        songListPane.getPlaylistListPane().addFavSong(song);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // 设置favTag不可见
        favTag.setVisible(false);
    }

    // 初始化download图像
    private void initDownload() {

        // 实例化download图像
        download = new ImageView(Objects.requireNonNull(getClass().getResource("../image/download.png")).toExternalForm());

        // 设置位置和宽高
        download.setLayoutX(5 * width / 8 - 27.5);
        download.setLayoutY(2.5);
        download.setFitHeight(25);
        download.setFitWidth(25);

        // 鼠标进入时，变成手形
        download.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，变成普通样式
        download.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

        // 鼠标单击时，调用songListPane.downloadSong(song)
        download.setOnMouseClicked(event -> {
            try {
                song = DataAPI.getSong(song.getSongID());
                songListPane.downloadSong(song);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        // 设置download不可见
        download.setVisible(false);
    }

    // 用于为songName设置单击鼠标调用player播放
    public Label getSongName() {
        return songName;
    }
}
