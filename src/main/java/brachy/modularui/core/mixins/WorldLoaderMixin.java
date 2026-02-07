package brachy.modularui.core.mixins;

import brachy.modularui.utils.RegistryAccessContainer;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.WorldLoader;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {

    @Inject(method = "load",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
                    shift = At.Shift.BEFORE))
    private static <D, R> void mui$captureRegistries1(CallbackInfoReturnable<CompletableFuture<R>> cir,
                                                      @Local(ordinal = 0) RegistryAccess.Frozen registriesWithDimensions) {
        RegistryAccessContainer.update(registriesWithDimensions, null);
    }

    @Definition(id = "load", method = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;")
    @Expression("? = load(?, ?, ?)")
    @Inject(method = "load", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private static <D, R> void mui$captureRegistries2(CallbackInfoReturnable<CompletableFuture<R>> cir,
                                                      @Local(ordinal = 1) RegistryAccess.Frozen registriesWithEverything) {
        RegistryAccessContainer.update(registriesWithEverything, null);
    }
}
