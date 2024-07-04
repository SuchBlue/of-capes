package net.drago.ofcapes.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.drago.ofcapes.util.PlayerHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow
    @Final
    private GameProfile profile;
    private boolean loadedCapeTexture = false;
    private Identifier identifier;

    @ModifyReturnValue(method = "getSkinTextures", at = @At("TAIL"))
    protected SkinTextures changeCapeTexture(SkinTextures original) {
        fetchCapeTexture();
        return new SkinTextures(
                original.texture(),
                null,
                identifier == null ? original.capeTexture() : identifier,
                identifier == null ? original.elytraTexture() : identifier,
                original.model(),
                true
        );
    }

    private void fetchCapeTexture() {
        if (loadedCapeTexture) return;
        loadedCapeTexture = true;
        PlayerHandler.loadPlayerCape(this.profile, id -> {
            this.identifier = id;
        });
    }
}