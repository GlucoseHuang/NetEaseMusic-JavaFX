package gui.playerPane;

import basic.Song;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

// 当前播放列表中一首歌的Pane
public class PlayingSongPane extends Pane {

    // 所需要的组件
    private Label songName, singer, duration;
    private ImageView remove;

    // 要被添加的Song
    private final Song song;

    // 当前的Player
    private final PlayerPane playerPane;

    // 歌曲之前的样式（第奇数和第偶数个不一样）
    private String originalStyle;

    // 这首歌是否被选中
    private boolean isChosen = false;

    // 为了将其余所有歌设为普通样式，需要songListPane.getChildren()
    private final Pane subPane;

    // 构造方法
    public PlayingSongPane(Song song, double width, double height, Pane subPane, PlayerPane playerPane) {

        // 设定SongPane的宽高
        this.setPrefSize(width, height);

        // 设定SongPane的player, songList, song
        this.playerPane = playerPane;
        this.subPane = subPane;
        this.song = song;

        // 初始化Label
        Font font = new Font("华文中宋", 16);
        initSongName(song, font, height, width);
        initSinger(song, font, height, width);
        initDuration(song, font, height, width);
        initRemove(song, height, width);

        // 初始化鼠标事件
        initMouseAction();

        // 将所有内容添加到Pane
        this.getChildren().addAll(songName, singer, duration, remove);
    }

    // 初始化songName标签
    public void initSongName(Song song, Font font, double height, double width) {

        // 标签内容为song.songName
        songName = new Label(song.getSongName());

        // 设置字体
        songName.setFont(font);

        //设置位置和宽度
        songName.setLayoutX(width / 16);
        songName.setLayoutY((height - font.getSize()) / 2);
        songName.setPrefWidth(5 * width / 8);
    }

    // 初始化singer标签
    public void initSinger(Song song, Font font, double height, double width) {

        // 标签内容为song.singer
        singer = new Label(song.getArtistName());

        //设置字体
        singer.setFont(font);

        //设置位置和宽度
        singer.setLayoutX(11 * width / 16);
        singer.setLayoutY((height - font.getSize()) / 2);
        singer.setPrefWidth(3 * width / 16);
    }

    // 初始化duration标签
    public void initDuration(Song song, Font font, double height, double width) {

        // 标签内容为song.durationString()
        duration = new Label(song.durationString());

        // 设置字体
        duration.setFont(font);

        //设置位置
        duration.setLayoutX(14 * width / 16);
        duration.setLayoutY((height - font.getSize()) / 2);
    }

    // 初始化remove
    public void initRemove(Song song, double height, double width) {
        remove = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/removeSong.png")).toExternalForm()));

        // 设置大小和位置
        remove.setFitWidth(22);
        remove.setFitHeight(22);
        remove.setLayoutX(10 * width / 16);
        remove.setLayoutY(height - 21);

        // 单击时移除这首歌
        remove.setOnMouseClicked(event -> playerPane.getPlayingSongListPane().deleteSong(song.getSongID()));

        // 鼠标进入时，鼠标变成手形
        remove.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，鼠标变成默认样式
        remove.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

        // 设置不可见
        remove.setVisible(false);
    }

    // 为Pane设定鼠标事件
    private void initMouseAction() {
        this.setOnMouseClicked(event -> {

            // 将其他歌的样式设置为普通样式
            for (Node songPane : subPane.getChildren()) {
                ((PlayingSongPane) songPane).isChosen = false;
                songPane.setStyle(((PlayingSongPane) songPane).getOriginalStyle());
            }

            // 将这首歌设置为已选中，并设为深灰色
            isChosen = true;
            setStyle("-fx-background-color: #cacaca;");

            // 如果不是单击，就播放这首歌
            if (event.getClickCount() > 1) {
                try {
                    playerPane.playMusic(song);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // 鼠标进入时，如果这首歌没有被选中，则变成浅灰色，显示remove
        this.setOnMouseEntered(event -> {
            if (!isChosen) {
                setStyle("-fx-background-color: #eeeeee");
            }
            remove.setVisible(true);
        });

        // 鼠标移出时，如果这首歌没有被选中，则变成基本样式，隐藏remove
        this.setOnMouseExited(event -> {
            if (!isChosen) {
                setStyle(originalStyle);
            }
            remove.setVisible(false);
        });
    }

    // 设置初始的style，便于在更改后还原
    public void setOriginalStyle(String style) {
        originalStyle = style;
    }

    // 获取初始的style
    public String getOriginalStyle() {
        return originalStyle;
    }

    // 是否被选中
    public boolean isChosen() {
        return isChosen;
    }
}
