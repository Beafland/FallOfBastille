package com.github.beafland.fallofbastille;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameEventListener eventListener;
    private String playerRole;

    public GameClient(String serverAddress, int port, GameEventListener eventListener) throws IOException {
        this.eventListener = eventListener;
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("No host found: " + e.getMessage());
            throw new IOException("Server not found", e);
        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            throw e;
        }

        new Thread(this::listenToServer).start();
    }

    public String getPlayerRole() {
        return playerRole;
    }

    private void listenToServer() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                // Role selection
                if (serverMessage.startsWith("Role:")) {
                    System.out.println("Role updated: " + serverMessage.split(":")[1]);
                    eventListener.updateRole(serverMessage.split(":")[1]);
                    playerRole = serverMessage.split(":")[1];
                } else if (serverMessage.startsWith("StartGame")) {
                    System.out.println("Game client received game start command");
                    startGame();
                } else if (serverMessage.startsWith("KeyPressed:")) {
                    String keyCode = serverMessage.substring(11);
                    handleKeyPress(keyCode);
                } else if (serverMessage.startsWith("KeyReleased:")) {
                    String keyCode = serverMessage.substring(12);
                    handleKeyRelease(keyCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleKeyPress(String keyCode) {
        // Update game state based on key press, e.g., move character
        System.out.println("[GameClient.java]: " + this.playerRole + " | " + keyCode);
        if (playerRole.equals("Mage")) {
            if (keyCode.equals("LEFT") || keyCode.equals("RIGHT") || keyCode.equals("SPACE") || keyCode.equals("UP")) {
                System.out.println("[Mage] Oppo Key Pressed: " + keyCode);
                KeyCode key = KeyCode.valueOf(keyCode);
                if (key == null) {
                    System.out.println("Invalid key code received: " + keyCode);
                    return;
                }

                Platform.runLater(() -> {
                    eventListener.onKeyPressed(KeyCode.valueOf(keyCode));
                });
            }
        } else {
            if (keyCode.equals("A") || keyCode.equals("D") || keyCode.equals("G") || keyCode.equals("W")) {
                System.out.println("[Machan] Oppo Key Pressed: " + keyCode);
                eventListener.onKeyPressed(KeyCode.valueOf(keyCode));
            }
        }
    }

    private void handleKeyRelease(String keyCode) {
        // Handle key release
        System.out.println("Key Released: " + keyCode);
        KeyCode key = KeyCode.valueOf(keyCode);
        if (key == null) {
            System.out.println("[2]Invalid key code received: " + keyCode);
            return;
        }

        if (playerRole.equals("Mage")) {
            if (keyCode.equals("LEFT") || keyCode.equals("RIGHT") || keyCode.equals("SPACE") || keyCode.equals("UP")) {
                eventListener.onKeyReleased(KeyCode.valueOf(keyCode));
            }
        } else {
            System.out.println("[Mechan] Oppo Key Pressed: " + keyCode);
            eventListener.onKeyReleased(KeyCode.valueOf(keyCode));
        }
    }

    private void startGame() {
        // Transition to game scene
        Platform.runLater(() -> {
            eventListener.initGame();
        });
    }

    public void send(String message) {
        out.println(message);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
