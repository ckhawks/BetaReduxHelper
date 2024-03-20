package pw.stellaric.BetaReduxHelper.util;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BRHWebsocketClient extends WebSocketClient {

    private static final long RECONNECT_INTERVAL = 5; // Reconnect interval in seconds
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    BetaReduxHelper brh;
    WebsocketHandler websocketHandler;

    public BRHWebsocketClient(URI serverUri, BetaReduxHelper brh, WebsocketHandler websocketHandler) {
        super(serverUri);
        this.brh = brh;
        this.websocketHandler = websocketHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        try {
            System.out.println("Opened WebSocket connection");
            this.websocketHandler.sendWebsocketMessage("mcServerConnectionEstablished", this.brh.getServer().getServerName() + " " + this.brh.getServer().getVersion());
        } catch (Exception e) {
            System.out.println("Error in WebsocketClient onOpen: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(String message) {
        try {
//            this.brh.log("WS Message received: " + message);
            JSONObject messageData = new JSONObject(message);
            String type = messageData.getString("type");

            // Assuming "data" is a nested JSON object
            JSONObject data = messageData.getJSONObject("data");

            switch (type) {
                case "discordMessageToGame":
                    // Handle message to Discord
                    String output = ChatColor.DARK_AQUA + "DISCORD " + ChatColor.WHITE + "@" + data.getString("username") + ": " + data.getString("message");
                    this.brh.getServer().broadcastMessage(output);
                    this.brh.log(output);
                    break;
                case "discordMessageToGameCommand":
                    String command = data.getString("command"); // Extracting the command as a String
                    if (command.equalsIgnoreCase("addmember")) {
                        this.brh.getServer().dispatchCommand(new ConsoleCommandSender(this.brh.getServer()), "pex user " + data.getString("username") + " group set member");
                        this.brh.log("Gave " + data.getString("username") + " member role from websocket command.");
                        this.websocketHandler.sendWebsocketMessage("gameMessageToDiscord", "Added **" + data.getString("username") + "** as member.");
                    }

//                // Extracting the arguments as a JSONArray
//                JSONArray arguments = data.getJSONArray("arguments");
//                // Iterate over each argument in the arguments JSONArray
//                for (int i = 0; i < arguments.length(); i++) {
//                    String arg = arguments.getString(i); // Get each argument as a String
//                    // Do something with each argument
//                }
//                // Execute command or take action based on the command
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error in WebsocketClient onMessage: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed WebSocket connection. Reason: " + code + reason + remote);
        // Schedule a reconnect attempt
        scheduleReconnect();
    }

    @Override
    public void onError(Exception ex) {
        try {
            System.out.println("WebSocket error: " + ex.getMessage());
            ex.printStackTrace();

            // Schedule a reconnect attempt if not already connected
            if (!isOpen()) {
                scheduleReconnect();
            }
        } catch (Exception e) {
            System.out.println("Error in WebsocketClient onError: " + e.getMessage());
        }
    }

    private void scheduleReconnect() {
        System.out.println("Attempting to reconnect in " + RECONNECT_INTERVAL + " seconds...");
        scheduler.schedule(() -> {
            System.out.println("Reconnecting to WebSocket server...");
            try {
                reconnect();
            } catch (Exception e) {
                System.out.println("Reconnect attempt failed: " + e.getMessage());
                // Schedule another reconnect attempt if the current one fails
                scheduleReconnect();
            }
        }, RECONNECT_INTERVAL, TimeUnit.SECONDS);
    }
}