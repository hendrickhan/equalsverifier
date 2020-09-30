package nl.jqno.equalsverifier.internal.reflection;

import java.lang.reflect.Field;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

/**
 * Wraps an object to provide reflective access to it. ObjectAccessor can copy and scramble the
 * wrapped object.
 *
 * @param <T> The specified object's class.
 */
public final class InPlaceObjectAccessor<T> extends ObjectAccessor<T> {

    /** Private constructor. Call {@link ObjectAccessor#of(Object)} to instantiate. */
    /* default */ InPlaceObjectAccessor(T object, Class<T> type) {
        super(object, type);
    }

    /** {@inheritDoc} */
    @Override
    public T copy() {
        T copy = Instantiator.of(type()).instantiate();
        return copyInto(copy);
    }

    /** {@inheritDoc} */
    @Override
    public <S extends T> S copyIntoSubclass(Class<S> subclass) {
        S copy = Instantiator.of(subclass).instantiate();
        return copyInto(copy);
    }

    /** {@inheritDoc} */
    @Override
    public T copyIntoAnonymousSubclass() {
        T copy = Instantiator.of(type()).instantiateAnonymousSubclass();
        return copyInto(copy);
    }

    private <S> S copyInto(S copy) {
        for (Field field : FieldIterable.of(type())) {
            FieldAccessor accessor = new FieldAccessor(get(), field);
            accessor.copyTo(copy);
        }
        return copy;
    }

    /** {@inheritDoc} */
    @Override
    public void scramble(PrefabValues prefabValues, TypeTag enclosingType) {
        for (Field field : FieldIterable.of(type())) {
            FieldAccessor accessor = new FieldAccessor(get(), field);
            accessor.changeField(prefabValues, enclosingType);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void shallowScramble(PrefabValues prefabValues, TypeTag enclosingType) {
        for (Field field : FieldIterable.ofIgnoringSuper(type())) {
            FieldAccessor accessor = new FieldAccessor(get(), field);
            accessor.changeField(prefabValues, enclosingType);
        }
    }
}
