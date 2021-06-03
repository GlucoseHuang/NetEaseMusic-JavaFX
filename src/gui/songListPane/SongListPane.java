package gui.songListPane;

import basic.Playlist;
import basic.Song;
import data.DataAPI;
import gui.leftPane.PlaylistListPane;
import gui.playerPane.PlayerPane;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

// 歌曲的列表Pane，包含很多SongPane
public class SongListPane extends Pane {

    // 三个需要的组件
    private Label songName, singer, duration;

    // 需要PlaylistListPane，用于让songPane判断是否是收藏的歌曲，收藏歌曲，取消收藏歌曲
    private final PlaylistListPane playlistListPane;

    // song和player用于播放选中的songPane对应的歌曲
    private Song song;
    private final PlayerPane playerPane;

    // 表头Pane
    private Pane lineHeaderPane;

    // SongListPane所属的playlistHeaderPane
    private final PlaylistHeaderPane playlistHeaderPane;

    // 歌曲总数量
    private int songNum = 0;

    // 表头的位置
    private double lineHeaderPaneY = 250;

    // 是否是喜欢的歌曲的列表
    public boolean isFavSongList = false;

    // 用于下载时弹出窗口到stage
    private final Stage stage;

    // 构造方法
    public SongListPane(PlayerPane playerPane, PlaylistListPane playlistListPane, Stage stage) {

        // 设置
        this.playerPane = playerPane;
        this.playlistListPane = playlistListPane;
        this.stage = stage;

        // 初始化lineHeaderPane
        initLineHeaderPane();

        // 实例化playlistHeaderPane
        playlistHeaderPane = new PlaylistHeaderPane(playerPane, playlistListPane);

        // 设置playlistHeaderPane位置
        playlistHeaderPane.setLayoutX(0);
        playlistHeaderPane.setLayoutY(0);

        // 将lineHeaderPane和playlistHeaderPane添加到songListPane
        this.getChildren().addAll(lineHeaderPane, playlistHeaderPane);

        // 设置背景颜色
        this.setStyle("-fx-background-color: #fafafa;");

        // 设置鼠标滚动事件
        this.setOnScroll(event -> {

            double newY = getLayoutY() + event.getDeltaY();
            newY = Double.max(550 - lineHeaderPaneY - songNum * 30 - 50, newY);
            newY = Double.min(0, newY);

            // 设定newY
            setLayoutY(newY);
        });
    }

    // 初始化lineHeaderPane
    private void initLineHeaderPane() {

        // 实例化lineHeaderPane
        lineHeaderPane = new Pane();

        // 设置位置和宽高
        lineHeaderPane.setLayoutX(0);
        lineHeaderPane.setLayoutY(lineHeaderPaneY);
        double width = 800;
        lineHeaderPane.setPrefSize(width, 50);

        // 设置背景颜色
        lineHeaderPane.setStyle("-fx-background-color: #fafafa;");

        // 初始化songName, singer, duration三个表头
        initSongName(width);
        initSinger(width);
        initDuration(width);

        // 将songName, singer, duration添加到lineHeaderPane
        lineHeaderPane.getChildren().addAll(songName, singer, duration);
    }

    // 初始化songName
    private void initSongName(double width) {

        // 实例化songName
        songName = new Label("歌曲");

        // 设置字体和颜色
        songName.setFont(new Font("Microsoft YaHei", 20));
        songName.setStyle("-fx-text-fill: #a4a4a4;");

        // 设置位置
        songName.setLayoutX(width / 8);
        songName.setLayoutY(10);

    }

    // 初始化singer
    private void initSinger(double width) {

        // 实例化singer
        singer = new Label("歌手");

        // 设置字体和颜色
        singer.setFont(new Font("Microsoft YaHei", 20));
        singer.setStyle("-fx-text-fill: #a4a4a4;");

        //设置位置
        singer.setLayoutX(5 * width / 8);
        singer.setLayoutY(10);
    }

    // 初始化duration
    private void initDuration(double width) {

        // 实例化duration
        duration = new Label("时长");

        // 设置字体和颜色
        duration.setFont(new Font("Microsoft YaHei", 20));
        duration.setStyle("-fx-text-fill: #a4a4a4;");

        // 设置位置
        duration.setLayoutX(7 * width / 8);
        duration.setLayoutY(10);
    }

    // 添加歌曲
    private void addSongs(List<Song> songs) throws SQLException {

        // 为所有的song创建一个songPane并放置
        for (int i = 0; i < songs.size(); i++) {

            // 用当前song实例化一个songPane
            SongPane songPane = new SongPane(i + 1, songs.get(i), this);

            // 设置songPane位置
            songPane.setLayoutX(0);
            songPane.setLayoutY(lineHeaderPaneY + 50 + i * 30);

            // 设置songPane.songName的鼠标单击事件，用于播放
            songPane.getSongName().setOnMouseClicked(event -> {
                try {
                    playerPane.playMusic(DataAPI.getSong(song.getSongID()));
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            });

            // 将songPane添加到songListPane
            this.getChildren().add(songPane);
        }

        // songListPane.length设为songs的数量
        this.songNum = songs.size();
    }

    // 更改songListPane中的歌曲，如果是来源于leftPane中的播放列表
    public void setSongs(List<Song> songs, boolean isFavSongList) throws SQLException, IOException {

        // 设置为是/不是喜欢的歌曲的列表
        this.isFavSongList = isFavSongList;

        // 设置位置
        setLayoutY(0);

        // lineHeaderPane从0开始（不需要playlistHeaderPane）
        this.lineHeaderPaneY = 0;
        lineHeaderPane.setLayoutY(this.lineHeaderPaneY);

        // 清除之前的所有songPane，重新添加songPane
        getChildren().clear();
        getChildren().addAll(this.lineHeaderPane);
        this.songNum = 0;
        addSongs(songs);
    }

    // 更改songListPane中的歌曲，如果是来源于网络歌单
    public void setSongs(List<Song> songs, Playlist playlist) throws SQLException, IOException {

        // 设置位置
        setLayoutY(0);

        // 设置为不是喜欢的歌曲的列表
        this.isFavSongList = false;

        // lineHeaderPane从250开始（需要playlistHeaderPane）
        this.lineHeaderPaneY = 250;
        lineHeaderPane.setLayoutY(this.lineHeaderPaneY);
        playlistHeaderPane.setPlaylist(playlist);

        // 清除之前的所有songPane，重新添加songPane
        getChildren().clear();
        getChildren().addAll(playlistHeaderPane, lineHeaderPane);
        this.songNum = 0;
        addSongs(songs);
    }

    // 下载歌曲，单击download图像时出现
    public void downloadSong(Song song) throws IOException, SQLException {

        // 获取歌曲源地址
        String musicSrc = DataAPI.getMusicSrc(song.getSongID());

        // 如果没有源地址
        if (musicSrc.equals("http://music.163.com/404")) {
            Stage stage = new Stage();
            stage.setScene(new Scene(new Label("《" + song.getSongName() + "》存在版权问题，暂不提供下载！"), 500, 100));
            stage.show();
            return;
        }

        // 解析源地址URL，建立连接
        URL url = new URL(musicSrc);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();

        // 设置建立连接和读取资源的超时时间
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(10000);

        // 如果http返回值不为200
        if (urlCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Stage stage = new Stage();
            stage.setScene(new Scene(new Label("HTTP连接建立失败，无法下载！"), 500, 100));
            stage.show();
            return;
        }

        // 实例化一个FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("下载歌曲到");
        fileChooser.setInitialFileName(song.getSongName() + ".mp3");
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());

        // 弹出保存文件窗口
        File file = fileChooser.showSaveDialog(stage);

        // 如果选择文件窗口返回null，则结束下载过程
        if (file == null) {
            return;
        }

        // 开始读取输入字节流并保存至本地
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[2048];
        int count;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        out.close();
        in.close();

        // 弹出窗口提示下载成功
        Stage stage = new Stage();
        stage.setScene(new Scene(new Label("下载成功，文件保存路径： " + file.getAbsolutePath()), 500, 100));
        stage.show();
    }

    // 获取PlaylistListPane，用于判断是否是收藏的歌曲，收藏歌曲，取消收藏歌曲
    public PlaylistListPane getPlaylistListPane() {
        return playlistListPane;
    }

    // 设置songListPane中被选中的歌曲
    public void setSong(Song song) {
        this.song = song;
    }
}