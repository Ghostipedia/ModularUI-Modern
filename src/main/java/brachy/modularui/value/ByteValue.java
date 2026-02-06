package brachy.modularui.value;

import brachy.modularui.api.value.IByteValue;

public class ByteValue implements IByteValue<Byte> {

    protected byte value;

    public static Dynamic wrap(IByteValue<Byte> val) {
        return new Dynamic(val::getByteValue, val::setByteValue);
    }

    @Override
    public byte getByteValue() {
        return value;
    }

    @Override
    public void setByteValue(byte b) {
        value = b;
    }

    @Override
    public Byte getValue() {
        return getByteValue();
    }

    @Override
    public void setValue(Byte value) {
        setByteValue(value);
    }

    @Override
    public Class<Byte> getValueType() {
        return Byte.class;
    }

    public interface Supplier {

        byte getByte();
    }

    public interface Consumer {

        void setByte(byte b);
    }

    public static class Dynamic extends ByteValue {

        private final Supplier getter;
        private final Consumer setter;

        public Dynamic(Supplier getter, Consumer setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public byte getByteValue() {
            return this.getter.getByte();
        }

        @Override
        public void setByteValue(byte b) {
            this.setter.setByte(b);
        }
    }
}
