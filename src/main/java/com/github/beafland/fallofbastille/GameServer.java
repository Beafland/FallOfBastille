package com.github.beafland.fallofbastille;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private ServerSocket serverSocketOne;
    private ServerSocket serverSocketTwo;
    private Socket playerOne;
    private Socket playerTwo;
    private final int port;
    private boolean running = true;
    private final ExecutorService pool;
    //private final Map<Integer, GameRoom> rooms; // Map of room numbers to GameRoom instances
	//private Map<GameRoom, ClientHandler> clientHandlers;

    public GameServer(int port) {
        this.port = port;
        this.pool = Executors.newCachedThreadPool();
        //this.rooms = new ConcurrentHashMap<>(); // Thread-safe map
    }

    public void startServer() throws IOException {
    	/*
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                pool.execute(new ClientHandler(clientSocket, this));
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stopServer();
        }*/
    	
    	serverSocketOne = new ServerSocket(5555);
    	
        System.out.println("Waiting for players to connect...");

        playerOne = serverSocketOne.accept();
        System.out.println("Player One connected: " + playerOne.getInetAddress());

        playerTwo = serverSocketOne.accept();
        System.out.println("Player Two connected: " + playerTwo.getInetAddress());

        // 仅在两个玩家都连接后启动线程
        Thread playerOneHandler = new PlayerHandler(playerOne, playerTwo, "Mechan", "1st");
        Thread playerTwoHandler = new PlayerHandler(playerTwo, playerOne, "Mage", "2nd");
        playerOneHandler.start();
        playerTwoHandler.start();
    }

    public void stopServer() {
        try {
            running = false;
            if (serverSocketOne != null && !serverSocketOne.isClosed()) {
                serverSocketOne.close();
            }
            pool.shutdown();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.out.println("Error closing the server: " + e.getMessage());
        }
    }
    
    public void handleClient(Socket clientSocket) throws IOException {
        // Input and output streams for communication
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            // Process commands sent by the client
            // Update game state accordingly
            // Broadcast updated game state to clients
        	System.out.println(inputLine);
        }
    }

    /*
    // Attempt to create or join a room
    public synchronized boolean tryJoinRoom(int roomNumber, ClientHandler handler) {
        if (roomNumber < 1 || roomNumber > 9999) {
            return false; // Invalid room number
        }
        GameRoom room = rooms.get(roomNumber);
        if (room == null) {
            // Create new room if it doesn't exist and the number is valid
            room = new GameRoom(roomNumber);
            rooms.put(roomNumber, room);
        }
        return room.addPlayer(handler); // Attempt to add player to the room
    }
	
    // Notify the server that a battle in a room has finished
    public synchronized void notifyBattleFinished(int roomNumber) {
        rooms.remove(roomNumber); // Remove the room, making the number available again
    }
    */
    // Example method to broadcast messages to all clients
    /*
    public synchronized void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers.values()) {
            clientHandler.sendMessage(message);
        }
    }
    */

    // Remove a client handler from the list
    /*
    public synchronized void removeClientHandler(ClientHandler handler) {
        clientHandlers.remove(handler);
        System.out.println("Client disconnected. Total clients: " + clientHandlers.size());
    }
    */
    
    private class PlayerHandler extends Thread {
        private Socket inputPlayer;
        private Socket outputPlayer;
        private String playerRole; // 新增角色标识
        public String debugName;

        public PlayerHandler(Socket input, Socket output, String role, String name) {
            this.inputPlayer = input;
            this.outputPlayer = output;
            this.playerRole = role;
            this.debugName = name;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputPlayer.getInputStream()));
                PrintWriter writer = new PrintWriter(outputPlayer.getOutputStream(), true);
                
                String message;
                
                System.out.println("Name: " + debugName + " | " + playerRole);
                writer.println("Role:" + playerRole);
                
                while ((message = reader.readLine()) != null) {
                    writer.println(message); // 转发消息
                    System.out.println("Sent to other player: " + this.playerRole + message);
                    //outputPlayer.getOutputStream().write((message + "\n").getBytes());
                    //outputPlayer.getOutputStream().flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputPlayer.close();
                    outputPlayer.close();
                } catch (IOException e) {
                    System.err.println("Failed to close sockets: " + e.getMessage());
                }
            }
        }

    }
    
    

    public static void main(String[] args) throws IOException {
        int port = 5555; // Example port number
        GameServer server = new GameServer(port);
        server.startServer();
    }
}
