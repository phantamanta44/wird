package xyz.phanta.wird.parser;

import xyz.phanta.wird.grammar.Classification;

import javax.annotation.Nullable;

public class ParseFrame {

    private final Classification classification;
    private final int bodyIndex;
    @Nullable
    private final ParseFrame parent;

    public ParseFrame(Classification classification, int bodyIndex, @Nullable ParseFrame parent) {
        this.classification = classification;
        this.bodyIndex = bodyIndex;
        this.parent = parent;
    }

    public Classification getClassification() {
        return classification;
    }

    public int getBodyIndex() {
        return bodyIndex;
    }

    @Nullable
    public ParseFrame getParentFrame() {
        return parent;
    }

}
