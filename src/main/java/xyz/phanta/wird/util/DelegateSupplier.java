package xyz.phanta.wird.util;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class DelegateSupplier<T> implements Supplier<T> {

    @Nullable
    private Supplier<Supplier<T>> delegateSupplier;
    @Nullable
    private Supplier<T> delegate;

    public DelegateSupplier(Supplier<Supplier<T>> delegateSupplier) {
        this.delegateSupplier = delegateSupplier;
    }

    @Nullable
    @Override
    public T get() {
        if (delegate != null) {
            T value = delegate.get();
            if (value != null) return value;
            delegate = null;
        }
        while (delegateSupplier != null) {
            delegate = delegateSupplier.get();
            if (delegate != null) {
                T value = delegate.get();
                if (value != null) return value;
            } else {
                delegateSupplier = null;
            }
        }
        return null;
    }

}
