package brachy.modularui.screen;

import brachy.modularui.api.IMuiScreen;
import brachy.modularui.utils.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.function.Predicate;

public class EmbedHandler {

    public static int getEmbedWidth(ModularScreen screen) {
        return screen.getMainPanel().getArea().width;
    }

    public static int getEmbedHeight(ModularScreen screen) {
        return screen.getMainPanel().getArea().height;
    }

    public static void drawEmbed(ModularScreen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        drawEmbed(screen, graphics, mouseX, mouseY, partialTicks, r -> true);
    }

    public static void drawEmbedNoVanillaElements(ModularScreen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        drawEmbed(screen, graphics, mouseX, mouseY, partialTicks, r -> false);
    }

    public static void drawEmbed(ModularScreen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Predicate<Renderable> vanillaElementFilter) {
        graphics.pose().pushPose();
        screen.render(graphics, mouseX, mouseY, partialTicks);

        if (vanillaElementFilter != null) {
            RenderSystem.disableDepthTest();
            ClientScreenHandler.drawVanillaElements(graphics, screen.getScreenWrapper().wrappedScreen(), mouseX, mouseY, partialTicks, vanillaElementFilter);
            RenderSystem.enableDepthTest();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().popPose();
    }

    public static void drawEmbedForeground(ModularScreen screen, GuiGraphics graphics) {
        graphics.pose().pushPose();

        // let us draw foreground elements separately after everything else.
        //screen.getContext().getStencil().push(screen.getScreenArea());
        RenderSystem.disableDepthTest();
        Lighting.setupForFlatItems();

        screen.drawForeground(graphics);

        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        //screen.getContext().getStencil().pop();
        graphics.pose().popPose();
    }

    public record EmbedWrapper(ModularScreen screen) implements IMuiScreen {

        @Override
        public Screen wrappedScreen() {
            return Minecraft.getInstance().screen;
        }

        @Override
        public void updateGuiArea(Rectangle area) {}
    }
}
