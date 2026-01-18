package net.drago.ofcapes.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlayerHandler {
    public static Map<String, Identifier> capes = new HashMap<>();

    public interface ReturnCapeTexture {
        void response(Identifier id);
    }

    public static void loadPlayerCape(GameProfile player, ReturnCapeTexture response) {
        try {
            String uuid = player.id().toString();
            URL capeURL = new URI(String.format("http://s.optifine.net/capes/%s.png", player.name())).toURL();
            CompletableFuture.supplyAsync(() -> {
                try {
                    return capeURL.openStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).thenAcceptAsync(capeStream -> {
                NativeImage capeImage;
                try {
                    capeImage = parseCape(NativeImage.read(capeStream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Identifier capeTexture = Identifier.of("of-capes", uuid);
                MinecraftClient.getInstance().execute(() -> {
                    NativeImageBackedTexture nIBT = new NativeImageBackedTexture(() -> "of-capes:" + uuid, capeImage);
                    MinecraftClient.getInstance().getTextureManager().registerTexture(capeTexture, nIBT);
                });
                capes.put(uuid, capeTexture);
                response.response(capeTexture);
            });
        } catch (Exception ignored) {}
    }

    public static NativeImage parseCape(NativeImage image) {
        if(image == null) return null;

        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        for (int imageSrcHeight = image.getHeight(); imageWidth < imageSrcWidth
                || imageHeight < imageSrcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setColorArgb(x, y, image.getColorArgb(x, y));
            }
        }
        image.close();
        return imgNew;
    }
}
