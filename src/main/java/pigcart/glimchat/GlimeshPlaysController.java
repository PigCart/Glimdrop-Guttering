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
                // need to figure out vector addition i guess :(
                case "LEFT": {
                    float lookingY = player.getRotationClient().y;
                    if ( lookingY == 0 ) { //If looking South
                        Vec3d vec3d = new Vec3d(pos.x + 1, pos.y, pos.z + 1); //Face South-East
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY ==  -45 ) { //If looking south-east
                        Vec3d vec3d = new Vec3d(pos.x + 1 , pos.y, pos.z ); //Face East
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == -90 ) { //If looking East
                        Vec3d vec3d = new Vec3d(pos.x + 1, pos.y, pos.z - 1); //Face North-east
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if (lookingY == -135 ) { //If looking North-East
                        Vec3d vec3d = new Vec3d(pos.x, pos.y, pos.z - 1); //Face North
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 180 ) { //If looking North
                        Vec3d vec3d = new Vec3d(pos.x - 1 , pos.y, pos.z - 1 ); //Face north-west
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 135 ) { //If looking North-West
                        Vec3d vec3d = new Vec3d(pos.x - 1, pos.y, pos.z); //face West
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 90 ) { //If looking West
                        Vec3d vec3d = new Vec3d(pos.x - 1, pos.y, pos.z + 1); //face South-West
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 45 ) { //If looking South-West
                        Vec3d vec3d = new Vec3d(pos.x - 1, pos.y, pos.z + 1); //face South
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    }
                }
                case "RIGHT": {
                    float lookingY = player.getRotationClient().y;
                    if ( lookingY == 0 ) { //If looking South
                        Vec3d vec3d = new Vec3d(pos.x - 1, pos.y, pos.z + 1); //Face South-West
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY ==  -45 ) { //If looking south-east
                        Vec3d vec3d = new Vec3d(pos.x , pos.y, pos.z + 1); //Face South
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == -90 ) { //If looking East
                        Vec3d vec3d = new Vec3d(pos.x + 1, pos.y, pos.z + 1); //Face South-east
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if (lookingY == -135 ) { //If looking North-East
                        Vec3d vec3d = new Vec3d(pos.x + 1, pos.y, pos.z); //Face East
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 180 ) { //If looking North
                        Vec3d vec3d = new Vec3d(pos.x + 1 , pos.y, pos.z - 1 ); //Face north-East
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 135 ) { //If looking North-West
                        Vec3d vec3d = new Vec3d(pos.x, pos.y, pos.z - 1); //face North
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 90 ) { //If looking West
                        Vec3d vec3d = new Vec3d(pos.x - 1, pos.y, pos.z - 1); //face North-West
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    } else if ( lookingY == 45 ) { //If looking South-West
                        Vec3d vec3d = new Vec3d(pos.x - 1, pos.y, pos.z); //face West
                        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, vec3d);
                        break;
                    }
                }
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
