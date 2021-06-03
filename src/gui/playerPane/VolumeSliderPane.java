package gui.playerPane;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;

// 音量滑动条
public class VolumeSliderPane extends Pane {

    // 三个组件：leftBox, rightBox, thumb
    private VBox leftBox;
    private VBox rightBox;
    private ImageView thumb;

    // 音量值
    private double volumeValue;

    // 宽高
    private final double width;
    private final double height;

    // 要调整音量的mediaPlayer
    private MediaPlayer mediaPlayer;

    // 构造方法
    public VolumeSliderPane(double width, double height, double initValue) {

        // 初始化宽高，用于setVolume()
        this.width = width;
        this.height = height;

        // 初始化LeftBox, RightBox, Thumb
        initLeftBox(width, height);
        initRightBox(width, height);
        initThumb(width, height);

        // 将LeftBox, RightBox, Thumb添加到VolumeSliderPane
        this.getChildren().addAll(rightBox, leftBox, thumb);

        // 设置初始声音
        setVolumePercent(initValue);

        // 鼠标进入时显示thumb图像，鼠标变成手形
        this.setOnMouseEntered(event -> {
            thumb.setVisible(true);
            setCursor(Cursor.HAND);
        });

        // 鼠标移出时隐藏thumb图像，鼠标变成默认样式
        this.setOnMouseExited(event -> {
            thumb.setVisible(false);
            setCursor(Cursor.DEFAULT);
        });
    }

    // 初始化leftBox
    public void initLeftBox(double width, double height) {

        // 实例化leftBox为VBox
        leftBox = new VBox();

        // 设置样式
        leftBox.setStyle("-fx-background-color: #b9110b;-fx-background-radius: 4px;");

        // 设置leftBox位置和宽高
        leftBox.setLayoutX(height / 3);
        leftBox.setLayoutY(height / 3);
        leftBox.setPrefSize(0, height / 3);

        // 设置leftBox鼠标单击事件，改变音量
        leftBox.setOnMouseClicked(event -> setVolumePercent((event.getSceneX() - leftBox.getLayoutX() - 720) / (width - 2 * height / 3)));
    }

    // 初始化rightBox
    private void initRightBox(double width, double height) {

        // 实例化rightBox为VBox
        rightBox = new VBox();

        // 设置样式
        rightBox.setStyle("-fx-background-color: #858d9c;-fx-background-radius: 4px;");

        // 设置rightBox位置和宽高
        rightBox.setLayoutX(height / 3);
        rightBox.setLayoutY(height / 3);
        rightBox.setPrefSize(width - 2 * height / 3, height / 3);

        // 设置rightBox鼠标单击事件，改变音量
        rightBox.setOnMouseClicked(event -> setVolumePercent((event.getSceneX() - leftBox.getLayoutX() - 720) / (width - 2 * height / 3)));
    }

    // 初始化thumb
    private void initThumb(double width, double height) {

        // 实例化thumb图像
        thumb = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("../image/volumeThumb.png")).toExternalForm()));

        // 设置位置和宽高
        thumb.setFitWidth(height);
        thumb.setFitHeight(height);
        thumb.setLayoutY(0);

        // 设置为不可见
        thumb.setVisible(false);

        // 设置鼠标拖拽事件，改变音量
        thumb.setOnMouseDragged(event -> setVolumePercent((event.getSceneX() - leftBox.getLayoutX() - 720) / (width - 2 * height / 3)));
    }

    // 设置音量
    public void setVolumePercent(double v) {

        // v为大于0小于1.0的值
        v = Double.min(1.0, v);
        v = Double.max(0, v);

        // 如果已经有了对应的mediaPlayer，设置mediaPlayer音量
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(v);
        }

        // 设置VolumeSliderPane音量
        this.volumeValue = v;

        // 设置leftBox宽度
        leftBox.setPrefWidth((width - 2 * height / 3) * v);

        // 设置thumb位置
        thumb.setLayoutX((width - 2 * height / 3) * v - height / 6);
    }

    // 设置自己的mediaPlayer
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    // 获取当前音量值
    public double getVolumeValue() {
        return volumeValue;
    }
}