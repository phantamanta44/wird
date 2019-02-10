package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class SubclassificationPart extends ClassificationPart {

    private final String identifier;

    public SubclassificationPart(String identifier) {
        this.identifier = identifier;
    }

    @Nullable
    @Override
    public Supplier<? extends Consumed<? extends ParseTreeNode>> consume(Parser parser, String data, int from, int to, int level) {
        Classification classification = parser.getClassification(identifier);
        if (classification == null) throw new NoSuchElementException("Unknown classification: " + identifier);
        return parser.parseSubtree(classification, data, from, to, shouldRetainSpace(), level + 1);
    }

    @Override
    public String stringify(ParseTreeNode node) {
        return node.toString();
    }

    @Override
    public String toString() {
        return identifier;
    }

}
