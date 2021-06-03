package gui.playerPane;

import basic.Playlist;
import basic.Song;
import data.DataAPI;
import gui.leftPane.LeftPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 播放的Pane，包含播放进度条、暂停按钮、下一首按钮、上一首按钮、音量滑动条、播放MV按钮、显示当前播放列表按钮
public class PlayerPane extends Pane {

    // 一些需要的组件
    private ImageView playingSongList, last, next, pause, volume, playmv;
    private Label totalTime, currentTime;

    // 正在播放的曲目在播放列表中的index值
    private int currentPlayingIndex;

    // 当前音量的大小，1为最大值
    // 用于记录按下静音按钮前的音量
    private double currentVolume = 1.0;

    // 单击Songlist后出现的悬浮窗
    private PlayingSongListPane playingSongListPane;

    // 显示PlayingSongListPane的Stage
    private Stage playingSongListStage;

    // PlayingSongListPane的宽高
    private final double PlayingSongListPaneWidth = 600;
    private final double PlayingSongListPaneHeight = 500;

    // 歌曲的总长度
    private Duration duration;

    // 是否处于静音状态
    private Boolean isMuted = false;

    // 是否正在播放
    private Boolean isPlaying = false;

    // 用于判断currentTimeProperty改变时：
    // 是切换了歌曲，还是歌曲播放时的时间改变
    // 切换歌曲则需要重新加载MediaPlayer
    private Boolean needToLoadMediaPlayer = true;

    // 是否正在显示当前播放列表
    private Boolean isShowingSongList = false;

    // 当前播放列表中的内容
    private final List<Song> songList = new ArrayList<>();

    // 音频播放的组件
    private MediaPlayer mediaPlayer;

    // 滑动调整音频时间轴的组件
    private PlaySliderPane playSliderPane;

    // 滑动调整音量大小的组件
    private VolumeSliderPane volumeSliderPane;

    // 左侧的leftPane
    private final LeftPane leftPane;

    // Player的主Stage
    private final Stage primaryStage;

    // 构造方法
    public PlayerPane(Stage stage, LeftPane leftPane) {
        this.primaryStage = stage;
        this.leftPane = leftPane;
    }

    // 初始化Player
    public void initPlayerPane() {

        // 初始化需要的组件
        initPlayingSongList();
        initLast();
        initNext();
        initPause();
        initVolume();
        initPlayMv();
        initTotalTime();
        initCurrentTime();

        // 初始化进度条，音量条，当前播放列表
        initPlaySlider();
        initVolumeSlider();
        initPlayingSongListStage();

        // 将root, playSlider, volumeSlider加入new scene
        this.getChildren().addAll(playingSongList, last, next, pause, volume, playmv, totalTime, currentTime, playSliderPane, volumeSliderPane);

        // 设置primaryStage位置，将scene放入primaryStage
        primaryStage.setScene(new Scene(this, 1000, 50));
        primaryStage.setX(400);
        primaryStage.setY(800);

        // 初始化所有鼠标点击事件
        initMouseEvent();
    }

    // 初始化所有鼠标事件
    public void initMouseEvent() {

        // 点击last按键时，调用playLastSong()
        last.setOnMouseClicked(event -> {
            try {
                playLastSong();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });

        // 点击next按键时，调用playNextSong()
        next.setOnMouseClicked(event -> {
            try {
                playNextSong();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });

        // 为所有结点设置鼠标移入移出时变换样式
        for (Node node : new Node[]{last, next, pause, volume, playmv, playingSongList}) {
            node.setOnMouseEntered(event -> node.setCursor(Cursor.HAND));
            node.setOnMouseExited(event -> node.setCursor(Cursor.DEFAULT));
        }
    }

    // 初始化PlayingSongListPane
    private void initPlayingSongListStage() {

        // 实例化PlayingSongListPane
        playingSongListPane = new PlayingSongListPane(PlayingSongListPaneWidth, PlayingSongListPaneHeight, this);

        // 实例化playingSongListStage
        playingSongListStage = new Stage();

        // 将PlayingSongListPane放入new scene，再放入playingSongListStage
        playingSongListStage.setScene(new Scene(playingSongListPane, PlayingSongListPaneWidth, PlayingSongListPaneHeight));

        // 设置playingSongListStage没有边框
        playingSongListStage.initStyle(StageStyle.TRANSPARENT);

        // 将playingSongListStage的Owner设为primaryStage
        playingSongListStage.initOwner(primaryStage);

        // 设置不显示playingSongListStage
        playingSongListStage.hide();
    }

    // 初始化playingSongList
    public void initPlayingSongList() {
        playingSongList = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/playlist.png")).toExternalForm()));
        playingSongList.setFitHeight(18);
        playingSongList.setFitWidth(19);
        playingSongList.setLayoutX(927);
        playingSongList.setLayoutY(17);
        playingSongList.setOnMouseClicked(event -> showSongList());
    }

    // 初始化last
    public void initLast() {
        last = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/lastSong.png")).toExternalForm()));
        last.setFitHeight(30);
        last.setFitWidth(30);
        last.setLayoutX(30);
        last.setLayoutY(10);
        last.setOnMouseClicked(event -> {
            try {
                playLastSong();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 初始化next
    public void initNext() {
        next = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/nextSong.png")).toExternalForm()));
        next.setFitHeight(30);
        next.setFitWidth(30);
        next.setLayoutX(140);
        next.setLayoutY(10);
        next.setOnMouseClicked(event -> {
            try {
                playNextSong();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 初始化pause
    public void initPause() {
        pause = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/pause.png")).toExternalForm()));
        pause.setFitHeight(35);
        pause.setFitWidth(35);
        pause.setLayoutX(85);
        pause.setLayoutY(7.5);
        pause.setOnMouseClicked(event -> playOrPause());
    }

    // 初始化volume
    public void initVolume() {
        volume = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/volume.png")).toExternalForm()));
        volume.setFitHeight(12);
        volume.setFitWidth(16);
        volume.setLayoutX(692);
        volume.setLayoutY(19);
        volume.setOnMouseClicked(event -> mute());
    }

    // 初始化playMv
    public void initPlayMv() {
        playmv = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/playMv.png")).toExternalForm()));
        playmv.setFitHeight(30);
        playmv.setFitWidth(28);
        playmv.setLayoutX(880);
        playmv.setLayoutY(11);
        playmv.setOnMouseClicked(event -> {
            try {
                playMV();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 初始化totalTime
    public void initTotalTime() {
        totalTime = new Label("00:00");
        totalTime.setLayoutX(643);
        totalTime.setLayoutY(16);
        totalTime.setFont(new Font(11));
    }

    // 初始化currentTime
    public void initCurrentTime() {
        currentTime = new Label("00:00");
        currentTime.setLayoutX(202);
        currentTime.setLayoutY(16);
        currentTime.setFont(new Font(11));
    }

    // 初始化音频播放器
    public void initMediaPlayer() {

        // 为当前的播放时间添加Listener进行监听
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            // 当播放时间改变时
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {

                // 如果不需要加载，或media长度不为NaN
                if (!needToLoadMediaPlayer || !Double.isNaN(mediaPlayer.getMedia().getDuration().toMillis())) {

                    // 如果需要加载
                    if (needToLoadMediaPlayer) {

                        // 设置duration值
                        duration = mediaPlayer.getMedia().getDuration();

                        // 设置totalTime标签的文本
                        totalTime.setText(String.format("%02d:%02d", (int) duration.toMinutes(), (int) duration.toSeconds() % 60));

                        // 设置playerSlider的mediaPlayer
                        playSliderPane.setMediaPlayer(mediaPlayer);

                        // 设置volumeSlider的mediaPlayer
                        volumeSliderPane.setMediaPlayer(mediaPlayer);

                        // 设置mediaPlayer的音量
                        mediaPlayer.setVolume(volumeSliderPane.getVolumeValue());

                        // 设置正在播放
                        isPlaying = true;

                        // 设置播放/暂停ImageView的图像为暂停图像
                        pause.setImage((new Image(Objects.requireNonNull(getClass().getResource("../image/pause.png")).toExternalForm())));

                        // 设置为不需要加载
                        needToLoadMediaPlayer = false;

                    } else {

                        // 如果不需要加载，则设定playSlider的值和currentTime标签的内容
                        playSliderPane.setDurationPercent(newValue.toMillis() / duration.toMillis(), false);
                        currentTime.setText(String.format("%02d:%02d", (int) newValue.toMinutes(), (int) newValue.toSeconds() % 60));
                    }
                }
            }
        });

        // 一首歌播放完毕后，调用playNextSong()
        mediaPlayer.setOnEndOfMedia(() ->
        {
            try {
                playNextSong();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 初始化播放进度条
    public void initPlaySlider() {

        // 实例化playSlider
        playSliderPane = new PlaySliderPane(385, 15);

        // 设置位置
        playSliderPane.setLayoutX(245);
        playSliderPane.setLayoutY(17.5);
    }

    // 初始化音量条
    public void initVolumeSlider() {

        // 实例化volumeSliderPane
        volumeSliderPane = new VolumeSliderPane(140, 15, 1);

        // 设置位置
        volumeSliderPane.setLayoutX(720);
        volumeSliderPane.setLayoutY(17.5);
    }

    // 将一个播放列表作为当前播放列表
    public void addPlaylist(Playlist playlist) throws IOException, SQLException {

        // 清除当前播放列表中所有的内容
        songList.clear();
        playingSongListPane.delAllSong();

        // 读取歌曲
        List<Song> songs = DataAPI.getSongsOfPlaylist(playlist.getPlaylistID());

        // 将歌单中的所有歌曲放入songList
        for (Song song : songs) {

            // 如果这首歌已经在播放列表中，则跳过
            if (songList.contains(song)) {
                continue;
            }

            // 在PlayingSongListPane和songlist中添加这首歌
            playingSongListPane.addSong(song);
            songList.add(song);
        }

        // 播放第一首歌曲
        currentPlayingIndex = 0;
        playMusic(songs.get(0));
    }

    // 播放歌曲
    public void playMusic(Song song) throws IOException, SQLException {

        // 获取歌曲url
        String musicSrc = DataAPI.getMusicSrc(song.getSongID());

        // 如果musicSource为空，出现提示窗口，播放下一首歌曲
        if (musicSrc.equals("http://music.163.com/404")) {
            JOptionPane.showMessageDialog(null, "<html><font size=6>《" + song.getSongName() + "》存在版权问题，暂不提供播放！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 如果当前播放列表中没有这首歌
        if (!songList.contains(song)) {

            // 将这首歌添加到播放列表
            currentPlayingIndex = songList.size();
            songList.add(song);
            playingSongListPane.addSong(song);

        } else {

            // 如果播放列表中有这首歌，则设定nowPlayingIndex
            currentPlayingIndex = songList.indexOf(song);
        }

        // 播放这首歌
        playSong(musicSrc);
    }

    // ImageView last的单击事件，播放上一首歌
    public void playLastSong() throws IOException, SQLException {
        // 如果当前播放列表中有内容
        if (songList.size() > 0) {
            currentPlayingIndex = (currentPlayingIndex - 1 + songList.size()) % songList.size();
            playMusic(songList.get(currentPlayingIndex));
        }
    }

    // ImageView next的单击事件，以及一首歌曲播放结束后自动调用，播放下一首歌
    public void playNextSong() throws IOException, SQLException {
        // 如果当前播放列表中有内容
        if (songList.size() > 0) {
            currentPlayingIndex = (currentPlayingIndex + 1 + songList.size()) % songList.size();
            playMusic(songList.get(currentPlayingIndex));
        }
    }

    // 播放一首歌
    public void playSong(String musicSrc) throws SQLException {

        // 如果不需要加载
        if (!needToLoadMediaPlayer) {

            // 停止播放，释放所有资源
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        // 设置需要加载
        needToLoadMediaPlayer = true;

        // 实例化播放器，设定播放源
        mediaPlayer = new MediaPlayer(new Media(musicSrc));

        // 初始化播放器
        initMediaPlayer();

        // 开始播放
        mediaPlayer.play();

        // 在左侧的leftPane中显示歌曲
        leftPane.showSongDetail(songList.get(currentPlayingIndex));
    }

    // 单击播放/暂停ImageView时的事件
    public void playOrPause() {

        // 判断是否正在播放
        if (isPlaying) {

            // 如果正在播放，则暂停，
            pause.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/play.png")).toExternalForm()));
            mediaPlayer.pause();
            isPlaying = false;

        } else {

            // 如果正在暂停，则播放，改变ImageView内容
            pause.setImage((new Image(Objects.requireNonNull(getClass().getResource("../image/pause.png")).toExternalForm())));
            mediaPlayer.play();
            isPlaying = true;
        }
    }

    // 单击显示/隐藏播放列表ImageView时的事件
    public void showSongList() {

        // 判断是否正在显示播放列表
        if (isShowingSongList) {

            // 如果正在显示，则隐藏playingSongListStage
            playingSongListStage.hide();
            isShowingSongList = false;

        } else {

            // 如果正在隐藏，则设定位置，并显示playingSongListStage
            playingSongListStage.setX(primaryStage.getX() - PlayingSongListPaneWidth + primaryStage.getWidth());
            playingSongListStage.setY(primaryStage.getY() - PlayingSongListPaneHeight);
            playingSongListStage.show();
            isShowingSongList = true;
        }
    }

    // 单击音量ImageView时的事件
    public void mute() {

        // 判断是否正在静音
        if (isMuted) {

            // 如果正在静音，则设定音量，并改变ImageView
            volumeSliderPane.setVolumePercent(currentVolume);
            isMuted = false;
            volume.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/volume.png")).toExternalForm()));

        } else {

            // 如果未静音，则记录当前音量为currentVolume，设定静音，并改变ImageView
            currentVolume = volumeSliderPane.getVolumeValue();
            volumeSliderPane.setVolumePercent(0);
            isMuted = true;
            volume.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/mute.png")).toExternalForm()));
        }
    }

    // 播放MV界面
    public void playMV() throws IOException, SQLException {

        // 获取mv源地址
        String mvSource;
        try {
            mvSource = DataAPI.getMVSrc(songList.get(currentPlayingIndex).getSongID());
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        // 如果mv源地址为空，则提示暂无mv
        if (mvSource.equals("")) {
            JOptionPane.showMessageDialog(null, "<html><font size=6>暂无MV！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 暂停播放音乐
        pause.setImage(new Image(Objects.requireNonNull(getClass().getResource("../image/play.png")).toExternalForm()));
        mediaPlayer.pause();
        isPlaying = false;

        // 播放mv
        new PlayMV(mvSource, songList.get(currentPlayingIndex).getSongName());
    }

    // 获取PlayingSongListPane
    public PlayingSongListPane getPlayingSongListPane() {
        return playingSongListPane;
    }

    // 从playerPane中删除一首歌
    public void deleteSong(String songID) {
        songList.removeIf(song -> song.getSongID().equals(songID));
    }
}