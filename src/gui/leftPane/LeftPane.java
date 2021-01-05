package gui.leftPane;

import basic.Song;
import gui.playlistBlockListPane.TopListPane;
import gui.songListPane.SongListPane;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.SQLException;

// 用来装playlistListPane和songDetailPane的Pane，并且为songDetailPane和playlistListPane建立联系
public class LeftPane extends Pane {

    // 所需要的组件
    private SongDetailPane songDetailPane;
    private final PlaylistListPane playlistListPane;

    // 是否需要初始化songDetailPane
    private boolean needToInitSongDetailPane = true;

    // 构造方法
    public LeftPane() throws SQLException {
        playlistListPane = new PlaylistListPane();
        this.getChildren().add(playlistListPane);
    }

    // 初始化或更改
    public void showSongDetail(Song song) throws SQLException {

        // 是否需要先初始化歌曲信息界面
        if (needToInitSongDetailPane) {

            // 如果需要初始化，则进行初始化，并改变needToInitSongDetailPane为false
            songDetailPane = new SongDetailPane(song, playlistListPane);
            playlistListPane.setSongDetailPane(songDetailPane);
            songDetailPane.setLayoutX(0);
            songDetailPane.setLayoutY(550 - 60);
            this.getChildren().add(songDetailPane);
            needToInitSongDetailPane = false;

        } else {

            // 如果不需要初始化，则调用changeSong
            songDetailPane.changeSong(song);
        }
    }

    // 设置playlistListPane所需的SongListPane
    public void setSongListPane(SongListPane songListPane) {
        playlistListPane.setSongListPane(songListPane);
    }

    // 设定必要的scene和stage
    public void setEssentials(Stage stage, Scene scene1, Scene scene2, Scene scene3, TopListPane topListPane) {
        playlistListPane.setEssentials(stage, scene1, scene2, scene3, topListPane);
    }

    // 获取playlistListPane
    public PlaylistListPane getPlaylistListPane() {
        return playlistListPane;
    }
}