package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;

public class EmptyPart extends ClassificationPart {

    @Nullable
    @Override
    public Consumed consume(Parser parser, String data, int from, int to) {
        return new Consumed(0, null);
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
