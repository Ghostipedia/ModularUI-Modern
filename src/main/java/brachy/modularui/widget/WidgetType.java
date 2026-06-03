package brachy.modularui.widget;

import brachy.modularui.api.drawable.IDrawable;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.utils.serialization.codec.CodecRegistry;

import brachy.modularui.widgets.TextWidget;

import com.mojang.serialization.MapCodec;

public final class WidgetType<W extends IWidget> extends CodecRegistry.Entry<W> {

    public static final WidgetType<Widget<?>> WIDGET = reg("widget", Widget.CODEC);
    public static final WidgetType<ParentWidget<?>> PARENT = reg("parent", ParentWidget.CODEC);
    public static final WidgetType<ModularPanel<?>> PANEL = reg("panel", ModularPanel.CODEC);
    public static final WidgetType<IWidget> DRAWABLE = reg("drawable", IDrawable.DrawableWidget.CODEC);
    public static final WidgetType<TextWidget<?>> TEXT = reg("text", TextWidget.CODEC);

    private static final String WIDGET_TRANSLATION_KEY_FORMAT = "modularui.widget.%s.name";

    private static <W extends IWidget> WidgetType<W> reg(String name, MapCodec<W> codec) {
        return WidgetRegistry.INSTANCE.register(name, codec);
    }

    public static void init() {}

    WidgetType(String name, MapCodec<W> codec) {
        super(name, codec);
    }

    public String getTranslationKey() {
        return WIDGET_TRANSLATION_KEY_FORMAT.formatted(name().replace(':', '_'));
    }
}
