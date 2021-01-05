package main;

import data.DataAPI;
import gui.playerPane.PlayerPane;
import gui.playlistBlockListPane.PlaylistBlockListPane;
import gui.playlistBlockListPane.TopListPane;
import gui.leftPane.LeftPane;
import gui.songListPane.SongListPane;
import gui.titlePane.TitlePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

// 用于启动界面的类，继承Application类
public class Main extends Application {

    // 之后调用initXXStage()对4个stage进行初始化
    private Stage titleStage; // 最顶端的，用于显示搜索框，最小化按钮，关闭窗口按钮，Logo
    private Stage playerStage; // 最底端的，控制音乐播放
    private Stage leftStage; // 左端的，显示每日推荐和热榜按钮+用户收藏的音乐和歌单
    private Stage mainStage; // 右部的，用于显示主要内容（如热榜内容，歌单内容等）

    // 所需要的Pane
    private TitlePane titlePane; // titleStage的唯一Pane，最顶端的标题

    private PlayerPane playerPane; // 播放区域，仅有playerStage一个stage

    private LeftPane leftPane; // leftStage的唯一Pane

    // 下面三个都是mainStage的内容
    private SongListPane songListPane; // 歌单的songList，不是当前播放列表（songListPane中实例化了playlistHeaderPane）
    private PlaylistBlockListPane playlistBlockListPane; // 歌单块列表
    private TopListPane topListPane; // 热榜Pane

    // 三个scene
    private Scene scene1, scene2, scene3;

    // 初始化时titleStage距离屏幕左上角的位置
    private double x, y;

    // title_x, title_y 用于记录当前titleStage坐标位置
    // mouse_x, mouse_y为鼠标按下位置
    // 这四个变量用于实现鼠标拖拽操作
    private double title_x, title_y, mouse_x, mouse_y;

    // 这里的stage并没有被用到
    @Override
    public void start(Stage stage) throws Exception {

        // 初始化时titleStage坐标(100, 100)
        x = 200;
        y = 100;

        // 初始化各个stage以及为每个stage提供其他的stage和scene指针（顺序不能变，否则会出现空指针异常）
        initLeftStage();
        initPlayerStage();
        songListPane = new SongListPane(playerPane, leftPane.getPlaylistListPane(), titleStage);
        leftPane.setSongListPane(songListPane);
        playlistBlockListPane = new PlaylistBlockListPane(songListPane, scene2, mainStage);
        topListPane = new TopListPane();
        initMainStage();
        topListPane.setEssentials(mainStage, scene2, songListPane);
        initTitleStage();
        leftPane.setEssentials(mainStage, scene1, scene2, scene3, topListPane);

        // 将其余三个Stage的Owner设为titleStage
        mainStage.initOwner(titleStage);
        playerStage.initOwner(titleStage);
        leftStage.initOwner(titleStage);

        // show所有的stage
        titleStage.show();
        mainStage.show();
        playerStage.show();
        leftStage.show();

        // 设置单击右上角close ImageView的事件
        titlePane.getClose().setOnMouseClicked(event -> {
            try {
                DataAPI.exit(); // 关闭数据库连接
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                titleStage.close(); // 关闭titleStage
            }
        });

        //设置logo和标题
        titleStage.setTitle(" 网易云音乐");
        titleStage.getIcons().add(new Image(getClass().getResourceAsStream("../gui/image/icon.png")));
    }

    // titleStage配置
    private void initTitleStage() {

        // 实例化titleStage
        titleStage = new Stage();

        // 实例化titlePane
        titlePane = new TitlePane(songListPane, titleStage, mainStage, scene1, scene2);

        // 设置titleStage没有边框
        titleStage.initStyle(StageStyle.TRANSPARENT);

        // 由于stage中只能是scene，因此需要构造一个scene，将titlePane放入titleStage
        titleStage.setScene(new Scene(titlePane, 1000, 50));

        // 设置初始位置
        titleStage.setX(x);
        titleStage.setY(y);

        // setOnMousePressed和setOnMouseDragged两个监听器用于实现鼠标拖拽操作
        titlePane.setOnMousePressed(event -> {
            title_x = titleStage.getX();
            title_y = titleStage.getY();
            mouse_x = event.getScreenX();
            mouse_y = event.getScreenY();
        });
        titlePane.setOnMouseDragged(event -> {

            // new_x, new_y是titleStage的新坐标
            double new_x = event.getScreenX() + title_x - mouse_x;
            double new_y = event.getScreenY() + title_y - mouse_y;
            titleStage.setX(new_x);
            titleStage.setY(new_y);

            // 相应地对其他stage进行移动
            mainStage.setX(new_x + 200);
            mainStage.setY(new_y + 50);
            playerStage.setX(new_x);
            playerStage.setY(new_y + 600);
            leftStage.setX(new_x);
            leftStage.setY(new_y + 50);
        });
    }

    // playerStage配置
    private void initPlayerStage() {

        // 实例化一个新Stage用于放置player
        playerStage = new Stage();

        // 初始化player
        playerPane = new PlayerPane(playerStage, leftPane);
        playerPane.initPlayerPane();

        // 设置playerStage没有边框
        playerStage.initStyle(StageStyle.TRANSPARENT);

        // 设置playerStage位置
        playerStage.setX(x);
        playerStage.setY(y + 600);
    }

    // leftStage配置
    private void initLeftStage() throws SQLException {

        // 实例化leftStage
        leftStage = new Stage();

        // 实例化leftPane
        leftPane = new LeftPane();

        // 设置leftPane的背景颜色为白色
        leftPane.setStyle("-fx-background-color: #ffffff;");

        // 将leftPane添加到leftStage
        Scene scene = new Scene(leftPane, 200, 550);
        leftStage.setScene(scene);

        // 设置leftStage没有边框
        leftStage.initStyle(StageStyle.TRANSPARENT);

        // 设置leftStage位置
        leftStage.setX(x);
        leftStage.setY(y + 50);
    }

    // mainStage配置
    private void initMainStage() {

        // 实例化mainStage
        mainStage = new Stage();

        // scene1实例化及相关设置
        scene1 = new Scene(playlistBlockListPane, 800, 550);
        playlistBlockListPane.setStyle("-fx-background-color: #ffffff;");

        // scene2实例化及相关设置
        scene2 = new Scene(songListPane, 800, 550);
        playlistBlockListPane.setEssentials(scene2, mainStage);

        // scene3实例化
        scene3 = new Scene(topListPane, 800, 550);

        // 设置主Stage没有边框
        mainStage.initStyle(StageStyle.TRANSPARENT);

        // 将主Stage的初始scene设为scene1
        mainStage.setScene(scene1);

        // 设置主Stage的位置
        mainStage.setX(x + 200);
        mainStage.setY(y + 50);
    }

    // 程序入口
    public static void main(String[] args) {
        launch(args);
    }
}
