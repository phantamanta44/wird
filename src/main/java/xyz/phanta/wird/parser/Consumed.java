package xyz.phanta.wird.parser;

import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class Consumed {

    private final int lengthConsumed;
    @Nullable
    private final Supplier<ParseTreeNode> nodeProducer;

    public Consumed(int lengthConsumed, @Nullable Supplier<ParseTreeNode> nodeProducer) {
        this.lengthConsumed = lengthConsumed;
        this.nodeProducer = nodeProducer;
    }

    public int getLengthConsumed() {
        return lengthConsumed;
    }

    @Nullable
    public ParseTreeNode produceNode() {
        return nodeProducer != null ? nodeProducer.get() : null;
    }

}
