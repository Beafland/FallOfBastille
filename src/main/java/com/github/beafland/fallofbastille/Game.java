package com.github.beafland.fallofbastille;

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
import java.util.Optional;
import java.util.Set;

import com.github.beafland.fallofbastille.character.Mage;
import com.github.beafland.fallofbastille.character.Mechan;

public class Game extends Application implements GameEventListener {
    // Windows size
    public static final int WIDTH = 1900;
    public static final int HEIGHT = 1000;
    // Players
    public static Mechan mechan;
    public static Mage mage;
    // Collection to track pressed keys
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
        this.primaryStage = primaryStage;
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
        // Display a dialog box for users to enter the server address
        TextInputDialog dialog = new TextInputDialog("127.0.0.1");
        dialog.setTitle("Join a Game");
        dialog.setHeaderText("Enter Host IP Address");
        dialog.setContentText("IP Address:");

        // Traditional blocking modal dialog
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(hostIp -> {
            try {
                this.client = new GameClient(hostIp, 5555, this);  // Create a client with the user-entered IP address
                showRoleSelection(primaryStage, false);  // Display the role selection interface
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Connection Error", "Failed to connect to server: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

        // 创建一个包含控制说明的文本标签
        Label controlInstructions = new Label("Mechan controls: Arrow keys (↑ ↓ ← →) to move, Space to attack\n"
                + "Mage controls: W, A, S, D to move, G to charge and release attack");
        controlInstructions.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #333333; -fx-padding: 10px;");
        controlInstructions.setAlignment(Pos.CENTER);

        // 设置ToggleGroup的事件监听，当选择角色后发送"RoleSelected"消息
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == mechanButton) {
                playerRole = "Mechan";
                mageButton.setDisable(true); // Disable the Mage button after Mechan is selected
            } else if (newValue == mageButton) {
                playerRole = "Mage";
                mechanButton.setDisable(true); // Disable the Mechan button after Mage is selected
            }
            client.send("RoleSelected:" + playerRole); // Notify the server that the role has been selected
            client.send("Ready:" + playerRole); // Notify the server that this client is ready
        });

        // 创建水平布局容纳角色选择按钮
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(mechanButton, mageButton);

        // 创建垂直布局来组织文本标签和按钮
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(controlInstructions, buttonBox);

        StackPane root = new StackPane(layout);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
        Scene roleSelectionScene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(roleSelectionScene);
    }

    public void initGame() {
        initPlayer();
        System.out.println(playerRole);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane(canvas);

        // Set the background color of the StackPane
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));

        Scene scene = new Scene(root, WIDTH, HEIGHT + 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Set keyboard event listeners to control player movement
        canvas.setFocusTraversable(true); // Enable canvas to receive focus and keyboard events

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
                client.send("KeyPressed:" + code);  // Send key press information to the server
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
                client.send("KeyReleased:" + code.toString());  // Send key release information to the server

            onKeyReleased(code);
        });

        AnimationLoop loop = new AnimationLoop(gc, keysPressedMechan, keysPressedMage);
        loop.start();

        primaryStage.setScene(scene);
    }

    public void initPlayer() {
        mechan = new Mechan(1500, HEIGHT / 2); // Initialize player characters
        mage = new Mage(50, HEIGHT / 2); // Initialize player characters

        mechan.setEnemy(mage);
        mage.setEnemy(mechan);
    }

    @Override
    public void onKeyPressed(KeyCode key) {
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
        // Process key release logic
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
