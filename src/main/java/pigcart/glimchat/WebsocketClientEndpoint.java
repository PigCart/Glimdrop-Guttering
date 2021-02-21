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

import java.util.concurrent.*;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 */
public class WebsocketClientEndpoint extends WebSocketClient {

    public WebsocketClientEndpoint(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebsocketClientEndpoint(URI serverURI) {
        super(serverURI);
    }

    public WebsocketClientEndpoint(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    static ScheduledFuture<?> t;

    class Heartbeat implements Runnable {
        // idk if tick schedulers would be more suitable but I can't find any explanation of them
        public void run() {
            send("[\"1\",\"1\",\"phoenix\",\"heartbeat\",{}]");
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection open. Sending join request.");
        send("[\"1\",\"1\",\"__absinthe__:control\",\"phx_join\",{}]");

        t = executor.scheduleAtFixedRate(new Heartbeat(), 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("subscription:data")) {
            //convert message to JSON and get useful info to build the minecraft chat message

            //IDK what I'm supposed to do with this other data so I'm just going to get rid of it. Shut up I am the best programmer.
            String jsonStr = message.substring(135, message.length() - 1);

            //get the displayname and message from the received json. theres gotta be a more direct way to do this but im baby
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject result = jsonObj.getJSONObject("result");
            JSONObject data = result.getJSONObject("data");
            JSONObject chatMessage = data.getJSONObject("chatMessage");
            String msg = chatMessage.getString("message");
            JSONObject user = chatMessage.getJSONObject("user");
            String displayname = user.getString("displayname");
            // if we're listening to all channels it would be nice to include the channel name.
            if (ModConfig.getConfig().getChannel().equals(".")) {
                JSONObject channel = chatMessage.getJSONObject("channel");
                JSONObject streamer = channel.getJSONObject("streamer");
                String streamerDisplayname = streamer.getString("displayname");
                GlimChat.addGlimeshMessage("["+streamerDisplayname+"] "+displayname, msg, Formatting.WHITE);
            } else {
                GlimChat.addGlimeshMessage(displayname, msg, Formatting.WHITE);
            }
        }
        // heartbeat response
        else if (message.equals("[null,\"1\",\"phoenix\",\"phx_reply\",{\"response\":{},\"status\":\"ok\"}]")) {
            System.out.println("badum");
        }
        // Message received when initially connecting to Glimesh
        else if (message.equals("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{},\"status\":\"ok\"}]")) {
            // entering "." will connect the user to every chat
            if (ModConfig.getConfig().getChannel().equals(".")) {
                System.out.println("Glimesh is ready! Joining all channels");
                send("[\"1\",\"1\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage{user{displayname}message channel{streamer{displayname}}}}\"}]");
            }
            // otherwise it looks up the ID for the channel name entered.
            else {
                System.out.println("Glimesh is ready! Looking up ID for specified channel");
                send("[\"1\",\"1\",\"__absinthe__:control\",\"doc\",{\"query\":\"query{channel(username:\\\"" + ModConfig.getConfig().getChannel() + "\\\"){id}}\",\"variables\":{}}]");
            }
        }
        // Message received after querying ID
        else if (message.startsWith("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{\"data\":{\"channel\":{\"id")) {
            // fuck it this works
            String channelId = message.substring(82,message.length()-20);
            // https://i.imgur.com/7CG4IJu.png
            System.out.println("Found ID: " + channelId + ". Joining chat");
            send("[\"1\",\"1\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage(channelId:\\\"" + channelId + "\\\"){user{displayname}message}}\",\"variables\":{}}]");
        }
        // Message Recieved after joining a chat
        else if (message.startsWith("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{\"subscriptionId")) {
            GlimChat.addGlimeshMessage("", "Connected to " + ModConfig.getConfig().getChannel(), Formatting.BOLD);
        }
        // Message received after "joining"??? a channel that does not exist
        else if (message.equals("[\"1\",\"1\",\"__absinthe__:control\",\"phx_reply\",{\"response\":{\"data\":{\"channel\":null}},\"status\":\"ok\"}]")) {
            GlimChat.addGlimeshMessage("", "That channel does not exist.", Formatting.BOLD);
            GlimChat.websocketClientEndpoint.close();
        }
        // If it's none of these then idk
        else {
            GlimChat.addGlimeshMessage("Unexpected Message Received", message, Formatting.BOLD);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        GlimChat.addGlimeshMessage("Connection closed by " + (remote ? "Glimesh" : "You") + ". Code: " + code + " Reason: " + reason, "", Formatting.BOLD);
        // stop heartbeat
        t.cancel(false);


    }

    @Override
    public void onError(Exception ex) {
        GlimChat.addGlimeshMessage("Error", ex.toString(), Formatting.RED);
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public void heartbeat() {
        send("[\"1\",\"1\",\"phoenix\",\"heartbeat\",{}]");
        // send a heartbeat to Glimesh so the connection won't be closed
    }
}