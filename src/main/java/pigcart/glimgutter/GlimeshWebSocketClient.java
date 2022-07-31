package pigcart.glimgutter;

import java.net.URI;

import com.github.wnameless.json.flattener.JsonFlattener;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import pigcart.glimgutter.config.ModConfig;

import java.util.Map;
import java.util.concurrent.*;

public class GlimeshWebSocketClient extends WebSocketClient {

    public GlimeshWebSocketClient(URI serverURI) {
        super(serverURI);
    }

    ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    String followSubId = "";
    String chatSubId = "";
    String subSubId = "";

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    static ScheduledFuture<?> heartbeat;

    class Heartbeat implements Runnable {
        public void run() {
            send("[\"1\",\"1\",\"phoenix\",\"heartbeat\",{}]");
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Websocket open. sending join request"));
        send("[\"1\",\"join\",\"__absinthe__:control\",\"phx_join\",{}]");

        heartbeat = executor.scheduleAtFixedRate(new Heartbeat(), 30, 30, TimeUnit.SECONDS);

        followSubId = "";
        chatSubId = "";
        subSubId = "";
    }

    @Override
    public void onMessage(String message) {
        Map<String, Object> glimeshResponseMap = JsonFlattener.flattenAsMap(message);
        if (config.verboseFeedback) System.out.println(glimeshResponseMap);

        // ref sent by the client when joining | is null if message is a subscription response | use to differentiate between multiple connections
        Object joinRefObj = glimeshResponseMap.get("[0]");
        String joinRef = (joinRefObj != null) ? joinRefObj.toString() : "noJoinRef";

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
                //TODO: run the chat event command here
                if (GlimeshPlaysController.glimeshPlays) {
                    GlimeshPlaysController.process(glimeshResponseMap.get("[4].result.data.chatMessage.message").toString());
                }
                if (config.channel.equals(".")) {
                    GlimGutter.addUserChatMsg("["+glimeshResponseMap.get("[4].result.data.chatMessage.channel.streamer.displayname").toString()+"] "+glimeshResponseMap.get("[4].result.data.chatMessage.user.displayname").toString(), glimeshResponseMap.get("[4].result.data.chatMessage.message").toString(), ChatFormatting.BLUE, ChatFormatting.GRAY);
                    return;
                }
                GlimGutter.addUserChatMsg(glimeshResponseMap.get("[4].result.data.chatMessage.user.displayname").toString(), glimeshResponseMap.get("[4].result.data.chatMessage.message").toString(), ChatFormatting.BLUE, ChatFormatting.GRAY);

            } else if (topic.equals(followSubId)) {
                //TODO: run the follow event command here
                if (config.channel.equals(".")) {
                    GlimGutter.addInfoChatMsg(Component.literal("["+glimeshResponseMap.get("[4].result.data.followers.streamer.displayname")+"] "+glimeshResponseMap.get("[4].result.data.followers.user.displayname").toString()+" just followed!").withStyle(ChatFormatting.AQUA));
                    return;
                }
                GlimGutter.addInfoChatMsg(Component.literal(glimeshResponseMap.get("[4].result.data.followers.user.displayname").toString()+" just followed!").withStyle(ChatFormatting.AQUA));

            } else if (topic.equals(subSubId) && glimeshResponseMap.get("[4].result.data.chatMessage.isSubscriptionMessage").equals(true)) { //TODO: test if sub events actually work lol
                //TODO: run the sub event command here
                if (config.channel.equals(".")) {
                    GlimGutter.addInfoChatMsg(Component.literal("["+glimeshResponseMap.get("[4].result.data.chatMessage.channel.streamer.displayname").toString()+"] "+glimeshResponseMap.get("[4].result.data.chatMessage.user.displayname").toString() + " just subscribed!").withStyle(ChatFormatting.DARK_PURPLE));
                    return;
                }
                GlimGutter.addInfoChatMsg(Component.literal(glimeshResponseMap.get("[4].result.data.chatMessage.user.displayname").toString() + " just subscribed!").withStyle(ChatFormatting.DARK_PURPLE));
            }

        } else if (event.equals("phx_reply")) { // responses to API requests handled here
            if (glimeshResponseMap.get("[4].status").toString().equals("error")) GlimGutter.addInfoChatMsg(Component.literal("Error from request for "+glimeshResponseMap.get("[1]")+": "+glimeshResponseMap.get("[4].response.errors[0].message").toString()).withStyle(ChatFormatting.RED));
            switch (queryRef) {
                case "join" -> { // joined the websocket - now request subscriptions
                    if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Joined. Requesting Subscriptions"));
                    if (GlimGutter.config.channel.equals(".")) { // entering "." instead of a channel name will connect to every chat
                        send("[\"1\",\"chatSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage{channel{streamer{displayname}},user{displayname},message}}\"}]");
                        send("[\"1\",\"followSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{followers{streamer{displayname},user{displayname}}}\",\"variables\":{}}]");
                        send("[\"1\",\"subSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage{isSubscriptionMessage,channel{streamer{displayname}},user{displayname}}}\",\"variables\":{}}]");
                    } else { // otherwise look up the channel's ID
                        send("[\"1\",\"channelId\",\"__absinthe__:control\",\"doc\",{\"query\":\"query{channel(username:\\\"" + GlimGutter.config.channel + "\\\"){id}}\",\"variables\":{}}]");
                        send("[\"1\",\"streamerId\",\"__absinthe__:control\",\"doc\",{\"query\":\"query{user(username:\\\"" + GlimGutter.config.channel + "\\\"){id}}\",\"variables\":{}}]");
                    }
                }
                case "channelId" -> { // received the channel id, so now subscribe to that channel's chat
                    if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Received channel ID for chat subscriptions"));
                    String channelId = glimeshResponseMap.get("[4].response.data.channel.id").toString();
                    send("[\"1\",\"chatSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage(channelId:\\\"" + channelId + "\\\"){user{displayname},message}}\",\"variables\":{}}]");
                    send("[\"1\",\"subSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{chatMessage(channelId:\\\"" + channelId + "\\\"){isSubscriptionMessage,user{displayname}}}\",\"variables\":{}}]");
                }
                case "streamerId" -> {
                    if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Received streamer ID for follow subscription"));
                    String streamerId = glimeshResponseMap.get("[4].response.data.user.id").toString();
                    send("[\"1\",\"followSub\",\"__absinthe__:control\",\"doc\",{\"query\":\"subscription{followers(streamerId:\\\"" + streamerId + "\\\"){user{displayname}}}\",\"variables\":{}}]");
                }
                case "chatSub" -> {
                    chatSubId = glimeshResponseMap.get("[4].response.subscriptionId").toString();
                    if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Subscribed to Chat"));
                }
                case "followSub" -> {
                    followSubId = glimeshResponseMap.get("[4].response.subscriptionId").toString();
                    if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Subscribed to Followers"));
                }
                case "subSub" -> {
                    subSubId = glimeshResponseMap.get("[4].response.subscriptionId").toString();
                    if (config.verboseFeedback) GlimGutter.addInfoChatMsg(Component.literal("Subscribed to Subs"));
                }
            }
        } else {
            GlimGutter.addInfoChatMsg(Component.translatable("text.glimgutter.websocket.onmessage.unexpected").append(topic + "! Make sure you are using the latest version of the mod. If you are, please report this error.").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codes are documented in class org.java_websocket.framing.CloseFrame
        GlimGutter.addInfoChatMsg(Component.translatable("text.glimgutter.websocket.onclose."+code));
        if (!(reason == null || reason.equals(""))) {
            GlimGutter.addInfoChatMsg(Component.literal(reason));
        }
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