package pw.stellaric.BetaReduxHelper.util;

import org.json.JSONObject;
import pw.stellaric.BetaReduxHelper.BetaReduxHelper;

import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketHandler {
    private BRHWebsocketClient wsClient;
    BetaReduxHelper brh;

    public WebsocketHandler(BetaReduxHelper brh) {
        this.brh = brh;
        onEnable();
    }

    public void onEnable() {
        try {
            // Replace "ws://localhost:8765" with your WebSocket server URI
            wsClient = new BRHWebsocketClient(new URI("ws://localhost:8070"), this.brh, this);
            wsClient.connect();
            this.brh.log("Connection complete!");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        if (wsClient != null) {
            sendWebsocketMessage("gameMessageToDiscord", "Server shutting down...");
            wsClient.close();
        }
    }

    public void sendWebsocketMessage(String type, String data) {
        if (wsClient != null && wsClient.isOpen()) {
            // Constructing the JSON payload
            JSONObject payload = new JSONObject();
            payload.put("type", type);
            payload.put("data", data);

            // Convert the JSON payload to string
            String messageString = payload.toString();

            // Send the constructed string message to the WebSocket server
            wsClient.send(messageString);
        } else {
            this.brh.log("WebSocket is not connected. Type/Data not sent: " + type + " " + data);
        }
    }
}
