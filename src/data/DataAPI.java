package data;

import basic.Playlist;
import basic.Song;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 数据接口，gui需要的所有数据都从DataAPI获取
// 所有函数都是static类型，无需实例化
public class DataAPI {

    // 静态变量db
    private static Database db;

    // 对db进行初始化
    static {
        try {
            db = new Database();
            db.initTable();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 搜索歌曲
    @SuppressWarnings("SuspiciousToArrayCall")
    public static List<Song> searchSong(String searchText) throws IOException {
        if (searchText == null || searchText.equals("")) {
            return new ArrayList<>();
        }

        // 这里先将List<Object>转为Song[]，再又转回List<Song>返回
        return Arrays.asList(Request.searchByAPI(searchText, "song", 100).toArray(new Song[0]));
    }

    // 获取一页的每日推荐
    public static List<Playlist> getDailyRecommend(int page) throws IOException, SQLException {

        // 首先从数据库中读取
        List<String> playlistIDs = db.selectDaily_Recommend_Playlist(page);
        System.out.println(playlistIDs.size());

        if (playlistIDs.size() != 8) {

            // 如果从数据库中获取到的数据数量不正确，就从网络读取
            return Request.getDailyRecommendByAPI(page);

        } else {

            // 如果正确，就获取每个歌单的详细数据
            List<Playlist> playlists = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                playlists.add(getPlaylist(playlistIDs.get(i)));
            }
            return playlists;
        }
    }

    // 获取热榜的内容
    public static Playlist[] getTopList() throws IOException, SQLException {

        // 热榜对应的歌单ID
        List<String> playlistIDs = Arrays.asList("3779629", "3778678", "2884035", "19723756", "10520166", "180106", "60198", "21845217", "11641012", "120001", "60131", "3733003");

        // 逐一读取这些歌单的内容
        Playlist[] playlists = new Playlist[playlistIDs.size()];
        for (String playlistID : playlistIDs) {
            playlists[playlistIDs.indexOf(playlistID)] = getPlaylist(playlistID);
            System.out.println("getTopList" + playlistIDs.indexOf(playlistID));
        }

        return playlists;
    }

    // 根据playlistID，获取playlist的详细信息
    public static Playlist getPlaylist(String playlistID) throws SQLException, IOException {

        // 首先从数据库中读取
        Playlist playlist = db.selectPlaylist(playlistID);

        // 如果没有从数据库中获取到，就从网络API获取
        if (playlist.getPlaylistID() == null || playlist.getPlaylistID().equals("")) {
            System.out.println("getPlaylistDetailByAPI");
            playlist = Request.getPlaylistDetailByAPI(playlistID);
        }

        return playlist;
    }

    // 根据playlistID，获取playlist中包含的歌曲对象
    public static List<Song> getSongsOfPlaylist(String playlistID) throws IOException, SQLException {

        // 首先从数据库中读取
        List<String> songIDs = db.selectSongIDFromPlaylist(playlistID);

        // 如果没有从数据库中获取到，就从网络API获取
        if (songIDs.size() == 0) {
            songIDs = Request.getPlaylistDetailByAPI(playlistID).getSongIDs();
        }

        return getSongs(songIDs);
    }

    // 根据songID，获取歌曲对象
    public static Song getSong(String songID) throws SQLException, IOException {

        // 首先从数据库中读取
        Song song = db.selectSong(songID);

        // 如果没有从数据库中获取到，就从网络API获取
        if (song.getSongID() == null || song.getSongID().equals("")) {
            song = Request.getSongDetailByAPI(songID);
        }

        return song;
    }

    // 根据songIDs，获取多个歌曲对象
    public static List<Song> getSongs(List<String> songIDs) throws IOException, SQLException {

        // 首先在数据库中选择
        List<Song> songs = db.selectSongs(songIDs);

        // 不在数据库的，用网页获取（db.selectSongs()已经将songIDs中获取到的删除）
        songs.addAll(Request.getSongsDetailByAPI(songIDs));

        return songs;
    }

    // 获取收藏歌曲
    public static List<Song> getFavSongs() throws SQLException {
        return db.selectFavSongs();
    }

    // 获取收藏歌单
    public static List<Playlist> getFavPlaylists() throws SQLException {
        return db.selectFavPlaylists();
    }

    // 改变歌曲收藏状态
    public static void setFavSong(String songID, int value) throws SQLException, IOException {
        getSong(songID); // 防止数据库中没有这首歌
        db.setFavSong(songID, value);
    }

    // 改变歌单收藏状态
    public static void setFavPlaylist(String playlistID, int value) throws SQLException {
        // 收藏一个歌单的前提是要点开看，所以不需要判断数据库中有没有这个歌单
        db.setFavPlaylist(playlistID, value);
    }

    // 退出时关闭数据库连接
    public static void exit() throws SQLException {
        db.close();
    }

    // 判断一首歌是否是喜欢歌单
    public static boolean isFavSong(String songID) throws SQLException {
        return db.getFavSong(songID);
    }

    // 判断一个歌单是否是收藏歌单
    public static boolean isFavPlaylist(String playlistID) throws SQLException {
        return db.getFavPlaylist(playlistID);
    }

    // 获取一首歌的mv源
    public static String getMVSrc(String songID) throws SQLException, IOException {
        return Request.getMVSrcByAPI(getSong(songID).getMvID());
    }

    // 获取一首歌的mp3源
    public static String getMusicSrc(String songID) throws IOException {
        return Request.getMusicSrcByAPI(songID);
    }

    // 获取一首歌的时长
    public static Duration getDuration(String songID) throws SQLException, IOException {
        return Duration.millis(getSong(songID).getDuration());
    }
}