package xyz.phanta.wird.parser;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.grammar.body.ClassificationBody;
import xyz.phanta.wird.grammar.body.FlatClassificationBody;
import xyz.phanta.wird.grammar.body.LeftRecursiveClassificationBody;
import xyz.phanta.wird.grammar.part.*;
import xyz.phanta.wird.parser.finalizer.ClassificationFinalizer;
import xyz.phanta.wird.parser.finalizer.Finalizers;
import xyz.phanta.wird.parsetree.ParseTreeLeafNode;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grammar {

    private static final Classification C_GRAMMAR = new Classification(
            "grammar",
            new FlatClassificationBody(0,
                    new SubclassificationPart("classification"),
                    new RegularExpressionPart("(\\s*\\n)+").setRetainsSpace(),
                    new SubclassificationPart("grammar")
            ),
            new FlatClassificationBody(1,
                    new SubclassificationPart("classification")
            )
    );

    private static final Classification C_CLASSIFICATION = new Classification(
            "classification",
            new FlatClassificationBody(0,
                    new LiteralPart(":"),
                    new SubclassificationPart("classification_name"),
                    new SubclassificationPart("classification_bodies")
            )
    );

    private static final Classification C_CLASSIFICATION_BODIES = new Classification(
            "classification_bodies",
            new FlatClassificationBody(0,
                    new LiteralPart("="),
                    new SubclassificationPart("classification_body"),
                    new SubclassificationPart("classification_bodies")
            ),
            new FlatClassificationBody(1,
                    new LiteralPart("="),
                    new SubclassificationPart("classification_body")
            )
    );

    private static final Classification C_CLASSIFICATION_BODY = new Classification(
            "classification_body",
            new FlatClassificationBody(0,
                    new SubclassificationPart("space_retaining_conjunction"),
                    new SubclassificationPart("classification_body")
            ),
            new FlatClassificationBody(1,
                    new SubclassificationPart("part"),
                    new SubclassificationPart("classification_body")
            ),
            new FlatClassificationBody(2,
                    new SubclassificationPart("space_retaining_conjunction")
            ),
            new FlatClassificationBody(3,
                    new SubclassificationPart("part")
            )
    );

    private static final Classification C_SPACE_RETAINING_CONJUNCTION = new Classification(
            "space_retaining_conjunction",
            new FlatClassificationBody(0,
                    new LiteralPart("+"),
                    new SubclassificationPart("part")
            )
    );

    private static final Classification C_PART = new Classification(
            "part",
            new FlatClassificationBody(0,
                    new SubclassificationPart("classification_name")
            ),
            new FlatClassificationBody(1,
                    new LiteralPart("\""),
                    new SubclassificationPart("literal").setRetainsSpace(),
                    new LiteralPart("\"").setRetainsSpace()
            ),
            new FlatClassificationBody(2,
                    new LiteralPart("/"),
                    new SubclassificationPart("regexp").setRetainsSpace(),
                    new LiteralPart("/").setRetainsSpace()
            ),
            new FlatClassificationBody(3,
                    new LiteralPart("!")
            )
    );

    private static final Classification C_CLASSIFICATION_NAME = new Classification(
            "classification_name",
            new FlatClassificationBody(0,
                    new RegularExpressionPart("[\\w\\-_]+")
            )
    );

    private static final Classification C_LITERAL = new Classification(
            "literal",
            new FlatClassificationBody(0,
                    new SubclassificationPart("literal_segment"),
                    new SubclassificationPart("literal").setRetainsSpace()
            ),
            new FlatClassificationBody(1,
                    new SubclassificationPart("literal_segment")
            )
    );

    private static final Classification C_LITERAL_SEGMENT = new Classification(
            "literal_segment",
            new FlatClassificationBody(0,
                    new SubclassificationPart("str_escape")
            ),
            new FlatClassificationBody(1,
                    new RegularExpressionPart("[^\\\\\"]+")
            )
    );

    private static final Classification C_REGEXP = new Classification(
            "regexp",
            new FlatClassificationBody(0,
                    new SubclassificationPart("regexp_segment"),
                    new SubclassificationPart("regexp").setRetainsSpace()
            ),
            new FlatClassificationBody(1,
                    new SubclassificationPart("regexp_segment")
            )
    );

    private static final Classification C_REGEXP_SEGMENT = new Classification(
            "regexp_segment",
            new FlatClassificationBody(0,
                    new SubclassificationPart("str_escape")
            ),
            new FlatClassificationBody(1,
                    new RegularExpressionPart("[^\\\\/]+")
            )
    );

    private static final Classification C_STR_ESCAPE = new Classification(
            "str_escape",
            new FlatClassificationBody(0,
                    new RegularExpressionPart("\\\\.")
            )
    );

    public static final Grammar WIRD_GRAMMAR = new Grammar(Stream.of(
            C_GRAMMAR, C_CLASSIFICATION, C_CLASSIFICATION_BODIES, C_CLASSIFICATION_BODY, C_SPACE_RETAINING_CONJUNCTION,
            C_PART, C_CLASSIFICATION_NAME, C_LITERAL, C_LITERAL_SEGMENT, C_REGEXP, C_REGEXP_SEGMENT, C_STR_ESCAPE
    ).collect(Collectors.toMap(Classification::getIdentifier, c -> c)));

    private static final ClassificationFinalizer F_STRING_ESCAPER = n -> {
        ParseTreeParentNode escapeNode = n.getSubtree(0);
        ParseTreeLeafNode escape = escapeNode.getLeaf(0);
        escape.setContent(escape.getContent()
                .replaceAll(Pattern.quote("\\n"), "\n")
                .replaceAll(Pattern.quote("\\t"), "\t")
                .replaceAll(Pattern.quote("\\r"), "\r")
                .replaceAll(Pattern.quote("\\0"), "\0")
                .replaceAll("\\\\(.)", "$1"));
        n.getChildren().set(0, escape);
    };

    public static final ParserConfig WIRD_CONFIG = new ParserConfig.Builder()
            .withFinalizers("grammar", 0, Finalizers.omit(1), Finalizers.flatten(1))
            .withFinalizers("classification", Finalizers.omit(0), Finalizers.flatten(0))

            .withFinalizers("classification_bodies", Finalizers.omit(0))
            .withFinalizers("classification_bodies", 0, Finalizers.flatten(1))

            .withFinalizers("classification_body", 0, Finalizers.flatten(1), Finalizers.flatten(0))
            .withFinalizers("classification_body", 1, Finalizers.flatten(1))
            .withFinalizers("classification_body", 2, Finalizers.flatten(0))
            .withFinalizers("space_retaining_conjunction", Finalizers.omit(0), n -> n.getChild(0).mark("s"))

            .withFinalizers("part", 1, Finalizers.omit(0, 2))
            .withFinalizers("part", 2, Finalizers.omit(0, 2))

            .withFinalizers("literal", 0, Finalizers.flatten(1), Finalizers.flatten(0), Finalizers.join())
            .withFinalizers("literal", 1, Finalizers.flatten(0), Finalizers.join())
            .withFinalizers("literal_segment", 0, F_STRING_ESCAPER)
            .withFinalizers("regexp", 0, Finalizers.flatten(1), Finalizers.flatten(0), Finalizers.join())
            .withFinalizers("regexp", 1, Finalizers.flatten(0), Finalizers.join())
            .withFinalizers("regexp_segment", 0, Finalizers.flatten(0))
            .build();

    public static Grammar create(String grammar) {
        ParseTreeParentNode tree;
        try {
            tree = WIRD_GRAMMAR.newParser("grammar", WIRD_CONFIG).parse(grammar);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse grammar", e);
        }

        Map<String, Classification> classifications = new HashMap<>();
        for (ParseTreeParentNode classificationNode : tree.getSubtrees()) {
            List<ParseTreeParentNode> bodyNodes = classificationNode.getSubtree(1).getSubtrees();
            List<FlatClassificationBody> bodies = new ArrayList<>();
            String identifier = classificationNode.getLeaf(0).getContent();

            List<ClassificationBody> recursiveBodies = null;
            Classification recursiveClassification = null;

            for (int bodyIndex = 0; bodyIndex < bodyNodes.size(); bodyIndex++) {
                List<ParseTreeParentNode> partNodes = bodyNodes.get(bodyIndex).getSubtrees();
                ParseTreeParentNode first = partNodes.get(0);

                if (first.getBodyIndex() == 0 && first.getSubtree(0).getLeaf(0).getContent().equals(identifier)) {
                    if (recursiveBodies == null) {
                        recursiveBodies = new ArrayList<>();
                        recursiveClassification = new Classification(identifier, identifier + "'", recursiveBodies);
                    }
                    recursiveBodies.add(new LeftRecursiveClassificationBody(bodyIndex, recursiveClassification, walkParts(1, partNodes)));
                } else {
                    bodies.add(new FlatClassificationBody(bodyIndex, walkParts(0, partNodes)));
                }
            }

            if (recursiveBodies != null) {
                for (FlatClassificationBody body : bodies) {
                    body.markRecursive();
                    body.getParts().add(new DirectSubclassificationPart(recursiveClassification));
                }
                recursiveBodies.add(new FlatClassificationBody(-1, new EmptyPart()));
                classifications.put(identifier + "'", recursiveClassification);
            }
            classifications.put(identifier, new Classification(identifier, bodies));
        }
        return new Grammar(classifications);
    }

    private static List<ClassificationPart> walkParts(int partIndex, List<ParseTreeParentNode> partNodes) {
        List<ClassificationPart> parts = new ArrayList<>();
        for (; partIndex < partNodes.size(); partIndex++) {
            ParseTreeParentNode partNode = partNodes.get(partIndex);
            boolean retainSpace = partNode.isMarked("s");
            ClassificationPart part;
            switch (partNode.getBodyIndex()) {
                case 0:
                    part = new SubclassificationPart(partNode.getSubtree(0).getLeaf(0).getContent());
                    break;
                case 1:
                    part = new LiteralPart(partNode.getSubtree(0).getLeaf(0).getContent());
                    break;
                case 2:
                    part = new RegularExpressionPart(partNode.getSubtree(0).getLeaf(0).getContent());
                    break;
                case 3:
                    part = new EmptyPart();
                    break;
                default:
                    throw new IllegalStateException("Impossible state: " + partNode.getBodyIndex());
            }
            if (retainSpace) part.setRetainsSpace();
            parts.add(part);
        }
        return parts;
    }

    private final Map<String, Classification> classifications;

    private Grammar(Map<String, Classification> classifications) {
        this.classifications = classifications;
    }

    public Parser newParser(String root, ParserConfig config) {
        return new Parser(root, classifications, config);
    }

    public Parser newParser(String root) {
        return newParser(root, new ParserConfig.Builder().build());
    }

    @Override
    public String toString() {
        return classifications.values().stream()
                .map(Classification::toString)
                .sorted()
                .collect(Collectors.joining("\n"));
    }

}
