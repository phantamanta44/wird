package xyz.phanta.wird.parser;

import xyz.phanta.wird.grammar.Classification;

import javax.annotation.Nullable;

public class ParseFrame {

    private final Classification classification;
    private final int index;
    @Nullable
    private final ParseFrame parent;

    public ParseFrame(Classification classification, int index, @Nullable ParseFrame parent) {
        this.classification = classification;
        this.index = index;
        this.parent = parent;
    }

    public Classification getClassification() {
        return classification;
    }

    public int getIndex() {
        return index;
    }

    @Nullable
    public ParseFrame getParentFrame() {
        return parent;
    }

}
