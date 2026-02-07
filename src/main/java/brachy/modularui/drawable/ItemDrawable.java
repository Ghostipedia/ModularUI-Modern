package brachy.modularui.drawable;

import brachy.modularui.api.IJsonSerializable;
import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.screen.viewport.GuiContext;
import brachy.modularui.theme.WidgetTheme;
import brachy.modularui.utils.RegistryAccessContainer;
import brachy.modularui.widget.Widget;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Accessors(chain = true)
public class ItemDrawable implements IDrawable, IJsonSerializable<ItemDrawable> {

    private static final Codec<ItemStack> OPTIONAL_SINGLE_ITEM_CODEC = ExtraCodecs.optionalEmptyMap(ItemStack.SINGLE_ITEM_CODEC)
            .xmap(stack -> stack.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));

    public static final Codec<ItemDrawable> CODEC = OPTIONAL_SINGLE_ITEM_CODEC.xmap(ItemDrawable::new, ItemDrawable::getItem);

    @Getter
    @Setter
    private @NotNull ItemStack item = ItemStack.EMPTY;

    public ItemDrawable() {}

    public ItemDrawable(@NotNull ItemStack item) {
        setItem(item);
    }

    public ItemDrawable(@NotNull Item item) {
        setItem(item);
    }

    public ItemDrawable(@NotNull Item item, int amount) {
        setItem(item, amount);
    }

    public ItemDrawable(@NotNull Item item, int amount, @NotNull DataComponentPatch componentPatch) {
        setItem(item, amount, componentPatch);
    }

    public ItemDrawable(@NotNull Block item) {
        setItem(item);
    }

    public ItemDrawable(@NotNull Block item, int amount) {
        setItem(new ItemStack(item, amount));
    }

    public static ItemDrawable ofJson(JsonObject json) {
        return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        applyColor(widgetTheme.getColor());
        GuiDraw.drawItem(context.getGraphics(), this.item, x, y, width, height, context.getCurrentDrawingZ());
    }

    @Override
    public int getDefaultWidth() {
        return 16;
    }

    @Override
    public int getDefaultHeight() {
        return 16;
    }

    @Override
    public Widget<?> asWidget() {
        return IDrawable.super.asWidget().size(16);
    }

    @Tolerate
    public ItemDrawable setItem(@NotNull Item item) {
        return setItem(item, 1, DataComponentPatch.EMPTY);
    }

    @Tolerate
    public ItemDrawable setItem(@NotNull Item item, int amount) {
        return setItem(item, amount, DataComponentPatch.EMPTY);
    }

    @Tolerate
    public ItemDrawable setItem(@NotNull Item item, int amount, @NotNull DataComponentPatch componentPatch) {
        ItemStack stack = new ItemStack(item, amount);
        stack.applyComponents(componentPatch);
        return setItem(stack);
    }

    @Tolerate
    public ItemDrawable setItem(@NotNull Block item) {
        return setItem(item, 1);
    }

    @Tolerate
    public ItemDrawable setItem(@NotNull Block item, int amount) {
        return setItem(new ItemStack(item, amount));
    }

    @Override
    public Codec<ItemDrawable> getCodec() {
        return CODEC;
    }

    @Override
    public void loadFromJson(JsonObject json) {
        var jsonOps = RegistryAccessContainer.current().createSerializationContext(JsonOps.INSTANCE);

        setItem(OPTIONAL_SINGLE_ITEM_CODEC.parse(jsonOps, json).getOrThrow(JsonParseException::new));
    }

    @Override
    public boolean saveToJson(JsonObject json) {
        if (this.item.isEmpty()) {
            return true;
        }

        var jsonOps = RegistryAccessContainer.current().createSerializationContext(JsonOps.INSTANCE);
        JsonElement saved = OPTIONAL_SINGLE_ITEM_CODEC.encode(this.item, jsonOps, json).getOrThrow(JsonParseException::new);
        if (saved.isJsonObject()) {
            json.asMap().putAll(saved.getAsJsonObject().asMap());
        }
        return true;
    }
}
