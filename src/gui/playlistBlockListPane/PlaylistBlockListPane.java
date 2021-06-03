package gui.playlistBlockListPane;

import basic.Playlist;
import data.DataAPI;
import gui.songListPane.SongListPane;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

// 多个块状的播放列表的Pane组成的Pane
public class PlaylistBlockListPane extends Pane {

    // 页面操作pane(用于切换页面)
    private Pane pageOptionPane;

    // lastPage, nextPage, buttons均属于pageOptionPane
    private ImageView lastPage, nextPage;
    private Button[] buttons;

    //pageOptionPane中的页面按钮数量
    private final int maxPageNum = 8;

    // 用于更改songListPane内容
    private final SongListPane songListPane;

    // 所有的playlist
    private List<Playlist> playlists;

    // 当前页面（从1开始）
    private int currentPage = 1;

    // 高度的最大值
    private double maxHeight = 0;

    // 必要的stage和scene
    private Stage stage;
    private Scene scene;

    // 构造方法
    public PlaylistBlockListPane(SongListPane songListPane, Scene scene, Stage stage) throws IOException, InterruptedException, SQLException {

        // 设置所需的内容
        this.songListPane = songListPane;
        this.scene = scene;
        this.stage = stage;

        // 载入页面
        loadPage();

        // 初始化pageOptionPane
        initPageOptionPane();

        // 50为pageOptionPane的高度
        maxHeight += 50;

        // 将pageOptionPane添加到PlaylistBlockListPane
        this.getChildren().add(pageOptionPane);

        //设定鼠标滚动
        this.setOnScroll(event -> {
            double newY = getLayoutY() + event.getDeltaY();
            newY = Double.max(550 - maxHeight, newY);
            newY = Double.min(0, newY);
            setLayoutY(newY);
        });
    }

    // 初始化PageOptionPane
    private void initPageOptionPane() {

        // 实例化pageOptionPane
        pageOptionPane = new Pane();

        // 设置pageOptionPane位置、大小和背景颜色
        pageOptionPane.setLayoutX(0);
        pageOptionPane.setLayoutY(maxHeight);
        pageOptionPane.setPrefSize(800, 50);
        pageOptionPane.setStyle("-fx-background-color : #ffffff");

        // 初始化lastPage和nextPage图像
        double buttonWidthHeight = 40;
        double buttonGap = 20;
        double initialX = (800 - (maxPageNum + 2) * (buttonWidthHeight + buttonGap) + buttonGap) / 2;
        initLastPage(buttonWidthHeight, initialX);
        initNextPage(buttonWidthHeight, buttonGap, initialX);

        // 将lastPage和nextPage添加到pageOptionPane
        pageOptionPane.getChildren().addAll(lastPage, nextPage);

        // 将num个按钮添加到pageOptionPane
        buttons = new Button[maxPageNum];
        for (int i = 0; i < maxPageNum; i++) {

            // 实例化button
            if (i + 1 != currentPage) {
                buttons[i] = new Button((i + 1) + "");
            } else {
                buttons[i] = new Button("[" + (i + 1) + "]");
            }

            // 设置位置、大小、样式
            buttons[i].setLayoutX(initialX + (i + 1) * (buttonWidthHeight + buttonGap));
            buttons[i].setLayoutY(5);
            buttons[i].setPrefSize(buttonWidthHeight, buttonWidthHeight);
            buttons[i].setStyle("-fx-background-color : #ffffff");
            buttons[i].setFont(new Font(14));

            // 按钮触发事件
            int finalI = i;
            buttons[i].setOnAction(event -> {

                // 切换到所单击的页面
                try {
                    currentPage = Integer.parseInt(buttons[finalI].getText());
                } catch (NumberFormatException e) {
                    return;
                }

                try {
                    loadPage();
                } catch (IOException | InterruptedException | SQLException e) {
                    e.printStackTrace();
                }
            });

            // 鼠标进入时，变成手形，按钮背景颜色变红，文字颜色变白
            buttons[i].setOnMouseEntered(event -> {
                buttons[finalI].setStyle("-fx-background-color : #ec4141;");
                buttons[finalI].setTextFill(Paint.valueOf("#ffffff"));
                setCursor(Cursor.HAND);
            });

            // 鼠标移出时，变成手形，按钮背景颜色变白，文字颜色变黑
            buttons[i].setOnMouseExited(event -> {
                buttons[finalI].setStyle("-fx-background-color : #ffffff");
                buttons[finalI].setTextFill(Paint.valueOf("#000000"));
                setCursor(Cursor.DEFAULT);
            });

            // 将该button添加到pageOptionPane
            pageOptionPane.getChildren().add(buttons[i]);
        }
    }

    // 初始化lastPage图像
    private void initLastPage(double buttonWidthHeight, double initialX) {

        // 实例化lastPage
        lastPage = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/lastPage.png")).toExternalForm()));

        // 设置大小和位置
        lastPage.setFitWidth(buttonWidthHeight);
        lastPage.setFitHeight(buttonWidthHeight);
        lastPage.setLayoutY(5);
        lastPage.setLayoutX(initialX);

        // 鼠标进入时，变成手形
        lastPage.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，变成普通样式
        lastPage.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

        // 鼠标单击时，翻到上一页
        lastPage.setOnMouseClicked(event -> {
            if (currentPage >= 1) {
                currentPage -= 1;
                try {
                    loadPage();
                } catch (IOException | InterruptedException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 初始化nextPage图像
    private void initNextPage(double buttonWidthHeight, double buttonGap, double initialX) {

        // 实例化nextPage
        nextPage = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/nextPage.png")).toExternalForm()));

        // 设置大小和位置
        nextPage.setFitHeight(buttonWidthHeight);
        nextPage.setFitWidth(buttonWidthHeight);
        nextPage.setLayoutY(5);
        nextPage.setLayoutX(initialX + (maxPageNum + 1) * (buttonWidthHeight + buttonGap));

        // 鼠标进入时，变成手形
        nextPage.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，变成普通样式
        nextPage.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));

        // 鼠标单击时，翻到下一页
        nextPage.setOnMouseClicked(event -> {
            currentPage += 1;
            try {
                loadPage();
            } catch (IOException | InterruptedException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // 加载所有的playlistBlock
    public void loadPlaylistBlock(List<Playlist> playlists) {
        if (this.playlists != null) {
            this.getChildren().clear();
            this.getChildren().add(pageOptionPane);
        }

        this.playlists = playlists;
        for (int i = 0; i < this.playlists.size(); i++) {

            PlaylistBlockPane playlistBlockPane = new PlaylistBlockPane(playlists.get(i));
            playlistBlockPane.setLayoutY((playlistBlockPane.getSumH() + 15) * (float) (i / 4) + 15);
            playlistBlockPane.setLayoutX((playlistBlockPane.getBlockPaneWidth() + 15) * (i % 4) + 15);

            maxHeight = Double.max(maxHeight, playlistBlockPane.getLayoutY() + playlistBlockPane.getSumH());

            playlistBlockPane.setOnMouseClicked(event -> {
                try {
                    songListPane.setSongs(DataAPI.getSongsOfPlaylist(playlistBlockPane.getPlaylist().getPlaylistID()), playlistBlockPane.getPlaylist());
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
                stage.setScene(scene);
            });
            this.getChildren().add(playlistBlockPane);
        }
    }

    // 载入页面
    public void loadPage() throws IOException, InterruptedException, SQLException {

        // 首先改变底部的按钮文本
        if (playlists != null) {
            for (int j = 0; j < maxPageNum; j++) {

                // 页面序号
                int page = currentPage + j + 1 - Integer.min(4, currentPage);

                // 如果是当前页面，就在前后加上[]
                if (page == currentPage) {
                    buttons[j].setText("[" + page + "]");
                } else {
                    buttons[j].setText(page + "");
                }
            }
        }

        // 加载playlistBlock
        loadPlaylistBlock(DataAPI.getDailyRecommend(currentPage));
    }

    // 提供所需的参数，用于切换场景和关闭窗口等
    public void setEssentials(Scene scene, Stage stage) {
        this.stage = stage;
        this.scene = scene;
    }
}