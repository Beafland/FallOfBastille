package com.github.beafland.fallofbastille;

import com.github.beafland.fallofbastille.character.HealthBarUI;
import com.github.beafland.fallofbastille.character.Mage;
import com.github.beafland.fallofbastille.character.Mechan;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class Game extends Application {
    public static final int WIDTH = 2000;
    public static final int HEIGHT = 1000;

    public static Mechan mechan;
    public static Mage mage;
    public static HealthBarUI mechanHealth;
    public static HealthBarUI mageHealth;

    // 添加一个集合来跟踪按下的键
    private final Set<KeyCode> keysPressed = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fall Of Bastille");

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT + 100, Color.DARKGREY);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        primaryStage.setScene(scene);
        primaryStage.show();

        //init players
        initPlayer();

        // 设置键盘事件监听器，控制玩家移动
        canvas.setFocusTraversable(true); // 让canvas能够接收焦点和键盘事件
        canvas.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP) {
                if (!keysPressed.contains(KeyCode.UP)) {
                    mechan.Jump();
                    keysPressed.add(code);
                }
            }
            if (code == KeyCode.W) {
                if (!keysPressed.contains(KeyCode.W)) {
                    mage.Jump();
                    keysPressed.add(code);
                }
            } else {
                keysPressed.add(code);
            }
        });
        canvas.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.G) {
                mage.FireBallRelease();
            }
            keysPressed.remove(code);
        });


        AnimationLoop loop = new AnimationLoop(gc, keysPressed);
        loop.start();
    }

    public void initPlayer() {
        mechan = new Mechan(1500, HEIGHT / 2); // 初始化玩家角色
        mage = new Mage(50, HEIGHT / 2); // 初始化玩家角色
    }

}


