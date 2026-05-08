package brachy.modularui.test;

import brachy.modularui.api.IPanelHandler;
import brachy.modularui.api.IUIHolder;
import brachy.modularui.drawable.GuiTextures;
import brachy.modularui.factory.PlayerInventoryGuiData;
import brachy.modularui.factory.inventory.InventoryTypes;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.screen.UISettings;
import brachy.modularui.utils.Alignment;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.value.sync.SyncHandlers;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.SlotGroupWidget;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.slot.ItemSlot;
import brachy.modularui.widgets.slot.ModularSlot;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class TestItem extends Item implements ICurioItem, IUIHolder<PlayerInventoryGuiData<?>> {

    public TestItem(Properties properties) {
        super(properties);
        CuriosApi.registerCurio(this, this);
    }

    @Override
    public ModularScreen createScreen(PlayerInventoryGuiData<?> data, ModularPanel<?> mainPanel) {
        return new ModularScreen(ModularUI.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData<?> data, PanelSyncManager syncManager, UISettings settings) {
        IItemHandler itemHandler = data.getUsedItemStack().getCapability(Capabilities.ItemHandler.ITEM);
        if (!(itemHandler instanceof IItemHandlerModifiable ihm)) return null;

        syncManager.registerSlotGroup("mixer_items", 2);
        // if the player slot is the slot with this item, then disallow any interaction
        // if the item is not in the player inventory (curio for example), then this items slot is not on the screen,
        // and we don't need to limit accessibility
        if (data.getInventoryType() == InventoryTypes.PLAYER) {
            syncManager.bindPlayerInventory(data.getPlayer(), (inv, index) -> index == data.getSlotIndex() ?
                    new ModularSlot(inv, index).accessibility(false, false) :
                    new ModularSlot(inv, index));
        }
        ModularPanel<?> panel = ModularPanel.defaultPanel("knapping_gui").resizeableOnDrag(true);
        panel.child(Flow.col().margin(7)
                        .child(new ParentWidget<>().widthRel(1f).expanded()
                                .child(SlotGroupWidget.builder()
                                        .row("I I")
                                        .row("  I")
                                        .row("   ")
                                        .row(" I ")
                                        .key('I', index -> new ItemSlot().slot(SyncHandlers.itemSlot(ihm, index)
                                                .ignoreMaxStackSize(true)
                                                .slotGroup("mixer_items")
                                                // do not allow putting items which can hold other items into the item
                                                // some mods don't do this on their backpacks, so it won't catch those cases
                                                .filter(stack -> stack.getCapability(Capabilities.ItemHandler.ITEM) == null)))
                                        .build()))
                        .child(SlotGroupWidget.playerInventory(false)))
                .child(GuiTextures.ANIMATED_TEXTURE_TEST.asWidget().size(32).leftRel(1f).topRel(0f).margin(7));

        return panel;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
