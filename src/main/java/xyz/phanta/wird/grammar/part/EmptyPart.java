package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeNode;
import xyz.phanta.wird.util.SingleSupplier;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class EmptyPart extends ClassificationPart {

    @Nullable
    @Override
    public Supplier<? extends Consumed<? extends ParseTreeNode>> consume(Parser parser, String data, int from, int to, int level) {
        return new SingleSupplier<>(new Consumed<>(0, null));
    }

    @Override
    public String stringify(ParseTreeNode node) {
        throw new UnsupportedOperationException("Cannot stringify an empty part!");
    }

    @Override
    public String toString() {
        return "!";
    }

}
