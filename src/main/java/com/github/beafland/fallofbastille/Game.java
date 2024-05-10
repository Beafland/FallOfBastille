package com.github.beafland.fallofbastille;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Game extends Application implements GameEventListener {
    //Windows size
    public static final int WIDTH = 2000;
    public static final int HEIGHT = 1000;
    //Players
    public static Mechan mechan;
    public static Mage mage;
    // Add a collection to track key presses
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
        // 弹出对话框让用户输入服务器地址
        TextInputDialog dialog = new TextInputDialog("127.0.0.1");
        dialog.setTitle("Join a Game");
        dialog.setHeaderText("Enter Host IP Address");
        dialog.setContentText("IP Address:");

        // 传统的阻塞式模态对话框
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(hostIp -> {
            try {
                this.client = new GameClient(hostIp, 5555, this);  // 使用用户输入的IP地址创建客户端
                showRoleSelection(primaryStage, false);  // 显示角色选择界面
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
//      Mechan button
        ImageView mechanImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mechan/mechanician.png"))));
        ToggleButton mechanButton = new ToggleButton();
        mechanButton.setGraphic(mechanImage);
        mechanButton.setToggleGroup(group);

//      Mage button
        ImageView mageImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Mage/mage.png"))));
        ToggleButton mageButton = new ToggleButton();
        mageButton.setGraphic(mageImage);
        mageButton.setToggleGroup(group);

        Label waitingLabel = new Label();
        waitingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 60)); // setting text
        waitingLabel.setAlignment(Pos.CENTER); // Alignment text

        // Set the ToggleGroup's event listener to send the "RoleSelected" message when a role is selected.
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == mechanButton) {
                playerRole = "Mechan";
                mageButton.setDisable(true); // Disabling the Mechan button when the Mage button is selected
            } else if (newValue == mageButton) {
                playerRole = "Mage";
                mechanButton.setDisable(true); // Disabling the Mage button when the Mechan button is selected
            }
            client.send("RoleSelected:" + playerRole); // Notify the server that a role has been selected
            client.send("Ready:" + playerRole); // Notify the server that the client is ready
            waitingLabel.setText("Waiting for connection...."); // Update waiting for news
        });

        HBox hbox = new HBox(20); // Layout with VBox
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(mageButton, mechanButton);

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(hbox, waitingLabel);

        StackPane root = new StackPane(vbox);
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
        Scene roleSelectionScene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(roleSelectionScene);
    }

    public void initGame() {
        //init players
        initPlayer();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane(canvas);

        // Set the background color of the StackPane
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));

        Scene scene = new Scene(root, WIDTH, HEIGHT + 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Setting up keyboard event listeners to control player movement
        canvas.setFocusTraversable(true); // Enabling canvas to receive focus and keyboard events

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
                client.send("KeyPressed:" + code);  // Send key press message to server
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
                client.send("KeyReleased:" + code.toString());  // Send key press message to server

            onKeyReleased(code);
        });


        AnimationLoop loop = new AnimationLoop(gc, keysPressedMechan, keysPressedMage);
        loop.start();

        primaryStage.setScene(scene);
    }

    public void initPlayer() {
        mechan = new Mechan(1500, HEIGHT / 2); // Initialising the Player Character
        mage = new Mage(50, HEIGHT / 2);

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
        // Handling key release logic
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


