package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.ParseFrame;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;

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
    public abstract Consumed consume(Parser parser, ParseFrame frame, int index, String data);

    public abstract String stringify(ParseTreeNode node);

}
