package gui.leftPane;

import basic.Playlist;
import basic.Song;
import data.DataAPI;
import gui.playlistBlockListPane.TopListPane;
import gui.songListPane.SongListPane;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

// 很多PlaylistPane组成的Pane
public class PlaylistListPane extends Pane {

    // 六个Pane组件
    private InfoPane find;
    private InfoPane myMusic;
    private InfoPane myFav;
    private PlaylistPane recommendPlaylist;
    private PlaylistPane rank;
    private PlaylistPane favSong;

    // 当前排列到的高度，用于排列Pane
    private double currentHeight = 0;

    // 对应的songListPane
    private SongListPane songListPane;

    // 对应的topListPane
    private TopListPane topListPane;

    // 对应的leftPane
    private SongDetailPane songDetailPane;

    // 必需的stage和scene
    private Stage mainStage;
    private Scene scene1, scene2, scene3;

    // 构造方法
    public PlaylistListPane() throws SQLException {

        // 设定宽高
        this.setWidth(200);
        this.setHeight(550);

        // 初始化find, reconList, rank, myMusic, favSong, myFav
        initFind();
        initRecommendPlaylist();
        initRank();
        initMyMusic();
        initFavSong();
        initMyFav();

        // 将find, reconList, rank, myMusic, favSong, myFav添加到PlaylistListPane
        this.getChildren().addAll(find, recommendPlaylist, rank, myMusic, myFav, favSong);

        // 初始化所有收藏的歌单的playlistPanes，得到favPlaylistNum
        int favPlaylistNum = initAllFavPlaylistPanes();

        // 设定鼠标滚轮事件
        this.setOnScroll(event -> {
            double newY = getLayoutY() + event.getDeltaY();
            newY = Double.max(550 - currentHeight - favPlaylistNum * 30 - 60, newY);
            newY = Double.min(0, newY);
            setLayoutY(newY);
        });
    }

    // 初始化发现infoPane
    private void initFind() {

        // 实例化find，设置位置
        find = new InfoPane(" 发现");
        find.setLayoutY(currentHeight);

        // 每多一个内容就将height增加对应的值
        currentHeight += 40;
    }

    // 初始化推荐歌单playlistPane
    private void initRecommendPlaylist() {

        // 实例化推荐歌单playlistPane，设置位置
        recommendPlaylist = new PlaylistPane(new Image(Objects.requireNonNull(getClass().getResource("../image/music.png")).toExternalForm()), "推荐歌单");
        recommendPlaylist.setLayoutY(currentHeight);

        // 鼠标单击时切换mainStage的scene
        recommendPlaylist.setOnMouseClicked(event -> mainStage.setScene(scene1));

        // 每多一个内容就将height增加对应的值
        currentHeight += 30;
    }

    // 初始化排行榜playlistPane
    private void initRank() {

        // 实例化排行榜playlistPane，设置位置
        rank = new PlaylistPane(new Image(Objects.requireNonNull(getClass().getResource("../image/music.png")).toExternalForm()), "排行榜");
        rank.setLayoutY(currentHeight);

        // 鼠标单击时切换mainStage的scene，并为topListPane设置stage，scene和songListPane
        rank.setOnMouseClicked(event -> {
            topListPane.setEssentials(mainStage, scene2, songListPane);
            mainStage.setScene(scene3);
        });

        // 每多一个内容就将height增加对应的值
        currentHeight += 30;
    }

    // 初始化我的音乐infoPane
    private void initMyMusic() {

        // 实例化我的音乐infoPane，设置位置
        myMusic = new InfoPane(" 我的音乐");
        myMusic.setLayoutY(currentHeight);

        // 每多一个内容就将height增加对应的值
        currentHeight += 40;
    }

    // 初始化我喜欢的音乐playlistPane
    private void initFavSong() {

        // 实例化我喜欢的音乐playlistPane，设置位置
        favSong = new PlaylistPane(new Image(Objects.requireNonNull(getClass().getResource("../image/love.png")).toExternalForm()), "我喜欢的音乐");
        favSong.setLayoutY(currentHeight);

        // 单击时切换界面
        favSong.setOnMouseClicked(event -> {

            try {
                // 获取所有的喜欢的音乐，设定songListPane的内容，切换场景
                songListPane.setSongs(DataAPI.getFavSongs(), true);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }

            mainStage.setScene(scene2);
        });

        // 每多一个内容就将height增加对应的值
        currentHeight += 30;
    }

    // 设定我的收藏infoPane
    private void initMyFav() {

        // 实例化我的收藏infoPane，设定位置
        myFav = new InfoPane(" 我的收藏");
        myFav.setLayoutY(currentHeight);

        // 每多一个内容就将height增加对应的值
        currentHeight += 40;
    }

    // 喜欢一首歌
    public void addFavSong(Song song) throws SQLException, IOException {

        // 将歌曲添加到favSongs
        DataAPI.setFavSong(song.getSongID(), 1);

        // 刷新songListPane内容
        refreshSongListPane();

        // 刷新songDetailPane内容
        if (songDetailPane != null) {
            songDetailPane.refreshTavTag();
        }
    }

    // 取消喜欢一首歌
    public void removeFavSong(Song song) throws SQLException, IOException {

        // 将这首歌从favSongs中删除
        DataAPI.setFavSong(song.getSongID(), 0);

        // 刷新songListPane内容
        refreshSongListPane();

        // 刷新songDetailPane内容
        if (songDetailPane != null) {
            songDetailPane.refreshTavTag();
        }
    }

    // 刷新songListPane
    public void refreshSongListPane() throws SQLException, IOException {

        // 如果当前songListPane是喜欢的歌曲的列表，则songListPane.setSongs()，并设置场景
        if (songListPane.isFavSongList) {
            songListPane.setSongs(DataAPI.getFavSongs(), true);
            mainStage.setScene(scene2);
        }
    }

    // 添加喜欢的歌单
    public void addFavPlaylist(Playlist playlist) throws SQLException {

        // favPlaylists中添加playlist
        DataAPI.setFavPlaylist(playlist.getPlaylistID(), 1);

        // 重新加载favPlaylists
        List<Playlist> favPlaylists = DataAPI.getFavPlaylists();

        // 实例化新的playlistPane，并添加到相应位置
        PlaylistPane playlistPane = new PlaylistPane(new Image(Objects.requireNonNull(getClass().getResource("../image/playlists.png")).toExternalForm()), playlist.getPlaylistName());
        playlistPane.setLayoutY(currentHeight + (favPlaylists.size() - 1) * 30);

        // 设置鼠标单击时songListPane.setSongs
        playlistPane.setOnMouseClicked(event -> {
            try {
                songListPane.setSongs(DataAPI.getSongsOfPlaylist(playlist.getPlaylistID()), playlist);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });

        // 将该playlistPane添加到playlistListPane
        this.getChildren().add(playlistPane);
    }

    // 取消收藏歌单
    public void delFavPlaylist(Playlist playlist) throws SQLException {

        // favPlaylists中删除playlist
        DataAPI.setFavPlaylist(playlist.getPlaylistID(), 0);

        // 删除所有的歌单playlistPane
        this.getChildren().clear();
        this.getChildren().addAll(find, recommendPlaylist, rank, myMusic, favSong, myFav);

        // 重新添加所有收藏的歌单的playlistPanes
        initAllFavPlaylistPanes();
    }

    // 初始化所有的FavPlatlistPane
    private int initAllFavPlaylistPanes() throws SQLException {

        // 获取favPlaylists
        List<Playlist> favPlaylists = DataAPI.getFavPlaylists();

        for (int i = 0; i < favPlaylists.size(); i++) {

            // 获取playlist
            Playlist playlist = favPlaylists.get(i);

            // 实例化playlistPane
            PlaylistPane playlistPane = new PlaylistPane(new Image(Objects.requireNonNull(getClass().getResource("../image/playlists.png")).toExternalForm()), playlist.getPlaylistName());

            // 设置位置
            playlistPane.setLayoutY(currentHeight + i * 30);

            // 设置鼠标单击时调用songListPane.setSongs()并更改场景
            playlistPane.setOnMouseClicked(event -> {
                try {
                    songListPane.setSongs(DataAPI.getSongsOfPlaylist(playlist.getPlaylistID()), playlist);
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
                mainStage.setScene(scene2);
            });

            // 将playlistPane添加到playlistListPane
            this.getChildren().add(playlistPane);
        }

        // 设定playlistListPane位置
        this.setPrefSize(200, currentHeight + favPlaylists.size() * 30);

        // 返回favPlaylists的大小
        return favPlaylists.size();
    }

    // 设定对应的songListPane
    public void setSongListPane(SongListPane songListPane) {
        this.songListPane = songListPane;
    }

    // 设定对应的songDetailPane
    public void setSongDetailPane(SongDetailPane songDetailPane) {
        this.songDetailPane = songDetailPane;
    }

    // 设定必要的scene和stage
    public void setEssentials(Stage mainStage, Scene scene1, Scene scene2, Scene scene3, TopListPane topListPane) {
        this.scene1 = scene1;
        this.scene2 = scene2;
        this.scene3 = scene3;
        this.mainStage = mainStage;
        this.topListPane = topListPane;
    }
}