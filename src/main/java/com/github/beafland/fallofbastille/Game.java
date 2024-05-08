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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Game extends Application implements GameEventListener {
	private GameClient client;
	
    public static final int WIDTH = 2000;
    public static final int HEIGHT = 1000;

    public static Mechan mechan;
    public static Mage mage;
    public static HealthBarUI mechanHealth;
    public static HealthBarUI mageHealth;

    // 添加一个集合来跟踪按下的键
    private final Set<KeyCode> keysPressedMechan = new HashSet<>();
    private final Set<KeyCode> keysPressedMage = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
    	String server = "localhost";
        
    	client = new GameClient(server, 5555, this);
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
            System.out.println("KeyPressed:" + code.toString());
            client.send("KeyPressed:" + code.toString());  // 发送键按下信息到服务器
            /*
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
            */
        });
        canvas.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            client.send("KeyReleased:" + code.toString());  // 发送键释放信息到服务器
            /*
            if (code == KeyCode.G) {
                mage.FireBallRelease();
            }
            keysPressed.remove(code);
            */
        });


        AnimationLoop loop = new AnimationLoop(gc, keysPressedMechan, keysPressedMage);
        loop.start();
    }

    public void initPlayer() {
        mechan = new Mechan(1500, HEIGHT / 2); // 初始化玩家角色
        mage = new Mage(50, HEIGHT / 2); // 初始化玩家角色
    }
    
    @Override
    public void onKeyPressed(KeyCode key) {
        if (key == KeyCode.UP) {
        	if (!keysPressedMechan.contains(KeyCode.UP)) {
                mechan.Jump();
                keysPressedMechan.add(KeyCode.UP);
            }
        }
        else if (key == KeyCode.W) {
        	if (!keysPressedMage.contains(KeyCode.W)) {
                mage.Jump();
                keysPressedMechan.add(KeyCode.W);
            }
        }
        // 其他按键逻辑
    }

    @Override
    public void onKeyReleased(KeyCode key) {
        // 处理按键释放逻辑
    	if (key == KeyCode.UP || key == KeyCode.DOWN || key == KeyCode.LEFT || key == KeyCode.RIGHT || key == KeyCode.SPACE) {
    		keysPressedMechan.remove(key);
    	}
    	else if (key == KeyCode.W || key == KeyCode.S || key == KeyCode.A || key == KeyCode.D || key == KeyCode.G) {
    		keysPressedMage.remove(key);
    		if (key == KeyCode.G) {
                mage.FireBallRelease();
            }
    	}
    }
    
    @Override
    public void onJump(String playerType) {
        if (playerType.equals("Mechan")) {
        	if (!keysPressedMechan.contains(KeyCode.UP)) {
                mechan.Jump();
                keysPressedMechan.add(KeyCode.UP);
            }
        } else if (playerType.equals("Mage")) {
        	if (!keysPressedMage.contains(KeyCode.W)) {
                mage.Jump();
                keysPressedMage.add(KeyCode.W);
            }
        }
    }

}


