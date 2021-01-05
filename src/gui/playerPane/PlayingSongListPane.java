package gui.playerPane;

import basic.Song;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

// 当前播放列表Pane
public class PlayingSongListPane extends Pane {

    // PlayingSongListPane中所包含的两个Pane
    private Pane titlePane; // “播放列表”四个字的Pane
    private Pane subPane; // PlayingSongListPane的子Pane，用于显示当前歌曲列表

    // “播放列表”四个字
    private Label info;

    // 歌曲数量
    private int length = 0;

    // PlayingSongListPane所属的Player，用于赋给SongPane播放音乐
    private final PlayerPane playerPane;

    // 在播放列表中的Song的SongID
    private final List<String> songIDs = new ArrayList<>();

    // 构造方法
    public PlayingSongListPane(double width, double height, PlayerPane playerPane) {

        // 设置位置和宽高
        this.setWidth(width);
        this.setHeight(height);
        this.setLayoutY(0);

        // 设置player
        this.playerPane = playerPane;

        // 初始化title和subPane
        initTitle();
        initSubPane();

        // 将title和subPane添加至PlayingSongListPane
        this.getChildren().addAll(titlePane, subPane);

        // 鼠标移出时，将没有选中的歌恢复为基本样式
        this.setOnMouseExited(event -> {
            for (Node songPane : subPane.getChildren()) {
                if (!((PlayingSongPane) songPane).isChosen()) {
                    songPane.setStyle((((PlayingSongPane) songPane).getOriginalStyle()));
                }
            }
        });
    }

    // 初始化title
    public void initTitle() {

        // 实例化title
        titlePane = new Pane();

        // 设置title的位置和宽高
        titlePane.setLayoutX(0);
        titlePane.setLayoutY(0);
        titlePane.setPrefSize(this.getWidth(), 40);

        // 初始化info，将其添加进title
        initInfo();
        titlePane.getChildren().add(info);

        // 设置title的背景色
        titlePane.setStyle("-fx-background-color: #ec4141");
    }

    // 初始化info
    private void initInfo() {

        // 实例化info
        info = new Label("播放列表");

        // 设定字体，大小，颜色
        info.setFont(new Font("Microsoft YaHei", 20));
        info.setPrefSize(100, 30);
        info.setStyle("-fx-text-fill: white;");

        // 设定位置
        info.setLayoutX(this.getWidth() / 2 - 40);
        info.setLayoutY(5);
    }

    // 初始化subPane
    public void initSubPane() {

        // 实例化subPane
        subPane = new Pane();

        // 设置位置
        subPane.setLayoutY(40);
        subPane.setLayoutX(0);

        // 设置鼠标滚轮事件
        subPane.setOnScroll(event -> {

            // 新的LayoutY
            double newLayoutY = subPane.getLayoutY() + event.getDeltaY();

            // 新的LayoutY应该小于this.getHeight() - length * 25，大于40
            newLayoutY = Double.max(this.getHeight() - length * 25, newLayoutY); // 单首歌高度为25
            newLayoutY = Double.min(40, newLayoutY);
            subPane.setLayoutY(newLayoutY);
        });
    }

    // 在当前播放列表中添加一首歌
    public void addSong(Song song) {

        // 如果播放列表中已经有这首歌，则跳过
        if (songIDs.contains(song.getSongID())) return;

        // 初始化这首歌的PlayingSongPane（注：单首歌高度为25）
        PlayingSongPane playingSongPane = new PlayingSongPane(song, this.getWidth(), 25, subPane, playerPane);

        // 设置songPane的位置（注：单首歌高度为25）
        playingSongPane.setLayoutX(0);
        playingSongPane.setLayoutY(length * 25);

        // 如果是第偶数首歌，背景设为浅灰色，否则为白色
        if (length % 2 == 0) {
            playingSongPane.setOriginalStyle("-fx-background-color: #ffffff;");
            playingSongPane.setStyle("-fx-background-color: #ffffff;");
        } else {
            playingSongPane.setOriginalStyle("-fx-background-color: #f5f5f5;");
            playingSongPane.setStyle("-fx-background-color: #f5f5f5;");
        }

        // 将新的songPane添加到subPane
        subPane.getChildren().add(playingSongPane);
        length += 1;

        // 将这首歌的SongID添加到songIDs
        songIDs.add(song.getSongID());
    }

    // 删除当前播放列表中的所有歌曲
    public void delAllSong() {
        length = 0;
        subPane.getChildren().clear();
        songIDs.clear();
    }

    // 删除某一首歌
    public void deleteSong(String songID) {
        subPane.getChildren().remove(songIDs.indexOf(songID));
        songIDs.remove(songID);
        playerPane.deleteSong(songID);
        length = 0;
        for (Node songPane : subPane.getChildren()) {
            songPane.setLayoutY(length * 25);
            length += 1;
        }
    }

}