package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class ClassificationPart {

    private boolean retainSpace = false;

    public ClassificationPart setRetainsSpace() {
        retainSpace = true;
        return this;
    }

    public boolean shouldRetainSpace() {
        return retainSpace;
    }

    @Nullable
    public abstract Supplier<? extends Consumed<? extends ParseTreeNode>> consume(
            Parser parser, String data, int from, int to, int level);

    public abstract String stringify(ParseTreeNode node);

}
