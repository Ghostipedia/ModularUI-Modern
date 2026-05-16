package brachy.modularui.drawable;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.screen.viewport.GuiContext;
import brachy.modularui.theme.WidgetTheme;
import brachy.modularui.utils.math.MathUtils;

import lombok.Getter;

import net.minecraft.Util;

import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;

public abstract class AbstractProgressDrawable<D extends AbstractProgressDrawable<D>> implements IDrawable {

    @Getter private DoubleSupplier progress;
    @Getter private IDrawable emptyBackground;
    @Getter private IDrawable filledTexture;
    @Getter protected float progressStepSize = 0;

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        if (this.emptyBackground != null) this.emptyBackground.draw(context, x, y, width, height, widgetTheme);
        if (this.filledTexture == null) return;
        pushProgressStencil(context, x, y, width, height, widgetTheme);
        getFilledTexture().draw(context, x, y, width, height, widgetTheme);
        context.getStencil().pop();
    }

    protected abstract void pushProgressStencil(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme);

    protected float getCurrentProgress(int width, int height) {
        float p = this.progress == null ? 1f : (float) this.progress.getAsDouble();
        p = MathUtils.clamp(p, 0, 1);
        float stepSize = getCurrentProgressStepSize(width, height);
        if (stepSize > 0) {
            int c = (int) (p / stepSize);
            p = c * stepSize;
        }
        return p;
    }

    protected float getCurrentProgressStepSize(int width, int height) {
        return this.progressStepSize;
    }

    /**
     * Sets the displayed progress value. The progress is clamped between 0 (empty) and 1 (filled).
     *
     * @param progress progress supplier
     * @return this
     */
    public D progress(DoubleSupplier progress) {
        this.progress = progress;
        return self();
    }

    /**
     * Sets a fixed progress value to display. The progress is clamped between 0 (empty) and 1 (filled).
     *
     * @param progress progress
     * @return this
     */
    public D progress(double progress) {
        return progress(() -> progress);
    }

    /**
     * Sets a progress supplier which linearly increases from 0 to 1 with the given duration in milliseconds.
     *
     * @param durationMilliSeconds duration in milliseconds
     * @return this
     */
    public D progressDuration(int durationMilliSeconds) {
        return progress(() -> Util.getMillis() % durationMilliSeconds / (double) durationMilliSeconds);
    }

    /**
     * Sets a progress supplier which linearly increases from 0 to 1 with the given duration.
     *
     * @param duration duration
     * @param unit     time unit of the previous duration argument
     * @return this
     */
    public D progressDuration(long duration, TimeUnit unit) {
        return progressDuration((int) unit.toMillis(duration));
    }

    /**
     * Sets the empty texture which is always fully displayed.
     *
     * @param drawable empty texture
     * @return this
     */
    public D emptyTexture(IDrawable drawable) {
        this.emptyBackground = drawable;
        return self();
    }

    /**
     * Sets the filled texture which is partially drawn based on the current progress.
     *
     * @param drawable filled texture.
     * @return this
     */
    public D filledTexture(IDrawable drawable) {
        this.filledTexture = drawable;
        return self();
    }

    /**
     * Sets a progress step size. The displayed progress will be clamped to the closest multiple of this value.
     * Small values are smooth and high values are choppy. Values higher than 1 means the displayed progress is always 0.
     *
     * @param progressStepSize progress step size
     * @return this.
     */
    public D progressStepSize(float progressStepSize) {
        this.progressStepSize = progressStepSize;
        return self();
    }

    @SuppressWarnings("unchecked")
    protected D self() {
        return (D) this;
    }
}
