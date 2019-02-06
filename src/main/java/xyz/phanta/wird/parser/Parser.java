package xyz.phanta.wird.parser;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.grammar.ClassificationBody;
import xyz.phanta.wird.grammar.part.ClassificationPart;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Parser {

    private final Classification root;
    private final Map<String, Classification> classifications;
    private final ParserConfig config;
    private int level = 0;

    Parser(String root, Map<String, Classification> classifications, ParserConfig config) {
        this.root = classifications.get(root);
        if (this.root == null) throw new NoSuchElementException("Unknown classification: " + root);
        this.classifications = classifications;
        this.config = config;
    }

    public ParseTreeParentNode parse(String data) {
        Consumed result = parseSubtree(root, null, data, 0, false);
        if (result == null) throw new IllegalStateException("Could not parse!");
        if (!data.substring(result.getLengthConsumed()).trim().isEmpty()) {
            throw new IllegalStateException("Could only parse " + result.getLengthConsumed() + " characters!");
        }
        return (ParseTreeParentNode)result.produceNode();
    }

    @Nullable
    public Consumed parseSubtree(Classification classification, @Nullable ParseFrame parent, String data, int index, boolean retainSpace) {
        String prefix = null;
        if (config.shouldDebugPrint()) {
            StringBuilder prefixBuilder = new StringBuilder();
            for (int i = 0; i < level; i++) prefixBuilder.append("|   ");
            prefix = prefixBuilder.toString();
            level += 1;
            System.out.println(prefix + "Entering branch " + classification.getIdentifier() + " at " + index);
        }
        ParseFrame frame = new ParseFrame(classification, index, parent);
        possibilities:
        for (int bodyIndex = 0; bodyIndex < classification.getBodies().size(); bodyIndex++) {
            ClassificationBody possibility = classification.getBodies().get(bodyIndex);
            if (config.shouldDebugPrint()) System.out.println(prefix + "Trying possibility " + possibility);
            ParseTreeParentNode node = new ParseTreeParentNode(classification, bodyIndex, retainSpace);
            int runningIndex = index;
            for (ClassificationPart part : possibility.getParts()) {
                if (!part.shouldRetainSpace()) {
                    while (runningIndex < data.length() && Character.isWhitespace(data.charAt(runningIndex))) {
                        ++runningIndex;
                    }
                    if (runningIndex >= data.length()) {
                        if (config.shouldDebugPrint()) System.out.println(prefix + "Ran out of data");
                        continue possibilities;
                    }
                }
                Consumed result = part.consume(this, frame, runningIndex, data);
                if (result == null) {
                    if (config.shouldDebugPrint()) System.out.println(prefix + "Could not parse part " + part);
                    continue possibilities;
                }
                runningIndex += result.getLengthConsumed();
                node.addChild(result.produceNode());
            }
            if (config.shouldDebugPrint()) {
                System.out.println(prefix + "Successfully produced subtree " + classification.getIdentifier() + ": " + node.stringify(false));
            }
            config.getFinalizers(classification.getIdentifier(), bodyIndex).forEach(f -> f.finalize(node));
            level -= 1;
            return new Consumed(runningIndex - index, () -> node);
        }
        if (config.shouldDebugPrint()) System.out.println(prefix + "Branch parsing ended at " + classification.getIdentifier());
        level -= 1;
        return null;
    }

    @Nullable
    public Classification getClassification(String identifier) {
        return classifications.get(identifier);
    }

}
