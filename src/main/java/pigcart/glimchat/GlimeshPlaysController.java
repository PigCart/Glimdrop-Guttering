package pigcart.glimchat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlimeshPlaysController {
    public static Map<String, Long> movementFlags = new ConcurrentHashMap<>();
    public static Map<String, Long> directionFlags = new ConcurrentHashMap<>();
    public static long timestamp = System.currentTimeMillis(), pressDuration = 2000L;
    public static boolean glimeshPlays;

    static {
        movementFlags.put("A", 0L);
        movementFlags.put("D", 0L);
        movementFlags.put("W", 0L);
        movementFlags.put("S", 0L);
        movementFlags.put("J", 0L);
        directionFlags.put("UP", 0L);
        directionFlags.put("DOWN", 0L);
        directionFlags.put("LEFT", 0L);
        directionFlags.put("RIGHT", 0L);
        directionFlags.put("EAST", 0L);
        directionFlags.put("WEST", 0L);
        directionFlags.put("NORTH", 0L);
        directionFlags.put("SOUTH", 0L);
    }

    public static void process(String msg) {

        if (movementFlags.containsKey(msg)) movementFlags.replace(msg, timestamp);
        else {
        //else if (directionFlags.containsKey(msg)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            Vec3d pos = player.getPos();
            //player.getRotationVector();
            switch (msg) {
                case "UP": {
                    Vec3d vec3d = new Vec3d(pos.x, pos.y + 1, pos.z);
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                    break;
                }
                case "DOWN": {
                    Vec3d vec3d = new Vec3d(pos.x, pos.y - 1, pos.z);
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                    break;
                }
                case "LEFT": {
                    float lookingY = player.getRotationClient().y;
                    if ( lookingY == -90) { //If looking East
                        Vec3d vec3d = new Vec3d(pos.x + 1, pos.y, pos.z - 1); //face north-east
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                    }
                    break;
                }
                //
                case "EAST": {
                    Vec3d vec3d = new Vec3d(pos.x + 1, pos.y, pos.z);
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                    break;
                }
                case "WEST": {
                    Vec3d vec3d = new Vec3d(pos.x - 1, pos.y, pos.z);
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                    break;
                }
                case "NORTH": {
                    Vec3d vec3d = new Vec3d(pos.x, pos.y, pos.z - 1);
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                    break;
                }
                case "SOUTH": {
                    Vec3d vec3d = new Vec3d(pos.x, pos.y, pos.z + 1);
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                    break;
                }
            }
        }
    }

    public static boolean shouldPress(String s) {
        return timestamp - movementFlags.get(s) < pressDuration;
    }


    public static void update() {
        timestamp = System.currentTimeMillis();
    }

}
