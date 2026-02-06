package brachy.modularui;

import brachy.modularui.api.drawable.IKey;
import brachy.modularui.factory.UIFactories;
import brachy.modularui.factory.inventory.InventoryTypes;
import brachy.modularui.network.NetworkHandler;
import brachy.modularui.screen.ModularContainerMenu;
import brachy.modularui.test.ModularUITestingRegistration;
import brachy.modularui.theme.ThemeManager;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.Command;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class CommonProxy {

    public CommonProxy() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(this);
        NeoForge.EVENT_BUS.addListener(this::registerReloadListeners);
        NeoForge.EVENT_BUS.addListener(this::onTick);
        NeoForge.EVENT_BUS.addListener(this::registerCommand);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModularUIConfig.CONFIG, ModularUI.MOD_ID + ".toml");

        /* MUI Initialization */
        UIFactories.init();
        InventoryTypes.init();

        NetworkHandler.init();
        ModularUIMenuTypes.register(modBus);
        if (ModularUI.isDev()) {
            ModularUITestingRegistration.register(modBus);
        }
    }

    public void onTick(PlayerTickEvent event) {
        if (event.getEntity().containerMenu instanceof ModularContainerMenu containerMenu) {
            containerMenu.onUpdate();
        }
    }

    public void registerReloadListeners(AddReloadListenerEvent event) {
        ModularUI.updateFrozenRegistry(event.getRegistryAccess());
        if (ModularUI.isClientThread()) {
            event.addListener(new ThemeManager());
        }
    }

    public void registerCommand(RegisterCommandsEvent event) {
        var command = Commands.literal("mui")
                .then(Commands.literal("reload_themes")
                        .executes(ctx -> {
                            ThemeManager.reload();
                            // TODO translations for this
                            ctx.getSource().sendSuccess(() -> Component.literal("ModularUI Themes reloaded").withStyle(IKey.GREEN), true);
                            return Command.SINGLE_SUCCESS;
                        }));
        event.getDispatcher().register(command);
    }
}
