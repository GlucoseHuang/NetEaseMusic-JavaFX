package gui.titlePane;

import data.DataAPI;
import gui.songListPane.SongListPane;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

//最上方的Pane，包含logo，搜索框，关闭按钮，最小化按钮
public class TitlePane extends Pane {

    // TitlePane的五个组件
    private ImageView logo, close, minimize, search;
    private TextField searchTextField;

    // 歌曲列表Pane
    private final SongListPane songListPane;

    // 其所在的Stage，用于关闭和最小化
    private final Stage titleStage;

    // 主Stage
    private final Stage mainStage;

    // 显示推荐歌单的scene
    private final Scene scene1;

    // doSearch后切换到的scene
    private final Scene scene2;

    // 构造方法
    public TitlePane(SongListPane songListPane, Stage titleStage, Stage mainStage, Scene scene1, Scene scene2) {

        // 配置需要的内容
        this.songListPane = songListPane;
        this.titleStage = titleStage;
        this.mainStage = mainStage;
        this.scene1 = scene1;
        this.scene2 = scene2;

        //设置大小和背景颜色
        this.setPrefSize(1000, 50);
        this.setStyle("-fx-background-color: #ec4141");

        // 初始化logo, close, minimize, search, searchtextfield
        initLogo();
        initClose();
        initMinimize();
        initSearch();
        initSearchTextField();

        // 将logo, close, minimize, search, searchtextfield添加到titlePane
        this.getChildren().addAll(logo, close, minimize, searchTextField, search);
    }

    // 初始化logo ImageView
    private void initLogo() {

        // 根据本地文件实例化ImageView
        logo = new ImageView(new Image(getClass().getResource("../image/logo.png").toExternalForm()));

        // 设置logo位置
        logo.setFitWidth(150);
        logo.setFitHeight(42);
        logo.setLayoutX(15);
        logo.setLayoutY(2);

        // 单击时回到首页
        logo.setOnMouseClicked(event -> mainStage.setScene(scene1));

        // 鼠标进入时，鼠标变成手形
        logo.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移开时，鼠标变回指针
        logo.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
    }

    // 初始化close ImageView
    private void initClose() {

        // 根据本地文件实例化ImageView
        close = new ImageView(new Image(getClass().getResource("../image/close.png").toExternalForm()));

        // 设置close图标位置
        close.setFitWidth(20);
        close.setFitHeight(20);
        close.setLayoutX(950);
        close.setLayoutY(15);

        // 鼠标进入时，鼠标变成手形，图片变成亮色
        close.setOnMouseEntered(event -> {
            close.setImage(new Image(getClass().getResource("../image/close_h.png").toExternalForm()));
            setCursor(Cursor.HAND);
        });

        // 鼠标移开时，鼠标变回指针，图片变回暗色
        close.setOnMouseExited(event -> {
            close.setImage(new Image(getClass().getResource("../image/close.png").toExternalForm()));
            setCursor(Cursor.DEFAULT);
        });
    }

    // 初始化minimize ImageView
    private void initMinimize() {

        // 根据本地文件实例化ImageView
        minimize = new ImageView(new Image(getClass().getResource("../image/minimize.png").toExternalForm()));

        // 设置minimize图标位置
        minimize.setFitWidth(20);
        minimize.setFitHeight(20);
        minimize.setLayoutX(900);
        minimize.setLayoutY(15);

        // 设置鼠标单击时最小化
        minimize.setOnMouseClicked(event -> titleStage.setIconified(true));

        // 鼠标进入时，鼠标变成手形，图片变成亮色
        minimize.setOnMouseEntered(event -> {
            setCursor(Cursor.HAND);
            minimize.setImage(new Image(getClass().getResource("../image/minimize_h.png").toExternalForm()));
        });

        // 鼠标移开时，鼠标变回指针，图片变回暗色
        minimize.setOnMouseExited(event -> {
            setCursor(Cursor.DEFAULT);
            minimize.setImage(new Image(getClass().getResource("../image/minimize.png").toExternalForm()));
        });
    }

    // 初始化搜索图标ImageView
    private void initSearch() {
        search = new ImageView(new Image(getClass().getResource("../image/search.png").toExternalForm()));

        // 设置搜索图标位置
        search.setFitWidth(20);
        search.setFitHeight(20);
        search.setLayoutX(455);
        search.setLayoutY(15);

        // 单击搜索图标进行搜索
        search.setOnMouseClicked(event -> {
            try {
                doSearch();
            } catch (IOException | InterruptedException | SQLException e) {
                e.printStackTrace();
            }
        });

        // 鼠标进入时，鼠标变成手形，图片变成亮色
        search.setOnMouseEntered(event -> {
            setCursor(Cursor.HAND);
            search.setImage(new Image(getClass().getResource("../image/search_h.png").toExternalForm()));
        });

        // 鼠标移开时，鼠标变回指针，图片变回暗色
        search.setOnMouseExited(event -> {
            setCursor(Cursor.DEFAULT);
            search.setImage(new Image(getClass().getResource("../image/search.png").toExternalForm()));
        });
    }

    // 初始化搜索框TextField
    private void initSearchTextField() {
        searchTextField = new TextField();

        // 设置搜索框样式和位置
        searchTextField.setPromptText("搜索歌曲");
        searchTextField.setStyle("-fx-background-radius: 32px;-fx-text-inner-color: #000000;-fx-background-color: #e13e3e;-fx-text-fill:#ffffff");
        searchTextField.setPrefSize(200, 20);
        searchTextField.setLayoutX(284);
        searchTextField.setLayoutY(12.5);

        // 按回车进行搜索
        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    doSearch();
                } catch (IOException | InterruptedException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 搜索（单击搜索图标或在搜索框内按Enter键触发）
    private void doSearch() throws IOException, InterruptedException, SQLException {

        // 改变songListPane的样式和内容
        songListPane.setSongs(DataAPI.searchSong(searchTextField.getText()), false);

        // 主Stage内容设为scene2
        mainStage.setScene(scene2);
    }

    // ImageView close的get方法，用于设置单击close事件
    public ImageView getClose() {
        return close;
    }
}
