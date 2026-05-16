package brachy.modularui.drawable;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.screen.viewport.GuiContext;
import brachy.modularui.theme.WidgetTheme;

import brachy.modularui.utils.math.MathUtils;

import net.minecraft.Util;

import lombok.Getter;

import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;

public class ProgressDrawable implements IDrawable {

    @Getter private DoubleSupplier progress;
    @Getter private IDrawable emptyBackground;
    @Getter private IDrawable filledTexture;
    @Getter private Direction direction = Direction.RIGHT;
    @Getter private float progressStepSize = 0;
    @Getter private int progressPixelStepSize = 0;

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        if (this.emptyBackground != null) this.emptyBackground.draw(context, x, y, width, height, widgetTheme);
        if (this.filledTexture == null) return;

        float p = getCurrentProgress(width, height);
        float u0 = 0, u1 = 1, v0 = 0, v1 = 1;
        switch (this.direction) {
            case LEFT -> u0 = 1 - p;
            case RIGHT -> u1 = p;
            case UP -> v0 = 1 - p;
            case DOWN -> v1 = p;
        }
        context.getStencil().push(x + u0 * width, y + v0 * height, (u1 - u0) * width, (v1 - v0) * height);
        this.filledTexture.draw(context, x, y, width, height, widgetTheme);
        context.getStencil().pop();
    }

    private float getCurrentProgress(int width, int height) {
        float p = this.progress == null ? 1f : (float) this.progress.getAsDouble();
        p = MathUtils.clamp(p, 0, 1);
        float stepSize = this.progressStepSize;
        if (this.progressPixelStepSize > 0) {
            float s = switch (this.direction) {
                case LEFT, RIGHT -> width;
                case UP, DOWN -> height;
            };
            stepSize = this.progressPixelStepSize / s;
        }
        if (stepSize > 0) {
            int c = (int) (p / stepSize);
            p = c * stepSize;
        }
        return p;
    }

    /**
     * Sets the displayed progress value. The progress is clamped between 0 (empty) and 1 (filled).
     *
     * @param progress progress supplier
     * @return this
     */
    public ProgressDrawable progress(DoubleSupplier progress) {
        this.progress = progress;
        return this;
    }

    /**
     * Sets a fixed progress value to display. The progress is clamped between 0 (empty) and 1 (filled).
     *
     * @param progress progress
     * @return this
     */
    public ProgressDrawable progress(double progress) {
        return progress(() -> progress);
    }

    /**
     * Sets a progress supplier which linearly increases from 0 to 1 with the given duration in milliseconds.
     *
     * @param durationMilliSeconds duration in milliseconds
     * @return this
     */
    public ProgressDrawable progressDuration(int durationMilliSeconds) {
        return progress(() -> Util.getMillis() % durationMilliSeconds / (double) durationMilliSeconds);
    }

    /**
     * Sets a progress supplier which linearly increases from 0 to 1 with the given duration.
     *
     * @param duration duration
     * @param unit     time unit of the previous duration argument
     * @return this
     */
    public ProgressDrawable progressDuration(long duration, TimeUnit unit) {
        return progressDuration((int) unit.toMillis(duration));
    }

    /**
     * Sets the empty texture which is always fully displayed.
     *
     * @param drawable empty texture
     * @return this
     */
    public ProgressDrawable emptyTexture(IDrawable drawable) {
        this.emptyBackground = drawable;
        return this;
    }

    /**
     * Sets the filled texture which is partially drawn based on the current progress.
     *
     * @param drawable filled texture.
     * @return this
     */
    public ProgressDrawable filledTexture(IDrawable drawable) {
        this.filledTexture = drawable;
        return this;
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
    public ProgressDrawable progressStepSize(float progressStepSize) {
        this.progressStepSize = progressStepSize;
        this.progressPixelStepSize = 0;
        return this;
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
