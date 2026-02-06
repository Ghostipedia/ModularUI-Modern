package brachy.modularui.core.mixins.client;

import com.mojang.blaze3d.vertex.BufferBuilder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// TODO check if this is used
@Mixin(BufferBuilder.class)
public interface BufferBuilderAccessor {

    @Accessor
    int getVertices();
}
