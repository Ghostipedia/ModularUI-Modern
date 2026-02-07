package brachy.modularui.core.mixins;

import net.minecraft.network.RegistryFriendlyByteBuf;

import brachy.modularui.core.extensions.IRegistryFriendlyByteBufExtension;

import org.spongepowered.asm.mixin.Mixin;

// implement IRegistryFriendlyByteBufExtension on RegistryFriendlyByteBuf at runtime
@Mixin(RegistryFriendlyByteBuf.class)
public class RegistryFriendlyByteBufMixin implements IRegistryFriendlyByteBufExtension {
}
