package xyz.phanta.wird.parser.finalizer;

import xyz.phanta.wird.grammar.part.SyntheticPart;
import xyz.phanta.wird.parsetree.ParseTreeLeafNode;
import xyz.phanta.wird.parsetree.ParseTreeNode;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Finalizers {

    public static ClassificationFinalizer flatten(int index) {
        return n -> {
            ParseTreeParentNode child = (ParseTreeParentNode)n.getChildren().remove(index);
            n.getChildren().addAll(index, child.getChildren());
        };
    }

    public static ClassificationFinalizer wrap(int from, int count, int bodyIndex, ClassificationFinalizer... inside) {
        return n -> {
            List<ParseTreeNode> children = n.getChildren();
            List<ParseTreeNode> toWrap = children.subList(from, from + count);
            ParseTreeParentNode wrapper = new ParseTreeParentNode(n.getClassification(), bodyIndex, false);
            wrapper.getChildren().addAll(toWrap);
            toWrap.clear();
            children.add(0, wrapper);
            for (ClassificationFinalizer finalizer : inside) finalizer.finalize(wrapper);
        };
    }

    public static ClassificationFinalizer omit(int... omissions) {
        Arrays.sort(omissions);
        return n -> {
            for (int i = omissions.length - 1; i >= 0; i--) n.getChildren().remove(omissions[i]);
        };
    }

    public static ClassificationFinalizer join(String separator) {
        return n -> {
            String joined = n.getChildren().stream()
                    .map(c -> ((ParseTreeLeafNode)c).getContent())
                    .collect(Collectors.joining(separator));
            n.getChildren().clear();
            n.getChildren().add(new ParseTreeLeafNode(new SyntheticPart(), joined));
        };
    }

    public static ClassificationFinalizer join() {
        return n -> {
            String joined = n.getChildren().stream()
                    .map(c -> ((ParseTreeLeafNode)c).getContent())
                    .collect(Collectors.joining());
            n.getChildren().clear();
            n.getChildren().add(new ParseTreeLeafNode(new SyntheticPart(), joined));
        };
    }

    public static ClassificationFinalizer unescape() {
        return n -> {
            ParseTreeLeafNode escape = n.getLeaf(0);
            escape.setContent(escape.getContent()
                    .replaceAll(Pattern.quote("\\n"), "\n")
                    .replaceAll(Pattern.quote("\\t"), "\t")
                    .replaceAll(Pattern.quote("\\r"), "\r")
                    .replaceAll(Pattern.quote("\\0"), "\0")
                    .replaceAll("\\\\(.)", "$1"));
        };
    }

    public static <K, V extends Comparable<V>> Comparator<? super Map.Entry<K, V>> foo() {
        return Map.Entry.<K, V>comparingByValue().reversed();
    }

    public static ClassificationFinalizer mark(String... marks) {
        return n -> {
            for (String mark : marks) n.mark(mark);
        };
    }

}
