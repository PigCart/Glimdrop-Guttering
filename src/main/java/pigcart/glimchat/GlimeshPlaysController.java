package pigcart.glimchat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlimeshPlaysController {
    public static Map<String, Long> flags = new ConcurrentHashMap<>();
    public static long timestamp = System.currentTimeMillis(), pressDuration = 800L;
    public static boolean glimeshPlays;

    static {
        flags.put("A", 0L);
        flags.put("D", 0L);
        flags.put("W", 0L);
        flags.put("S", 0L);
        flags.put("J", 0L);
    }

    public static void process(String msg) {

        boolean isCommand = glimeshPlays && msg.length() == 1 && flags.containsKey(msg);

        if (isCommand) flags.replace(msg, timestamp);
    }

    public static boolean shouldPress(String s) {
        return timestamp - flags.get(s) < pressDuration;
    }


    public static void update() {
        timestamp = System.currentTimeMillis();
    }

}
