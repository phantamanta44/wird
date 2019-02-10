package xyz.phanta.wird.parser;

import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class Consumed<T extends ParseTreeNode> {

    private final int lengthConsumed;
    @Nullable
    private final Supplier<? extends T> nodeProducer;

    public Consumed(int lengthConsumed, @Nullable Supplier<? extends T> nodeProducer) {
        this.lengthConsumed = lengthConsumed;
        this.nodeProducer = nodeProducer;
    }

    int getLengthConsumed() {
        return lengthConsumed;
    }

    @Nullable
    T produceNode() {
        return nodeProducer != null ? nodeProducer.get() : null;
    }

}
