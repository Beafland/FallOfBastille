package com.github.beafland.fallofbastille;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
    	
        // 接收并设置角色
        String roleInfo = in.readLine();
        if (roleInfo.startsWith("Role:")) {
            this.playerRole = roleInfo.substring(5);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA: "+ this.playerRole);
        }
        // debug
        //this.playerRole = "Mechan";
    	
    	new Thread(this::listenToServer).start();
    }
    
    private void listenToServer() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
            	System.out.println("Client received from server: "+ serverMessage);
                if (serverMessage.startsWith("KeyPressed:")) {
                    String keyCode = serverMessage.substring(11);
                    handleKeyPress(keyCode, playerRole);
                } else if (serverMessage.startsWith("KeyReleased:")) {
                    String keyCode = serverMessage.substring(13);
                    handleKeyRelease(keyCode, playerRole);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleKeyPress(String keyCode, String playerRole) {
        // 根据按键码更新游戏状态，例如移动角色
        System.out.println("Key Pressed: " + keyCode);
        
        if (playerRole.equals("Mechan")) {
        	if (keyCode == "LEFT" || keyCode == "RIGHT" || keyCode == "SPACE" || keyCode == "UP") {
        		eventListener.onKeyPressed(KeyCode.getKeyCode(keyCode));
        	}
        }
        else {
        	eventListener.onKeyPressed(KeyCode.getKeyCode(keyCode));
        }
        // 这里可以调用GameRoom或者其他管理游戏逻辑的类的方法
    }

    private void handleKeyRelease(String keyCode, String playerRole) {
        // 处理按键释放
        System.out.println("Key Released: " + keyCode);
        eventListener.onKeyReleased(KeyCode.getKeyCode(keyCode));
        // 这里可以调用GameRoom或者其他管理游戏逻辑的类的方法停止移动等
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
