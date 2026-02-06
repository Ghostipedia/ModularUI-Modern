package brachy.modularui.widgets;

import brachy.modularui.api.value.ISyncOrValue;
import brachy.modularui.api.value.IValue;
import brachy.modularui.screen.RichTooltip;

import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class FluidDisplayWidget extends AbstractFluidDisplayWidget<FluidDisplayWidget> {

    private IValue<FluidStack> value;
    private boolean displayAmount = true;

    @Override
    public boolean isValidSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        return syncOrValue.isValueOfType(FluidStack.class);
    }

    @Override
    protected void setSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        super.setSyncOrValue(syncOrValue);
        this.value = syncOrValue.castValueNullable(FluidStack.class);
    }

    @Override
    protected boolean displayAmountText() {
        return this.displayAmount;
    }

    @Override
    protected @Nullable FluidStack getFluidStack() {
        return this.value != null ? this.value.getValue() : null;
    }

    public FluidDisplayWidget displayAmount(boolean displayAmount) {
        this.displayAmount = displayAmount;
        return this;
    }

    public FluidDisplayWidget fluidTooltip(BiConsumer<RichTooltip, FluidStack> tooltip) {
        return tooltipAutoUpdate(true).tooltipBuilder(t -> tooltip.accept(t, getFluidStack()));
    }
}
