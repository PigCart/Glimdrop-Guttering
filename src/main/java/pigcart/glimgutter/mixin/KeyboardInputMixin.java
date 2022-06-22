package pigcart.glimgutter.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pigcart.glimgutter.GlimeshPlaysController;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    private static float calculateImpulse(boolean bl, boolean bl2) {
        if (bl == bl2) {
            return 0.0F;
        } else {
            return bl ? 1.0F : -1.0F;
        }
    }

    public KeyboardInputMixin(Options options) { this.options = options; }

    @Shadow private final Options options;

    @Inject(method = "tick" , at = @At("HEAD"), cancellable = true)
    public void tick(boolean bl, float f, CallbackInfo ci) {
        if (!GlimeshPlaysController.glimeshPlays) return;

        GlimeshPlaysController.update();

        this.up = GlimeshPlaysController.shouldPress("W");
        this.down = GlimeshPlaysController.shouldPress("S");
        this.left = GlimeshPlaysController.shouldPress("A");
        this.right = GlimeshPlaysController.shouldPress("D");
        this.forwardImpulse = calculateImpulse(this.up, this.down);
        this.leftImpulse = calculateImpulse(this.left, this.right);
        this.jumping = GlimeshPlaysController.shouldPress("J");
        this.shiftKeyDown = this.options.keyShift.isDown();
        if (bl) {
            this.leftImpulse *= f;
            this.forwardImpulse *= f;
        }
        ci.cancel();
    }
}
