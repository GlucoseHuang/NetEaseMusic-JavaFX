package gui.playerPane;

import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

// 当前播放歌曲的进度条
public class PlaySliderPane extends Pane {

    // 三个组件：leftBox, rightBox, thumb
    private VBox leftBox;
    private VBox rightBox;
    private ImageView thumb;

    // 宽高
    private final double width;
    private final double height;

    // 要调整进度的mediaPlayer
    private MediaPlayer mediaPlayer;

    // 构造方法
    public PlaySliderPane(double width, double height) {

        // 初始化长宽，用于setValue()
        this.width = width;
        this.height = height;

        // 初始化LeftBox, RightBox, Thumb
        initLeftBox(width, height);
        initRightBox(width, height);
        initThumb(width, height);

        // 将LeftBox, RightBox, Thumb添加到PlayerSliderPane
        this.getChildren().addAll(rightBox, leftBox, thumb);

        // 鼠标进入时，鼠标变成手形
        this.setOnMouseEntered(event -> setCursor(Cursor.HAND));

        // 鼠标移出时，鼠标变成默认样式
        this.setOnMouseExited(event -> setCursor(Cursor.DEFAULT));
    }

    // 初始化leftBox
    private void initLeftBox(double width, double height) {

        // 实例化leftBox为VBox
        leftBox = new VBox();

        // 设置样式
        leftBox.setStyle("-fx-background-color: #b9110b;-fx-background-radius: 4px;");

        // 设置leftBox位置和宽高
        leftBox.setLayoutX(height / 3);
        leftBox.setLayoutY(height / 3);
        leftBox.setPrefSize(0, height / 3);

        // 设置leftBox鼠标单击事件，改变当前播放位置
        leftBox.setOnMouseClicked(event -> setDurationPercent((event.getSceneX() - leftBox.getLayoutX() - 245) / (width - 2 * height / 3), true));
    }

    // 初始化rightBox
    private void initRightBox(double width, double height) {

        // 实例化rightBox为VBox
        rightBox = new VBox();

        // 设置样式
        rightBox.setStyle("-fx-background-color: #858d9c; -fx-background-radius: 4px;");

        // 设置rightBox位置和宽高
        rightBox.setPrefSize(width - 2 * height / 3, height / 3);
        rightBox.setLayoutX(height / 3);
        rightBox.setLayoutY(height / 3);

        // 设置rightBox鼠标单击事件，改变当前播放位置
        rightBox.setOnMouseClicked(event -> setDurationPercent((event.getSceneX() - leftBox.getLayoutX() - 245) / (width - 2 * height / 3), true));
    }

    // 初始化thumb
    private void initThumb(double width, double height) {

        // 实例化thumb图像
        thumb = new ImageView(new Image(getClass().getResource("../image/playThumb.png").toExternalForm()));

        // 设置thumb位置和宽高
        thumb.setLayoutY(0);
        thumb.setFitWidth(height);
        thumb.setFitHeight(height);

        // 设置鼠标拖拽事件，改变当前播放位置
        thumb.setOnMouseDragged(event -> setDurationPercent((event.getSceneX() - leftBox.getLayoutX() - 245) / (width - 2 * height / 3), true));
    }

    // 设定value
    public void setDurationPercent(double v, boolean isSeeking) {

        // v为大于0小于1.0的值
        v = Double.min(1.0, v);
        v = Double.max(0, v);

        // 设置leftBox的宽度
        leftBox.setPrefWidth((width - 2 * height / 3) * v);

        // 设置thumb的位置
        thumb.setLayoutX((width - 2 * height / 3) * v - height / 6);

        // 如果需要调整mediaPlayer的播放进度
        if (isSeeking) {
            mediaPlayer.seek(new Duration(v * mediaPlayer.getMedia().getDuration().toMillis()));
        }
    }

    // 设定当前的mediaPlayer
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}