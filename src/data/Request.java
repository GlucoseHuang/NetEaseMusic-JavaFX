package data;

import basic.Playlist;
import basic.Song;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 从API读取数据，仅供DataAPI使用
// 所有函数都是static类型，无需实例化
class Request {

    // 初始化数据库对象db
    private static Database db;

    static {
        try {
            db = new Database();
            db.initTable();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 传入String类型URL，获取url内容
    private static String getContent(String urlString) throws IOException {

        // 建立到urlString的连接
        URL url = new URL(urlString);
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection connection = (HttpURLConnection) urlConnection;

        //获取url内容，存入urlContent
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String current;
        StringBuilder urlContent = new StringBuilder();
        while ((current = in.readLine()) != null) {
            urlContent.append(current);
        }

        // 返回读到的内容
        return urlContent.toString();
    }

    // 检索
    public static List<Object> searchByAPI(String keyword, String type, int number) throws IOException {

        // 请求数量不能大于2000
        if (number >= 2000) {
            System.out.println("请求的结果太多！");
            return new ArrayList<>();
        }

        // 这里注意，我们需要的将keyword转换为utf-8编码，因为keyword可能含有中文
        keyword = URLEncoder.encode(keyword, "utf-8");

        // 结果List列表
        List<Object> results = new ArrayList<>();

        // 将请求类型字符串转化为数字
        int typeNum;
        switch (type) {
            case "song":
                typeNum = 1;
                break;
            case "album":
                typeNum = 10;
                break;
            case "artist":
                typeNum = 100;
                break;
            case "playlist":
                typeNum = 1000;
                break;
            default:
                System.out.println("Type Not Match");
                return new ArrayList<>();
        }

        // 设定每次读取的limits
        List<Integer> limits = new ArrayList<>();
        for (int i = 0; i < (number / 100); i++) {
            limits.add(100);
        }
        if (number % 100 != 0) {
            limits.add(number % 100);
        }

        // 开始读取网页内容并解析
        String url, content;
        int offset = 0;
        for (int limit : limits) {
            url = "http://www.hjmin.com/search/?keywords=" + keyword + "&offset=" + offset + "&limit=" + limit + "&type=" + typeNum;
            content = getContent(url);
            JSONObject rawObj = new JSONObject(content);

            // 根据搜索类型进行进一步的解析：
            // 1为song，10为album，100为artist，1000为playlist
            switch (typeNum) {
                case 1:
                    content = rawObj.getJSONObject("result").getJSONArray("songs").toString();
                    results.addAll(ParseJSON.parseSongs(content, false));
                    break;
                case 10:
                case 100:
                case 1000:
                    // TODO 由于时间关系，其他检索方式还有待完成
            }
            offset += limit;
        }

        return results;
    }

    // 获取歌曲详细信息
    // http://www.hjmin.com/song/detail?ids=1497035816
    public static Song getSongDetailByAPI(String songID) throws IOException, SQLException {
        Song song = ParseJSON.parseSongJSON(getContent("http://www.hjmin.com/song/detail?ids=" + songID), false, true);

        // 将读取到的信息写入MySQL中
        db.insertSong(song);

        return song;
    }

    // 注：这里源地址返回的是302重定向指令，需要使用不同的方式处理
    // https://music.163.com/song/media/outer/url?id=33894312.mp3
    public static String getMusicSrcByAPI(String songID) throws IOException {

        // 解析源地址URL，建立连接
        URL url = new URL("https://music.163.com/song/media/outer/url?id=" + songID + ".mp3");
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();

        // 设置请求头（UA和cookie）
        urlCon.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.60");
        urlCon.setRequestProperty("cookie", "_ntes_nnid=1c026bd4724746189da4bbbac15b47b9,1607667369129; _ntes_nuid=1c026bd4724746189da4bbbac15b47b9; NMTID=00OusK5IygCuCWLn070n_iV6gLPnhEAAAF2UHFB7g; WM_TID=NpaOeYprbM5FFEAEBEduLGJROkJwIxv8; _iuqxldmzr_=32; ntes_kaola_ad=1; WM_NI=FsdyhzKf%2BqPQJPYLZEAvSJK5OA22M%2BaTb8ChQ%2BiNlCRynhUzc1M5GTNaHpZiiwiMIS7avDlcMuF0pHJZuWM%2B0O3H9dpPRGNEpTECnXD4s%2FSKVspp5IsIv55coyehAQiTV0E%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6ee8ed93db3ee82b5c26586b88eb7c45b939a8abaf54191a9ff9bf348a9f08cb2ca2af0fea7c3b92a86adac9aeb7289bae592d35b8a9097add94f83aa9dabca3f8d9ffed3d021a38caed9c26493f5a7afef4992a7a289d872a1ef83d6c6608ebf8891d963aba9a7aecc59f5bfbaa5ef698fab8ad1f3448a86bbb8d021f491fda9bc70b4909ea8cd4fb48c8ba4ed5488ecbdd0d24196adc091b843b197bad6b869ada7ab89c45bab8c9ad1c837e2a3; MUSIC_U=f8ababe6f57a6bc7b80127713f01030263a8bd66354c1adc4b4f8a1d177633ae0931c3a9fbfe3df2; __csrf=9d66fba8f3074172a4fdc1d2d1ba53b6; __remember_me=true; JSESSIONID-WYYY=jnkzj%2Fpapc%2Bt0rbc%2F2nGS2U3YgpGJJ38EIfMAG485swu3VOMQ%2BwOJ3ZJJovD005hD%2F47KptaEJVuQuBVFcKRX8uxjxY7ZK0uWzwuoTScEW6pmFdENMesyp9837tQFzFt6dNKh0s813Txkd9nHG%2F6zPzYxVXtwzKzOUbx1rZh0UAV2ihR%3A1608178115978; WEVNSM=1.0.0; WNMCID=zphsoz.1608176316498.01.0");

        // 从得到的回复的header字段中找到重定向到的地址并返回
        return urlCon.getHeaderField("Location");
    }

    // 获取歌单详细信息
    // http://www.hjmin.com/playlist/detail?id=24381616
    public static Playlist getPlaylistDetailByAPI(String playlistID) throws IOException, SQLException {
        System.out.println("http://www.hjmin.com/playlist/detail?id=" + playlistID);

        Playlist playlist = ParseJSON.parsePlaylistJSON(getContent("http://www.hjmin.com/playlist/detail?id=" + playlistID), false);

        // 将读取到的信息写入MySQL中
        db.insertPlaylist(playlist);

        return playlist;
    }

    // 获取每日推荐
    // http://www.hjmin.com/personalized?limit=8
    public static List<Playlist> getDailyRecommendByAPI(int page) throws IOException, SQLException {

        List<Playlist> playlists = ParseJSON.parsePlaylists(new JSONObject(getContent("http://www.hjmin.com/personalized?limit=" + (page * 8))).getJSONArray("result").toString(), true);

        // 将读取到的信息写入MySQL中
        db.insertPlaylists(playlists);
        db.insertDaily_Recommend_Playlist(playlists);

        // 返回获取到的最后8个playlist
        return playlists.subList(playlists.size() - 8, playlists.size());
    }

    // 获取多首歌的详细信息
    // http://www.hjmin.com/song/detail?ids=1462573209,1377053293
    public static List<Song> getSongsDetailByAPI(List<String> songIDs) throws IOException, SQLException {

        List<Song> songs = new ArrayList<>();

        // 由于URL长度存在限制，因此不能将所有的songID放在同一个URL中进行请求
        int songIDNum = songIDs.size();
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < songIDNum; i++) {
            ids.append(songIDs.get(i));

            // 进行到最后一个songID或者长度超出限制时，就进行请求，否则加上,(逗号)，将下一个songID继续添加在URL内
            if ((i != songIDNum - 1) && ids.length() < 1900) {
                ids.append(",");
            } else {
                System.out.println("http://www.hjmin.com/song/detail?ids=" + ids);

                String content = getContent("http://www.hjmin.com/song/detail?ids=" + ids);
                songs.addAll(ParseJSON.parseSongs(new JSONObject(content).getJSONArray("songs").toString(), true));

                // 清除stringBuilder的内容
                ids.delete(0, ids.length());
            }
        }

        // 我们读取到后，把信息写入MySQL中
        db.insertSongs(songs);

        return songs;
    }

    // 由mvID获取mv视频源
    // http://www.hjmin.com/mv/url?id=5436712
    public static String getMVSrcByAPI(String mvID) throws IOException {
        try {
            return new JSONObject(getContent("http://www.hjmin.com/mv/url?id=" + mvID)).getJSONObject("data").getString("url");
        } catch (JSONException e) {
            return "";
        }
    }
}

// 仅为Request类提供服务，将JSON格式的内容解析为字符串或Song和Playlist对象
// 所有函数都是static类型，无需实例化
class ParseJSON {

    // 解析Song的JSON，返回Song对象
    // 例：http://www.hjmin.com/song/detail?ids=1497035816
    // isSearch和isBrief变量均是用来判断JSON数据的格式，因为随着API的不同，JSON数据的格式会有小的变化
    public static Song parseSongJSON(String string, boolean isSearch, boolean isBrief) {

        Song song = new Song();

        JSONObject songObj;
        if (!isSearch) {
            songObj = new JSONObject(string).getJSONArray("songs").getJSONObject(0);
        } else {
            songObj = new JSONObject(string);
        }

        song.setSongName(songObj.getString("name"));
        song.setSongID(songObj.getLong("id") + "");
        song.setAlbumID(songObj.getJSONObject((!isBrief) ? "album" : "al").getLong("id") + "");

        if (isSearch && isBrief) {
            song.setAlbumPosition(songObj.getInt("no"));
        }

        if (isBrief) {
            song.setImgSrc(songObj.getJSONObject("al").getString("picUrl"));
        }

        JSONArray artistArray = songObj.getJSONArray((!isBrief) ? "artists" : "ar");
        StringBuilder artistName = new StringBuilder();
        for (int i = 0; i < artistArray.length(); i++) {
            artistName.append(artistArray.getJSONObject(i).getString("name"));

            // 如果不是最后一个歌手，则加“ \ ”
            if (i != artistArray.length() - 1) {
                artistName.append(" \\ ");
            }
        }
        song.setArtistName(artistName.toString());

        song.setDuration(songObj.getInt((!isBrief) ? "duration" : "dt"));
        song.setMvID(songObj.getLong((!isBrief) ? "mvid" : "mv") + "");

        return song;
    }

    // 获取包含多个Song对象的List
    public static List<Song> parseSongs(String string, boolean isBrief) {
        List<Song> songs = new ArrayList<>();

        JSONArray songObjArray = new JSONArray(string);
        for (int i = 0; i < songObjArray.length(); i++) {
            JSONObject songObj = songObjArray.getJSONObject(i);
            songs.add(parseSongJSON(songObj.toString(), true, isBrief));
        }

        return songs;
    }

    // 解析playlist的JSON，返回playlist对象
    // 例：http://www.hjmin.com/playlist/detail?id=24381616
    // isDaily变量是用来判断JSON数据的格式的，因为随着API的不同，JSON数据的格式会有小的变化
    public static Playlist parsePlaylistJSON(String string, boolean isDaily) {

        Playlist playlist = new Playlist();
        JSONObject playlistObj;

        if (isDaily) {
            playlistObj = new JSONObject(string);
        } else {
            playlistObj = new JSONObject(string).getJSONObject("playlist");
        }

        playlist.setPlaylistName(playlistObj.getString("name"));
        playlist.setPlaylistID(playlistObj.getLong("id") + "");
        playlist.setPlayCount(playlistObj.getLong("playCount"));
        if (!isDaily) {
            playlist.setImgSrc(playlistObj.getString("coverImgUrl"));
            JSONArray songArray = playlistObj.getJSONArray("trackIds");
            List<String> songIDs = new ArrayList<>();
            for (int i = 0; i < songArray.length(); i++) {
                JSONObject songObj = songArray.getJSONObject(i);
                songIDs.add(songObj.getLong("id") + "");
            }
            playlist.setSongIDs(songIDs);
        } else {
            playlist.setImgSrc(playlistObj.getString("picUrl"));
        }

        return playlist;
    }

    // 获取包含多个playlist对象的List
    public static List<Playlist> parsePlaylists(String string, boolean isDaily) {
        List<Playlist> playlists = new ArrayList<>();

        JSONArray playlistObjArray = new JSONArray(string);
        for (int i = 0; i < playlistObjArray.length(); i++) {
            JSONObject playlistObj = playlistObjArray.getJSONObject(i);
            playlists.add(parsePlaylistJSON(playlistObj.toString(), isDaily));
        }

        return playlists;
    }
}