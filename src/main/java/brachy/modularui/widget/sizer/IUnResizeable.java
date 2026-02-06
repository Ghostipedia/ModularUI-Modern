package brachy.modularui.widget.sizer;

import brachy.modularui.api.layout.IResizeable;
import brachy.modularui.api.widget.IGuiElement;

/**
 * A variation of {@link IResizeable} with default implementations which don't do anything
 */
public interface IUnResizeable extends IResizeable {

    IUnResizeable INSTANCE = new IUnResizeable() {

        @Override
        public boolean resize(IGuiElement guiElement, boolean isParentLayout) {
            return true;
        }

        @Override
        public Area getArea() {
            Area.SHARED.set(0, 0, 0, 0);
            return Area.SHARED;
        }
    };

    @Override
    default void initResizing() {}

    @Override
    default boolean postResize(IGuiElement guiElement) {
        return true;
    }

    @Override
    default boolean isXCalculated() {
        return true;
    }

    @Override
    default boolean isYCalculated() {
        return true;
    }

    @Override
    default boolean isWidthCalculated() {
        return true;
    }

    @Override
    default boolean isHeightCalculated() {
        return true;
    }

    @Override
    default boolean areChildrenCalculated() {
        return true;
    }

    @Override
    default boolean isLayoutDone() {
        return true;
    }

    @Override
    default void setLayoutDone(boolean done) {}

    @Override
    default boolean canRelayout(boolean isParentLayout) {
        return false;
    }

    @Override
    default void setChildrenResized(boolean resized) {}

    @Override
    default void setResized(boolean x, boolean y, boolean w, boolean h) {}

    @Override
    default boolean isXMarginPaddingApplied() {
        return true;
    }

    @Override
    default void setXMarginPaddingApplied(boolean b) {}

    @Override
    default boolean isYMarginPaddingApplied() {
        return true;
    }

    @Override
    default void setYMarginPaddingApplied(boolean b) {}
}
