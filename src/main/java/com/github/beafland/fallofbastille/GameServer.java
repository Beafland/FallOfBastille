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
    private ConcurrentHashMap<Boolean, String> playerRoles = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Boolean, Boolean> playerReadiness = new ConcurrentHashMap<>();
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
    	
    	/*
    	serverSocketOne = new ServerSocket(5555);
    	
        System.out.println("Waiting for players to connect...");

        playerOne = serverSocketOne.accept();
        System.out.println("Player One connected: " + playerOne.getInetAddress());

        playerTwo = serverSocketOne.accept();
        System.out.println("Player Two connected: " + playerTwo.getInetAddress());

        // 仅在两个玩家都连接后启动线程
        Thread playerOneHandler = new PlayerHandler(playerOne, playerTwo, "Mechan");
        Thread playerTwoHandler = new PlayerHandler(playerTwo, playerOne, "Mage");
        playerOneHandler.start();
        playerTwoHandler.start();
        */
    	
    	/*
        new Thread(() -> {
            try {
                GameServer server = new GameServer(5555);
                server.startServer();  // This call is blocking, so it's run on a separate thread
            } catch (IOException e) {
                System.out.println("Failed to start server: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
        */

    	
    	    Thread serverThread = new Thread(() -> {
    	        try {
    	            serverSocketOne = new ServerSocket(port);
    	            System.out.println("Server is waiting for players to connect...");

    	            // Accept first player
    	            playerOne = serverSocketOne.accept();
    	            System.out.println("Player One connected: " + playerOne.getInetAddress());

    	            // Accept second player
    	            playerTwo = serverSocketOne.accept();
    	            System.out.println("Player Two connected: " + playerTwo.getInetAddress());

    	            // Start handling players after both are connected
    	            Thread playerOneHandler = new PlayerHandler(playerOne, playerTwo, "Mechan", true, playerReadiness, playerRoles);
    	            Thread playerTwoHandler = new PlayerHandler(playerTwo, playerOne, "Mage", false, playerReadiness, playerRoles);
    	            playerOneHandler.start();
    	            playerTwoHandler.start();
    	        } catch (IOException e) {
    	            System.err.println("Server failed to start: " + e.getMessage());
    	            e.printStackTrace();
    	        }
    	    });
    	    serverThread.start();

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
        private Boolean isServer;
        private Map<Boolean, Boolean> playerReadiness;
        private Map<Boolean, String> playerRoles;
        private Map<Boolean, Boolean> playerReady;
        

        public PlayerHandler(Socket input, Socket output, String role, Boolean isServer, ConcurrentHashMap<Boolean, Boolean> playerReadiness, ConcurrentHashMap<Boolean, String> playerRoles) {
        	this.inputPlayer = input;
            this.outputPlayer = output;
            this.playerRole = role;
            this.isServer = isServer;
            this.playerReadiness = playerReadiness;
            this.playerRoles = playerRoles;
        }
        
        private void checkAndStartGame() throws IOException {
        	System.out.println("Player readiness keys: " + playerReadiness.keySet());

            if (playerReadiness.size() == 2 && playerReadiness.values().stream().allMatch(r -> r)) {  // Check if all players are ready

                PrintWriter out = new PrintWriter(inputPlayer.getOutputStream(), true);
                out.println("StartGame");
                out = new PrintWriter(outputPlayer.getOutputStream(), true);
                out.println("StartGame");

            }
        }

        @Override
        public void run() {
            try {
                //writer.println("Role:" + playerRole);
                
                try {
                    // 仅在 inputPlayer 和 outputPlayer 非 null 时继续
                    if (inputPlayer != null && outputPlayer != null) {
                    	BufferedReader reader = new BufferedReader(new InputStreamReader(inputPlayer.getInputStream()));
                        PrintWriter writer = new PrintWriter(outputPlayer.getOutputStream(), true);
                        
                        String message;
                        while ((message = reader.readLine()) != null) {
                            // 处理接收到的信息
    		                while ((message = reader.readLine()) != null) {
    		                	if (message.startsWith("Ready:")) {
    		                		
    		                		String selectedRole = message.split(":")[1];
    		                		
    		                		// Do not process if already selected
    		                		if (playerReadiness.containsKey(!isServer) && playerRoles.get(!isServer).equals(selectedRole)) {
    		                        	System.out.println("Role: " + selectedRole + "is already selected");
    		                        	writer.println("RoleSelectionFailed:" + playerRole);
    		                        } else {
    			                        playerRoles.put(isServer, selectedRole);
    			                        playerReadiness.put(isServer, true);
    			                        System.out.println("[Gameserver.java] Role ready: " + isServer + " | " + selectedRole);
    			                        
    			                        String oppoRole;
    			                        if (playerRole.equals("Mechan")) {
    			                        	oppoRole = "Mage";
    			                        } else {
    			                        	oppoRole = "Mechan";
    			                        }
    			                        
    			                        writer.println("Role:" + oppoRole);
    		                		}
    		                        checkAndStartGame();
    		                	}
    		                	
    		                	else if (message.startsWith("RoleSelected:")) {
    		                		String selectedRole = message.split(":")[1];
    		                        if (playerReadiness.containsKey(!isServer) && playerRoles.get(!isServer).equals(selectedRole)) {
    		                        	System.out.println("Role: " + selectedRole + "is already selected");
    		                        }
    		                	}
    		                	else {
    		                		writer.println(message); // 转发消息
    		                	}
    		                    
    		                    System.out.println("Sent to other player: " + this.playerRole + message);
    		                    //outputPlayer.getOutputStream().write((message + "\n").getBytes());
    		                    //outputPlayer.getOutputStream().flush();
		                		}
                        }
                    } else {
                        System.err.println("One of the sockets is null.");
                    }
		            } catch (IOException e) {
		            		e.printStackTrace();
    			    } finally {
		                    inputPlayer.close();
		                    outputPlayer.close();
                    } 
                } catch (IOException e) {
                    System.err.println("Failed to handle client connection: " + e.getMessage());
                    e.printStackTrace();
                }

                

        }

    }
    
    
    public static void main(String[] args) throws IOException {
        int port = 5555; // Example port number
        GameServer server = new GameServer(port);
        server.startServer();
    }
}
