package pigcart.glimchat;

import java.net.URI;
import java.util.Map;

import net.minecraft.util.Formatting;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;
import pigcart.glimchat.config.ModConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 */
public class WebsocketClientEndpoint extends WebSocketClient {

    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public WebsocketClientEndpoint(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebsocketClientEndpoint(URI serverURI) {
        super(serverURI);
    }

    public WebsocketClientEndpoint(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        GlimChat.addGlimeshMessage("","Connection open. Sending join request.", Formatting.BOLD);
        send("[\"1\",\"1\",\"__absinthe__:control\",\"phx_join\",{}]");

        //this.scheduleHeartbeat();
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Glimesh Message: "+message);
        if (message.contains("subscription:data")) {
            //convert message to JSON and get useful info to build the minecraft chat message

            //IDK what I'm supposed to do with this other data so I'm just going to get rid of it. Shut up I am the best programmer.
            String jsonStr = message.substring(135, message.length() - 1);

            //get the displayname and message from the received json. This feels clunky there's probably a much better way
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject result = jsonObj.getJSONObject("result");
            JSONObject data = result.getJSONObject("data");
            JSONObject chatMessage = data.getJSONObject("chatMessage");
            String msg = chatMessage.getString("message");
            JSONObject user = chatMessage.getJSONObject("user");
            String displayname = user.getString("displayname");
            GlimChat.addGlimeshMessage(displayname,msg,Formatting.WHITE);
        }
        else if (message.equals("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{},\"status\":\"ok\"}]")) {
            GlimChat.addGlimeshMessage("","Glimesh is ready! Looking up ID for " + ModConfig.getConfig().getChannel(),Formatting.BOLD);
            send("[\"1\",\"1\",\"__absinthe__:control\",\"doc\",{\"query\":\"query{channel(username:\\\""+ModConfig.getConfig().getChannel()+"\\\"){id}}\",\"variables\":{}}]");
        }
        else if (message.startsWith("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{\"data\":{\"channel\":{\"id")) {
            // so I COULD parse all the json... ooooorrrr I could DO THIS CURSED THING INSTEAD:
            String channelId = message.substring(82, message.length() - 20);
            GlimChat.addGlimeshMessage("", "Found ID: " + channelId + ". Joining chat.", Formatting.BOLD);
            send("[\"1\",\"1\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage(channelId:\\\"" + channelId + "\\\"){user{displayname}message}}\",\"variables\":{}}]");
        }
        else if (message.startsWith("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{\"subscriptionId")) {
            GlimChat.addGlimeshMessage("","Connected to " + ModConfig.getConfig().getChannel(),Formatting.BOLD);
        }
        else {
            GlimChat.addGlimeshMessage("Message Received", message, Formatting.GOLD);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        GlimChat.addGlimeshMessage("Connection Closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason", reason, Formatting.BOLD);
        // stop heartbeat
        //scheduledExecutorService.shutdownNow();

    }

    @Override
    public void onError(Exception ex) {
        GlimChat.addGlimeshMessage("Error", ex.toString(), Formatting.RED);
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }
    public void heartbeat() {
        GlimChat.addGlimeshMessage("GlimChat", "Sending Heartbeat", Formatting.BOLD);
        //send("[\"1\",\"1\",\"phoenix\",\"heartbeat\",{}]");
        // send a heartbeat to Glimesh so the connection won't be closed
    }
    public void scheduleHeartbeat() {
        //scheduledExecutorService.scheduleAtFixedRate(this::heartbeat, 1L, 1L, TimeUnit.SECONDS);
        // schedule heartbeat
    }
}