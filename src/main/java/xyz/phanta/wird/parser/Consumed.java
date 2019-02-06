package xyz.phanta.wird.parser;

import xyz.phanta.wird.parsetree.ParseTreeNode;

import java.util.function.Supplier;

public class Consumed {

    private final int lengthConsumed;
    private final Supplier<ParseTreeNode> nodeProducer;

    public Consumed(int lengthConsumed, Supplier<ParseTreeNode> nodeProducer) {
        this.lengthConsumed = lengthConsumed;
        this.nodeProducer = nodeProducer;
    }

    public int getLengthConsumed() {
        return lengthConsumed;
    }

    public ParseTreeNode produceNode() {
        return nodeProducer.get();
    }

}
