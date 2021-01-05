package gui.playlistBlockListPane;

import basic.Playlist;
import data.DataAPI;
import gui.songListPane.SongListPane;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

// 热榜的Pane，其中放置的是PlaylistBlockPane
public class TopListPane extends Pane {

    // 用于切换场景，改变songListPane的内容
    private Stage stage;
    private Scene scene;
    private SongListPane songListPane;

    // 当前选中的playlist
    private Playlist currentPlaylist;

    // 构造方法
    public TopListPane() throws IOException, SQLException {

        // 设定宽高和背景颜色
        this.setWidth(800);
        this.setHeight(550);
        this.setStyle("-fx-background-color: #ffffff");
        // 获取topList
        Playlist[] topList = DataAPI.getTopList();

        // 记录最大的height，用于设定滚轮事件
        double maxHeight = 0;

        // 将每个topList以PlaylistBlockPane形式放在对应位置
        for (int i = 0; i < topList.length; i++) {

            // 实例化PlaylistBlockPane
            PlaylistBlockPane playlistBlockPane = new PlaylistBlockPane(topList[i]);

            // 设置playlistBlockPane位置
            playlistBlockPane.setLayoutX((playlistBlockPane.getBlockPaneWidth() + 15) * (float) (i % 4) + 15);
            playlistBlockPane.setLayoutY((playlistBlockPane.getSumH() + 15) * (float) (i / 4) + 15);

            // 更新maxHeight，获取到最终的maxHeight，用于设定滚轮事件
            maxHeight = Double.max(maxHeight, playlistBlockPane.getLayoutY() + playlistBlockPane.getSumH());

            // 鼠标进入playlistBlockPane时，设定currentPlaylist为playlistBlockPane.playlist
            playlistBlockPane.setOnMouseEntered(event -> currentPlaylist = playlistBlockPane.getPlaylist());

            // 鼠标移出playlistBlockPane时，设定currentPlaylist为null
            playlistBlockPane.setOnMouseExited(event -> currentPlaylist = null);

            // 将playlistBlockPane添加到TopListPane
            this.getChildren().add(playlistBlockPane);
        }

        // 设置鼠标滚轮事件
        double finalMaxHeight = maxHeight;
        this.setOnScroll(event -> {
            double newY = getLayoutY() + event.getDeltaY();
            newY = Double.max(550 - finalMaxHeight, newY);
            newY = Double.min(0, newY);
            setLayoutY(newY);
        });

        // 鼠标点击时，如果currentPlaylist不为空，则切换场景，展示playlist的详细信息
        this.setOnMouseClicked(event -> {
            if (currentPlaylist != null) {
                try {
                    songListPane.setSongs(DataAPI.getSongsOfPlaylist(currentPlaylist.getPlaylistID()), currentPlaylist);
                    stage.setScene(scene);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 为topListPane提供所需的stage和scene
    public void setEssentials(Stage stage, Scene scene, SongListPane songListPane) {
        this.songListPane = songListPane;
        this.stage = stage;
        this.scene = scene;
    }
}
