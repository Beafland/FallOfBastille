package com.github.beafland.fallofbastille;

import com.github.beafland.fallofbastille.character.HealthBarUI;
import com.github.beafland.fallofbastille.character.Mage;
import com.github.beafland.fallofbastille.character.Mechan;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Game extends Application implements GameEventListener {
    //Windows size
    public static final int WIDTH = 2000;
    public static final int HEIGHT = 1000;
    //Players
    public static Mechan mechan;
    public static Mage mage;
    // 添加一个集合来跟踪按下的键
    private final Set<KeyCode> keysPressedMechan = new HashSet<>();
    private final Set<KeyCode> keysPressedMage = new HashSet<>();
    private GameClient client;
    // UI
    private Stage primaryStage;
    private String playerRole = "local";
    private RadioButton rbMechan;
    private RadioButton rbMage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String server = "127.0.0.1";
        this.primaryStage = primaryStage;

        //client = new GameClient(server, 5555, this);
        //this.playerRole = client.getPlayerRole();
        //System.out.println("Assigned role: " + playerRole);
        primaryStage.setTitle("Fall Of Bastille");
        initMenu();
    }


    public void initMenu() {
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cover.png"))));

        Button startButton = new Button("Start Local Game");
        startButton.setPrefSize(200, 100);
        startButton.setOnAction(event -> initGame());

        Button hostButton = new Button("Be the Host");
        hostButton.setPrefSize(200, 100);
        hostButton.setOnAction(event -> {
            startServer();
            showRoleSelection(primaryStage, true); // Show role selection UI
        });

        Button joinButton = new Button("Join a Game");
        joinButton.setPrefSize(200, 100);
        joinButton.setOnAction(event -> {
            joinGame(); // Join the existing server
            showRoleSelection(primaryStage, false); // Show role selection UI
        });

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(imageView, startButton, hostButton, joinButton);

        StackPane root = new StackPane(vbox);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
        Scene menuScene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    public void updateOpponentRole(String role) {
        Platform.runLater(() -> {
            if (role.equals("Mechan")) {
                rbMechan.setDisable(true);
                rbMechan.setTextFill(Color.GRAY); // Set the font color to gray
            } else if (role.equals("Mage")) {
                rbMage.setDisable(true);
                rbMage.setTextFill(Color.GRAY); // Set the font color to gray
            }
        });
    }


    private void startServer() {
        try {
            GameServer server = new GameServer(5555);
            server.startServer();  // Start server synchronously to ensure it's ready
            client = new GameClient("127.0.0.1", 5555, this);  // Connect only after server is confirmed running
        } catch (IOException e) {
            System.out.println("Failed to start server or connect: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void joinGame() {
        // Assume the server is on localhost for simplicity
        try {
            this.client = new GameClient("127.0.0.1", 5555, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRoleSelection(Stage primaryStage, boolean isHost) {
        ToggleGroup group = new ToggleGroup();
        ImageView mechanImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mechan/mechanician.png"))));
        ToggleButton mechanButton = new ToggleButton();
        mechanButton.setGraphic(mechanImage);
        mechanButton.setToggleGroup(group);

        ImageView mageImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Mage/mage.png"))));
        ToggleButton mageButton = new ToggleButton();
        mageButton.setGraphic(mageImage);
        mageButton.setToggleGroup(group);

        // 设置 ToggleGroup 的事件监听器，当选择角色后发送 "RoleSelected" 消息
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == mechanButton) {
                playerRole = "Mechan";
                mageButton.setDisable(true); // 选中 Mage 按钮后禁用 Mechan 按钮
            } else if (newValue == mageButton) {
                playerRole = "Mage";
                mechanButton.setDisable(true); // 选中 Mechan 按钮后禁用 Mage 按钮
            }
            client.send("RoleSelected:" + playerRole); // 通知服务器角色已选择
            client.send("Ready:" + playerRole); // 通知服务器此客户端已准备就绪
        });

        HBox vbox = new HBox(20); // 使用 VBox 布局
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(mageButton, mechanButton);

        StackPane root = new StackPane(vbox);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
        Scene roleSelectionScene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(roleSelectionScene);
    }

    public void initGame() {
        //init players
        initPlayer();
        System.out.println(playerRole);
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

            if ((playerRole.equals("Mechan") || playerRole.equals("local")) &&
                    (code == KeyCode.LEFT || code == KeyCode.RIGHT || code == KeyCode.UP || code == KeyCode.SPACE)) {
                onKeyPressed(code);
            } else if ((playerRole.equals("Mage") || playerRole.equals("local")) &&
                    (code == KeyCode.A || code == KeyCode.D || code == KeyCode.W || code == KeyCode.G)) {
                onKeyPressed(code);
            }

            if (!playerRole.equals("local"))
                client.send("KeyPressed:" + code);  // 发送键按下信息到服务器
        });

        canvas.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();

            if ((playerRole.equals("Mechan") || playerRole.equals("local")) &&
                    (code == KeyCode.LEFT || code == KeyCode.RIGHT || code == KeyCode.UP || code == KeyCode.SPACE)) {
                onKeyReleased(code);
            } else if ((playerRole.equals("Mage") || playerRole.equals("local")) &&
                    (code == KeyCode.A || code == KeyCode.D || code == KeyCode.W || code == KeyCode.G)) {
                onKeyReleased(code);
            }

            if (!playerRole.equals("local"))
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

        mechan.setEnemy(mage);
        mage.setEnemy(mechan);
    }

    @Override
    public void onKeyPressed(KeyCode key) {
        //rewrite on Key Pressed logic
        if (key == KeyCode.UP) {
            if (!keysPressedMechan.contains(KeyCode.UP)) {
                mechan.Jump();
                keysPressedMechan.add(KeyCode.UP);
            }
        } else if (key == KeyCode.LEFT || key == KeyCode.RIGHT || key == KeyCode.SPACE) {
            keysPressedMechan.add(key);
        } else if (key == KeyCode.W) {
            if (!keysPressedMage.contains(KeyCode.W)) {
                mage.Jump();
                keysPressedMage.add(KeyCode.W);
            }
        } else if (key == KeyCode.A || key == KeyCode.D || key == KeyCode.G) {
            keysPressedMage.add(key);
        }

    }

    @Override
    public void onKeyReleased(KeyCode key) {
        // 处理按键释放逻辑
        if (key == KeyCode.UP || key == KeyCode.DOWN || key == KeyCode.LEFT || key == KeyCode.RIGHT || key == KeyCode.SPACE) {
            keysPressedMechan.remove(key);
        } else if (key == KeyCode.W || key == KeyCode.S || key == KeyCode.A || key == KeyCode.D || key == KeyCode.G) {
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

    @Override
    public void updateRole(String role) {
        this.playerRole = role;
    }

}


