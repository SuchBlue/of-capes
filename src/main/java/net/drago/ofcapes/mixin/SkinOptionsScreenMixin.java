package net.drago.ofcapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.math.BigInteger;
import java.util.Random;

@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends GameOptionsScreen {

	public SkinOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	// This is supposed to be a quick and simple mod for snapshots, FAPI may not be available,
	// and there should be little harm in using a hardcoded string in english.
	private final Text changeBtnText = Text.of("Open Cape Editor");

	@Inject(at = @At("TAIL"), method = "addOptions")
	public void addOptions(CallbackInfo info) {
		ButtonWidget btn = ButtonWidget.builder(changeBtnText, button -> {
					BigInteger intA = new BigInteger(128, new Random());
					BigInteger intB = new BigInteger(128, new Random(System.identityHashCode(new Object())));
					String serverId = intA.xor(intB).toString(16);
                    assert this.client != null;
					assert this.client.getSession().getUuidOrNull() != null;
                    String url = String.format("https://optifine.net/capeChange?u=%s&n=%s&s=%s", this.client.getSession().getUuidOrNull(), this.client.getSession().getUsername(), serverId);
                    Util.getOperatingSystem().open(url);
				})
				//.dimensions(this.width, this.height, 150, 20)
				.build();
		btn.setX(5);
		btn.setY(5);
		this.addDrawableChild(btn);
	}
}
