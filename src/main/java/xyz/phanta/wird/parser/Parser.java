package xyz.phanta.wird.parser;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.grammar.body.ClassificationBody;
import xyz.phanta.wird.grammar.part.ClassificationPart;
import xyz.phanta.wird.parsetree.ParseTreeNode;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;
import xyz.phanta.wird.util.DelegateSupplier;
import xyz.phanta.wird.util.SingleSupplier;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class Parser {

    private final Classification root;
    private final Map<String, Classification> classifications;
    private final ParserConfig config;

    Parser(String root, Map<String, Classification> classifications, ParserConfig config) {
        this.root = classifications.get(root);
        if (this.root == null) throw new NoSuchElementException("Unknown classification: " + root);
        this.classifications = classifications;
        this.config = config;
    }

    public ParseTreeParentNode parse(String data) {
        Supplier<Consumed<ParseTreeParentNode>> results = parseSubtree(root, data, 0, data.length(), false, 0);
        if (results == null) throw new IllegalStateException("Could not parse!");
        Consumed<ParseTreeParentNode> result;
        int farthest = 0;
        while ((result = results.get()) != null) {
            String remaining = data.substring(result.getLengthConsumed());
            if (remaining.isEmpty() || remaining.chars().allMatch(Character::isWhitespace)) {
                return Objects.requireNonNull(result.produceNode());
            } else {
                farthest = Math.max(farthest, result.getLengthConsumed());
            }
        }
        throw new IllegalStateException("Could only parse " + farthest + " chars: "
                + data.substring(farthest, Math.min(farthest + 10, data.length())).replaceAll("[\r\n]", "\\\\n"));
    }

    @Nullable
    public Supplier<Consumed<ParseTreeParentNode>> parseSubtree(Classification classification,
                                                                String data, int from, int to, boolean retainSpace, int level) {
        if (config.shouldDebugPrint()) debug(level, "Enter class: " + classification.getIdentifier());

        Iterator<? extends ClassificationBody> iter = classification.getBodies().iterator();
        return new DelegateSupplier<>(() -> {
            while (iter.hasNext()) {
                ClassificationBody body = iter.next();
                if (config.shouldDebugPrint()) debug(level, "Try body: " + body);

                Supplier<Consumed<ParseTreeParentNode>> results = visitParts(
                        classification, body.getBodyIndex(), retainSpace, body.getParts(), 0, data, from, to, level);

                if (results != null) {
                    return () -> {
                        Consumed<ParseTreeParentNode> result = results.get();
                        if (result == null) return null;
                        ParseTreeParentNode node = Objects.requireNonNull(result.produceNode());
                        if (config.shouldDebugPrint()) debug(level, "Successful subtree: " + node.stringify(false));
                        return new Consumed<>(result.getLengthConsumed(), new LazilyFinalizedNode(node, body, config));
                    };
                }
            }

            if (config.shouldDebugPrint()) debug(level, "Exhausted bodies");
            return null;
        });
    }

    @Nullable
    private Supplier<Consumed<ParseTreeParentNode>> visitParts(Classification classification, int bodyIndex, boolean retainSpace,
                                                               List<ClassificationPart> parts, int partIndex, String data, int from, int to, int level) {
        if (partIndex >= parts.size()) {
            return new SingleSupplier<>(
                    new Consumed<>(0, () -> new ParseTreeParentNode(classification, bodyIndex, retainSpace)));
        }

        ClassificationPart part = parts.get(partIndex);
        if (config.shouldDebugPrint()) debug(level, "Consume part: " + part);

        int initialFrom = from;
        if (!part.shouldRetainSpace()) {
            while (from < to && Character.isWhitespace(data.charAt(from))) ++from;
        }

        Supplier<? extends Consumed<? extends ParseTreeNode>> results = part.consume(this, data, from, to, level);
        if (results == null) {
            if (config.shouldDebugPrint()) debug(level, "Part failed: no match");
            return null;
        }

        int finalFrom = from;
        int finalVoidedSpace = from - initialFrom;
        return new DelegateSupplier<>(() -> {
            Consumed<? extends ParseTreeNode> result;
            while ((result = results.get()) != null) {
                ParseTreeNode partNode = result.produceNode();
                int resultLength = result.getLengthConsumed();
                Supplier<Consumed<ParseTreeParentNode>> tails = visitParts(classification, bodyIndex, retainSpace,
                        parts, partIndex + 1, data, finalFrom + resultLength, to, level);
                if (tails != null) {
                    if (partNode != null) {
                        if (config.shouldDebugPrint()) debug(level, "Consumed: " + partNode.stringify(false));

                        return () -> {
                            Consumed<ParseTreeParentNode> tail = tails.get();
                            if (tail == null) return null;
                            ParseTreeParentNode node = Objects.requireNonNull(tail.produceNode());
                            node.prependChild(partNode);
                            return new Consumed<>(tail.getLengthConsumed() + resultLength + finalVoidedSpace, new SingleSupplier<>(node));
                        };
                    } else {
                        if (config.shouldDebugPrint()) debug(level, "Consumed empty");
                        return () -> {
                            Consumed<ParseTreeParentNode> tail = tails.get();
                            if (tail == null) return null;
                            return new Consumed<>(tail.getLengthConsumed() + resultLength + finalVoidedSpace, tail::produceNode);
                        };
                    }
                }
            }

            if (config.shouldDebugPrint()) debug(level, "Exhausted parts");
            return null;
        });
    }

    private void debug(int level, String msg) {
        for (int i = 0; i < level; i++) System.out.print("|  ");
        System.out.println(msg);
    }

    @Nullable
    public Classification getClassification(String identifier) {
        return classifications.get(identifier);
    }

    private static class LazilyFinalizedNode implements Supplier<ParseTreeParentNode> {

        private final ParseTreeParentNode node;
        private final ClassificationBody body;
        private final ParserConfig config;
        private boolean finalized = false;

        private LazilyFinalizedNode(ParseTreeParentNode node, ClassificationBody body, ParserConfig config) {
            this.node = node;
            this.body = body;
            this.config = config;
        }

        @Override
        public ParseTreeParentNode get() {
            if (!finalized) {
                finalized = true;
                body.finalize(node, config);
            }
            return node;
        }

    }

}
