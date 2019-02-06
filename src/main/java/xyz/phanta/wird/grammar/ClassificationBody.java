package xyz.phanta.wird.grammar;

import xyz.phanta.wird.grammar.part.ClassificationPart;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassificationBody {

    private final List<ClassificationPart> parts;

    public ClassificationBody(List<ClassificationPart> parts) {
        this.parts = parts;
    }

    public ClassificationBody(ClassificationPart... parts) {
        this(Arrays.asList(parts));
    }

    public List<ClassificationPart> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return parts.stream()
                .map(p -> p.shouldRetainSpace() ? ("+ " + p) : p.toString())
                .collect(Collectors.joining(" "));
    }

}
