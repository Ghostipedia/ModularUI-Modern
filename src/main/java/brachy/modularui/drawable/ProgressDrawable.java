package brachy.modularui.drawable;

import brachy.modularui.screen.viewport.GuiContext;
import brachy.modularui.theme.WidgetTheme;

import lombok.Getter;

public class ProgressDrawable extends AbstractProgressDrawable<ProgressDrawable> {

    @Getter private Direction direction = Direction.RIGHT;
    @Getter private int progressPixelStepSize = 0;

    @Override
    public void pushProgressStencil(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        float p = getCurrentProgress(width, height);
        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;
        switch (this.direction) {
            case LEFT -> u0 = 1 - p;
            case RIGHT -> u1 = p;
            case UP -> v0 = 1 - p;
            case DOWN -> v1 = p;
        }
        context.getStencil().push(x + u0 * width, y + v0 * height, (u1 - u0) * width, (v1 - v0) * height);
    }

    @Override
    protected float getCurrentProgressStepSize(int width, int height) {
        float stepSize = this.progressStepSize;
        if (this.progressPixelStepSize > 0) {
            float s = switch (this.direction) {
                case LEFT, RIGHT -> width;
                case UP, DOWN -> height;
            };
            stepSize = this.progressPixelStepSize / s;
        }
        return stepSize;
    }

    /**
     * Sets the direction in which the progress should move.
     *
     * @param direction direction
     * @return this
     */
    public ProgressDrawable direction(Direction direction) {
        this.direction = direction == null ? Direction.RIGHT : direction;
        return this;
    }

    /**
     * Sets the progress to move right to left.
     *
     * @return this
     */
    public ProgressDrawable left() {
        return direction(Direction.LEFT);
    }

    /**
     * Sets the progress to move left to right.
     *
     * @return this
     */
    public ProgressDrawable right() {
        return direction(Direction.RIGHT);
    }

    /**
     * Sets the progress to move down to up.
     *
     * @return this
     */
    public ProgressDrawable up() {
        return direction(Direction.UP);
    }

    /**
     * Sets the progress to move up to down.
     *
     * @return this
     */
    public ProgressDrawable down() {
        return direction(Direction.DOWN);
    }

    /**
     * Sets a progress step size. The displayed progress will be clamped to the closest multiple of this value.
     * Small values are smooth and high values are choppy. Values higher than 1 means the displayed progress is always 0.
     *
     * @param progressStepSize progress step size
     * @return this.
     */
    @Override
    public ProgressDrawable progressStepSize(float progressStepSize) {
        this.progressPixelStepSize = 0;
        return super.progressStepSize(progressStepSize);
    }


    /**
     * Sets a pixel progress step size. This is similar to {@link #progressStepSize(float)}, but this in units of pixel.
     * This can be useful when you have an actual texture.
     *
     * @param progressPixelStepSize pixel progress size
     * @return this
     * @see #progressStepSize(float)
     */
    public ProgressDrawable progressPixelStepSize(int progressPixelStepSize) {
        this.progressPixelStepSize = progressPixelStepSize;
        this.progressStepSize = 0;
        return this;
    }

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
