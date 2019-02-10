package xyz.phanta.wird.grammar.body;

import xyz.phanta.wird.grammar.part.ClassificationPart;
import xyz.phanta.wird.parser.ParserConfig;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ClassificationBody {

    private final int bodyIndex;
    private final List<ClassificationPart> parts;

    ClassificationBody(int bodyIndex, List<ClassificationPart> parts) {
        this.bodyIndex = bodyIndex;
        this.parts = parts;
    }

    public int getBodyIndex() {
        return bodyIndex;
    }

    public List<ClassificationPart> getParts() {
        return parts;
    }

    public void finalize(ParseTreeParentNode node, ParserConfig config) {
        config.getFinalizers(node.getClassification().getIdentifier(), node.getBodyIndex())
                .forEach(f -> f.finalize(node));
    }

    @Override
    public String toString() {
        return parts.stream()
                .map(p -> p.shouldRetainSpace() ? ("+ " + p) : p.toString())
                .collect(Collectors.joining(" "));
    }

}
