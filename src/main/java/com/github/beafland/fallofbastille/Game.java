package com.github.beafland.fallofbastille;

import com.github.beafland.fallofbastille.character.HealthBarUI;
import com.github.beafland.fallofbastille.character.Mage;
import com.github.beafland.fallofbastille.character.Mechan;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Game extends Application implements GameEventListener {
	private GameClient client;
	
    public static final int WIDTH = 2000;
    public static final int HEIGHT = 1000;

    public static Mechan mechan;
    public static Mage mage;
    public static HealthBarUI mechanHealth;
    public static HealthBarUI mageHealth;

    private String playerRole;

    // 添加一个集合来跟踪按下的键
    private final Set<KeyCode> keysPressedMechan = new HashSet<>();
    private final Set<KeyCode> keysPressedMage = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
    	String server = "127.0.0.1";

    	client = new GameClient(server, 5555, this);
    	this.playerRole = client.getPlayerRole();
    	System.out.println("Assigned role: " + playerRole);
        primaryStage.setTitle("Fall Of Bastille");
        initMenu(primaryStage);
    }

    public void initMenu(Stage primaryStage){
        //add cover image
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cover.png")))); // 使用getResource()获取资源

        // 创建菜单按钮
        Button startButton = new Button("Start Game");
        startButton.setPrefSize(200, 100); // 配置按钮大小
        startButton.setOnAction(event -> initGame(primaryStage)); // 点击按钮开始游戏

        // 将按钮和图片添加到垂直布局中
        VBox vbox = new VBox(20); // 设置垂直间距
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(imageView, startButton);

        // 设置场景的背景颜色
        StackPane root = new StackPane(vbox);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
        Scene menuScene = new Scene(root, WIDTH, HEIGHT);

        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    public void initGame(Stage primaryStage){
        //init players
        initPlayer();

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane(canvas);

        // Set the background color of the StackPane
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));

        Scene scene = new Scene(root, WIDTH, HEIGHT + 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 设置键盘事件监听器，控制玩家移动
        canvas.setFocusTraversable(true); // 让canvas能够接收焦点和键盘事件
        canvas.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            System.out.println("KeyPressed:" + code.toString());

            if (playerRole.equals("Mechan") && (code == KeyCode.LEFT || code == KeyCode.RIGHT || code == KeyCode.UP || code == KeyCode.SPACE)) {
            	onKeyPressed(code);
            }
            else if (playerRole.equals("Mage") && (code == KeyCode.A || code == KeyCode.D || code == KeyCode.W || code == KeyCode.G)) {
            	onKeyPressed(code);
            }

            client.send("KeyPressed:" + code.toString());  // 发送键按下信息到服务器
        });
        canvas.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();

            if (playerRole.equals("Mechan") && (code == KeyCode.LEFT || code == KeyCode.RIGHT || code == KeyCode.UP || code == KeyCode.SPACE)) {
            	onKeyReleased(code);
            }
            else if (playerRole.equals("Mage") && (code == KeyCode.A || code == KeyCode.D || code == KeyCode.W || code == KeyCode.G)) {
            	onKeyReleased(code);
            }
            client.send("KeyReleased:" + code.toString());  // 发送键释放信息到服务器
            onKeyReleased(code);
        });


        AnimationLoop loop = new AnimationLoop(gc, keysPressedMechan, keysPressedMage);
        loop.start();

        primaryStage.setScene(scene);
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
        
        else if (key == KeyCode.LEFT || key == KeyCode.RIGHT || key == KeyCode.UP || key == KeyCode.SPACE) {
        	System.out.println("[Game.java][Mechan] Moved: " + key.toString());
    		keysPressedMechan.add(key);
    	}

    	else if (key == KeyCode.W) {
	        	if (!keysPressedMage.contains(KeyCode.W)) {
	                mage.Jump();
	                keysPressedMage.add(KeyCode.W);
	            }
	    }

        else if (key == KeyCode.A || key == KeyCode.D || key == KeyCode.W || key == KeyCode.G) {
        	System.out.println("[Game.java][Mage] Moved: " + key.toString());
    		keysPressedMage.add(key);
    	}

    }

    @Override
    public void onKeyReleased(KeyCode key) {
        // 处理按键释放逻辑
    	if (key == KeyCode.UP || key == KeyCode.DOWN || key == KeyCode.LEFT || key == KeyCode.RIGHT || key == KeyCode.SPACE) {
    		System.out.println("[Game.java]" + playerRole + " Removed: " + key.toString());
    		keysPressedMechan.remove(key);
    	}

    	else if (key == KeyCode.W || key == KeyCode.S || key == KeyCode.A || key == KeyCode.D || key == KeyCode.G) {
	    		System.out.println("[Game.java]" + playerRole + " Removed: " + key.toString());
	    		keysPressedMage.remove(key);
	    		if (key == KeyCode.G) {
	                mage.FireBallRelease();
	            }
	    }
    }
    
    @Override
    public void onJump(String playerType) {
        	if (!keysPressedMechan.contains(KeyCode.UP)) {
                mechan.Jump();
                keysPressedMechan.add(KeyCode.UP);

        } else if (!keysPressedMage.contains(KeyCode.W)) {
                mage.Jump();
                keysPressedMage.add(KeyCode.W);
        }
    }

}


