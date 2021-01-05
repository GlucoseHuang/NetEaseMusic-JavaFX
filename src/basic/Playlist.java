package basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 基本类：歌单
public class Playlist {
    private String imgSrc; // 歌单图片源地址
    private String playlistName; // 歌单标题
    private String playlistID; // 歌单ID
    private long playCount = 0; // 播放数
    private List<String> songIDs = new ArrayList<>(); // 包含的songID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(playlistID, playlist.playlistID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistID);
    }

    // 将playCount数值转化为字符串
    public String playCountString() {
        if (playCount < 100000) {
            return "" + playCount;
        } else {
            double w = playCount / 10000.0;
            if (w > 10000) {
                return String.format("%.2f亿", w / 10000.0);
            } else {
                return String.format("%.2f万", w);
            }
        }
    }

    public List<String> getSongIDs() {
        return songIDs;
    }

    public void setSongIDs(List<String> songIDs) {
        this.songIDs = songIDs;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(long playCount) {
        this.playCount = playCount;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "imgSrc='" + imgSrc + '\'' +
                ", playlistName='" + playlistName + '\'' +
                ", playlistID='" + playlistID + '\'' +
                ", playCount=" + playCount +
                ", songIDs=" + songIDs +
                '}';
    }
}
