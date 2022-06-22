package pigcart.glimgutter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.phys.Vec3;

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
        // TODO: add timer for how long the action should last
        if (movementFlags.containsKey(msg)) movementFlags.replace(msg, timestamp);
        else {
        //else if (directionFlags.containsKey(msg)) {
            LocalPlayer player = Minecraft.getInstance().player;
            Vec3 pos = player.position();
            //player.getRotationVector();
            switch (msg) {
                // TODO: Fix this or depend on that numpad movement mod y'know the one
                case "UP": {
                    Vec3 vec3 = new Vec3(pos.x, pos.y + 1, pos.z);
                    player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                    break;
                }
                case "DOWN": {
                    Vec3 vec3 = new Vec3(pos.x, pos.y - 1, pos.z);
                    player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                    break;
                }
                case "LEFT": {
                    float lookingY = player.getRotationVector().y;
                    if ( lookingY == 0 ) { //If looking South
                        Vec3 vec3 = new Vec3(pos.x + 1, pos.y, pos.z + 1); //Face South-East
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY ==  -45 ) { //If looking south-east
                        Vec3 vec3 = new Vec3(pos.x + 1 , pos.y, pos.z ); //Face East
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == -90 ) { //If looking East
                        Vec3 vec3 = new Vec3(pos.x + 1, pos.y, pos.z - 1); //Face North-east
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if (lookingY == -135 ) { //If looking North-East
                        Vec3 vec3 = new Vec3(pos.x, pos.y, pos.z - 1); //Face North
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 180 ) { //If looking North
                        Vec3 vec3 = new Vec3(pos.x - 1 , pos.y, pos.z - 1 ); //Face north-west
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 135 ) { //If looking North-West
                        Vec3 vec3 = new Vec3(pos.x - 1, pos.y, pos.z); //face West
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 90 ) { //If looking West
                        Vec3 vec3 = new Vec3(pos.x - 1, pos.y, pos.z + 1); //face South-West
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 45 ) { //If looking South-West
                        Vec3 vec3 = new Vec3(pos.x - 1, pos.y, pos.z + 1); //face South
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    }
                }
                case "RIGHT": {
                    float lookingY = player.getRotationVector().y;
                    if ( lookingY == 0 ) { //If looking South
                        Vec3 vec3 = new Vec3(pos.x - 1, pos.y, pos.z + 1); //Face South-West
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY ==  -45 ) { //If looking south-east
                        Vec3 vec3 = new Vec3(pos.x , pos.y, pos.z + 1); //Face South
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == -90 ) { //If looking East
                        Vec3 vec3 = new Vec3(pos.x + 1, pos.y, pos.z + 1); //Face South-east
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if (lookingY == -135 ) { //If looking North-East
                        Vec3 vec3 = new Vec3(pos.x + 1, pos.y, pos.z); //Face East
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 180 ) { //If looking North
                        Vec3 vec3 = new Vec3(pos.x + 1 , pos.y, pos.z - 1 ); //Face north-East
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 135 ) { //If looking North-West
                        Vec3 vec3 = new Vec3(pos.x, pos.y, pos.z - 1); //face North
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 90 ) { //If looking West
                        Vec3 vec3 = new Vec3(pos.x - 1, pos.y, pos.z - 1); //face North-West
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    } else if ( lookingY == 45 ) { //If looking South-West
                        Vec3 vec3 = new Vec3(pos.x - 1, pos.y, pos.z); //face West
                        player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                        break;
                    }
                }
                case "EAST": {
                    Vec3 vec3 = new Vec3(pos.x + 1, pos.y, pos.z);
                    player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                    break;
                }
                case "WEST": {
                    Vec3 vec3 = new Vec3(pos.x - 1, pos.y, pos.z);
                    player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                    break;
                }
                case "NORTH": {
                    Vec3 vec3 = new Vec3(pos.x, pos.y, pos.z - 1);
                    player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
                    break;
                }
                case "SOUTH": {
                    Vec3 vec3 = new Vec3(pos.x, pos.y, pos.z + 1);
                    player.lookAt(EntityAnchorArgument.Anchor.FEET, vec3);
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
