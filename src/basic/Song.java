package basic;

import java.util.Objects;

// 基本类：歌曲
public class Song {
    private String songName = ""; // 歌曲名
    private String artistName = ""; // 歌手名称
    private double duration; // 歌曲时长，单位：毫秒
    private String imgSrc = ""; // 所属专辑的封面的图片地址
    private String songID = ""; // 歌曲ID
    private String mvID = ""; // 如果等于0说明没有mv
    private String albumID; // 所属的专辑ID
    private int albumPosition; // 在专辑中的位置

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String durationString() {
        return String.format("%02d:%02d", (int) this.duration / 60000, ((int) this.duration / 1000) % 60);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return Objects.equals(songID, song.getSongID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(songID);
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public void setMvID(String mvID) {
        this.mvID = mvID;
    }

    public void setAlbumPosition(int albumPosition) {
        this.albumPosition = albumPosition;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public double getDuration() {
        return duration;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getSongID() {
        return songID;
    }

    public String getMvID() {
        return mvID;
    }

    public String getAlbumID() {
        return albumID;
    }

    public int getAlbumPosition() {
        return albumPosition;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", duration=" + duration +
                ", imgSrc='" + imgSrc + '\'' +
                ", songID='" + songID + '\'' +
                ", mvID='" + mvID + '\'' +
                ", albumID='" + albumID + '\'' +
                ", albumPosition=" + albumPosition +
                '}';
    }
}
