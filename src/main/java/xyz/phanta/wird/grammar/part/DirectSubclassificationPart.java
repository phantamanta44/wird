package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;

public class DirectSubclassificationPart extends ClassificationPart {

    private final Classification classification;

    public DirectSubclassificationPart(Classification classification) {
        this.classification = classification;
    }

    @Nullable
    @Override
    public Consumed consume(Parser parser, String data, int from, int to) {
        return parser.parseSubtree(classification, data, from, to, shouldRetainSpace());
    }

    @Override
    public String stringify(ParseTreeNode node) {
        return node.toString();
    }

    @Override
    public String toString() {
        return classification.getDisplayName();
    }

}
