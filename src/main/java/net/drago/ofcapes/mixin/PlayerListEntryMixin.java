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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow
    @Final
    private GameProfile profile;
    @Unique
    private boolean loadedCapeTexture = false;
    @Unique
    private Identifier identifier;

    @ModifyReturnValue(method = "getSkinTextures", at = @At("TAIL"))
    protected SkinTextures changeCapeTexture(SkinTextures original) {
        if(!loadedCapeTexture) fetchCapeTexture();
        return new SkinTextures(
                original.texture(),
                original.textureUrl(),
                identifier == null ? original.capeTexture() : identifier,
                identifier == null ? original.elytraTexture() : identifier,
                original.model(),
                original.secure()
        );
    }

    @Unique
    private void fetchCapeTexture() {
        loadedCapeTexture = true;
        PlayerHandler.loadPlayerCape(this.profile, id -> this.identifier = id);
    }
}