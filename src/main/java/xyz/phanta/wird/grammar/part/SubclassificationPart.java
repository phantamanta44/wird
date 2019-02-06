package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.ParseFrame;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;

public class SubclassificationPart extends ClassificationPart {

    private final String identifier;

    public SubclassificationPart(String identifier) {
        this.identifier = identifier;
    }

    @Nullable
    @Override
    public Consumed consume(Parser parser, ParseFrame frame, int index, String data) {
        Classification classification = parser.getClassification(identifier);
        if (classification == null) throw new NoSuchElementException("Unknown classification: " + identifier);
        if (frame.getClassification().equals(classification) && index == frame.getIndex()) {
            throw new IllegalArgumentException("Head recursion!");
        }
        return parser.parseSubtree(classification, frame, data, index, shouldRetainSpace());
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
