package com.github.beafland.fallofbastille;

import com.github.beafland.fallofbastille.character.Player;
import com.github.beafland.fallofbastille.character.PlayerController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class Game extends Application {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;

    public static Player player;
    public static PlayerController playerController;

    // 添加一个集合来跟踪按下的键
    private final Set<KeyCode> keysPressed = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        player = new Player(50, HEIGHT / 2); // 初始化玩家角色
        playerController = new PlayerController(player);

        // 设置键盘事件监听器，控制玩家移动
        canvas.setFocusTraversable(true); // 让canvas能够接收焦点和键盘事件
        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
        canvas.setOnKeyPressed(event -> keysPressed.add(event.getCode()));
        canvas.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Fall Of Bastille");
        primaryStage.show();

        AnimationLoop loop = new AnimationLoop(gc, keysPressed);
        loop.start();
    }
}


