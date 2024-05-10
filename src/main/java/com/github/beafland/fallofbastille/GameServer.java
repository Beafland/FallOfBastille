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
    	            Thread playerOneHandler = new PlayerHandler(playerOne, playerTwo, true, playerReadiness, playerRoles);
    	            Thread playerTwoHandler = new PlayerHandler(playerTwo, playerOne, false, playerReadiness, playerRoles);
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
    
    private class PlayerHandler extends Thread {
        private Socket inputPlayer;
        private Socket outputPlayer;
        private Boolean isServer;
        private Map<Boolean, Boolean> playerReadiness;
        private Map<Boolean, String> playerRoles;
        

        public PlayerHandler(Socket input, Socket output, Boolean isServer, ConcurrentHashMap<Boolean, Boolean> playerReadiness, ConcurrentHashMap<Boolean, String> playerRoles) {
        	this.inputPlayer = input;
            this.outputPlayer = output;
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
    		                		
                                    playerRoles.put(isServer, selectedRole);
                                    playerReadiness.put(isServer, true);
                                    
                                    String oppoRole;
                                    if (selectedRole.equals("Mechan")) {
                                        oppoRole = "Mage";
                                    } else {
                                        oppoRole = "Mechan";
                                    }
                                    
                                    writer.println("Role:" + oppoRole);
                                
    		                        checkAndStartGame();
    		                	} else if (message.startsWith("RoleSelected:")) {
    		                		String selectedRole = message.split(":")[1];
    		                        if (playerReadiness.containsKey(!isServer) && playerRoles.get(!isServer).equals(selectedRole)) {
    		                        	System.out.println("Role: " + selectedRole + "is already selected");
    		                        }
    		                	} else {
    		                		writer.println(message); // 转发消息
    		                	}
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
