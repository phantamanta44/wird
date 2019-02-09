package xyz.phanta.wird.parser;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.grammar.body.ClassificationBody;
import xyz.phanta.wird.grammar.part.ClassificationPart;
import xyz.phanta.wird.parsetree.ParseTreeNode;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class Parser {

    private final Classification root;
    private final Map<String, Classification> classifications;
    private final ParserConfig config;
    private int level = -1;

    Parser(String root, Map<String, Classification> classifications, ParserConfig config) {
        this.root = classifications.get(root);
        if (this.root == null) throw new NoSuchElementException("Unknown classification: " + root);
        this.classifications = classifications;
        this.config = config;
    }

    public ParseTreeParentNode parse(String data) {
        Consumed result = parseSubtree(root, data, 0, data.length(), false);
        if (result == null) throw new IllegalStateException("Could not parse!");
        String remaining = data.substring(result.getLengthConsumed());
        if (!(remaining.isEmpty() || remaining.chars().allMatch(Character::isWhitespace))) {
            int count = result.getLengthConsumed();
            throw new IllegalStateException("Could only parse " + count + " chars: "
                    + data.substring(count, Math.min(count + 10, data.length())).replaceAll("[\r\n]", "\\\\n"));
        }
        return Objects.requireNonNull((ParseTreeParentNode)result.produceNode());
    }

    @Nullable
    public Consumed parseSubtree(Classification classification, String data, int from, int to, boolean retainSpace) {
        ++level;
        if (config.shouldDebugPrint()) debug("Enter class: " + classification.getIdentifier());

        List<? extends ClassificationBody> bodies = classification.getBodies();
        bodyIteration:
        for (ClassificationBody body : bodies) {
            if (config.shouldDebugPrint()) debug("Try body: " + body);

            List<ClassificationPart> parts = body.getParts();
            ParseTreeParentNode node = new ParseTreeParentNode(classification, body.getBodyIndex(), retainSpace);
            int bodyFrom = from;

            for (ClassificationPart part : parts) {
                if (config.shouldDebugPrint()) debug("Consume part: " + part);

                if (!part.shouldRetainSpace()) {
                    while (bodyFrom < to && Character.isWhitespace(data.charAt(bodyFrom))) ++bodyFrom;
                }

                Consumed result = part.consume(this, data, bodyFrom, to);
                if (result == null) {
                    if (config.shouldDebugPrint()) debug("Part failed: no match");
                    continue bodyIteration;
                }

                ParseTreeNode partNode = result.produceNode();
                if (partNode != null) {
                    if (config.shouldDebugPrint()) debug("Consumed: " + partNode.stringify(false));
                    node.addChild(partNode);
                } else {
                    if (config.shouldDebugPrint()) debug("Consumed empty");
                }
                bodyFrom += result.getLengthConsumed();
            }

            if (config.shouldDebugPrint()) debug("Successful subtree: " + node.stringify(false));
            --level;
            body.finalize(node);
            return new Consumed(bodyFrom - from, new LazilyFinalizedNode(node, config));
        }

        if (config.shouldDebugPrint()) debug("Exhausted bodies");
        --level;
        return null;
    }

    private void debug(String msg) {
        for (int i = 0; i < level; i++) System.out.print("|  ");
        System.out.println(msg);
    }

    @Nullable
    public Classification getClassification(String identifier) {
        return classifications.get(identifier);
    }

    private static class LazilyFinalizedNode implements Supplier<ParseTreeNode> {

        private final ParseTreeParentNode node;
        private final ParserConfig config;
        private boolean finalized = false;

        private LazilyFinalizedNode(ParseTreeParentNode node, ParserConfig config) {
            this.node = node;
            this.config = config;
        }

        @Override
        public ParseTreeNode get() {
            if (!finalized) {
                config.getFinalizers(node.getClassification().getIdentifier(), node.getBodyIndex())
                        .forEach(f -> f.finalize(node));
            }
            return node;
        }

    }

}
