package xyz.phanta.wird.parsetree;

import xyz.phanta.wird.grammar.Classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParseTreeParentNode extends ParseTreeNode {

    private final Classification classification;
    private final int bodyIndex;
    private final boolean retainSpace;
    private final List<ParseTreeNode> children = new ArrayList<>();

    public ParseTreeParentNode(Classification classification, int bodyIndex, boolean retainSpace) {
        this.classification = classification;
        this.bodyIndex = bodyIndex;
        this.retainSpace = retainSpace;
    }

    public Classification getClassification() {
        return classification;
    }

    public int getBodyIndex() {
        return bodyIndex;
    }

    public void addChild(ParseTreeNode node) {
        children.add(node);
    }

    public ParseTreeNode getChild(int index) {
        return children.get(index);
    }

    public ParseTreeParentNode getSubtree(int index) {
        return (ParseTreeParentNode)getChild(index);
    }

    public ParseTreeLeafNode getLeaf(int index) {
        return (ParseTreeLeafNode)getChild(index);
    }

    public List<ParseTreeNode> getChildren() {
        return children;
    }

    @SuppressWarnings("unchecked")
    public List<ParseTreeParentNode> getSubtrees() {
        return (List<ParseTreeParentNode>)(List)children;
    }

    @Override
    public String stringify(boolean space) {
        StringBuilder sb = new StringBuilder();
        if (space && !retainSpace) sb.append(" ");
        sb.append(children.get(0).stringify(false));
        for (int i = 1; i < children.size(); i++) sb.append(children.get(i).stringify(true));
        return sb.toString();
    }

    @Override
    public String toString() {
        return classification.getIdentifier() + " {\n"
                + children.stream()
                .flatMap(s -> Arrays.stream(s.toString().split("\n")))
                .map(n -> "  " + n)
                .collect(Collectors.joining("\n"))
                + "\n}";
    }

}
