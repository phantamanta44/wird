package xyz.phanta.wird.parsetree;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public abstract class ParseTreeNode {

    @Nullable
    private Set<String> marks;

    public abstract String stringify(boolean space);

    public void mark(String mark) {
        if (marks == null) marks = new HashSet<>();
        marks.add(mark);
    }

    public void unmark(String mark) {
        if (marks != null) marks.remove(mark);
    }

    public boolean isMarked(String mark) {
        return marks != null && marks.contains(mark);
    }

}
