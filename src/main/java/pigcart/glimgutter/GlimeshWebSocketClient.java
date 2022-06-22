package pigcart.glimgutter;

import java.net.URI;

import com.github.wnameless.json.flattener.JsonFlattener;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.util.Map;
import java.util.concurrent.*;

public class GlimeshWebSocketClient extends WebSocketClient {

    public GlimeshWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    String followSubId = "";
    String chatSubId = "";

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    static ScheduledFuture<?> heartbeat;

    class Heartbeat implements Runnable {
        public void run() {
            send("[\"1\",\"1\",\"phoenix\",\"heartbeat\",{}]");
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("[\"1\",\"join\",\"__absinthe__:control\",\"phx_join\",{}]");

        heartbeat = executor.scheduleAtFixedRate(new Heartbeat(), 30, 30, TimeUnit.SECONDS);

        followSubId = "";
        chatSubId = "";
    }

    // here we deal with the process of connecting to glimesh and extracting the desired information from the api responses
    @Override
    public void onMessage(String message) {
        Map<String, Object> glimeshResponseMap = JsonFlattener.flattenAsMap(message);

        // ref sent by the client when joining | is null if message is a subscription response | use to differentiate between multiple connections
        Object joinRefObj = glimeshResponseMap.get("[0]");
        String joinRef = (joinRefObj != null) ? joinRefObj.toString() : "noJoinRef";
        // TODO: Allow for multiple connections

        // latest ref sent by the client | is null if response is for event we've subscribed to | use to track which request the received data is fulfilling
        Object queryRefObj = glimeshResponseMap.get("[1]");
        String queryRef = (queryRefObj != null) ? queryRefObj.toString() : "noQueryRef";

        // __absinthe__:control (api response) / phoenix (heartbeat response) / __absinthe__:doc:blahblahblah (subscription response, also the subscription id)
        String topic = glimeshResponseMap.get("[2]").toString();

        // phx_reply (api reply) / subscription:data
        String event = glimeshResponseMap.get("[3]").toString();

        // ([4] contains the requested data)

        if (event.equals("subscription:data")) {
            if (topic.equals(chatSubId)) {
                GlimGutter.addUserChatMsg(glimeshResponseMap.get("[4].result.data.chatMessage.user.displayname").toString(), glimeshResponseMap.get("[4].result.data.chatMessage.message").toString());
                // TODO: show channel names when connected to every channel
                if (GlimeshPlaysController.glimeshPlays) {
                    GlimeshPlaysController.process(glimeshResponseMap.get("[4].result.data.chatMessage.message").toString());
                }
                // TODO: add subscriber events
            } else if (topic.equals(followSubId)) {
                GlimGutter.addInfoChatMsg(Component.literal("New follower! " + message).withStyle(ChatFormatting.GOLD)); //test msg
                // TODO: add follower events
            }

        } else if (event.equals("phx_reply")) { // responses to API requests handled here
            switch (queryRef) {

                case "join": // joined the websocket - now request subscriptions
                    if (GlimGutter.config.channel.equals(".")) { // entering "." instead of a channel name will connect to every chat
                        send("[\"1\",\"chatSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage{user{displayname}message}}\"}]");
                    } else { // otherwise look up the channel's ID
                        send("[\"1\",\"channelId\",\"__absinthe__:control\",\"doc\",{\"query\":\"query{channel(username:\\\""+ GlimGutter.config.channel+"\\\"){id}}\",\"variables\":{}}]");
                    }
                    break;

                case "channelId": // received the channel id, so now subscribe to that channel's chat
                    // TODO: abide by config options for showing chatters / followers / subscibers
                    String channelId = glimeshResponseMap.get("[4].response.data.channel.id").toString();
                    send("[\"1\",\"chatSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage(channelId:\\\""+channelId+"\\\"){user{displayname}message}}\",\"variables\":{}}]");
                    // TODO: Subscribe to the channel's follower updates
                    break;

                case "chatSub":
                    chatSubId = glimeshResponseMap.get("[4].response.subscriptionId").toString();
                    break;

                case "followSub":
                    followSubId = glimeshResponseMap.get("[4].response.subscriptionId").toString();
            }
        } else {
            GlimGutter.addInfoChatMsg(Component.translatable("text.glimgutter.websocket.onmessage.unexpected").append(topic + "! Make sure you are using the latest version of the mod. If you are, please report this error.").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codes are documented in class org.java_websocket.framing.CloseFrame
        // TODO: Use close codes to reconnect if necessary and tell the user if something wacky happens
        GlimGutter.addInfoChatMsg(Component.translatable("text.glimgutter.websocket.onclose").append(String.valueOf(code)));
        if (heartbeat != null) {
            heartbeat.cancel(false);
        }
    }

    @Override
    public void onError(Exception ex) {
        GlimGutter.addInfoChatMsg(Component.translatable("text.glimgutter.websocket.onerror").append(ex.toString()).withStyle(ChatFormatting.RED));
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }
}