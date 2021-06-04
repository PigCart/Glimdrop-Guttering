package pigcart.glimchat.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pigcart.glimchat.GlimeshPlaysController;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    public KeyboardInputMixin(GameOptions settings) { this.settings = settings; }

    @Shadow private final GameOptions settings;

    @Inject(method = "tick" , at = @At("HEAD"), cancellable = true)
    public void tick(boolean bl, CallbackInfo ci) {
        if (!GlimeshPlaysController.glimeshPlays) return;

        GlimeshPlaysController.update();

        this.pressingForward = GlimeshPlaysController.shouldPress("W");
        this.pressingBack = GlimeshPlaysController.shouldPress("S");
        this.pressingLeft = GlimeshPlaysController.shouldPress("A");
        this.pressingRight = GlimeshPlaysController.shouldPress("D");
        this.movementForward = this.pressingForward == this.pressingBack ? 0.0F : (this.pressingForward ? 1.0F : -1.0F);
        this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0F : (this.pressingLeft ? 1.0F : -1.0F);
        this.jumping = GlimeshPlaysController.shouldPress("J");
        this.sneaking = this.settings.keySneak.isPressed();
        if (bl) { //wait what does this do
            this.movementSideways = (float)((double)this.movementSideways * 0.3D);
            this.movementForward = (float)((double)this.movementForward * 0.3D);
        }
        ci.cancel();
    }
}
