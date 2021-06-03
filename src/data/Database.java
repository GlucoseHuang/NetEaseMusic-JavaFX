package data;

import basic.Playlist;
import basic.Song;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

// 用于向数据库中读取和写入数据，仅供DataAPI和Request使用
class Database {

    // 建立的连接对象
    private final Connection conn;

    // 构造方法
    public Database() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/net_ease_music?serverTimezone=GMT%2B8", "root", "123admin");
    }

    // 关闭数据库连接
    public void close() throws SQLException {
        conn.close();
    }

    // 当天的日期字符串
    public static String getTodayString() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return (c.get(Calendar.YEAR)) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + (c.get(Calendar.DAY_OF_MONTH));
    }

    // 初始化连接
    public void initTable() throws ClassNotFoundException, SQLException {

        // 新建表song
        String createTableSongSQL =
                "create table if not exists song (" +
                        "`song_name` varchar(255)," +
                        "`singer` varchar(255)," +
                        "`duration` float," +
                        "`image_source` varchar(255)," +
                        "`songID` varchar(20) primary key," +
                        "`mvID` varchar(20)," +
                        "`albumID` varchar(20)," +
                        "`albumPosition` int," +
                        "`isFav` bit" +
                        ");";

        // 新建表playlist
        String createTablePlaylistSQL =
                "create table if not exists playlist (" +
                        "`image_source` varchar(255)," +
                        "`title` varchar(255)," +
                        "`playlistID` varchar(20) primary key," +
                        "`isFav` bit," +
                        "`playCount` bigint," +
                        "`updateTime` char(10)" +
                        ");";

        // 新建表song_playlist
        String createTableSong_PlaylistSQL =
                "create table if not exists song_playlist (" +
                        "`songID` varchar(20)," +
                        "`playlistID` varchar(20)," +
                        "PRIMARY KEY(songID, playlistID)" +
                        ");";

        // 新建表daily_recommend
        String createTableDaily_Recommend_PlaylistSQL =
                "create table if not exists Daily_Recommend_Playlist(" +
                        "`sequence` int," +
                        "`playlistID` varchar(20)," +
                        "`date` char(10)," +
                        "PRIMARY KEY(sequence, date)" +
                        ");";

        // 运行以上的所有Sql，新建表
        conn.prepareStatement(createTableSongSQL).executeUpdate();
        conn.prepareStatement(createTablePlaylistSQL).executeUpdate();
        conn.prepareStatement(createTableSong_PlaylistSQL).executeUpdate();
        conn.prepareStatement(createTableDaily_Recommend_PlaylistSQL).executeUpdate();
    }

    // 从数据库中获取一个歌单的详细信息
    public Playlist selectPlaylist(String playlistID) throws SQLException {

        Playlist playlist = new Playlist();

        String selectPlaylistSQL = "select * from playlist where playlistID=" + playlistID;
        ResultSet rs = conn.prepareStatement(selectPlaylistSQL).executeQuery();

        if (rs.next()) {
            playlist.setImgSrc(rs.getString("image_source"));
            playlist.setPlaylistName(rs.getString("title"));
            playlist.setPlaylistID(playlistID);
            playlist.setPlayCount(rs.getLong("playCount"));
            playlist.setSongIDs(selectSongIDFromPlaylist(playlistID));
        }

        return playlist;
    }

    // 从数据库中获取一个歌单下的全部歌曲的ID
    public List<String> selectSongIDFromPlaylist(String playlistID) throws SQLException {
        List<String> songIDs = new ArrayList<>();

        String selectPlaylistSQL = "select songID from song_playlist where playlistID=" + playlistID;
        ResultSet rs = conn.prepareStatement(selectPlaylistSQL).executeQuery();
        while (rs.next()) {
            songIDs.add(rs.getString("songID"));
        }

        return songIDs;
    }

    // 从数据库中获取一首歌
    public Song selectSong(String songID) throws SQLException {
        Song song = new Song();

        String selectSongSQL = "select * from song where songID=" + songID;
        ResultSet rs = conn.prepareStatement(selectSongSQL).executeQuery();

        if (rs.next()) {
            song.setSongName(rs.getString("song_name"));
            song.setArtistName(rs.getString("singer"));
            song.setDuration(rs.getDouble("duration"));
            song.setImgSrc(rs.getString("image_source"));
            song.setSongID(songID);
            song.setMvID(rs.getString("mvID"));
        }

        return song;
    }

    // 从数据库中获取多首歌
    public List<Song> selectSongs(List<String> songIDs) throws SQLException {

        List<Song> songs = new ArrayList<>();

        // 构造sql语句
        StringBuilder selectSongSQL = new StringBuilder("select * from song where ");
        for (String songID : songIDs) {
            selectSongSQL.append("songID=").append(songID);
            if (songIDs.indexOf(songID) != (songIDs.size() - 1)) {
                selectSongSQL.append(" or ");
            }
        }
        System.out.println(selectSongSQL);

        // 将匹配结果转化为song对象，添加到songs
        ResultSet rs = conn.prepareStatement(selectSongSQL.toString()).executeQuery();
        while (rs.next()) {
            Song song = new Song();
            song.setSongName(rs.getString("song_name"));
            song.setArtistName(rs.getString("singer"));
            song.setDuration(rs.getDouble("duration"));
            song.setImgSrc(rs.getString("image_source"));
            String songID = rs.getString("songID");
            song.setSongID(songID);
            songIDs.remove(songID);
            song.setMvID(rs.getString("mvID"));
            songs.add(song);
        }

        return songs;
    }

    // 从Daily_Recommend_Playlist中获取固定页数的内容
    public List<String> selectDaily_Recommend_Playlist(int page) throws SQLException {
        List<String> playlistIDs = new ArrayList<>();
        String selectSongSQL = "select playlistID from daily_recommend_playlist " +
                "where date='" + getTodayString() + "' and (sequence div 8=" + page + ") order by sequence;";
        ResultSet rs = conn.prepareStatement(selectSongSQL).executeQuery();

        System.out.println(selectSongSQL);
        while (rs.next()) {
            playlistIDs.add(rs.getString("playlistID"));
        }

        return playlistIDs;
    }

    // 向数据库中插入一首歌
    public void insertSong(Song song) throws SQLException {

        // 首先判断这首歌存不存在
        String insertSongSQL = "replace into song" +
                "(song_name, singer, duration, image_source, songID, mvID, albumID, albumPosition, isFav) VALUES(" +
                "'" + song.getSongName().replaceAll("'", "''") + "'," + // 将'换为''，以避免字符串中的单引号导致sql语法错误
                "'" + song.getArtistName().replaceAll("'", "''") + "'," +
                "'" + song.getDuration() + "'," +
                "'" + song.getImgSrc() + "'," +
                "'" + song.getSongID() + "'," +
                "'" + song.getMvID() + "'," +
                "'" + song.getAlbumID() + "'," +
                song.getAlbumPosition() + "," +
                "(select a.isFav from (select isFav from song where songID = " + song.getSongID() + ") a)" +
                ");";

        conn.prepareStatement("DELETE FROM song WHERE `songID`='" + song.getSongID() + "';").executeUpdate();

        System.out.println(insertSongSQL);
        conn.prepareStatement(insertSongSQL).executeUpdate();
    }

    // 向数据库中插入多首歌
    public void insertSongs(List<Song> songs) throws SQLException {
        for (Song song : songs) {
            insertSong(song);
        }
    }

    // 向数据库中插入歌单歌曲对应关系
    public void insertSongIDtoPlatlist(String playlistID, List<String> songIDs) throws SQLException {
        for (String songID : songIDs) {
            String insertSongIDtoPlatlistSQL = "replace into song_playlist" +
                    "(songID, playlistID) VALUES ('" + songID + "','" + playlistID + "');";
            conn.prepareStatement(insertSongIDtoPlatlistSQL).executeUpdate();
        }
    }

    // 向数据库中插入一个歌单
    public void insertPlaylist(Playlist playlist) throws SQLException {
        String insertPlaylistSQL = "replace into playlist" +
                "(image_source, title, playlistID, playCount, updateTime, isFav) VALUES(" +
                "'" + playlist.getImgSrc() + "'," +
                "'" + playlist.getPlaylistName() + "'," +
                "'" + playlist.getPlaylistID() + "'," +
                playlist.getPlayCount() + "," +
                "'" + getTodayString() + "'," +
                "(select a.isFav from (select isFav from playlist where playlistID = " + playlist.getPlaylistID() + ") a)" +
                ");";
        System.out.println(insertPlaylistSQL);

        conn.prepareStatement("DELETE FROM playlist WHERE `playlistID` = '+" + playlist.getPlaylistID() + "+'").executeUpdate();
        conn.prepareStatement(insertPlaylistSQL).executeUpdate();

        // 向数据库中插入歌单歌曲对应关系
        insertSongIDtoPlatlist(playlist.getPlaylistID(), playlist.getSongIDs());
    }

    // 向数据库中插入多个歌单
    public void insertPlaylists(List<Playlist> playlists) throws SQLException {
        for (Playlist playlist : playlists) {
            insertPlaylist(playlist);
        }
    }

    // 向数据库中加入每日推荐的数据
    public void insertDaily_Recommend_Playlist(List<Playlist> playlists) throws SQLException {

        String date = getTodayString();

        for (int i = 0; i < playlists.size(); i++) {
            String insertDaily_Recommend_PlaylistSQL = "replace into Daily_Recommend_Playlist" +
                    "(sequence, playlistID, date) VALUES (" + i + "," +
                    "'" + playlists.get(i).getPlaylistID() + "'," +
                    "'" + date + "'" +
                    ");";
            System.out.println(insertDaily_Recommend_PlaylistSQL);
            conn.prepareStatement(insertDaily_Recommend_PlaylistSQL).executeUpdate();
        }
    }

    // 获取FavSongs
    public List<Song> selectFavSongs() throws SQLException {
        List<Song> songs = new ArrayList<>();

        String selectSongsSQL = "select * from song where isFav = 1;";
        ResultSet rs = conn.prepareStatement(selectSongsSQL).executeQuery();

        while (rs.next()) {
            Song song = new Song();
            song.setSongName(rs.getString("song_name"));
            song.setArtistName(rs.getString("singer"));
            song.setDuration(rs.getDouble("duration"));
            song.setImgSrc(rs.getString("image_source"));
            song.setSongID(rs.getString("songID"));
            song.setMvID(rs.getString("mvID"));
            songs.add(song);
        }

        return songs;
    }

    // 获取FavPlaylists
    public List<Playlist> selectFavPlaylists() throws SQLException {

        List<Playlist> playlists = new ArrayList<>();

        String selectPlaylistSQL = "select * from playlist where isFav = 1";
        ResultSet rs = conn.prepareStatement(selectPlaylistSQL).executeQuery();

        while (rs.next()) {
            Playlist playlist = new Playlist();
            playlist.setImgSrc(rs.getString("image_source"));
            playlist.setPlaylistName(rs.getString("title"));
            playlist.setPlaylistID(rs.getString("playlistID"));
            playlist.setPlayCount(rs.getLong("playCount"));
            playlist.setSongIDs(selectSongIDFromPlaylist(rs.getString("playlistID")));
            playlists.add(playlist);
        }

        return playlists;
    }

    // 设定是否喜欢歌曲
    public void setFavSong(String songID, int value) throws SQLException {
        System.out.println("update song set song.isFav=" + value + " where song.songID=" + songID);
        conn.prepareStatement("update song set song.isFav=" + value + " where song.songID=" + songID).executeUpdate();
    }

    // 设定是否收藏歌单
    public void setFavPlaylist(String playlistID, int value) throws SQLException {
        System.out.println("update playlist set playlist.isFav=" + value + " where playlist.playlistID=" + playlistID);
        conn.prepareStatement("update playlist set playlist.isFav=" + value + " where playlist.playlistID=" + playlistID).executeUpdate();
    }

    // 获取是否是喜欢歌曲
    public boolean getFavSong(String songID) throws SQLException {
        String selectSongsSQL = "select * from song where isFav = 1 and songID = " + songID + ";";
        ResultSet rs = conn.prepareStatement(selectSongsSQL).executeQuery();

        return rs.next();
    }

    // 获取是否是喜欢歌单
    public boolean getFavPlaylist(String playlistID) throws SQLException {
        String selectPlaylistSQL = "select * from playlist where isFav = 1 and playlistID = " + playlistID + ";";
        ResultSet rs = conn.prepareStatement(selectPlaylistSQL).executeQuery();

        return rs.next();
    }
}