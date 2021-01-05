package gui.playerPane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

// 弹出窗口，播放MV，仅供PlayerPane使用
class PlayMV {

    // 进度条的三个组件：进度条左半边，进度条右半边，滑块
    private final VBox left = new VBox();
    private final VBox right = new VBox();
    private final ImageView thumb = new ImageView(new Image(getClass().getResource("../image/mvThumb.png").toExternalForm()));

    // 其他组件：音量值文本，暂停的图标，关闭的图标
    private Label volumeText;
    private ImageView pause, close;

    // 进度条是否隐藏
    private Boolean isSliderHiding = false;

    // 放映相关
    private final MediaPlayer mediaPlayer;
    private final MediaView mediaView;
    private final Media media;

    // 播放mv的stage和scene
    private final Stage mvStage;
    private Scene mvScene;

    // 播放到了百分之多少
    private double playPercentage;

    // 用于计时
    private double sTime;

    // 记录鼠标和stage的坐标，用于拖动窗口
    private final double[] mouseXY = new double[2];
    private final double[] stageXY = new double[2];

    // 是否正在播放
    private Boolean isPlaying = true;

    // 是否需要加载mediaPlayer
    private Boolean needLoading = true;
    // 下面是常量：

    // 窗口的宽高
    private final double videoHeight = 540;
    private final double videoWidth = 960;

    // 进度条不隐藏时高度为normal_h
    private final double normal_h = 5.0;

    // 滑块的大小为thumbSize
    private final double thumbSize = 20.0;

    // 构造方法
    public PlayMV(String url, String songName) {

        // 设定media, mediaPlayer, mediaView
        media = new Media(url);
        mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);

        // 实例化播放mv的Stage，并展示
        mvStage = new Stage(StageStyle.TRANSPARENT);
        mvStage.setScene(new Scene(new Pane(mediaView)));
        mvStage.getIcons().add(new Image(getClass().getResource("../image/icon.png").toExternalForm()));
        mvStage.setTitle(songName);
        mvStage.show();

        // 开始播放
        mediaPlayer.play();

        // 监听mediaPlayer的当前播放时间
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                if (needLoading) {

                    // 设置mediaView的大小
                    mediaView.setFitHeight(videoHeight);
                    mediaView.setFitWidth(videoWidth);

                    // 设置从头播放
                    playPercentage = 0.0;

                    // 初始化音量值文本、关闭图像、暂停图像、进度条
                    initVolumeText();
                    initSlider();
                    initPause();
                    initClose();

                    // 设置scene和stage，展示stage
                    mvScene = new Scene(new Pane(mediaView, left, right, thumb, volumeText, pause, close), videoWidth, videoHeight);
                    mvStage.setScene(mvScene);
                    mvStage.show();

                    // 设置为加载完毕，不需要再加载
                    needLoading = false;

                } else {

                    // 如果不需要加载，则更新playPercentage，更新进度条的位置和状态
                    playPercentage = mediaPlayer.getCurrentTime().toSeconds() / media.getDuration().toSeconds();
                    updateSlider();

                    // 计时三秒钟，三秒后隐藏进度条，如果mvStage被最大化，则隐藏鼠标
                    if (System.currentTimeMillis() - sTime > 3000) {
                        isSliderHiding = true;
                        if (mvStage.isMaximized()) {
                            mvScene.setCursor(Cursor.NONE);
                        }
                    }
                }
            }

            // 初始化volumeText
            private void initVolumeText() {

                // 实例化
                volumeText = new Label();

                // 设置字体
                volumeText.setFont(new Font(20));

                // 设置背景
                volumeText.setStyle("-fx-background-color: #cceb5f;");

                // 设置透明度
                volumeText.setStyle("-fx-opacity: 0.8;");

                // 设置位置
                volumeText.setLayoutX(5);
                volumeText.setLayoutY(5);

                // 不可见
                volumeText.setVisible(false);
            }

            // 初始化pause
            private void initPause() {

                // 实例化pause
                pause = new ImageView(getClass().getResource("../image/stopTag.png").toExternalForm());

                // 设置pause大小
                pause.setFitWidth(100);
                pause.setFitHeight(100);

                // 设置pause的透明度
                pause.setStyle("-fx-opacity: 0.4;");

                // 设置pause不可见
                pause.setVisible(false);
            }

            // 初始化close
            private void initClose() {

                // 实例化close
                close = new ImageView(new Image(getClass().getResource("../image/closeMv.png").toExternalForm()));

                // 设置位置和大小
                close.setFitWidth(40);
                close.setFitHeight(40);
                close.setLayoutX(mediaView.getFitWidth() - 50);
                close.setLayoutY(10);

                // 鼠标进入和移出事件时，鼠标样式变化，图像变化
                close.setOnMouseEntered(event -> {
                    close.setCursor(Cursor.HAND);
                    close.setImage(new Image(getClass().getResource("../image/closeMv_h.png").toExternalForm()));
                });
                close.setOnMouseExited(event -> {
                    close.setImage(new Image(getClass().getResource("../image/closeMv.png").toExternalForm()));
                    close.setCursor(Cursor.DEFAULT);
                });

                // 鼠标单击时关闭媒体
                close.setOnMouseClicked(event -> {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    mvStage.close();
                });
            }
        });

        // 为mediaView设置鼠标事件
        setMediaViewMouseEvent();
    }

    // 为mediaView设置鼠标事件
    private void setMediaViewMouseEvent() {

        // 鼠标进入mediaView时，设置进度条可见，记录当前时间，设置close可见
        mediaView.setOnMouseEntered(event -> {
            isSliderHiding = false;
            sTime = System.currentTimeMillis();
            close.setVisible(true);
        });

        // 鼠标在mediaView中移动时，如果进度条隐藏，则设置进度条可见，记录当前时间
        mediaView.setOnMouseMoved(event -> {
            if (isSliderHiding) {
                isSliderHiding = false;
                sTime = System.currentTimeMillis();
            }

            // 根据鼠标的位置，设置鼠标的样式：
            // 如果width - bias < 鼠标X < width + bias
            double bias = 10;
            if ((event.getSceneX() > (mvStage.getWidth() - bias)) && (event.getSceneX() < (mvStage.getWidth() + bias))) {

                // 如果height - bias < 鼠标Y < height - bias
                if ((event.getSceneY() > mvStage.getHeight() - bias) && (event.getSceneY() < mvStage.getHeight() + bias)) {

                    // 鼠标样式设定为右下缩放
                    mvScene.setCursor(Cursor.SE_RESIZE);
                } else {

                    // 鼠标样式设定为右缩放
                    mvScene.setCursor(Cursor.E_RESIZE);
                }

            } else {

                // 如果鼠标位置不在该范围内，则设定为默认样式
                mvScene.setCursor(Cursor.DEFAULT);
            }
        });

        // 鼠标在mediaView上滚动时，调整音量
        mediaView.setOnScroll(event -> {

            // 调整音量
            double v = mediaPlayer.getVolume() + event.getDeltaY() * 0.00025;
            v = Double.max(0, v);
            v = Double.min(1.0, v);
            mediaPlayer.setVolume(v);

            // 显示volumeText
            volumeText.setText("音量：" + Math.round(mediaPlayer.getVolume() * 100));
            volumeText.setVisible(true);

            // 5秒之后隐藏volumeText
            new Thread(new Task<String>() {
                @Override
                protected String call() throws Exception {
                    Thread.sleep(5000);
                    volumeText.setVisible(false);
                    return "";
                }
            }).start();
        });

        // mediaView被点击（分为单击和多击）
        mediaView.setOnMouseClicked(event -> {

            // 如果是单击
            if (event.getClickCount() == 1) {

                // 判断是否正在播放
                if (isPlaying) {

                    // 如果正在播放，则暂停，设置isPlaying，显示pause
                    mediaPlayer.pause();
                    isPlaying = false;
                    pause.setLayoutX(mediaView.getFitWidth() - 120);
                    pause.setLayoutY(mediaView.getFitHeight() * 0.9 - 100); // 进度条距顶部的距离为0.9（百分比）
                    pause.setVisible(true);

                } else {

                    // 如果不是正在播放，则播放，设置isPlaying，隐藏pause
                    mediaPlayer.play();
                    isPlaying = true;
                    pause.setVisible(false);
                }
            } else { // 如果是双击

                // 如果没有最大化
                if (!mvStage.isMaximized()) {

                    // 最大化
                    mvStage.setMaximized(true);

                    // 调整mediaView大小
                    mediaView.setFitWidth(Screen.getPrimary().getBounds().getWidth());
                    mediaView.setFitHeight(Screen.getPrimary().getBounds().getHeight());

                } else {

                    // 取消最大化
                    mvStage.setMaximized(false);

                    // 调整watchStage和mediaView大小
                    mvStage.setHeight(videoHeight);
                    mvStage.setWidth(videoWidth);
                    mediaView.setFitHeight(videoHeight);
                    mediaView.setFitWidth(videoWidth);
                }

                // 调整pause的位置
                pause.setLayoutX(mediaView.getFitWidth() - 120);
                pause.setLayoutY(mediaView.getFitHeight() * 0.9 - 100); // 进度条距顶部的距离为0.9（百分比）

                // 更新进度条的位置和状态
                updateSlider();

                // 设置close的位置
                close.setLayoutX(mediaView.getFitWidth() - 50);

            }
        });

        // 鼠标按下时，记录下当前鼠标位置和mvStage位置，用于拖动窗口操作
        mediaView.setOnMousePressed(event -> {
            mouseXY[0] = event.getScreenX();
            mouseXY[1] = event.getScreenY();
            stageXY[0] = mvStage.getX();
            stageXY[1] = mvStage.getY();
        });

        // 鼠标在mediaView中拖拽，可能是拖动窗口，也可能是缩放窗口
        mediaView.setOnMouseDragged(event -> {

            // 根据鼠标的样式判断要进行的操作
            Cursor cursor = mvScene.getCursor();
            if (cursor == Cursor.DEFAULT) {

                // 如果是拖拽，则设定mvStage的XY
                mvStage.setX(stageXY[0] + event.getScreenX() - mouseXY[0]);
                mvStage.setY(stageXY[1] + event.getScreenY() - mouseXY[1]);

            } else {

                // 如果是缩放窗口，则调整各组件的位置
                // 设置mvStage的宽高
                mvStage.setWidth(event.getScreenX() - stageXY[0]);
                mvStage.setHeight(mvStage.getWidth() * videoHeight / videoWidth); // 按比例缩放

                // 设置mediaView的位置
                mediaView.setFitWidth(mvStage.getWidth());
                mediaView.setFitHeight(mvStage.getHeight());

                // 设置pause的位置
                pause.setLayoutX(mediaView.getFitWidth() - 120);
                pause.setLayoutY(mediaView.getFitHeight() * 0.9 - 100); // 进度条距顶部的距离为0.9（百分比）

                // 设置close的位置
                close.setLayoutX(mediaView.getFitWidth() - 50);

                // 更新进度条的位置和状态
                updateSlider();
            }
        });
    }

    // 初始化滑动进度条
    private void initSlider() {

        initLeft();
        initRight();
        initThumb();

        thumb.setOnMouseDragged(event -> mediaPlayer.seek(new Duration(event.getSceneX() * media.getDuration().toMillis() / mediaView.getFitWidth())));

        // 为进度条设置鼠标事件
        for (Node node : new Node[]{left, right, thumb}) {

            // 鼠标进入时变色，鼠标移出时变色
            node.setOnMouseEntered(event -> {
                left.setStyle("-fx-background-color: #2560ce");
                right.setStyle("-fx-background-color: #7e899c");
            });
            node.setOnMouseExited(event -> {
                left.setStyle("-fx-background-color: #79a1ec");
                right.setStyle("-fx-background-color: #dfe8f8");
            });

            // 鼠标单击时移动到相应位置
            node.setOnMouseClicked(event -> mediaPlayer.seek(new Duration(event.getSceneX() * media.getDuration().toMillis() / mediaView.getFitWidth())));
        }
    }

    // 初始化进度条的左部
    private void initLeft() {
        left.setPrefSize(0, normal_h);
        left.setStyle("-fx-background-color: #79a1ec; -fx-background-radius: 4px;");
        left.setLayoutX(0);
        left.setLayoutY(0.9 * mediaView.getFitHeight());
    }

    // 初始化进度条的右部
    private void initRight() {
        right.setPrefSize(mediaView.getFitWidth(), normal_h);
        right.setStyle("-fx-background-color: #dfe8f8; -fx-background-radius: 4px;");
        right.setLayoutX(0);
        right.setLayoutY(0.9 * mediaView.getFitHeight()); // 进度条距顶部的距离为0.9（百分比）
    }

    // 初始化进度条的滑块
    private void initThumb() {
        thumb.setFitWidth(thumbSize);
        thumb.setFitHeight(thumbSize);
        thumb.setLayoutX(0 - thumbSize / 2);
        thumb.setLayoutY(right.getLayoutY() - (thumbSize - normal_h) / 2);
    }

    // 更新进度条的隐藏/显示状态和位置
    private void updateSlider() {

        // 是否正在隐藏
        if (isSliderHiding) {

            // 如果正在隐藏，显示slider（hided_h=2.0）
            thumb.setVisible(false);
            left.setPrefHeight(2.0);
            left.setLayoutY(mediaView.getFitHeight() - 2.0);
            right.setPrefHeight(2.0);
            right.setLayoutY(mediaView.getFitHeight() - 2.0);

        } else {

            // 如果正在显示，则隐藏slider
            thumb.setVisible(true);
            left.setPrefHeight(normal_h);
            left.setLayoutY(0.9 * mediaView.getFitHeight());  // 进度条距顶部的距离为0.9（百分比）
            right.setPrefHeight(normal_h);
            right.setLayoutY(0.9 * mediaView.getFitHeight());  // 进度条距顶部的距离为0.9（百分比）
        }

        // 设置left和right的长宽
        left.setPrefWidth(mediaView.getFitWidth() * playPercentage);
        right.setPrefWidth(mediaView.getFitWidth() * (1 - playPercentage));

        // 设置right的位置，thumb的位置
        right.setLayoutX(mediaView.getFitWidth() * playPercentage);
        thumb.setLayoutX(right.getLayoutX() - thumbSize / 2);
        thumb.setLayoutY(right.getLayoutY() - (thumbSize - normal_h) / 2);
    }
}