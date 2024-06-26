package jakraes.betterwithvoice.mixins;

import jakraes.betterwithvoice.BetterWithVoice;
import jakraes.betterwithvoice.interfaces.IGameSettingsMixin;
import jakraes.betterwithvoice.misc.SenderThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.PlayerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerInput.class, remap = false)
public class PlayerInputMixin {
	private final int V_KEY = 47;
	@Final
	@Shadow
	public Minecraft mc;
	private SenderThread senderThread;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void betterwithvoice$constructorInject(Minecraft minecraft, CallbackInfo ci) {
		senderThread = new SenderThread(minecraft, minecraft.getSendQueue());
	}

	@Inject(method = "keyEvent", at = @At("HEAD"))
	public void betterwithvoice$keyEventInject(int keyCode, boolean pressed, CallbackInfo ci) {
		if (((IGameSettingsMixin) mc.gameSettings).betterwithvoice$getActivateVoiceKey().isKeyboardKey(keyCode)) {
			if (pressed && !senderThread.isAlive()) {
				BetterWithVoice.LOGGER.info("Sending");
				senderThread.start();
			} else if (!pressed) {
				BetterWithVoice.LOGGER.info("Stopping");
				senderThread.end();
				senderThread = new SenderThread(mc, mc.getSendQueue());
			}
		}
	}
}
