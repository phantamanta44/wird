package xyz.phanta.wird.util;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SingleSupplier<T> implements Supplier<T> {

    @Nullable
    private T value;

    public SingleSupplier(T value) {
        this.value = value;
    }

    @Nullable
    @Override
    public T get() {
        if (value == null) return null;
        T result = value;
        value = null;
        return result;
    }

}
