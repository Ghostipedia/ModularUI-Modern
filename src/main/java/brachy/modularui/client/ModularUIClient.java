package brachy.modularui.client;

import brachy.modularui.ModularUI;
import brachy.modularui.ModularUIMenuTypes;
import brachy.modularui.animation.AnimatorManager;
import brachy.modularui.drawable.DrawableSerialization;
import brachy.modularui.factory.inventory.InventoryTypes;
import brachy.modularui.screen.ContainerScreenWrapper;
import brachy.modularui.screen.ModularContainerMenu;
import brachy.modularui.theme.ThemeManager;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import lombok.Getter;

@Mod(value = ModularUI.MOD_ID, dist = Dist.CLIENT)
public class ModularUIClient {

    @Getter
    private static final DeltaTracker.Timer timer60Fps = new DeltaTracker.Timer(60f, 0, FloatUnaryOperator.identity());

    public ModularUIClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);

        if (!ModularUI.isDataGen()) {
            CursorHandler.init();
            AnimatorManager.init();
            // enable stencil bits, must call on render thread
            RenderSystem.recordRenderCall(() -> Minecraft.getInstance().getMainRenderTarget().enableStencil());

            DrawableSerialization.init();
            InventoryTypes.init();
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void registerScreens(final RegisterMenuScreensEvent event) {
        event.<ModularContainerMenu, ContainerScreenWrapper>register(ModularUIMenuTypes.MODULAR_CONTAINER.get(),
                ContainerScreenWrapper::new);
    }

    @SubscribeEvent
    public void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ThemeManager.INSTANCE);
    }
}
