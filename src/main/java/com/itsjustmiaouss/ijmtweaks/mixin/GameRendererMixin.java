package com.itsjustmiaouss.ijmtweaks.mixin;

import com.itsjustmiaouss.ijmtweaks.config.IJMTweaksConfig;
import com.itsjustmiaouss.ijmtweaks.keybind.IJMTweaksBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique boolean zoomEnabled = false;

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void setZoomFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        GameOptions options = MinecraftClient.getInstance().options;

        if (IJMTweaksBindings.zoomKeyBinding.isPressed()) {
            zoomEnabled = true;
            options.smoothCameraEnabled = true;

            float zoomLevel = IJMTweaksConfig.get().zoomLevel;
            double zoomFactor =  (1.0 - (zoomLevel / 100));
            if(zoomFactor == 0) zoomFactor = 0.05;

            cir.setReturnValue(cir.getReturnValue() * zoomFactor);
        }

        if(!IJMTweaksBindings.zoomKeyBinding.isPressed() && zoomEnabled) {
            zoomEnabled = false;
            options.smoothCameraEnabled = false;
        }
    }

    // Credit: https://github.com/Bestsoft101/SmallMods/blob/nonightvisionflicker/src/client/java/b100/nonightvisionflicker/mixin/client/GameRendererMixin.java
	@Inject(method = "getNightVisionStrength", at = @At("HEAD"), cancellable = true)
	private static void overrideGetNightVisionStrength(LivingEntity entity, float tickDelta, CallbackInfoReturnable<Float> ci) {
		ci.setReturnValue(entity.hasStatusEffect(StatusEffects.NIGHT_VISION) ? 1.0f : 0.0f);
		ci.cancel();
	}

}
