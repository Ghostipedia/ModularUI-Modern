package brachy.modularui.drawable.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.KeybindContents;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.ScoreContents;
import net.minecraft.network.chat.contents.SelectorContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.util.InsertingContents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ExtraComponentCodecs {

    public static final Codec<Component> NEVER_FLAT_COMPONENT_CODEC = Codec.recursive("Component", ExtraComponentCodecs::createCodec);


    private static <T extends StringRepresentable, E> MapCodec<E> createLegacyComponentMatcher(T[] types,
                                                                                              Function<T, MapCodec<? extends E>> codecGetter,
                                                                                              Function<E, T> typeGetter) {
        MapCodec<E> fuzzyCodec = new FuzzyCodec<>(
                Stream.of(types).map(codecGetter).toList(), val -> codecGetter.apply(typeGetter.apply(val))
        );
        MapCodec<E> namedTypeCodec = StringRepresentable.fromValues(() -> types).dispatchMap(typeGetter, codecGetter);
        MapCodec<E> codec = new StrictEither<>("type", namedTypeCodec, fuzzyCodec);
        return ExtraCodecs.orCompressed(codec, namedTypeCodec);
    }

    private static Codec<Component> createCodec(Codec<Component> self) {
        ComponentContents.Type<?>[] types = new ComponentContents.Type[] {
                PlainTextContents.TYPE, TranslatableContents.TYPE, KeybindContents.TYPE, ScoreContents.TYPE, SelectorContents.TYPE, NbtContents.TYPE, InsertingContents.TYPE
        };
        MapCodec<ComponentContents> contentsCodec = createLegacyComponentMatcher(types, ComponentContents.Type::codec, ComponentContents::type);
        return RecordCodecBuilder.create(instance -> instance.group(
                contentsCodec.forGetter(Component::getContents),
                ExtraCodecs.nonEmptyList(self.listOf()).optionalFieldOf("extra", List.of()).forGetter(Component::getSiblings),
                Style.Serializer.MAP_CODEC.forGetter(Component::getStyle)
        ).apply(instance, ModularComponent::new));
    }

    static class FuzzyCodec<T> extends MapCodec<T> {

        private final List<MapCodec<? extends T>> codecs;
        private final Function<T, MapEncoder<? extends T>> encoderGetter;

        public FuzzyCodec(List<MapCodec<? extends T>> codecs, Function<T, MapEncoder<? extends T>> encoderGetter) {
            this.codecs = codecs;
            this.encoderGetter = encoderGetter;
        }

        @Override
        public <S> DataResult<T> decode(DynamicOps<S> ops, MapLike<S> input) {
            for (MapDecoder<? extends T> mapdecoder : this.codecs) {
                DataResult<? extends T> dataresult = mapdecoder.decode(ops, input);
                if (dataresult.result().isPresent()) {
                    return (DataResult<T>) dataresult;
                }
            }

            return DataResult.error(() -> "No matching codec found");
        }

        @Override
        public <S> RecordBuilder<S> encode(T input, DynamicOps<S> ops, RecordBuilder<S> prefix) {
            MapEncoder<T> mapencoder = (MapEncoder<T>) this.encoderGetter.apply(input);
            return mapencoder.encode(input, ops, prefix);
        }

        @Override
        public <S> Stream<S> keys(DynamicOps<S> ops) {
            return this.codecs.stream().flatMap(p_304401_ -> p_304401_.keys(ops)).distinct();
        }

        @Override
        public String toString() {
            return "FuzzyCodec[" + this.codecs + "]";
        }
    }

    static class StrictEither<T> extends MapCodec<T> {

        private final String typeFieldName;
        private final MapCodec<T> typed;
        private final MapCodec<T> fuzzy;

        public StrictEither(String typeFieldName, MapCodec<T> typed, MapCodec<T> fuzzy) {
            this.typeFieldName = typeFieldName;
            this.typed = typed;
            this.fuzzy = fuzzy;
        }

        @Override
        public <O> DataResult<T> decode(DynamicOps<O> ops, MapLike<O> input) {
            return input.get(this.typeFieldName) != null ? this.typed.decode(ops, input) : this.fuzzy.decode(ops, input);
        }

        @Override
        public <O> RecordBuilder<O> encode(T input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
            return this.fuzzy.encode(input, ops, prefix);
        }

        @Override
        public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
            return Stream.concat(this.typed.keys(ops), this.fuzzy.keys(ops)).distinct();
        }
    }
}
