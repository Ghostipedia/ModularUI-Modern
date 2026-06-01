package brachy.modularui.widget;

import brachy.modularui.api.widget.IWidget;
import brachy.modularui.utils.serialization.codec.CodecRegistry;
import brachy.modularui.utils.serialization.codec.CodecUtil;

import com.mojang.serialization.MapCodec;

public class WidgetRegistry extends CodecRegistry<IWidget, WidgetType<?>> {

    public static final WidgetRegistry INSTANCE = new WidgetRegistry();

    public final MapCodec<IWidget> dispatchCodec = CodecUtil.dispatchNullable(byNameCodec(), IWidget::getType, t -> t.codec().codec());

    private WidgetRegistry() {}

    public <W extends IWidget> WidgetType<W> register(String name, MapCodec<W> codec) {
        var t = new WidgetType<>(name, codec);
        register(t);
        return t;
    }
}
