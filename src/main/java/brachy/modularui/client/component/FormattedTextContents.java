package brachy.modularui.client.component;

import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;

import org.jetbrains.annotations.NotNullByDefault;

import java.util.Optional;

@NotNullByDefault
public record FormattedTextContents(FormattedText text) implements ComponentContents {

    @Override
    public <R> Optional<R> visit(FormattedText.ContentConsumer<R> acceptor) {
        return text.visit(acceptor);
    }

    @Override
    public Type<?> type() {
        // this is meant to never ever be serialized. If this is called, bad things will happen.
        return PlainTextContents.TYPE;
    }

    @Override
    public <R> Optional<R> visit(FormattedText.StyledContentConsumer<R> acceptor, Style style) {
        return text.visit(acceptor, style);
    }
}
