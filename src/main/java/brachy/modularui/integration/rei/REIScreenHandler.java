package brachy.modularui.integration.rei;

import brachy.modularui.api.IMuiScreen;
import brachy.modularui.api.widget.IGuiElement;
import brachy.modularui.integration.recipeviewer.handlers.GhostIngredientSlot;
import brachy.modularui.integration.recipeviewer.handlers.IngredientProvider;
import brachy.modularui.integration.recipeviewer.handlers.RecipeViewerHandler;
import brachy.modularui.utils.Rectangle;

import net.minecraft.client.gui.screens.Screen;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import lombok.Getter;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.drag.DraggableStack;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackProvider;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor;
import me.shedaniel.rei.api.client.gui.drag.DraggedAcceptorResult;
import me.shedaniel.rei.api.client.gui.drag.DraggingContext;
import me.shedaniel.rei.api.client.gui.widgets.TextField;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class REIScreenHandler<T extends Screen & IMuiScreen> extends RecipeViewerHandler
        implements DraggableStackProvider<T>, ExclusionZonesProvider<T> {

    private static final Map<Class<?>, REIScreenHandler<?>> CACHE = new Reference2ReferenceOpenHashMap<>();
    static DraggableStack currentIngredient = null;
    protected final Class<T> clazz;
    // I have to do this mess because of conflicting comparable impls.
    @Getter
    private final DraggableStackVisitor<T> draggableVisitor = new DraggableStackVisitor<T>() {

        @Override
        public <R extends Screen> boolean isHandingScreen(R screen) {
            return REIScreenHandler.this.isHandingScreen(screen);
        }

        @Override
        public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<T> context,
                                                                  DraggableStack stack) {
            currentIngredient = stack;
            return context.getScreen().getScreen().getContext()
                    .getRecipeViewerSettings().getGhostIngredientSlots().stream()
                    .map(target -> BoundsProvider.ofRectangle(asREIRect(target.getArea())));
        }

        @Override
        public DraggedAcceptorResult acceptDraggedStack(DraggingContext<T> context,
                                                        DraggableStack stack) {
            List<GhostIngredientSlot<?>> ghostSlots = context.getScreen().getScreen().getContext()
                    .getRecipeViewerSettings().getGhostIngredientSlots();
            for (var slot : ghostSlots) {
                if (!slot.isEnabled()) {
                    continue;
                }
                var entryStack = stack.getStack();

                if (slot.ingredientHandlingOverride(entryStack)) {
                    currentIngredient = null;
                    return DraggedAcceptorResult.ACCEPTED;
                }
                REIStackConverter.Converter<?> converter = REIStackConverter.getForNullable(slot.ingredientClass());
                if (converter == null) {
                    continue;
                }
                var converted = converter.convertFrom(entryStack);
                if (converted != null) {
                    // noinspection unchecked,rawtypes
                    ((GhostIngredientSlot) slot).setGhostIngredient(converted);
                    currentIngredient = null;
                    return DraggedAcceptorResult.ACCEPTED;
                }
            }
            currentIngredient = null;
            return DraggedAcceptorResult.PASS;
        }
    };

    private REIScreenHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Screen & IMuiScreen> REIScreenHandler<T> of(Class<T> clazz) {
        return (REIScreenHandler<T>) CACHE.computeIfAbsent(clazz, clz -> new REIScreenHandler<>((Class<T>) clz));
    }

    public static <T extends Screen & IMuiScreen> void register(Class<T> clazz, ScreenRegistry registry) {
        of(clazz).register(registry);
    }

    private static me.shedaniel.math.Rectangle asREIRect(Rectangle rect) {
        return new me.shedaniel.math.Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    public void register(ScreenRegistry registry) {
        registry.registerDraggableStackProvider(this);
        registry.registerDraggableStackVisitor(this.getDraggableVisitor());
    }

    @Override
    public @Nullable DraggableStack getHoveredStack(DraggingContext<T> context, double mouseX, double mouseY) {
        IGuiElement hovered = context.getScreen().getScreen().getContext().getTopHovered();
        if (hovered instanceof IngredientProvider<?> provider) {
            var override = provider.ingredientOverride();
            if (override != null) {
                currentIngredient = (DraggableStack) override;
                return currentIngredient;
            }

            REIStackConverter.Converter<?> converter = REIStackConverter.getForNullable(provider.ingredientClass());
            if (converter == null) {
                return null;
            }
            @SuppressWarnings({"rawtypes", "unchecked"})
            var converted = ((REIStackConverter.Converter) converter).convertTo(provider);
            return new DraggableStack() {

                @Override
                public EntryStack<?> getStack() {
                    if (converted.isEmpty()) return EntryStack.empty();
                    return converted.getFirst();
                }

                @Override
                public void drag() {
                    currentIngredient = this;
                }

                @Override
                public void release(DraggedAcceptorResult result) {
                    currentIngredient = null;
                }
            };
        }
        return null;
    }

    @Override
    public <R extends Screen> boolean isHandingScreen(R screen) {
        return screen instanceof IMuiScreen;
    }

    @Override
    public DraggingContext<T> getContext() {
        return DraggableStackProvider.super.getContext();
    }

    @Override
    public double getPriority() {
        return DraggableStackProvider.super.getPriority();
    }

    @Override
    public Collection<me.shedaniel.math.Rectangle> provide(T screen) {
        return screen.getScreen().getContext()
                .getRecipeViewerSettings().getAllExclusionAreas().stream()
                .map(REIScreenHandler::asREIRect)
                .toList();
    }

    @Override
    public void setSearchFocused(boolean focused) {
        TextField searchField = REIRuntime.getInstance().getSearchTextField();
        if (searchField != null) searchField.setFocused(focused);
    }

    @Override
    public @Nullable Object getCurrentlyDragged() {
        if (currentIngredient == null) return null;
        return currentIngredient.get().getValue();
    }
}
