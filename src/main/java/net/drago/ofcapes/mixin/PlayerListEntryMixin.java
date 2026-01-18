package net.drago.ofcapes.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.drago.ofcapes.util.PlayerHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
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

        AssetInfo.TextureAsset newTexture = new AssetInfo.TextureAsset() {
            @Override
            public Identifier texturePath() {
                return identifier;
            }

            @Override
            public Identifier id() {
                return identifier;
            }
        };

        return new SkinTextures(
                original.body(),
                identifier == null ? original.cape() : newTexture,
                identifier == null ? original.elytra() : newTexture,
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