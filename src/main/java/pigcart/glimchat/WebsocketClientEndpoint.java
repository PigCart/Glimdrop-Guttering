package pigcart.glimchat;

import java.net.URI;
import java.util.Map;

import net.minecraft.util.Formatting;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

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
        GlimChat.addGlimeshMessage("","Sending Join Request", Formatting.BOLD);
        send("[\"1\",\"1\",\"__absinthe__:control\",\"phx_join\",{}]");

        //this.scheduleHeartbeat();
    }

    @Override
    public void onMessage(String message) {
        GlimChat.addGlimeshMessage("Message Received", message, Formatting.GOLD);

        if (message.equals("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{},\"status\":\"ok\"}]")) {
            //send("query{channel(username:\"PigCart\"){id}}");
            //IDK HOW TO QUERY THE API THIS DOESN'T WORK
            //FOR NOW JUST USE channelId: 2586 TO CONNECT TO PIGCART
            GlimChat.addGlimeshMessage("", "Attempting to subscribe to chat", Formatting.BOLD);
            send("[\"1\",\"1\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{ chatMessage(channelId: 2586) { user { displayname } message } }\",\"variables\":{} }]");
            //send("[\"6\",\"7\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{ chatMessage(channelId: \"2\")    { user { username avatar } message } }\",\"variables\":{} }]");
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        GlimChat.addGlimeshMessage("Connection Closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason", reason, Formatting.RED);
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